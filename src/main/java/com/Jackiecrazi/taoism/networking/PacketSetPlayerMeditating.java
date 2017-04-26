package com.Jackiecrazi.taoism.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.taoistichandlers.PlayerResourceStalker;

public class PacketSetPlayerMeditating implements IMessage {
	private boolean toggle;
	private int PlayerID;
	public PacketSetPlayerMeditating(){}
	public PacketSetPlayerMeditating(boolean toggle,EntityPlayer p){
		this.toggle=toggle;
		this.PlayerID=p.getEntityId();
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		PlayerID=buf.readInt();
		toggle=buf.readBoolean();
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		buf.writeInt(PlayerID);
		buf.writeBoolean(toggle);
	}
	public static class SetMeditateHandler implements IMessageHandler<PacketSetPlayerMeditating,IMessage>{

		@Override
		public IMessage onMessage(PacketSetPlayerMeditating message,
				MessageContext ctx) {
			final EntityPlayer thePlayer = (EntityPlayer) Taoism.proxy
					.getPlayerEntityFromContext(ctx);

			EntityPlayer theEntity = (EntityPlayer) thePlayer.world.getEntityByID(message.PlayerID);
			if(theEntity!=null)
			PlayerResourceStalker.get(theEntity).setIsMeditating(message.toggle);
			//System.out.println(theEntity.toString()+" is now meditating is "+message.toggle+" on the "+ctx.side+" side");
			
			return null;
		}

	}
}
