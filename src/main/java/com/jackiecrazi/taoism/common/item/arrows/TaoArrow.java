package com.jackiecrazi.taoism.common.item.arrows;

import com.jackiecrazi.taoism.api.BinaryMachiavelli;
import com.jackiecrazi.taoism.api.alltheinterfaces.IDamageType;
import com.jackiecrazi.taoism.common.entity.projectile.arrows.EntityTaoArrow;
import com.jackiecrazi.taoism.common.entity.projectile.arrows.EntityTaoArrowBlunt;
import com.jackiecrazi.taoism.common.entity.projectile.arrows.EntityTaoArrowHarpoon;
import com.jackiecrazi.taoism.common.entity.projectile.arrows.EntityTaoArrowScream;
import com.jackiecrazi.taoism.common.item.TaoItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.List;

public class TaoArrow extends ItemArrow implements IDamageType {
    private Constructor[] factories = {
            ObfuscationReflectionHelper.findConstructor(EntityTaoArrowBlunt.class, World.class, EntityLivingBase.class,ItemStack.class),
            ObfuscationReflectionHelper.findConstructor(EntityTaoArrowScream.class, World.class, EntityLivingBase.class,ItemStack.class),
            ObfuscationReflectionHelper.findConstructor(EntityTaoArrowHarpoon.class, World.class, EntityLivingBase.class,ItemStack.class)
    };

    public TaoArrow() {
        //this.setCreativeTab(Taoism.tabArr);
        this.setUnlocalizedName("taoarrow");
        this.setRegistryName("taoarrow");
        this.setHasSubtypes(true);

    }

    public boolean hasMotor(ItemStack is){
        return BinaryMachiavelli.getBoolean(is.getItemDamage(),16);
    }

    /**
     * @param is
     * @return 0 for nothing, 1 for incendiary, 2 for poison smoke, 3 for fragmenting
     */
    public int getWarhead(ItemStack is){
        return BinaryMachiavelli.getInteger(is.getItemDamage(),14,15);
    }

    @Nonnull
    public EntityArrow createArrow(@Nonnull World worldIn, @Nonnull ItemStack stack, EntityLivingBase shooter) {
        try {
            EntityTaoArrow eta=(EntityTaoArrow) factories[stack.getItemDamage()].newInstance(worldIn, shooter,stack);
            if(hasMotor(stack))eta.setCharge(EntityTaoArrow.maxCharge);
            eta.setWarhead(getWarhead(stack));
            return eta;
        } catch (Exception e) {
            e.printStackTrace();
        }
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(worldIn, shooter);
        entitytippedarrow.setPotionEffect(new ItemStack(Items.ARROW));
        return entitytippedarrow;
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if(tab.equals(this.getCreativeTab()))
        for (int x = 0; x < factories.length; x++) {
            items.add(new ItemStack(TaoItems.arrow, 1, x));
        }
    }

    public boolean isInfinite(ItemStack stack, ItemStack bow, EntityPlayer player) {
        int enchant = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.INFINITY, bow);
        return enchant <= 0;
    }


    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        //TODO list stats like draw strength and durability

    }

    @Override
    public int getDamageType(ItemStack is) {
        return 2;
    }
}
