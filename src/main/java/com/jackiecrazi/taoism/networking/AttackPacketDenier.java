package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class AttackPacketDenier extends SimpleChannelInboundHandler<CPacketAnimation> {
    // The player this handler is associated with
    private EntityPlayerMP player;

    public AttackPacketDenier(final FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        this.player = ((NetHandlerPlayServer) event.getHandler()).player;
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        //player is swinging offhand
        return super.acceptInboundMessage(msg) && player.swingingHand == EnumHand.OFF_HAND && (player.getHeldItemOffhand().getItem() instanceof TaoWeapon || TaoCombatUtils.isValidWeapon(player.getHeldItemOffhand()));
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final CPacketAnimation msg) {
        //nom
    }
}
