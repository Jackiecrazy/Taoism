package com.jackiecrazi.taoism.common.entity;

import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class EntityMove extends Entity {
    public static final ArrayList<Class<? extends EntityMove>> moves = new ArrayList<>();

    /*public static EntityMove create(EntityLivingBase attacker) {
        return TaoCombatUtils.getMoveFromEntity(attacker);
    }*/

    protected EntityLivingBase attacker;
    @Nullable
    protected ItemStack stack;
    protected ArrayList<EntityLivingBase> hitlist = new ArrayList<>(), attacked = new ArrayList<>();

    public EntityMove(World worldIn) {
        super(worldIn);
    }

    public EntityMove(World worldIn, EntityLivingBase attacker, ItemStack stack) {
        super(worldIn);
        this.attacker = attacker;
        this.stack = stack;
        this.setLocationAndAngles(attacker.posX, attacker.posY, attacker.posZ, 0f, 0f);
    }

    public EntityMove(EntityLivingBase attacker) {
        this(attacker.world, attacker, attacker.getHeldItem(EnumHand.MAIN_HAND));
        //System.out.println("init");
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            if (attacker.getHeldItem(EnumHand.MAIN_HAND) != stack && attacker.getHeldItem(EnumHand.OFF_HAND) != stack)
                this.setDead();
            try {
                if (attacker != null) this.setLocationAndAngles(attacker.posX, attacker.posY, attacker.posZ, 0f, 0f);
                this.attackPre(attacker, stack, ticksExisted);
                TaoWeapon.spawn = false;
                for (Entity e : this.compileList(attacker, stack, ticksExisted)) {
                    if (e instanceof EntityLivingBase && !attacked.contains(e)) {
                        EntityLivingBase en = (EntityLivingBase) e;
                        this.attack(attacker, en, stack, ticksExisted);
                        attacked.add(en);
                    }
                    //System.out.println("bye");
                }
                TaoWeapon.spawn = true;
                this.attackPost(attacker, stack, ticksExisted);
                if (ticksExisted >= this.duration(attacker, attacker, stack)) this.setDead();
            } catch (Exception e) {
                e.printStackTrace();
                this.setDead();
            }
        }
    }

    @Override
    protected void entityInit() {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        attacker = (EntityLivingBase) world.getEntityByID(compound.getInteger("owner"));
        stack = new ItemStack(compound.getCompoundTag("item"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("owner", attacker.getEntityId());
        if (stack != null)
            compound.setTag("item", stack.writeToNBT(new NBTTagCompound()));
    }

    //start, each tick check and deal damage. If entity is blocking or parrying, stop, deduct appropriate posture from both sides, end
    //have a few "coda ticks" where the weapon is just where it is, slowly fading out
    @Nonnull
    public abstract ArrayList<Entity> compileList(EntityLivingBase attacker, ItemStack stack, int duration);

    public abstract void attack(EntityLivingBase attacker, EntityLivingBase defender, ItemStack stack, int duration) throws IllegalAccessException;

    public void attackPre(EntityLivingBase attacker, ItemStack stack, int duration) {
    }

    public void attackPost(EntityLivingBase attacker, ItemStack stack, int duration) {
    }

    public abstract float damageMultiplier(EntityLivingBase attacker, EntityLivingBase defender, ItemStack stack, int duration);

    public abstract int duration(EntityLivingBase attacker, EntityLivingBase defender, ItemStack stack);

    /**
     * called when attack is parried. To make attacks unblockable, override this
     *
     * @param attacker
     * @param defender
     * @param stack
     */
    public void interrupted(EntityLivingBase attacker, EntityLivingBase defender, ItemStack stack) {

    }

    public boolean canExecute(EntityLivingBase attacker, ItemStack stack) {
        return true;
    }
}
