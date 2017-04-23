package com.Jackiecrazi.taoism.common.taoistichandlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.networking.PacketAnimUpdate;

public class AnimationStalker implements IExtendedEntityProperties {
	private EntityPlayer player;
	private boolean activated;
	private ItemStack stackInQuestion;
	private boolean isRightClick;
	private int slot;//disables slot changing
	private int type;// 0 is normal, 1 is shift, 2 is jump, taking dominance
						// based on the number

	public AnimationStalker(EntityPlayer p) {
		this.player = p;
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound spr = (NBTTagCompound) compound.getTag("Taoism");
		if (spr == null)
			spr = new NBTTagCompound();
		NBTTagCompound saved = new NBTTagCompound();
		saved.setInteger("type", type);
		try {
			stackInQuestion.writeToNBT(saved);
		} catch (NullPointerException e) {
		}
		saved.setBoolean("activated", activated);
		saved.setBoolean("isRightClick", isRightClick);
		// TODO unlocked skills go here
		spr.setTag("animStuff", saved);
		compound.setTag("Taoism", spr);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound save = (NBTTagCompound) compound.getTag("Taoism");
		NBTTagCompound saved = (NBTTagCompound) save.getTag("animStuff");
		activated = saved.getBoolean("activated");
		isRightClick = saved.getBoolean("isRightClick");
		type = saved.getByte("type");
		try {
			stackInQuestion.readFromNBT(saved);
		} catch (NullPointerException e) {
		}

	}

	@Override
	public void init(Entity entity, World world) {

	}

	public static AnimationStalker getThis(EntityPlayer p) {
		return (AnimationStalker) p
				.getExtendedProperties("TaoisticAnimationStalker");
	}

	/**
	 * Automatically sends a packet, so no worries. Parameters: whether to
	 * enable animations, is it a right click, item stack in question, what type
	 * it is, and hotbar slot. type can be split into 0 for normal, 1 for shifting, and 2 for
	 * jump.
	 */
	public void updateAnimation(boolean active, boolean isRightClick,
			ItemStack is, int type, int slot) {
		activated = active;
		this.isRightClick = isRightClick;
		stackInQuestion = is;
		this.type = type;
		this.slot=slot;
		if (player instanceof EntityPlayerMP) {
			Taoism.net.sendTo(new PacketAnimUpdate(active, is, isRightClick,
					type,slot), (EntityPlayerMP) player);
		}
	}

	public boolean isActive() {
		return activated;
	}

	public boolean getIsRightClick() {
		return isRightClick;
	}
	/**
	 *  0 for normal, 1 for shifting, and 2 for jump.
	 */
	public int getType() {
		return type;
	}
	public ItemStack getItemStack(){
		return stackInQuestion;
	}
	public int getSlot(){
		return slot;
	}
}
