package com.jackiecrazi.taoism.common.item.weapon;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.MaterialStatWrapper;
import com.jackiecrazi.taoism.api.WeaponPerk;
import com.jackiecrazi.taoism.api.WeaponPerk.HandlePerk;
import com.jackiecrazi.taoism.api.WeaponStatWrapper;
import com.jackiecrazi.taoism.client.KeyOverlord;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.config.AbstractWeaponConfigOverlord;

public class ItemBlueprint extends Item {
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

	public boolean acceptsMat(ItemStack i, MaterialStatWrapper w) {
		return AbstractWeaponConfigOverlord.lookup(i).accepts(w);
	}

	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			for (WeaponStatWrapper wsw : AbstractWeaponConfigOverlord.enabledParts) {

				items.add(generate(wsw));
			}
		}
	}

	public static ItemStack generate(WeaponStatWrapper wsw) {
		ItemStack ret = new ItemStack(TaoItems.blueprint);
		ret.setTagCompound(new NBTTagCompound());
		ret.getTagCompound().setString("part", wsw.getClassification());
		ret.getTagCompound().setInteger("dam", wsw.getOrdinal());
		return ret;
	}

	public WeaponStatWrapper toWSW(ItemStack i) {
		if (!i.hasTagCompound()) return null;
		return AbstractWeaponConfigOverlord.lookup(i);
	}

	public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer p, EnumHand h) {

		return super.onItemRightClick(w, p, h);
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (toWSW(stack) != null) {
			WeaponStatWrapper wsw = toWSW(stack);
			tooltip.add(I18n.format("part.name", I18n.format("part." + wsw.getName() + ".name")));
			tooltip.add(I18n.format("part.type", I18n.format("part." + wsw.getClassification() + ".name")));
			tooltip.add(I18n.format("part.cost", wsw.getCost()));
			//stats
			if (KeyOverlord.isShiftDown()) {
				tooltip.add(TextFormatting.BOLD + I18n.format("part.damagem", wsw.getDamageMultiplier()) + TextFormatting.RESET);
				tooltip.add(TextFormatting.BOLD + I18n.format("part.damagetypem" + wsw.getDamageType()) + TextFormatting.RESET);
				tooltip.add(TextFormatting.BOLD + I18n.format("part.durabilitym", wsw.getDurabilityMultiplier()) + TextFormatting.RESET);
				tooltip.add(TextFormatting.BOLD + I18n.format("part.range", wsw.getRange()) + TextFormatting.RESET);
				tooltip.add(TextFormatting.BOLD + I18n.format("part.speedm", wsw.getSpeedMultiplier()) + TextFormatting.RESET);

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
}
