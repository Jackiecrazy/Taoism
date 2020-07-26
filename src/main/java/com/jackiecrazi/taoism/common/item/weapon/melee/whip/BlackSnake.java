package com.jackiecrazi.taoism.common.item.weapon.melee.whip;

import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
    public BlackSnake() {
        super(1, 1.2, 6, 0);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return new PartDefinition[0];
    }

    @Override
    public int getDamageType(ItemStack is) {
        return getLastAttackedRangeSq(is)>3?1:0;
    }

    @Override
    public int getMaxChargeTime() {
        return 0;
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 0;
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return 0;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 0;
    }
}
