package com.jackiecrazi.taoism.common.item.weapon.melee.axe;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityBanfu;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.potions.TaoPotion;
import com.jackiecrazi.taoism.utils.TaoPotionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BanFu extends TaoWeapon {
    //Like the axe, a powerful weapon designed to counter heavy armor. Good power and defense potential, decent reach, combo and trickery
    //Leap attacks deal double damage, attacks always decrease posture,
    // and lowers the enemy's defense by 2 points per successful attack per chi level, for 3 seconds
    //execution: whirl into a frenzy, drawing nearby mobs into the twister
    //      in most places it is a whirlwind (extra cutting damage)
    //      in dry places it is a dust devil (blind)
    //      in watery places it is a waterspout (drown)
    //      in hot places it is a fire whirl (fire)
    // you attack constantly, alternating between both hands, knockback converted into more damage where applicable
    // enemy is prevented from dying until this ends, at which point all enemies are flung up and away in a rain of blood

    private static final boolean[] harvestList = {false, false, true, false};

    public BanFu() {
        super(3, 1.4, 7f, 1.5f);
        this.setHarvestLevel("axe", 2);
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return StaticRefs.SIMPLE;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 3f;
    }

    public int getMaxChargeTime() {
        return 100;
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0.8f;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack stack) {
        if(isCharged(elb, stack)&&!elb.world.isRemote){
            if(gettagfast(stack).getBoolean("thrown")){
                Entity ebf= elb.world.getEntityByID(gettagfast(stack).getInteger("thrownID"));
                if(ebf instanceof EntityBanfu){
                    ((EntityBanfu) ebf).onRecall();
                }
            }else {
                EntityBanfu ebf = new EntityBanfu(elb.world, elb, getHand(stack));
                ebf.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, 1f, 0.0F);
                elb.world.spawnEntity(ebf);
                gettagfast(stack).setInteger("thrownID", ebf.getEntityId());
            }
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
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        //trap the opponent's weapon, resetting attack timer.
        //the next attack in 5 seconds deals 0.35*damage posture regardless of block.
        //Taoism.setAtk(defender, 0);
        super.parrySkill(attacker, defender, item);
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return attacker.motionY < 0 ? 2f : 1f;
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        super.attackStart(ds, attacker, target, item, orig);
        if (isCharged(attacker, item)) {
            TaoCasterData.getTaoCap(target).consumePosture(orig * 0.35f, true, attacker);
        }
        dischargeWeapon(attacker, item);
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (chi > 0) {
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(TaoPotion.ARMORBREAK, 60, (chi) - 1), false);
        }
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return !attacker.onGround;
    }
}
