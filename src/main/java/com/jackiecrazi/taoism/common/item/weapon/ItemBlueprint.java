package com.jackiecrazi.taoism.common.item.weapon;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.client.gui.SlotLianQi;

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
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TaoConfigs.weapc.lookup(stack.getItemDamage()).getName());
	}*/
	
	public abstract SlotLianQi[] getSlots(IInventory in);
}
