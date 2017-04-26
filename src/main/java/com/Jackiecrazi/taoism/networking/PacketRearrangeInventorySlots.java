package com.Jackiecrazi.taoism.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.container.ContainerTPInv;

public class PacketRearrangeInventorySlots implements IMessage {
	private int nm;
	public PacketRearrangeInventorySlots() {}
	public PacketRearrangeInventorySlots(int nmbr){
		nm=nmbr;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nm=buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(nm);
	}

	public static class InvChangeHandler implements IMessageHandler<PacketRearrangeInventorySlots,IMessage>{

		@Override
		public IMessage onMessage(PacketRearrangeInventorySlots message,
				MessageContext ctx) {
			final EntityPlayer thePlayer = (EntityPlayer) Taoism.proxy
					.getPlayerEntityFromContext(ctx);
			if((thePlayer.openContainer ==null || !(thePlayer.openContainer instanceof ContainerTPInv)))
			return null;
			System.out.println(message.nm);
			((ContainerTPInv)thePlayer.openContainer).change(message.nm);
			return null;
		}

	}
}
