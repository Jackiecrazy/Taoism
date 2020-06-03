package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDodge implements IMessage {
    int side;//0 left, 1 back, 2 right, 3 front
    /*
    //decreased gravity and high jump: scales with level, nerf it down a little, decreased gravity scales with horizontal speed, up to half
    backflip: level 0, direction+jump in midair, get 1 charge from being on ground, so can only do once, melds into double jump when unlocked
    wall run: level 3, sprint next to wall, check collidedhorizontally, keep sprint on and move to look, fall if not sprinting
    extended jump: level 2, sprint+jump, horizontal velocity amplified along with vertical velocity
    extended aerial dodge: 2, double direction in air, recharged with jump
    double jump: 3, jump when airborne), hook into jump key
    wall jump in selected direction: 1, jump while touching wall),
    kick jump: 2, replace double jump automatically when impacting an entity), knock back both parties by parry rules, medium unblockable posture damage
    slide: 0, sprint sneak, gives iframes while charging forward, basically forward rolling
     */

    public PacketDodge() {

    }

    public PacketDodge(int towards) {
        side = towards;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        side = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(side);
    }

    public static class DodgeHandler implements IMessageHandler<PacketDodge, IMessage> {

        @Override
        public IMessage onMessage(PacketDodge message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                //System.out.println("packet dodge received");
                final EntityPlayer p = Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                TaoMovementUtils.attemptDodge(p, message.side);
            });
            return null;
        }
    }
}
