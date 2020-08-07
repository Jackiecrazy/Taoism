package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.capability.TaoCasterData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class AttackPacketDenierOld extends SimpleChannelInboundHandler<CPacketAnimation> {
    // The player this handler is associated with
    private EntityPlayerMP player;

    public AttackPacketDenierOld(final FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        this.player = ((NetHandlerPlayServer) event.getHandler()).player;
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        //player is swinging offhand
        if (super.acceptInboundMessage(msg)) {
            System.out.println(TaoCasterData.getTaoCap(player).getOffhandCool());
            System.out.println(player.swingingHand);
            System.out.println(player.swingProgress);
            System.out.println(player.isSwingInProgress);
            System.out.println(player.randomUnused1);
        }
        return super.acceptInboundMessage(msg) && TaoCasterData.getTaoCap(player).getOffhandCool() < 3;// && player.swingingHand == EnumHand.OFF_HAND && (player.getHeldItemOffhand().getItem() instanceof TaoWeapon || TaoCombatUtils.isValidWeapon(player.getHeldItemOffhand()));
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final CPacketAnimation msg) {
        //nom
        System.out.println("nom");
    }
}
