package com.jackiecrazi.taoism.common.item.weapon;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.api.WeaponStatWrapper;
import com.jackiecrazi.taoism.client.gui.SlotLianQi;
import com.jackiecrazi.taoism.config.MaterialsConfig;
import com.jackiecrazi.taoism.config.TaoConfigs;

public abstract class ItemBlueprint extends Item {
	//TODO add instances that correspond to weapon, bow, arrow, armor, etc. and define them to open slots that only accept materials of a type that is called by the weaponstatwrapper of the one
	//organization: method that returns a list of custom slots. these slots inherit the type of wsw (e.g. head, siyah) and have an index that they use to cycle through the wsw list
	//clicking on the slot when it's empty brings up the selection screen
	//XXX real world interaction? if so, how?
	public ItemBlueprint() {
		this.setUnlocalizedName("burupurinto");
		this.setRegistryName("taoblueprint");
		this.setCreativeTab(Taoism.tabBlu);
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
	}
	
	/*public boolean acceptsMat(int damage,ItemStack i){
		if(MaterialsConfig.findMat(i)==null)return false;
		return TaoConfigs.weapc.lookup(damage).isHard()==MaterialsConfig.findMat(i).isHard;
	}
	*/
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)){
			for(WeaponStatWrapper wsw:TaoConfigs.weapc.enabledParts.values()){
				
				items.add(new PartData(wsw.getClassification(), MaterialsConfig.findMat("gemDiamond").msw, wsw.getOrdinal()).toStack());
			}
		}
	}
	
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
		if(entityLiving instanceof EntityPlayer){
			EntityPlayer p=(EntityPlayer)entityLiving;
			
		}
		return super.onItemUseFinish(stack, worldIn, entityLiving);
    }
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		//tooltip.add(TaoConfigs.weapc.lookup(stack.getItemDamage()).getName());
	}
	
	public abstract SlotLianQi[] getSlots(IInventory in);
}
