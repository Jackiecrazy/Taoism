package com.Jackiecrazi.taoism;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class TaoisticPotions extends Potion {
	/**
	 * level -1 is xiuwei, level -2 is normal, level -3 ignores armor and held, level -4 ignores aura, level five to eight
	 * makes you immune to scrying effects from higher xiuwei, effect is lvl-5
	 */
	public static Potion Hide;
	final int tickrate;
	final int halvetick;
	private ItemStack stackIcon;

	public TaoisticPotions(boolean isBad, int colour, int tick,
			int halvetick, Item icon,String name) {
		super(isBad, colour);
		this.tickrate = tick;
		this.halvetick = tick;
		this.setIcon(new ItemStack(icon));
		this.setPotionName(name);
	}

	public void setIcon(ItemStack i) {
		stackIcon = i;
	}

	public void renderInventoryEffect(int x, int y, PotionEffect effect,
			Minecraft mc) {
		x += 6;
		y += 7;
		RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
		if (stackIcon == null)
			stackIcon = new ItemStack(Items.POTATO);
		RenderHelper.enableGUIStandardItemLighting();
		itemRender.renderItemIntoGUI(stackIcon, x, y);
		RenderHelper.enableStandardItemLighting();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		boolean ret = false;
		if (this.equals(Hide)&&amplifier<-1)
			ret = true;
		return ret;
	}

	@Override
	public void performEffect(EntityLivingBase l, int potency) {
		if (this.equals(Hide))
			l.setInvisible(true);
	}

	public static void brew() {
		Hide = new TaoisticPotions(false, 0xFFFFFF, 20,
				20, Items.STRING,"taoisticpotions.hide");

	}
}
