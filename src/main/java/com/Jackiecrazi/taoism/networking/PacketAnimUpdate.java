package com.Jackiecrazi.taoism.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.taoistichandlers.AnimationStalker;

public class PacketAnimUpdate implements IMessage {
	private boolean activated;
	private ItemStack stackInQuestion;
	private boolean isRightClick;
	private int slot;//prevent changing slots
	private int type;//0 is normal, 1 is shift, 2 is jump, taking dominance based on the number

    public PacketAnimUpdate() 
    {}
    public PacketAnimUpdate(boolean active,ItemStack is, boolean isRightClick, int i,int slot) 
    {
    	activated=active;
    	stackInQuestion=is;
        this.isRightClick=isRightClick;
        type=i;
        this.slot=slot;
    }
    public PacketAnimUpdate(ItemStack is, boolean isRightClick, byte tp,int slot) 
    {
     this(true,is,isRightClick,tp,slot);
    }
    public PacketAnimUpdate(boolean active){//ONLY USE TO DISABLE!
    	this(active,null,true,(byte) 0,0);
    }

    @Override
    public void fromBytes(ByteBuf buf) 
    {
    	buf.readBoolean();
     stackInQuestion=ByteBufUtils.readItemStack(buf);
     isRightClick=buf.readBoolean();
     type=buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) 
    {
    	buf.writeBoolean(activated);
     ByteBufUtils.writeItemStack(buf, stackInQuestion);
     buf.writeBoolean(isRightClick);
     buf.writeByte(type);
    }

    public static class UpdateAnimationHandler implements IMessageHandler<PacketAnimUpdate, 
          IMessage> 
    {
        @Override
        public IMessage onMessage(final PacketAnimUpdate message, 
              MessageContext ctx) 
        {
            final AbstractClientPlayer p = (AbstractClientPlayer) Taoism.proxy.
                  getPlayerEntityFromContext(ctx);
            AnimationStalker.getThis(p).updateAnimation(message.activated, message.isRightClick, message.stackInQuestion, (!p.isCollidedVertically?2:p.isSneaking()?1:0),message.slot);//message.type);
            return null;
        }
    }

}
