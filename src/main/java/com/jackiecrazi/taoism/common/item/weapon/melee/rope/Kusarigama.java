package com.jackiecrazi.taoism.common.item.weapon.melee.rope;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITetherItem;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityKusarigamaShot;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;

public class Kusarigama extends TaoWeapon implements ITetherItem {
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
     * right click throws the weighted chain with speed and max range depending on charge, follow-ups do some extra damage
     * dashing into the enemy at full charge engages tether
     * parrying the tethered enemy gives them weakness, mining fatigue is constantly applied
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
        if (e instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) e;
            updateTetheringVelocity(stack, elb);
            final Entity engaged = getTetheringEntity(stack, elb);
            if (engaged != null && (NeedyLittleThings.getDistSqCompensated(engaged, elb) > 36 || getBuff(stack, "blowsLeft") < 1))
                disengage(elb, stack);
            if (engaged instanceof EntityLivingBase)
                TaoPotionUtils.attemptAddPot((EntityLivingBase) engaged, new PotionEffect(MobEffects.MINING_FATIGUE, 20), false);
        }
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack is) {
        if (!elb.world.isRemote) {
            if (getHand(is) == EnumHand.OFF_HAND) {
                if (isEngaged(is)) {
                    disengage(elb, is);
                } else if (!isThrown(is)) {
                    EntityKusarigamaShot eks = new EntityKusarigamaShot(elb.world, elb, getHand(is));
                    eks.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, (getMaxChargeTime() - is.getItemDamage()) / (float) getMaxChargeTime(), 0.0F);
                    elb.world.spawnEntity(eks);
                    gettagfast(elb.getHeldItemMainhand()).setInteger("dartID", eks.getEntityId());
                }
            }
        }
        return super.onEntitySwing(elb, is);
    }

    private boolean isEngaged(ItemStack is) {
        return gettagfast(is).getInteger("tether") > 0;
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
        tooltip.add(I18n.format("kusarigama.tether"));
        tooltip.add(I18n.format("kusarigama.tethertick"));
        tooltip.add(I18n.format("kusarigama.escape"));
        tooltip.add(I18n.format("kusarigama.pulldown"));
        tooltip.add(I18n.format("kusarigama.harvest"));
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return getHand(is) == EnumHand.OFF_HAND ? 0 : 2;
    }

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    @Override
    protected boolean[] harvestable(ItemStack is) {
        return harvestList;
    }

    @Override
    protected boolean onCollideWithEntity(EntityLivingBase elb, Entity collidingEntity, ItemStack stack) {
        if (TaoCasterData.getTaoCap(elb).getRollCounter() < CombatConfig.rollThreshold && stack.getItemDamage() == 0) {
            setBuff(elb, stack, "tether", collidingEntity.getEntityId());
            setBuff(elb, stack, "blowsLeft", 6 + TaoCasterData.getTaoCap(elb).getQiFloored());
            stack.setItemDamage(getMaxChargeTime());
            elb.world.playSound(null, elb.posX, elb.posY, elb.posZ, SoundEvents.ENTITY_LEASHKNOT_PLACE, SoundCategory.PLAYERS, 1, 1);
            return true;
        }
        return false;
    }

    @Override
    public int getMaxChargeTime() {
        return 40;
    }

    @Override
    protected int chargePerTick(ItemStack is) {
        return isThrown(is) || isEngaged(is) ? 0 : 1;
    }

    @Override
    public boolean canBlock(EntityLivingBase defender, Entity attacker, ItemStack item, boolean recharged, float amount) {
        return getHand(item) == EnumHand.MAIN_HAND && super.canBlock(defender, attacker, item, recharged, amount);
    }

    @Override
    public void onParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        if (defender.getEntityId() == getBuff(item, "tether")) {
            TaoPotionUtils.attemptAddPot(attacker, new PotionEffect(MobEffects.WEAKNESS, 20), false);
            setBuff(defender, item, "blowsLeft", getBuff(item, "blowsLeft") - 1);
            //defender.world.playSound(null, defender.posX, defender.posY, defender.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1, 1);
        }
        if (getHand(item) == EnumHand.OFF_HAND) {
            TaoPotionUtils.attemptAddPot(attacker, new PotionEffect(MobEffects.MINING_FATIGUE, getMaxChargeTime() - item.getItemDamage()), false);
            item.setItemDamage(getMaxChargeTime());
        }
    }

    @Override
    public float postureDealtBase(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return super.postureDealtBase(attacker, defender, item, amount);
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        stack.setItemDamage(getMaxChargeTime());
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return target.isPotionActive(MobEffects.MINING_FATIGUE) ? Event.Result.ALLOW : Event.Result.DENY;
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
    public float onBeingHurt(DamageSource ds, EntityLivingBase defender, ItemStack item, float amount) {
        setBuff(defender, item, "blowsLeft", getBuff(item, "blowsLeft") - 3);
        return super.onBeingHurt(ds, defender, item, amount);
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        setBuff(attacker, stack, "hitCharge", 0);
        if (getHand(stack) == EnumHand.OFF_HAND) {
            setBuff(attacker, stack, "hitCharge", getMaxChargeTime() - stack.getItemDamage());
            attacker.getHeldItemMainhand().setItemDamage(getMaxChargeTime());
        }
        setBuff(attacker, stack, "blowsLeft", getBuff(stack, "blowsLeft") - 1);
    }

    @Override
    public Entity getTetheringEntity(ItemStack stack, EntityLivingBase wielder) {
        return wielder.world.getEntityByID(getBuff(stack, "tether"));
    }

    private void disengage(EntityLivingBase elb, ItemStack is) {
        EntityKusarigamaShot erd = getBall(is, elb);
        if (erd != null) erd.setDead();
        Entity e = elb.world.getEntityByID(getBuff(is, "tether"));
        if (e instanceof EntityLivingBase) {
            int leftover = getBuff(is, "blowsLeft");
            e.attackEntityFrom(DamageSource.FALL, leftover);
            TaoCasterData.getTaoCap((EntityLivingBase) e).consumePosture(leftover, true);
        }
        setBuff(elb, is, "tether", -1);
        setBuff(elb, is, "blowsLeft", 0);
    }

    private EntityKusarigamaShot getBall(ItemStack is, EntityLivingBase elb) {
        if (isThrown(is) && elb.world.getEntityByID(getBallID(is)) != null)
            return (EntityKusarigamaShot) elb.world.getEntityByID(getBallID(is));
        return null;
    }

    private int getBallID(ItemStack is) {
        return gettagfast(is).getInteger("dartID");
    }

    @Nullable
    @Override
    public Vec3d getTetheredOffset(ItemStack stack, EntityLivingBase wielder) {
        return null;
    }

    @Nullable
    @Override
    public Entity getTetheredEntity(ItemStack stack, EntityLivingBase wielder) {
        return getHand(stack) == EnumHand.OFF_HAND ? wielder : null;
    }

    @Override
    public double getTetherLength(ItemStack stack) {
        return 6;
    }

    @Override
    public boolean renderTether(ItemStack stack) {
        return true;
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
        return getHand(item) == EnumHand.OFF_HAND ? 1 + (item.getItemDamage() / 20f) : 1.5f;
    }
}
