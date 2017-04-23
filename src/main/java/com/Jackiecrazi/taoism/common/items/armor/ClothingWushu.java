package com.Jackiecrazi.taoism.common.items.armor;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.common.items.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ClothingWushu extends GenericTaoistArmor {//TODO implement ISpecialArmor?
	/**
	 * First arg tells you the material, second the type of render (leather,maille,iron,diamond,gold), third which part this goes on (0-4 from top to bottom).
	 */
	public ClothingWushu(ArmorMaterial mat, int renderType, int pieceOfBody) {
		super(mat, renderType, pieceOfBody);
		this.setUnlocalizedName("Wushu"+pieceOfBody+mat.toString().toLowerCase());
		this.setMaxDamage(0);
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack) {
		return EnumRarity.common;
	}

	@Override
	public boolean getIsRepairable(ItemStack par1ItemStack,
			ItemStack par2ItemStack) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {
		iconHelm = ir.registerIcon("taoism:armor/wushuheadband");
	    iconChest = ir.registerIcon("taoism:armor/wushushirt");
	    iconLegs = ir.registerIcon("taoism:armor/wushupants");
	    iconBoots = ir.registerIcon("taoism:armor/wushushoes");
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player,
			ItemStack itemStack) {
		
	}
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	  {
	      return stack.getItem()==ModItems.wushuPants?"taoism:textures/models/armor/wushushoe.png": "taoism:textures/models/armor/wushubody.png";
	  }
	
}
