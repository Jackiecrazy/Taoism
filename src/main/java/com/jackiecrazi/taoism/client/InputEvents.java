package com.jackiecrazi.taoism.client;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.IChargeableWeapon;
import com.jackiecrazi.taoism.api.alltheinterfaces.IRange;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.networking.*;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Taoism.MODID)
public class InputEvents {
    private static final int ALLOWANCE = 7;
    private static final int CHARGE = 50;
    static int leftClickAt = 0;
    static int rightClickAt = 0;
    /**
     * left, back, right
     */
    private static long[] lastTap = {0, 0, 0, 0};
    private static boolean[] tapped = {false, false, false, false};
    private static boolean jump = false;
    private static boolean sneak = false;
    private static Entity lastTickLookAt;
    private static boolean rightClick = false;
    private static int lastOffTick = 0, lastMainTick = 0;

    private static boolean canWeaponAttack(EntityPlayer player, EnumHand hand, ItemStack stack) {
        return hand == EnumHand.OFF_HAND && stack != null && (stack.getItem() instanceof TaoWeapon || (TaoCasterData.getTaoCap(player).isInCombatMode() && (TaoCombatUtils.isWeapon(stack) || stack.isEmpty() || TaoCombatUtils.isShield(stack))));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void doju(InputUpdateEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        MovementInput mi = e.getMovementInput();
        final ITaoStatCapability itsc = TaoCasterData.getTaoCap(mc.player);
        if (KeyBindOverlord.combatMode.getKeyConflictContext().isActive() && KeyBindOverlord.combatMode.isPressed()) {
            mc.player.sendStatusMessage(new TextComponentTranslation("taoism.combat." + (itsc.isInCombatMode() ? "off" : "on")), true);
            Taoism.net.sendToServer(new PacketToggleCombatMode());
        }
        if (itsc.getRootTime() > 0) {
            //no moving while you're rooted!
            KeyBinding.unPressAllKeys();
            return;
        }
        if (itsc.getQi() > 0 && itsc.isInCombatMode()) {
            final boolean onSprint = mc.gameSettings.keyBindSprint.isPressed();
            if (mi.leftKeyDown && (!tapped[0] || onSprint)) {
                if (mc.world.getTotalWorldTime() - lastTap[0] <= ALLOWANCE || onSprint) {
                    Taoism.net.sendToServer(new PacketDodge(0));
                }
                lastTap[0] = mc.world.getTotalWorldTime();
            }
            tapped[0] = mi.leftKeyDown;
            if (mi.backKeyDown && (!tapped[1] || onSprint)) {
                if (mc.world.getTotalWorldTime() - lastTap[1] <= ALLOWANCE || onSprint) {
                    Taoism.net.sendToServer(new PacketDodge(1));
                }
                lastTap[1] = mc.world.getTotalWorldTime();
            }
            tapped[1] = mi.backKeyDown;
            if (mi.rightKeyDown && (!tapped[2] || onSprint)) {
                if (mc.world.getTotalWorldTime() - lastTap[2] <= ALLOWANCE || onSprint) {
                    Taoism.net.sendToServer(new PacketDodge(2));
                }
                lastTap[2] = mc.world.getTotalWorldTime();
            }
            tapped[2] = mi.rightKeyDown;
            if (mi.forwardKeyDown && (!tapped[3] || onSprint)) {
                if (mc.world.getTotalWorldTime() - lastTap[3] <= ALLOWANCE || onSprint) {
                    Taoism.net.sendToServer(new PacketDodge(3));
                }
                lastTap[3] = mc.world.getTotalWorldTime();
            }
            tapped[3] = mi.forwardKeyDown;
        }

        if (itsc.getDownTimer() > 0) {
            //no moving while you're down! (except for a safety roll)
            KeyBinding.unPressAllKeys();
            return;
        }

        if (itsc.getQi() > 0 && itsc.isInCombatMode()) {
            if (mi.jump && !jump) {
                //if(mc.world.getTotalWorldTime()-lastSneak<=ALLOWANCE){
                Taoism.net.sendToServer(new PacketJump());
                //}
            }
            jump = mi.jump;

            if (mc.player.isSprinting() && mi.sneak && !sneak) {
                //if(mc.world.getTotalWorldTime()-lastSneak<=ALLOWANCE){
                Taoism.net.sendToServer(new PacketSlide());
                //}
            }
            sneak = mi.sneak;
        }
    }

    @SubscribeEvent
    public static void longPress(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.pointedEntity != null && mc.player != null && mc.player.getHeldItemMainhand().getItem() instanceof IRange) {
                IRange ir = (IRange) mc.player.getHeldItemMainhand().getItem();
                float rangesq = ir.getReach(mc.player, mc.player.getHeldItemMainhand());
                rangesq *= rangesq;
                if (NeedyLittleThings.getDistSqCompensated(mc.pointedEntity, mc.player) > rangesq) {
                    mc.pointedEntity = null;
                    Vec3d look = mc.player.getLook(1).scale(3);
                    mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, look, null, new BlockPos(look));
                }
            }
            if (Taoism.proxy.isBreakingBlock(mc.player)) {
                leftClickAt = 0;
                rightClickAt = 0;
                return;
            }
            if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.player.getHeldItemMainhand().getItem() instanceof IChargeableWeapon) {
                leftClickAt++;
                if (leftClickAt == CHARGE) {
                    mc.player.sendStatusMessage(new TextComponentTranslation("weapon.spoiler"), true);
                    Taoism.net.sendToServer(new PacketChargeWeapon(EnumHand.MAIN_HAND));
                }
            }
            if (mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getActiveHand() != EnumHand.MAIN_HAND && mc.player.getHeldItemOffhand().getItem() instanceof IChargeableWeapon) {
                rightClickAt++;
                if (rightClickAt == CHARGE) {
                    mc.player.sendStatusMessage(new TextComponentTranslation("weapon.spoiler"), true);
                    Taoism.net.sendToServer(new PacketChargeWeapon(EnumHand.OFF_HAND));
                }
            }// else rightClickAt = 0;
        } else rightClick = false;
    }

    @SubscribeEvent
    public static void noHit(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.pointedEntity != null && mc.player != null && mc.player.getHeldItemMainhand().getItem() instanceof IRange) {
            IRange ir = (IRange) mc.player.getHeldItemMainhand().getItem();
            float rangesq = ir.getReach(mc.player, mc.player.getHeldItemMainhand());
            rangesq *= rangesq;
            if (NeedyLittleThings.getDistSqCompensated(mc.pointedEntity, mc.player) > rangesq) {
                mc.pointedEntity = null;
                Vec3d look = mc.player.getLook(1).scale(3);
                mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, look, null, new BlockPos(look));
                return;
            }
        }
//        if (!Taoism.proxy.isBreakingBlock(Minecraft.getMinecraft().player) && (mc.gameSettings.keyBindAttack.isKeyDown() || mc.gameSettings.keyBindUseItem.isKeyDown()) && (leftClickAt == 0 || rightClickAt == 0)) {
//            GameSettings gs = Minecraft.getMinecraft().gameSettings;
//            MoveCode move = new MoveCode(true, gs.keyBindForward.isKeyDown(), gs.keyBindBack.isKeyDown(), gs.keyBindLeft.isKeyDown(), gs.keyBindRight.isKeyDown(), gs.keyBindJump.isKeyDown(), gs.keyBindSneak.isKeyDown(), gs.keyBindAttack.isKeyDown());
//            Taoism.net.sendToServer(new PacketMakeMove(move));
//            leftClickAt = rightClickAt = 0;
//        }
    }

    //attacks when out of range, charges for l'execution
//    @SubscribeEvent
//    public static void pleasekillme(PlayerInteractEvent.LeftClickEmpty e) {
//        //System.out.println("hi");
//        EntityPlayer p = e.getEntityPlayer();
//        ItemStack i = p.getHeldItem(EnumHand.MAIN_HAND);
//        if (i.getItem() instanceof IRange) {
//            //System.out.println("range!");
//            IRange icr = (IRange) i.getItem();
//            Entity elb = NeedyLittleThings.raytraceEntity(p.world, p, icr.getReach(p, i));
//            if (elb != null) {
//                //System.out.println("sending packet!");
//                Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), true));
//            }
//        }
//    }

    @SubscribeEvent
    public static void noHitMouse(InputEvent.MouseInputEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.pointedEntity != null && mc.player != null && mc.player.getHeldItemMainhand().getItem() instanceof IRange) {
            IRange ir = (IRange) mc.player.getHeldItemMainhand().getItem();
            float rangesq = ir.getReach(mc.player, mc.player.getHeldItemMainhand());
            rangesq *= rangesq;
            if (NeedyLittleThings.getDistSqCompensated(mc.pointedEntity, mc.player) > rangesq) {
                mc.pointedEntity = null;
                Vec3d look = mc.player.getLook(1).scale(3);
                mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, look, null, new BlockPos(look));
                return;
            }
        }
//        if (!Taoism.proxy.isBreakingBlock(Minecraft.getMinecraft().player) && (mc.gameSettings.keyBindAttack.isKeyDown() || mc.gameSettings.keyBindUseItem.isKeyDown()) && (leftClickAt == 0 || rightClickAt == 0)) {
//            GameSettings gs = Minecraft.getMinecraft().gameSettings;
//            MoveCode move = new MoveCode(true, gs.keyBindForward.isKeyDown(), gs.keyBindBack.isKeyDown(), gs.keyBindLeft.isKeyDown(), gs.keyBindRight.isKeyDown(), gs.keyBindJump.isKeyDown(), gs.keyBindSneak.isKeyDown(), gs.keyBindAttack.isKeyDown());
//            Taoism.net.sendToServer(new PacketMakeMove(move));
//            System.out.println("dep");
//            leftClickAt = rightClickAt = 1;
//        }
    }

    @SubscribeEvent
    public static void punchy(PlayerInteractEvent.EntityInteract e) {
        if (TaoCombatUtils.isTwoHanded(e.getEntityPlayer().getHeldItemMainhand()) && e.getHand() == EnumHand.OFF_HAND)
            return;
        if (!rightClick && e.getHand() == EnumHand.OFF_HAND && canWeaponAttack(e.getEntityPlayer(), e.getHand(), e.getItemStack())) {
            rightClick = true;
            Entity n = ClientEvents.getEntityLookedAt(e.getEntity(), NeedyLittleThings.getAttributeModifierHandSensitive(EntityPlayer.REACH_DISTANCE, e.getEntityLiving(), EnumHand.OFF_HAND));
            if (n == null && TaoCombatUtils.isShield(e.getItemStack())) return;
            if (e.getEntityPlayer().ticksExisted >= lastOffTick +2) {
                Taoism.net.sendToServer(new PacketSweep(false, n));
                lastOffTick = e.getEntityPlayer().ticksExisted;
            }
        }
    }

    @SubscribeEvent
    public static void sweepSwing(PlayerInteractEvent.LeftClickEmpty e) {
        Entity n = ClientEvents.getEntityLookedAt(e.getEntity(), NeedyLittleThings.getAttributeModifierHandSensitive(EntityPlayer.REACH_DISTANCE, e.getEntityPlayer(), EnumHand.MAIN_HAND));
        if (e.getEntity().ticksExisted >= lastMainTick +2) {
            Taoism.net.sendToServer(new PacketSweep(true, n));
            lastMainTick = e.getEntity().ticksExisted;
        }
    }

    @SubscribeEvent
    public static void sweepSwingBlock(PlayerInteractEvent.LeftClickBlock e) {
        // if (Minecraft.getMinecraft()()) return;
        //float temp = CombatUtils.getCooledAttackStrength(e.getEntity(), EnumHand.MAIN_HAND, 0.5f);
        Entity n = ClientEvents.getEntityLookedAt(e.getEntity(), NeedyLittleThings.getAttributeModifierHandSensitive(EntityPlayer.REACH_DISTANCE, e.getEntityLiving(), EnumHand.MAIN_HAND));
        if (e.getEntity().ticksExisted >= lastMainTick +2) {
            Taoism.net.sendToServer(new PacketSweep(true, n));
            lastMainTick = e.getEntity().ticksExisted;
        }
    }

    @SubscribeEvent
    public static void sweepSwingOff(PlayerInteractEvent.RightClickEmpty e) {
        if (TaoCombatUtils.isTwoHanded(e.getEntityPlayer().getHeldItemMainhand()) && e.getHand() == EnumHand.OFF_HAND)
            return;
        if (!rightClick && canWeaponAttack(e.getEntityPlayer(), e.getHand(), e.getItemStack())) {
            rightClick = true;
            Entity n = ClientEvents.getEntityLookedAt(e.getEntity(), NeedyLittleThings.getAttributeModifierHandSensitive(EntityPlayer.REACH_DISTANCE, e.getEntityLiving(), EnumHand.OFF_HAND));
            if (n == null && TaoCombatUtils.isShield(e.getItemStack())) return;
            if (e.getEntityPlayer().ticksExisted >= lastOffTick +2) {
                Taoism.net.sendToServer(new PacketSweep(false, n));
                lastOffTick = e.getEntityPlayer().ticksExisted;
            }
        }
    }

    @SubscribeEvent
    public static void sweepSwingOffItem(PlayerInteractEvent.RightClickItem e) {
        if (TaoCombatUtils.isTwoHanded(e.getEntityPlayer().getHeldItemMainhand()) && e.getHand() == EnumHand.OFF_HAND)
            return;
        if (!rightClick && canWeaponAttack(e.getEntityPlayer(), e.getHand(), e.getItemStack())) {
            rightClick = true;
            Entity n = ClientEvents.getEntityLookedAt(e.getEntity(), NeedyLittleThings.getAttributeModifierHandSensitive(EntityPlayer.REACH_DISTANCE, e.getEntityLiving(), EnumHand.OFF_HAND));
            if (n == null && TaoCombatUtils.isShield(e.getItemStack())) return;
            if (e.getEntityPlayer().ticksExisted >= lastOffTick +2) {
                Taoism.net.sendToServer(new PacketSweep(false, n));
                lastOffTick = e.getEntityPlayer().ticksExisted;
            }
        }
    }

    @SubscribeEvent
    public static void sweepSwingOffItemBlock(PlayerInteractEvent.RightClickBlock e) {
        if (TaoCombatUtils.isTwoHanded(e.getEntityPlayer().getHeldItemMainhand()) && e.getHand() == EnumHand.OFF_HAND)
            return;
        if (!rightClick && canWeaponAttack(e.getEntityPlayer(), e.getHand(), e.getItemStack())) {
            rightClick = true;
            Entity n = ClientEvents.getEntityLookedAt(e.getEntity(), NeedyLittleThings.getAttributeModifierHandSensitive(EntityPlayer.REACH_DISTANCE, e.getEntityLiving(), EnumHand.OFF_HAND) - (e.getItemStack().isEmpty() ? 1 : 0));
            if (n == null && TaoCombatUtils.isShield(e.getItemStack())) return;
            if (e.getEntityPlayer().ticksExisted >= lastOffTick +2) {
                Taoism.net.sendToServer(new PacketSweep(false, n));
                lastOffTick = e.getEntityPlayer().ticksExisted;
            }
        }
    }
}
