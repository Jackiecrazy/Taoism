package com.Jackiecrazi.taoism.common.items.armor;

import com.Jackiecrazi.taoism.Taoism;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public abstract class GenericTaoistArmor extends ItemArmor {
	protected String helmRL,chestRL,legRL,bootRL;
	public IIcon iconHelm,iconChest,iconLegs,iconBoots;
	/**
	 * First arg tells you the material, second the type of render (leather,maille,iron,diamond,gold), third which part this goes on (0-4 from top to bottom).
	 */
	public GenericTaoistArmor(ArmorMaterial mat, int renderType,
			int pieceOfBody) {
		super(mat, renderType, pieceOfBody);
		this.setCreativeTab(Taoism.TabTaoistWeapon);
	}
	@SideOnly(Side.CLIENT)
	  public IIcon getIconFromDamage(int par1) {
	    return armorType == 2 ? iconLegs : armorType == 1 ? iconChest : armorType == 0 ? iconHelm : iconBoots;
	  }
	public abstract EnumRarity getRarity(ItemStack itemstack);
	public abstract boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack);
	@SideOnly(Side.CLIENT)
	public abstract void registerIcons(IIconRegister ir);
	public abstract void onArmorTick(World world, EntityPlayer player, ItemStack itemStack);
	public abstract String getArmorTexture(ItemStack stack, Entity entity, int slot, String type);
}
