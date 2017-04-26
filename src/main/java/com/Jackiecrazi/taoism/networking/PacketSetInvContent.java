package com.Jackiecrazi.taoism.networking;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.allTheInterfaces.EnumEquipmentType;
import com.Jackiecrazi.taoism.common.taoistichandlers.PlayerEquipmentStalker;

public class PacketSetInvContent implements IMessage {
	private ItemStack is;
	private int slot;
	
	public PacketSetInvContent() {}
	public PacketSetInvContent(EnumEquipmentType eet,ItemStack i){
		slot=Arrays.asList(EnumEquipmentType.values()).indexOf(eet);
		is=i;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		slot=buf.readInt();
		is=ByteBufUtils.readItemStack(buf);
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(slot);
		ByteBufUtils.writeItemStack(buf, is);
	}

	public static class InvContentHandler implements IMessageHandler<PacketSetInvContent,IMessage>{

		@Override
		public IMessage onMessage(PacketSetInvContent message,
				MessageContext ctx) {
			final EntityPlayer thePlayer = (EntityPlayer) Taoism.proxy
					.getPlayerEntityFromContext(ctx);
			PlayerEquipmentStalker.getEquipmentList(thePlayer).setEquipment(EnumEquipmentType.values()[message.slot], message.is);
			return null;
		}

	}
}
