package com.jackiecrazi.taoism.common.item.weapon;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.MaterialStatWrapper;
import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.api.WeaponPerk;
import com.jackiecrazi.taoism.api.WeaponPerk.HandlePerk;
import com.jackiecrazi.taoism.api.WeaponStatWrapper;
import com.jackiecrazi.taoism.client.KeyOverlord;
import com.jackiecrazi.taoism.config.AbstractWeaponConfigOverlord;
import com.jackiecrazi.taoism.config.MaterialsConfig;

public class ItemDummy extends Item {
	public ItemDummy() {
		this.setUnlocalizedName("taodum").setHasSubtypes(true).setRegistryName("taodum").setCreativeTab(Taoism.tabBlu);
		/*this.addPropertyOverride(new ResourceLocation("broken"), new IItemPropertyGetter()
		{
		    @SideOnly(Side.CLIENT)
		    public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
		    {
		    	if(!stack.hasTagCompound())stack.setTagCompound(new NBTTagCompound());
		        return stack.getTagCompound().getInteger("dam");
		    }
		});*/
	}

	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			for (WeaponStatWrapper wsw : AbstractWeaponConfigOverlord.enabledParts) {
				for (int aaa = 0; aaa < 5; aaa++){
					ItemStack is=new PartData(wsw.getClassification(), MaterialsConfig.getRandomMat(itemRand, wsw.matType()), wsw.getOrdinal()).toStack();
					//is.setItemDamage(ClientEvents.veryLazy("default"));
					items.add(is);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (!stack.hasTagCompound()) return;
		if (new PartData(stack.getTagCompound()).isValid()) {
			PartData pd = new PartData(stack.getTagCompound());
			WeaponStatWrapper wsw = pd.getWeaponSW();
			MaterialStatWrapper msw = pd.getMatSW();
			tooltip.add(I18n.format("part.name", I18n.format("part." + wsw.getName() + ".name")));
			tooltip.add(I18n.format("part.type", I18n.format("part." + wsw.getClassification() + ".name")));
			tooltip.add(I18n.format( I18n.format("part." + msw.name + ".name")));
			
			//stats
			if (KeyOverlord.isShiftDown()) {
				tooltip.add(TextFormatting.BOLD + I18n.format("part.damagem", wsw.getDamageMultiplier() * msw.damageOrSpringiness) + TextFormatting.RESET);
				tooltip.add(TextFormatting.BOLD + I18n.format("part.damagetypem" + wsw.getDamageType()) + TextFormatting.RESET);
				tooltip.add(TextFormatting.BOLD + I18n.format("part.durabilitym", wsw.getDurabilityMultiplier() * msw.durability) + TextFormatting.RESET);
				tooltip.add(TextFormatting.BOLD + I18n.format("part.range", wsw.getRange()) + TextFormatting.RESET);
				tooltip.add(TextFormatting.BOLD + I18n.format("part.speedm", wsw.getSpeedMultiplier() * msw.lightness) + TextFormatting.RESET);

				tooltip.add(I18n.format(TextFormatting.BOLD + "part.elementm", wsw.getElementalMultiplier()) + TextFormatting.RESET);//TODO display per element
			} else tooltip.add(TextFormatting.RED + I18n.format("part.shift") + TextFormatting.RESET);
			//applicable handles
			if (KeyOverlord.isAltDown()) {
				if (wsw.isHandle()) tooltip.add(TextFormatting.BOLD + I18n.format("handle.name", I18n.format(wsw.getHandleList()[0].name + ".name")) + TextFormatting.RESET);
				else {
					tooltip.add(TextFormatting.BOLD + I18n.format("part.handledhandles") + TextFormatting.RESET);
					for (HandlePerk hp : wsw.getHandleList()) {
						if (hp != null) tooltip.add(TextFormatting.BOLD + I18n.format("handle." + hp.name + ".name") + TextFormatting.RESET);
					}

				}
			} else tooltip.add(I18n.format(TextFormatting.GREEN + "part.alt") + TextFormatting.RESET);
			//perks
			if (KeyOverlord.isControlDown()) {
				tooltip.add(I18n.format(TextFormatting.BOLD + "part.perks") + TextFormatting.RESET);
				for (WeaponPerk hp : wsw.getPerks()) {
					if (hp != null) tooltip.add(TextFormatting.BOLD + I18n.format("perk." + hp.name + ".name") + TextFormatting.RESET);
				}
			} else tooltip.add(TextFormatting.BLUE + I18n.format("part.ctrl") + TextFormatting.RESET);
		}
	}

	/*public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer p, EnumHand h) {
		System.out.println("a");
		ItemStack i = p.getHeldItem(h);
		ArrayList<PartData> ais=new ArrayList<PartData>();
		boolean[] slot=new boolean[]{false,false,false,false,false,false,false,false,false};
		if (isValidPart(i)) {
			System.out.println("b");
			int n = 0;
			for (ItemStack is : searchHotbar(p)) {
				if(isValidPart(is)){
					ais.add(pd(is));
					slot[n]=true;
				}
				n++;
			}
			if(ais.size()>4)return super.onItemRightClick(w, p, h);
			boolean handle=false,head=false,guard=false,pommel=false;
			//check that everything exists only once
			for(PartData pd:ais){
				switch(pd.getWeaponSW().getClassification()){
				case StaticRefs.GUARD:
					if(!guard){
						guard=true;
						break;
					}else return super.onItemRightClick(w, p, h);
				case StaticRefs.HANDLE:
					if(!handle){
						handle=true;
						break;
					}else return super.onItemRightClick(w, p, h);
				case StaticRefs.POMMEL:
					if(!pommel){
						pommel=true;
						break;
					}else return super.onItemRightClick(w, p, h);
				case StaticRefs.HEAD:
					if(!head){
						head=true;
						break;
					}else return super.onItemRightClick(w, p, h);
					
				}
			}
			if(handle){
				for(int a=0;a<slot.length;a++){
					if(slot[a]){
						p.inventory.setInventorySlotContents(a, ItemStack.EMPTY);
					}
				}
				return new ActionResult<ItemStack>(EnumActionResult.PASS,TaoWeapon.createWeapon(p, ais));
			}
		}
		return super.onItemRightClick(w, p, h);
	}*/

	/*private ItemStack[] searchHotbar(EntityPlayer p) {
		ItemStack[] ret = new ItemStack[9];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = p.inventory.getStackInSlot(i);
		}
		return ret;
	}*/

	@Nullable
	private PartData pd(ItemStack i) {
		if (!isValidPart(i)) return null;
		return new PartData(i.getTagCompound());
	}

	private boolean isValidPart(ItemStack is) {
		if (is.isEmpty()) return false;
		if (!is.hasTagCompound()) return false;
		return new PartData(is.getTagCompound()).isValid();
	}
}
