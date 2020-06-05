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
    private int side;//0 left, 1 back, 2 right, 3 front
    /*
    //decreased gravity and high jump: scales with level, nerf it down a little, decreased gravity scales with horizontal speed, up to half
    backflip: level 0, direction+jump in midair, get 1 charge from being on ground, so can only do once, melds into double jump when unlocked
    wall run: level 3, sprint next to wall when airborne, check collidedhorizontally and if last tick is sprinting on client, then send packet to request wall sticking.
        NOTE: when sprinting and hitting a wall, there should be a brief moment in PlayerTickEvent.Pre where isSprinting() and collidedHorizontally are both true
        Record the side on which the player sticks, and disallow future attempts to stick on the same side
        After being stuck on wall, move at sprint speed. Perpendicular velocity is converted into vertical velocity
        Jump gets a little buff away from wall and sets you sprinting for another wall stick
        If at any moment your total velocity goes to or under walking speed, fall off. Half a second of grace during initial stick
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
