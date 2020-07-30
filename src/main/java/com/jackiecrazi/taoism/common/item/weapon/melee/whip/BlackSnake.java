package com.jackiecrazi.taoism.common.item.weapon.melee.whip;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
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

public class BlackSnake extends TaoWeapon {
    /*
    a whipping weapon that can be used as a bludgeon close up. High power and speed, mid range, low combo and defense
    cannot parry
    attacks beyond 4 blocks is a whip lash that inflicts critical cutting damage (1.2~3 with chi) along with a loud crack,
        this cannot be parried, but damage is doubly reduced by armor
    attacks within 4 blocks is a whack with the blackjack end, dealing good blunt damage and stunning with chi
     */
    private final PartDefinition[] parts = {
            StaticRefs.HANDLE,
            new PartDefinition("handlewrap", false, StaticRefs.STRING)
    };

    public BlackSnake() {
        super(1, 1.6, 6, 0.5f);
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
        if (!elb.world.isRemote && NeedyLittleThings.raytraceEntity(elb.world, elb, 2) == null) {
            if (!isThrown(is)) {
                EntityWhiplash ew = new EntityWhiplash(elb.world, elb, getHand(is), 5);
                ew.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, 1.3f, 0.0F);
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
        tooltip.add(TextFormatting.DARK_RED + I18n.format("blacksnake.armor") + TextFormatting.RESET);
        tooltip.add(I18n.format("blacksnake.sonicboom"));
        tooltip.add(I18n.format("blacksnake.blackjack"));
        tooltip.add(I18n.format("blacksnake.sneak"));
        tooltip.add(I18n.format("blacksnake.sneakboom"));
    }

    @Override
    public float getTrueReach(EntityLivingBase elb, ItemStack is) {
        return 2;
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
        return 1 + getQiFromStack(item) / 27f;
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (attacker.isSneaking()) {
            NeedyLittleThings.knockBack(attacker, target, -orig*1.5f, true);
            TaoCasterData.getTaoCap(target).setBindTime(10);
            return -orig*1.5f;
        }
        return super.knockback(attacker, target, stack, orig);
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        if (getBuff(item, "rangedAttack") == 0)
            return 0;
        return -target.getTotalArmorValue()/2;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getBuff(stack, "rangedAttack") == 0 || getBuff(stack, "boomer") == 1) {
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.SLOWNESS, 30), false);
            TaoPotionUtils.attemptAddPot(target, new PotionEffect(MobEffects.WEAKNESS, 30), false);
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
