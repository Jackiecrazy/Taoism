package com.jackiecrazi.taoism.common.item.weapon.melee.axe;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.List;

public class BanFu extends TaoWeapon {
    //Like the axe, a powerful weapon designed to counter heavy armor. Good power and defense potential, decent reach, combo and trickery
    //Leap attacks deal double damage, attacks always decrease posture,
    // and lowers the enemy's defense by 2 points per successful attack per chi level, for 3 seconds
    // if there is no more defense to be lowered (that is, you hit the cap for your level):
    // on mainhand, continue extending the potion effect. If over 5 seconds, increase potency and halve duration (stack every other hit)
    // on offhand, deal extra (cleave level) damage
    // so you can use it as a sapper would, as a main weapon to follow up with offhand, or you can offhand it for greater damage
    // or do both! Black whirlwind!
    //execution: whirl into a frenzy, drawing nearby mobs into the twister
    //double axe bonus: aspected whirlwind
    //      in most places it is a tornado (extra cutting damage)
    //      in dry places it is a dust devil (blind)
    //      in watery places it is a waterspout (drown)
    //      in hot places it is a fire whirl (fire)
    // you attack constantly, alternating between both hands, knockback converted into more damage where applicable
    // enemy is prevented from dying until this ends, at which point all enemies are flung up and away in a rain of blood

    private static final boolean[] harvestList = {false, false, true, false};

    public BanFu() {
        super(3, 1.4, 7f, 1f);
        this.setHarvestLevel("axe", 2);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.8f;
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return !attacker.onGround;
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (e instanceof EntityLivingBase && isCharged((EntityLivingBase) e, stack) && getHand(stack) != null) {
            final EntityLivingBase elb = (EntityLivingBase) e;
            //it spins you right round, baby, right round
            elb.rotationYaw += Math.min(getChargedTime(elb, stack) / 5, 7);
            if (!w.isRemote) {
                if (elb.ticksExisted % 10 == 0) {
                    if (!TaoCasterData.getTaoCap(elb).consumeQi(0.25f, 5)) {
                        dischargeWeapon(elb, stack);
                        return;
                    }
                }
                for (Entity a : w.getEntitiesWithinAABBExcludingEntity(elb, elb.getEntityBoundingBox().grow(getReach(elb, stack) * 3))) {
                    double distsq = NeedyLittleThings.getDistSqCompensated(elb, a);
                    Vec3d point = elb.getPositionVector();
                    //update the entity's relative position to the point
                    //if the distance is below expected, add outwards velocity
                    //if the distance is above expected, add inwards velocity
                    //otherwise do nothing
                    if (distsq > getReach(elb, stack) * getReach(elb, stack)) {
                        a.motionX += (point.x - a.posX) * 0.02;
                        a.motionZ += (point.z - a.posZ) * 0.02;
                    } else if (distsq < getReach(elb, stack) * getReach(elb, stack)) {
                        a.motionX -= (point.x - a.posX) * 0.02;
                        a.motionZ -= (point.z - a.posZ) * 0.02;
                    }
                    a.motionY = 0.03;
                    a.motionX += (a.posZ - elb.posZ) * 0.01;
                    a.motionZ -= (a.posX - elb.posX) * 0.01;
                    a.velocityChanged = true;
                    if (elb.ticksExisted % 10 == 0) {
                        if (a instanceof EntityLivingBase) {
                            ITaoStatCapability itsc = TaoCasterData.getTaoCap((EntityLivingBase) a);
                            if (!itsc.isRecordingDamage())
                                itsc.startRecordingDamage();
                        }
                        if (elb.ticksExisted % 20 == 0) {
                            TaoCombatUtils.attack(elb, a, getHand(stack));
                        } else
                            TaoCombatUtils.attack(elb, a, getHand(stack) == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                    }
                }
            } else {
                //client side, particle time!
                //10 rand calls per tick, should be fine...
                for (int h = 0; h < 5; h++) {
                    for (int f = 0; f < 5; f++) {
                        float angle = NeedyLittleThings.rad(w.rand.nextFloat() * 360);
                        elb.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, e.posX + MathHelper.sin(angle) * h, elb.posY + h, elb.posZ + MathHelper.cos(angle) * h, MathHelper.cos(angle), 0.1, -MathHelper.sin(angle));
                    }
                }
            }
        }
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return 3f;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack stack) {
        if (isCharged(elb, stack)) {
            dischargeWeapon(elb, stack);
            if (!elb.world.isRemote)
                return true;
        }
        return super.onEntitySwing(elb, stack);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("weapon.disshield") + TextFormatting.RESET);
        tooltip.add(I18n.format("banfu.leap"));
        tooltip.add(I18n.format("banfu.cleave"));
    }

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    @Override
    protected boolean[] harvestable(ItemStack is) {
        return harvestList;
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack stack) {
        super.dischargeWeapon(elb, stack);
        if (!elb.world.isRemote) {
            for (Entity a : elb.world.getEntitiesWithinAABBExcludingEntity(elb, elb.getEntityBoundingBox().grow(getReach(elb, stack) * 5))) {
                if (a instanceof EntityLivingBase) {
                    TaoCasterData.getTaoCap((EntityLivingBase) a).stopRecordingDamage(elb);
                }
                Vec3d point = elb.getPositionVector();
                a.motionX = -(point.x - a.posX) * 0.05;
                a.motionZ = -(point.z - a.posZ) * 0.05;
                a.velocityChanged = true;
            }
            if (elb.world instanceof WorldServer) {
                ((WorldServer) elb.world).spawnParticle(EnumParticleTypes.DRIP_LAVA, elb.posX, elb.posY + 7, elb.posZ, 200, 3, 0, 3, 0.5f);
            }
        }
//        else {
//            for (int i = (int) elb.posX - 6; i < (int) elb.posX + 6; ++i) {
//                for (int j = (int) elb.posZ - 6; j < (int) elb.posZ + 6; ++j) {
//                    double speed = Taoism.unirand.nextGaussian() * 0.05D;
//                    elb.world.spawnParticle(EnumParticleTypes.REDSTONE, i + (double) (Taoism.unirand.nextFloat() * 2.0F), elb.posY + 10.0D + (Taoism.unirand.nextFloat() * 5), j + (double) (Taoism.unirand.nextFloat()), 0, speed, 0, 1, 0, 0);
//                }
//            }
//        }
    }

    @Override
    public float postureDealtBase(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return isCharged(attacker, item) ? 0 : super.postureDealtBase(attacker, defender, item, amount);
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 2f;
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return getHand(stack) == EnumHand.OFF_HAND && TaoPotionUtils.getEffectiveLevel(target, TaoPotion.ARMORBREAK, SharedMonsterAttributes.ARMOR) >= TaoCasterData.getTaoCap(attacker).getQiFloored() - 1 ?
                orig + (TaoCasterData.getTaoCap(attacker).getQiFloored() / 2f) : orig;
    }

    @Override
    public float onStoppedRecording(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return super.onStoppedRecording(ds, attacker, target, item, orig);
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (chi > 0) {
            if (TaoPotionUtils.getEffectiveLevel(target, TaoPotion.ARMORBREAK, SharedMonsterAttributes.ARMOR) < chi - 1 || getHand(stack) == EnumHand.OFF_HAND)
                TaoPotionUtils.attemptAddPot(target, new PotionEffect(TaoPotion.ARMORBREAK, 60, (chi) - 1), false);
            else
                TaoPotionUtils.attemptAddPot(target, TaoPotionUtils.stackPot(target, new PotionEffect(TaoPotion.ARMORBREAK, 60, 0), TaoPotionUtils.POTSTACKINGMETHOD.ADD), false);
        }
    }
}
