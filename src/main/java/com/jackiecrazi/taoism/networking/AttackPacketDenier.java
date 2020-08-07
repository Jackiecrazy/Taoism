package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class AttackPacketDenier extends ChannelOutboundHandlerAdapter {
    // The player this handler is associated with
    private EntityPlayerSP player;

    public AttackPacketDenier(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        this.player = Minecraft.getMinecraft().player;
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
//        if (player.isSwingInProgress && player.swingingHand == EnumHand.OFF_HAND && (player.getHeldItemOffhand().getItem() instanceof TaoWeapon || TaoCombatUtils.isValidWeapon(player.getHeldItemOffhand()))) {
//        } else
            super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(player==null)player=Minecraft.getMinecraft().player;
        if (msg instanceof CPacketAnimation && player.isSwingInProgress && player.swingingHand == EnumHand.OFF_HAND && (player.getHeldItemOffhand().getItem() instanceof TaoWeapon || TaoCombatUtils.isValidWeapon(player.getHeldItemOffhand()))) {
            //nom
        } else
            super.write(ctx, msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
            super.flush(ctx);
    }
}
