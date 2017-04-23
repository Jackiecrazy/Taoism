package com.Jackiecrazi.taoism.common.taoistichandlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.common.inventory.InventoryTPInv;
import com.Jackiecrazi.taoism.common.inventory.InventoryWujiGongfa;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.Skill;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.XiuWeiHandler;
import com.Jackiecrazi.taoism.networking.PacketSetPlayerMeditating;

public class PlayerResourceStalker implements IExtendedEntityProperties {
	private final EntityPlayer player;
	public final InventoryTPInv itpi;
	public final InventoryWujiGongfa iwg;
	private boolean isMeditating,bottleneck,punishment;
	private double potential,atspd;

	// TODO life strength resilience spirit power potential
	//TODO battle mode like lock on, in this mode you can manipulate keys to your heart's content. Yay!
	
	//lingguo decreases potential but increases level directly
	public PlayerResourceStalker(EntityPlayer p) {
		this.player = p;
		p.getDataWatcher().addObject(WayofConfig.QiDWID, Float.valueOf(0));
		p.getDataWatcher().addObject(WayofConfig.LingLiDWID, Float.valueOf(0));
		itpi=new InventoryTPInv(p);
		iwg=new InventoryWujiGongfa(p);
	}

	@Override
	public void saveNBTData(NBTTagCompound n) {
		// write stuff
		/*
		 * NBTTagCompound lingli = new NBTTagCompound();
		 * lingli.setFloat("Value", player.getDataWatcher()
		 * .getWatchableObjectFloat(WayofConfig.LingLiDWID));
		 * n.setTag("lingLiStalker", lingli);
		 */
		n.setFloat("lingLiStalker", player.getDataWatcher()
				.getWatchableObjectFloat(WayofConfig.LingLiDWID));
		/*
		 * NBTTagCompound qi = new NBTTagCompound(); qi.setFloat( "Value",
		 * player.getDataWatcher().getWatchableObjectFloat(
		 * WayofConfig.QiDWID)); n.setTag("qiStalker", qi);
		 */
		n.setFloat("qiStalker", (player.getDataWatcher()
				.getWatchableObjectFloat(WayofConfig.QiDWID)));
		n.setBoolean("isTaoisticallyMeditating", isMeditating);
		
		itpi.writeToNBT(n);
	}

	@Override
	public void loadNBTData(NBTTagCompound n) {
		// read stuff
		// System.out.println("loading qi and ling...");
		// NBTTagCompound lingLi = (NBTTagCompound) n.getTag("lingLiStalker");
		player.getDataWatcher().updateObject(WayofConfig.LingLiDWID,
				Float.valueOf(n.getFloat("lingLiStalker")));
		// NBTTagCompound qi = (NBTTagCompound) n.getTag("qiStalker");
		player.getDataWatcher().updateObject(WayofConfig.LingLiDWID,
				Float.valueOf(n.getFloat("qiStalker")));
		isMeditating = n.getBoolean("isTaoisticallyMeditating");
		
		itpi.readFromNBT(n);
	}

	@Override
	public void init(Entity entity, World world) {
	}

	public static PlayerResourceStalker get(EntityPlayer p) {
		return (PlayerResourceStalker) p
				.getExtendedProperties("taoistplayerstalk");
	}

	public void addValues(int DWID, float amnt) {
		player.getDataWatcher().updateObject(
				DWID,
				Float.valueOf(player.getDataWatcher().getWatchableObjectFloat(
						DWID))
						+ amnt);
	}

	public boolean subtractValues(int DWID, float amnt) {
		player.getDataWatcher().updateObject(
				DWID,
				Float.valueOf(player.getDataWatcher().getWatchableObjectFloat(
						DWID))
						- amnt);
		if(DWID==WayofConfig.QiDWID)XiuWeiHandler.getThis(player).addXP(amnt/500f);
		return player.getDataWatcher().getWatchableObjectFloat(DWID) >= 0;
	}

	public void setValues(int DWID, float amnt) {
		player.getDataWatcher().updateObject(DWID, amnt);
	}

	public float getValues(int DWID) {
		return player.getDataWatcher().getWatchableObjectFloat(DWID);
	}

	public boolean getIsMeditating() {
		return isMeditating;
	}

	public void setIsMeditating(boolean on) {
		isMeditating = on;
		player.capabilities.isFlying=on;
		/*if(IApologizeForThisHandler.meditatingPlayersTemp.containsKey(player.getUniqueID())){
			
		}*/
		if (player instanceof EntityPlayerMP) {
			// if(!on)player.motionY=0;
			// Taoism.net.sendToAllAround(new
			// PacketSetPlayerMeditating(on,player), new
			// TargetPoint(player.dimension, player.posX, player.posY,
			// player.posZ, 64));
			// System.out.println("display data updated");
		} else {
			Taoism.net.sendToServer(new PacketSetPlayerMeditating(on, player));
			//System.out.println("sent packet to server");
		}
	}

	public void regenValues() {
		if (!player.worldObj.isRemote) {
			//regen qi
			if (player.getDataWatcher().getWatchableObjectFloat(
					WayofConfig.LingLiDWID) < 0.0F)
				player.getDataWatcher().updateObject(WayofConfig.LingLiDWID,
						Float.valueOf(0.0F));
			float Qimax = (XiuWeiHandler.getThis(player).getLevel() * XiuWeiHandler
					.getThis(player).getLevel()) * 4;
			if (player.getDataWatcher().getWatchableObjectFloat(
					WayofConfig.QiDWID) != Qimax) {
				addValues(WayofConfig.QiDWID, XiuWeiHandler.getThis(player)
						.getLevel() / 30);
				if (getIsMeditating()) {
					float val = XiuWeiHandler.getThis(player).getLevel() / 10f;
					addValues(WayofConfig.QiDWID, val);
					// System.out.println(val);
				}
				XiuWeiHandler.getThis(player).getSkillAwesomeness(Skill.BATNAME);
				//TODO determine based on gongfa (custom skill scroll basically), factor in time for stuff (exponential increase at times)
			}
			else {
				addValues(WayofConfig.QiDWID, 0.0F);
			}
			if (player.getDataWatcher().getWatchableObjectFloat(
					WayofConfig.QiDWID) >= Qimax)
				player.getDataWatcher().updateObject(WayofConfig.QiDWID,
						Float.valueOf(Qimax));
			else if (player.getDataWatcher().getWatchableObjectFloat(
					WayofConfig.QiDWID) < 0)
				player.getDataWatcher().updateObject(WayofConfig.QiDWID, 0F);
			
			//regen ling TODO not based off qi
			if (player.getDataWatcher().getWatchableObjectFloat(
					WayofConfig.LingLiDWID) < 0.0F)
				player.getDataWatcher().updateObject(WayofConfig.LingLiDWID,
						Float.valueOf(0.0F));
			float Lingmax = (XiuWeiHandler.getThis(player).getLevel() * XiuWeiHandler
					.getThis(player).getLevel()) * 4;
			if (player.getDataWatcher().getWatchableObjectFloat(
					WayofConfig.QiDWID) != Lingmax) {
				addValues(WayofConfig.LingLiDWID, XiuWeiHandler.getThis(player)
						.getLevel() / 30);
				if (getIsMeditating()) {
					float val = XiuWeiHandler.getThis(player).getLevel() / 10f;
					addValues(WayofConfig.LingLiDWID, val);
					// System.out.println(val);
				}
				XiuWeiHandler.getThis(player).getSkillAwesomeness(Skill.BATNAME);
			}
			else {
				addValues(WayofConfig.LingLiDWID, 0.0F);
			}
			if (player.getDataWatcher().getWatchableObjectFloat(
					WayofConfig.LingLiDWID) >= Lingmax)
				player.getDataWatcher().updateObject(WayofConfig.LingLiDWID,
						Float.valueOf(Lingmax));
			else if (player.getDataWatcher().getWatchableObjectFloat(
					WayofConfig.LingLiDWID) < 0)
				player.getDataWatcher().updateObject(WayofConfig.LingLiDWID, 0F);
		}
	}
	//TODO thunder?
	public void divinePunishment(){
		
	}
	
}
