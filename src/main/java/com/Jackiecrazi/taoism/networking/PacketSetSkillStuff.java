package com.Jackiecrazi.taoism.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianDan.LianDanHandler;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianQi.LianQiHandler;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.XiuWeiHandler;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.wuGong.WuGongHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSetSkillStuff implements IMessage {
	private String skill;
	private float XP;
	private int level;
	public PacketSetSkillStuff(){}
	public PacketSetSkillStuff(String sk,int lvl, float exp){
		this.skill=sk;
		this.XP=exp;
		this.level=lvl;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		skill=ByteBufUtils.readUTF8String(buf);
		level=buf.readInt();
		XP=buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, skill);
		buf.writeInt(level);
		buf.writeFloat(XP);
	}
	public static class SetSkillXPHandler implements IMessageHandler<PacketSetSkillStuff,IMessage>{

		@Override
		public IMessage onMessage(PacketSetSkillStuff message, MessageContext ctx) {
			EntityPlayer p=(ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : ctx.getServerHandler().playerEntity);
			String s=message.skill;
			if(s.equals("QiLiSkill")){
				XiuWeiHandler.getThis(p).setXP(message.XP);
				XiuWeiHandler.getThis(p).setLevel(message.level);
			}
			if(s.equals("WuGongSkill")){
				WuGongHandler.getThis(p).setXP(message.XP);
				WuGongHandler.getThis(p).setLevel(message.level);
			}
			if(s.equals("LianDanSkill")){
				LianDanHandler.getThis(p).setXP(message.XP);
				LianDanHandler.getThis(p).setLevel(message.level);
			}
			if(s.equals("LianQiSkill")){
				LianQiHandler.getThis(p).setXP(message.XP);
				LianQiHandler.getThis(p).setLevel(message.level);
			}
			//as more skills get added put them here. Remember if strings
			return null;
		}

	}

}
