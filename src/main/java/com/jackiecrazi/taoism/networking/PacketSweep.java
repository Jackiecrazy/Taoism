package com.jackiecrazi.taoism.networking;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketSweep implements IMessage {

    private int entityId;
    private boolean main;

    public PacketSweep() {
    }

    public PacketSweep(boolean mainHand, Entity entity) {
        if (entity != null)
            entityId = entity.getEntityId();
        main = mainHand;
    }

    public PacketSweep(int parEntityId, boolean mainHand) {
        entityId = parEntityId;
        main = mainHand;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = ByteBufUtils.readVarInt(buf, 4);
        main = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, entityId, 4);
        buf.writeBoolean(main);
    }

    public static class SweepHandler implements
            IMessageHandler<PacketSweep, IMessage> {
        @Override
        public IMessage onMessage(final PacketSweep message,
                                  MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                //System.out.println("packet acquired!");
                final EntityPlayerMP attacker = (EntityPlayerMP) Taoism.proxy
                        .getPlayerEntityFromContext(ctx);
                //attacker.getServerWorld().addScheduledTask(() -> {
                Entity theEntity = attacker.world
                        .getEntityByID(message.entityId);
                ItemStack heldItem = attacker.getHeldItem(message.main ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                if (heldItem != null && heldItem.getItem() instanceof TaoWeapon) {
                    TaoWeapon tw = (TaoWeapon) heldItem.getItem();
                    tw.aoe(heldItem, attacker, TaoCasterData.getTaoCap(attacker).getQiFloored());
                } else {
                    int horDeg = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING, heldItem) * 40;
                    int vertDeg = 60;
                    double reach = attacker.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
                    List<Entity> targets = attacker.world.getEntitiesInAABBexcluding(null, attacker.getEntityBoundingBox().grow(reach), TaoCombatUtils.VALID_TARGETS::test);
                    boolean sweep = false;
                    for (Entity target : targets) {
                        if (target == attacker || target == theEntity || attacker.isRidingOrBeingRiddenBy(target))
                            continue;
                        //!NeedyLittleThings.isFacingEntity(attacker,target)||
                        if (horDeg < 360 && !NeedyLittleThings.isFacingEntity(attacker, target, horDeg, vertDeg) || NeedyLittleThings.getDistSqCompensated(target, attacker) > reach * reach || target == theEntity)
                            continue;
                        TaoCombatUtils.attackAtStrength(attacker, target, message.main ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND, TaoCasterData.getTaoCap(attacker).getSwing(), TaoCombatUtils.causeLivingDamage(attacker));
                        sweep = true;
                    }
                    if (sweep) {
                        attacker.spawnSweepParticles();
                    }
                }
            });
            return null;
        }
    }
}
