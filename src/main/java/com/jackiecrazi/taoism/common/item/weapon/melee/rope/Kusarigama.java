package com.jackiecrazi.taoism.common.item.weapon.melee.rope;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityKusarigamaShot;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Kusarigama extends TaoWeapon {
    private static final boolean[] harvestList = {false, false, false, true};
    private static final PartDefinition[] parts = {
            StaticRefs.HEAD,
            StaticRefs.HANDLE,
            StaticRefs.CHAIN
    };

    /*
     * A sickle attached to a weighted chain. High defense and speed, medium power and range, low combo
     * Two handed, you hold the sickle in the offhand. Chain cannot block, but will store up charge by being held.
     * Charge reaches maximum after 40 ticks
     *
     * Right click to throw the weighted chain with range 6. This operates off charge rather than attack speed, so it'll always be ready at varying speeds
     * If this were to strike an opponent, following up with the sickle (range 2) within (charge) ticks deals up to 1.5x damage depending on charge
     * If the chain is in between you two when the enemy attacks, the chain will entangle the opponent's weapon
     * The opponent is now bound for (charge) ticks
     * Following up with the sickle will crit for double damage and decent posture damage
     *
     * redesign:
     * offhand defense multiplier goes from 3x to 1x with more windup, on offhand parry it'll inflict brief mining fatigue
     * crits on mining fatigue enemies, main hand defense at 1.5 so you have to be over half charge to parry on off
     * right click throws the weighted chain with speed and max range depending on charge
     * dashing into the enemy at full charge engages tether
     * parrying the tethered enemy gives them weakness and mining fatigue, you consistently crit with main hand
     * the tether breaks and returns after 6+(qi) blows have been traded, a successful hit on you counts as three hits
     * if the enemy tries to escape, deal good posture damage depending on trade count and pull them back slightly
     * right click to retrieve the ball for the same effect
     *
     * execution: throw sickle out around the neck-analogue of the enemy, then pull yourself to them and do a spinning head slice
     */
    public Kusarigama() {
        super(1, 1.5, 6, 1);
        this.addPropertyOverride(new ResourceLocation("offhand"), (stack, w, elb) -> {
            if (elb != null) {
                if (getHand(stack) == EnumHand.OFF_HAND) {
                    if (isThrown(stack)) return 3;
                    return 1;
                } else if (getHand(stack) == EnumHand.MAIN_HAND) return 2;
            }
            return 0;
        });
    }

    private boolean isThrown(ItemStack is) {
        return gettagfast(is).hasKey("dartID");
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
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
            if (getHand(is) == EnumHand.OFF_HAND) {
                if (!isThrown(is)) {
                    EntityKusarigamaShot eks = new EntityKusarigamaShot(elb.world, elb, getHand(is));
                    eks.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, (getMaxChargeTime() - is.getItemDamage()) / (float) getMaxChargeTime(), 0.0F);
                    elb.world.spawnEntity(eks);
                    gettagfast(elb.getHeldItemMainhand()).setInteger("dartID", eks.getEntityId());
                } else {
                    if (elb.isSneaking()) {
                        EntityKusarigamaShot erd = getBall(is, elb);
                        if (erd != null) erd.setDead();
                    }
                }
            }
        }
        return super.onEntitySwing(elb, is);
    }

    @Override
    protected double speed(ItemStack stack) {
        if (getHand(stack) == EnumHand.OFF_HAND) return 0;
        return super.speed(stack);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(I18n.format("kusarigama.ball"));
        tooltip.add(I18n.format("kusarigama.followup"));
        tooltip.add(I18n.format("kusarigama.parry"));
        tooltip.add(I18n.format("kusarigama.critfollowup"));
        tooltip.add(I18n.format("kusarigama.harvest"));
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 0 : 3;
    }

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    @Override
    protected boolean[] harvestable(ItemStack is) {
        return harvestList;
    }

    @Override
    public int getMaxChargeTime() {
        return 40;
    }

    @Override
    protected int chargePerTick(ItemStack is) {
        return isThrown(is) ? 0 : 1;
    }

    @Override
    public boolean canBlock(EntityLivingBase defender, Entity attacker, ItemStack item, boolean recharged, float amount) {
        return getHand(item) == EnumHand.MAIN_HAND && super.canBlock(defender, attacker, item, recharged, amount);
    }

    @Override
    public void onParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        EntityKusarigamaShot ball = getBall(item, defender);
        if (ball != null) {
            TaoCasterData.getTaoCap(attacker).setBindTime(ball.getCharge());
            defender.world.playSound(null, defender.posX, defender.posY, defender.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1, 1);
        }
    }

    @Override
    public float postureDealtBase(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        if (defender != null && TaoCasterData.getTaoCap(defender).getBindTime() > 0)
            return super.postureDealtBase(attacker, defender, item, amount) * 2;
        return super.postureDealtBase(attacker, defender, item, amount);
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        stack.setItemDamage(getMaxChargeTime());
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return TaoCasterData.getTaoCap(target).getBindTime() > 0 ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 2f;
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (getHand(stack) == EnumHand.OFF_HAND) return 0;
        return super.knockback(attacker, target, stack, orig);
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (getHand(stack) == EnumHand.OFF_HAND) return getBuff(stack, "hitCharge") / (float) getMaxChargeTime();
        if (getLastAttackedEntity(attacker.world, stack) == target && getHand(stack) == EnumHand.MAIN_HAND && getLastMove(stack).isValid() && !getLastMove(stack).isLeftClick()
                && lastAttackTime(attacker, stack) + getBuff(stack, "hitCharge") > attacker.world.getTotalWorldTime())
            return orig * (1 + getBuff(stack, "hitCharge") / ((float) getMaxChargeTime() * 2));
        return orig;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        setBuff(attacker, stack, "hitCharge", 0);
        if (getHand(stack) == EnumHand.OFF_HAND) {
            setBuff(attacker, stack, "hitCharge", getMaxChargeTime() - stack.getItemDamage());
            attacker.getHeldItemMainhand().setItemDamage(getMaxChargeTime());
        }
    }

    private EntityKusarigamaShot getBall(ItemStack is, EntityLivingBase elb) {
        if (isThrown(is) && elb.world.getEntityByID(getBallID(is)) != null)
            return (EntityKusarigamaShot) elb.world.getEntityByID(getBallID(is));
        return null;
    }

    private int getBallID(ItemStack is) {
        return gettagfast(is).getInteger("dartID");
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getHand(stack) == EnumHand.OFF_HAND;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return ((double) stack.getItemDamage()) / (double) getMaxChargeTime();
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return getHand(item) == EnumHand.OFF_HAND ? 1 + (item.getItemDamage() / 20f) : 1f;
    }
}
