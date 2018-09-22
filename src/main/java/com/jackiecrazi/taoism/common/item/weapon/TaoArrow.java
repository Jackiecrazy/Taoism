package com.jackiecrazi.taoism.common.item.weapon;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.common.entity.projectile.EntityTaoArrow;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TaoArrow extends ItemArrow {
	public TaoArrow()
    {
        this.setCreativeTab(Taoism.tabArr);
        this.setUnlocalizedName("taoarrow");
        this.setRegistryName("taoarrow");
        this.setHasSubtypes(true);

    }

    @Nonnull
	public EntityArrow createArrow(@Nonnull World worldIn, @Nonnull ItemStack stack, EntityLivingBase shooter)
    {
        EntityTaoArrow thing = new EntityTaoArrow(worldIn, shooter).setArrow(stack);

        //stack.damageItem(1, shooter);//you can use this for other stuff
        return thing;
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {

        }
    }

    public boolean isInfinite(ItemStack stack, ItemStack bow, EntityPlayer player)
    {
        int enchant = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.INFINITY, bow);
        return enchant <= 0;
    }



    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        //TODO list stats like draw strength and durability

    }

    //TODO add IModifiable api to attach upgrades

    public void onHit(){

    }
}
