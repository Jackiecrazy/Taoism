package com.Jackiecrazi.taoism.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.XiuWeiHandler;

public class PacketSetUnlockSkill implements IMessage {
	private String name;
	private int awesomeness;
	public PacketSetUnlockSkill() {}
	public PacketSetUnlockSkill(String n,int s){
		name=n;
		awesomeness=s;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name=ByteBufUtils.readUTF8String(buf);
		awesomeness=buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(awesomeness);
	}
	public static class SetSkillHandler implements IMessageHandler<PacketSetUnlockSkill,IMessage>{

		@Override
		public IMessage onMessage(PacketSetUnlockSkill msg,
				MessageContext ctx) {
			EntityPlayer p=(EntityPlayer) Taoism.proxy
					.getPlayerEntityFromContext(ctx);
			XiuWeiHandler.getThis(p).setSkillAwesomeness(msg.name, msg.awesomeness);
			//System.out.println("updated "+msg.name+" on side "+ctx.side+" to "+msg.awesomeness);
			return null;
		}
		
	}
}
