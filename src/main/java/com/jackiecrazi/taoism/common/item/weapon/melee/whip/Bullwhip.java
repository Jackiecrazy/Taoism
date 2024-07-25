package com.jackiecrazi.taoism.common.item.weapon.melee.whip;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityWhiplash;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

/*
one handed, no parry, can be parried by enemy:
Standard hit knocks back a little more than usual.
if it hits near the limit of its range (that is, 7 blocks), it creates a sonic boom that... basically is an explosion.
mobs in a very small area take splash damage, is disoriented and knocked away slightly.
Shift is the other way around, focusing on drawing enemies close:
standard hit will apply negative knockback so you two collide into each other, giving the target brief slow, fatigue and bind.
striking at max range will instead disarm (bind) the enemy for a good while.
Damage is doubly reduced by armor, but it hits hard to start off, as per other whips.
note to self: make the projectile icon a loop and fiddle with the tether so it looks like the loop is traveling down the whip.
 */
public class Bullwhip extends TaoWeapon {
    private final PartDefinition[] parts = {
            StaticRefs.HANDLE,
            new PartDefinition("handlewrap", false, StaticRefs.STRING)
    };

    public Bullwhip() {
        super(1, 1.6, 6, 1.6f);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0;
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (!w.isRemote
                && gettagfast(stack).hasKey("dartID")
                && e.world.getEntityByID(gettagfast(stack).getInteger("dartID")) == null) {
            gettagfast(stack).removeTag("dartID");
        }
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack is) {
        if (!elb.world.isRemote) {
            if (!isThrown(is)) {
                EntityWhiplash ew = new EntityWhiplash(elb.world, elb, getHand(is), 7);
                ew.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, 1f, 0.0F);
                elb.world.spawnEntity(ew);
                gettagfast(is).setInteger("dartID", ew.getEntityId());
            }
        }
        return super.onEntitySwing(elb, is);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.parry") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("weapon.projectile") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_RED + I18n.format("bullwhip.armor") + TextFormatting.RESET);
        tooltip.add(I18n.format("bullwhip.knockback"));
        tooltip.add(I18n.format("bullwhip.sonicboom"));
        tooltip.add(I18n.format("bullwhip.sneak"));
        tooltip.add(I18n.format("bullwhip.sneakboom"));
    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        return 0;
    }

    @Override
    public boolean canBlock(EntityLivingBase defender, Entity attacker, ItemStack item, boolean recharged, float amount) {
        return false;
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getBuff(item, "boomer") == 1 ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 0.5f;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return super.damageMultiplier(attacker, target, item);
    }

    @Override
    public float onKnockingBack(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (getBuff(stack, "boomer") == 1) {
            if (!attacker.isSneaking()) {
                //TaoPotionUtils.attemptAddPot(target, new PotionEffect(TaoPotion.DISORIENT, 20), false);
                TaoPotionUtils.disorient(target, 20);
            }
        }
        if (attacker.isSneaking()) {
            return -orig * 3;
        }
        return super.onKnockingBack(attacker, target, stack, orig) * 1.2f;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return -target.getTotalArmorValue()/2;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (attacker.isSneaking()) {
            int time=getBuff(stack, "boomer") == 1 ? 40 : 10;
            TaoCasterData.getTaoCap(target).setBindTime(time);
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.SLOWNESS, time), false);
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.WEAKNESS, time), false);
            TaoCasterData.getTaoCap(target).setBindTime(10);
        }
    }

    private boolean isThrown(ItemStack is) {
        return gettagfast(is).hasKey("dartID");
    }

    private EntityWhiplash getLash(ItemStack is, EntityLivingBase elb) {
        if (isThrown(is) && elb.world.getEntityByID(getBallID(is)) != null)
            return (EntityWhiplash) elb.world.getEntityByID(getBallID(is));
        return null;
    }

    private int getBallID(ItemStack is) {
        return gettagfast(is).getInteger("dartID");
    }
}
