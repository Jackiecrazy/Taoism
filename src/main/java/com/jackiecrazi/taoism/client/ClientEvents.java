package com.jackiecrazi.taoism.client;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.BinaryMachiavelli;
import com.jackiecrazi.taoism.api.MoveCode;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.IChargeableWeapon;
import com.jackiecrazi.taoism.api.alltheinterfaces.IRange;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITetherItem;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.capability.TaoStatCapability;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.config.HudConfig;
import com.jackiecrazi.taoism.networking.*;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Taoism.MODID)
public class ClientEvents {
    //TODO somehow cancel short attacks on the client...

    //Reflection time!
    public static final Field zaHando = ObfuscationReflectionHelper.findField(ItemRenderer.class, "field_187471_h");
    public static final Field okuyasu = ObfuscationReflectionHelper.findField(ItemRenderer.class, "field_187472_i");
    private static final int ALLOWANCE = 7;
    private static final int[] GRADIENTE = {
            new Color(200, 37, 56).getRGB(),
            new Color(177, 52, 51).getRGB(),
            new Color(141, 71, 43).getRGB(),
            new Color(103, 94, 36).getRGB(),
            new Color(69, 115, 30).getRGB(),
            new Color(46, 127, 24).getRGB()
    };
    private static final int[] GRADIENTSSP = {
            0xE0FF00,
            0xC0FF00,
            0xA0FF00,
            0x80FF00,
            0x60FF00,
            0x40FF00,
            0x20FF00,
            0x10FF00,
            0x00FF00
    };
    private static final int[] GRADIENTDOWN = {
            0xFF0000,
            0xFF2000,
            0xFF4000,
            0xFF6000,
            0xFF8000,
            0xFFA000,
            0xFFC000,
            0xFFE000,
            0xFFFF00 //max, step by 15
    };
    private static final ResourceLocation hud = new ResourceLocation(Taoism.MODID, "textures/hud/yeet.png");
    private static final ResourceLocation hood = new ResourceLocation(Taoism.MODID, "textures/hud/icons.png");
    private static final int CHARGE = 50;
    /**
     * left, back, right
     */
    private static long[] lastTap = {0, 0, 0, 0};
    private static boolean[] tapped = {false, false, false, false};
    private static boolean jump = false, sneak = false;
    private static int leftClickAt = 0, rightClickAt = 0;
    private static float currentQiLevel = 0;

    @SubscribeEvent
    public static void model(ModelRegistryEvent e) {
        for (Item i : TaoWeapon.listOfWeapons) {
            regWeap(i);
        }
        regWeap(TaoItems.prop);
    }

    private static void regWeap(Item i) {
        ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory"));
    }

    //attacks when out of range, charges for l'execution
    @SubscribeEvent
    public static void pleasekillme(PlayerInteractEvent.LeftClickEmpty e) {
        //System.out.println("hi");
        EntityPlayer p = e.getEntityPlayer();
        ItemStack i = p.getHeldItem(EnumHand.MAIN_HAND);
        if (i.getItem() instanceof IRange) {
            //System.out.println("range!");
            IRange icr = (IRange) i.getItem();
            Entity elb = NeedyLittleThings.raytraceEntity(p.world, p, icr.getReach(p, i));
            if (elb != null) {
                //System.out.println("sending packet!");
                Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), true));
            }
        }
    }

//    @SubscribeEvent
//    public static void squish(ItemTooltipEvent e){
//        e.getToolTip().removeIf(s -> (s.startsWith(TextFormatting.BLUE) && !KeyBindOverlord.isShiftDown())||());
//    }

    @SubscribeEvent
    public static void down(RenderLivingEvent.Pre event) {
        Tuple<Float, Float> sizes = new Tuple<>(event.getEntity().width, event.getEntity().height);//TaoCasterData.getTaoCap(event.getEntity()).getPrevSizes();
        if (event.getEntity().isEntityAlive()) {
            if (TaoCasterData.getTaoCap(event.getEntity()).getDownTimer() > 0) {
                GlStateManager.pushMatrix();
                //tall bois become flat bois
                if (sizes.getFirst() < sizes.getSecond()) {
                    GlStateManager.translate(event.getX(), event.getY(), event.getZ());
                    GlStateManager.rotate(180f, 0, 0, 0);
                    GlStateManager.rotate(90f, 1, 0, 0);
                    GlStateManager.rotate(event.getEntity().renderYawOffset, 0, 0, 1);
                    GlStateManager.rotate(event.getEntity().renderYawOffset, 0, 1, 0);
                    GlStateManager.translate(-event.getX(), -event.getY() - event.getEntity().height / 2, -event.getZ());
                }
                //cube bois become side bois
                //flat bois become flatter bois
                else {//sizes.getFirst().equals(sizes.getSecond())&&sizes.getFirst()==0 //this means it didn't update, which happens when there's nothing to change, i.e. you're flat already
                    GlStateManager.translate(event.getX(), event.getY(), event.getZ());
                    //GlStateManager.rotate(180f, 0, 0, 0);
                    //GlStateManager.rotate(180f, 0, 1, 0);
                    //GlStateManager.rotate(event.getEntity().renderYawOffset, 0, 0, 1);
                    //GlStateManager.rotate(event.getEntity().renderYawOffset, 0, 1, 0);
                    GlStateManager.translate(-event.getX(), -event.getY() - event.getEntity().height / 2, -event.getZ());
                }
                //multi bois do nothing
            }
            if (TaoCasterData.getTaoCap(event.getEntity()).getCannonballTime() > 0 && !event.getEntity().onGround) {
                int screw = TaoCasterData.getTaoCap(event.getEntity()).getCannonballTime();
                GlStateManager.pushMatrix();
                GlStateManager.translate(event.getX(), event.getY() + event.getEntity().height / 2, event.getZ());
                GlStateManager.rotate(screw * 7 + event.getPartialRenderTick(), MathHelper.sin((float) (event.getEntity().posX)), MathHelper.sin((float) (event.getEntity().posY)), MathHelper.sin((float) (-event.getEntity().posZ)));
                GlStateManager.translate(-event.getX(), -event.getY() - event.getEntity().height / 2, -event.getZ());
            }
//            EntityLivingBase e=event.getEntity();
//            if (e != null) {
//                final ItemStack offhand = e.getHeldItemOffhand();
//                if (offhand.getItem() instanceof ITetherItem) {
//                    ITetherItem ir = (ITetherItem) offhand.getItem();
//                    if (ir.renderTether(offhand) && ir.getTetheringEntity(offhand, e) != null && ir.getTetheredEntity(offhand, e) != null) {
//                        renderTether(ir.getTetheredEntity(offhand, e), ir.getTetheringEntity(offhand, e), event.getPartialRenderTick(), EnumHand.OFF_HAND);
//                    }
//                }
//                final ItemStack main = e.getHeldItemMainhand();
//                if (main.getItem() instanceof ITetherItem) {
//                    ITetherItem ir = (ITetherItem) main.getItem();
//                    if (ir.renderTether(main) && ir.getTetheringEntity(main, e) != null && ir.getTetheredEntity(main, e) != null) {
//                        renderTether(ir.getTetheredEntity(main, e), ir.getTetheringEntity(main, e), event.getPartialRenderTick(), EnumHand.MAIN_HAND);
//                    }
//                }
//            }
        }
    }

    @SubscribeEvent
    public static void downEnd(RenderLivingEvent.Post event) {
        if (event.getEntity().isEntityAlive()) {
            if (TaoCasterData.getTaoCap(event.getEntity()).getDownTimer() > 0) {//TaoCasterData.getTaoCap(event.getEntity()).getDownTimer()>0
                GlStateManager.popMatrix();
            }
            if (TaoCasterData.getTaoCap(event.getEntity()).getCannonballTime() > 0 && !event.getEntity().onGround) {
                GlStateManager.popMatrix();
            }
        }
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
    public static void updateMove(MouseEvent e) {
        if (!e.isButtonstate()) return;
        if (TaoCasterData.getTaoCap(Minecraft.getMinecraft().player).getDownTimer() > 0) {
            //no moving while you're down! (except for a safety roll)
            KeyBinding.unPressAllKeys();
            return;
        }
        if (!Taoism.proxy.isBreakingBlock(Minecraft.getMinecraft().player)) {
            GameSettings gs = Minecraft.getMinecraft().gameSettings;
            MoveCode move = new MoveCode(true, gs.keyBindForward.isKeyDown(), gs.keyBindBack.isKeyDown(), gs.keyBindLeft.isKeyDown(), gs.keyBindRight.isKeyDown(), gs.keyBindJump.isKeyDown(), gs.keyBindSneak.isKeyDown(), e.getButton() == 0);
            Taoism.net.sendToServer(new PacketMakeMove(move));
            leftClickAt = rightClickAt = 0;
        }
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
//            if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.player.getHeldItemMainhand().getItem() instanceof IChargeableWeapon) {
//                leftClickAt++;
//                if (leftClickAt == CHARGE) {
//                    //mc.player.sendStatusMessage(new TextComponentTranslation("weapon.spoiler"), true);
//                    Taoism.net.sendToServer(new PacketChargeWeapon(EnumHand.MAIN_HAND));
//                }
//            }
//            if (mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemOffhand().getItem() instanceof IChargeableWeapon) {
//                rightClickAt++;
//                if (rightClickAt == CHARGE) {
//                    //mc.player.sendStatusMessage(new TextComponentTranslation("weapon.spoiler"), true);
//                    Taoism.net.sendToServer(new PacketChargeWeapon(EnumHand.OFF_HAND));
//                }
//            }// else rightClickAt = 0;
        }
    }

    @SubscribeEvent
    public static void lasso(RenderWorldLastEvent event) {
        EntityLivingBase e = Minecraft.getMinecraft().player;
        if (e != null) {
            final ItemStack offhand = e.getHeldItemOffhand();
            if (offhand.getItem() instanceof ITetherItem) {
                ITetherItem ir = (ITetherItem) offhand.getItem();
                if (ir.renderTether(offhand) && ir.getTetheringEntity(offhand, e) != null && ir.getTetheredEntity(offhand, e) != null) {
                    renderTether(ir.getTetheredEntity(offhand, e), ir.getTetheringEntity(offhand, e), event.getPartialTicks(), EnumHand.OFF_HAND);
                }
            }
            final ItemStack main = e.getHeldItemMainhand();
            if (main.getItem() instanceof ITetherItem) {
                ITetherItem ir = (ITetherItem) main.getItem();
                if (ir.renderTether(main) && ir.getTetheringEntity(main, e) != null && ir.getTetheredEntity(main, e) != null) {
                    renderTether(ir.getTetheredEntity(main, e), ir.getTetheringEntity(main, e), event.getPartialTicks(), EnumHand.MAIN_HAND);
                }
            }
        }
    }

    private static void renderTether(Entity thrower, Entity bound, double partialTicks, EnumHand renderOnHand) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-thrower.prevPosX - (thrower.posX - thrower.prevPosX) * partialTicks, -thrower.prevPosY - (thrower.posY - thrower.prevPosY) * partialTicks, -thrower.prevPosZ - (thrower.posZ - thrower.prevPosZ) * partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
//            GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
//            GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
//
//            GlStateManager.disableRescaleNormal();
//            GlStateManager.popMatrix();
        int handSide = 1;

        float swingProgress = 0;//thrower.getSwingProgress(partialTicks);
        float sinSwing = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
        float throwerYawRenderTick = 0;
        if (thrower instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) thrower;
            handSide = (elb.getPrimaryHand() == EnumHandSide.RIGHT) == (renderOnHand == EnumHand.MAIN_HAND) ? 1 : -1;
            throwerYawRenderTick = (elb.prevRenderYawOffset + (elb.renderYawOffset - elb.prevRenderYawOffset) * (float) partialTicks) * 0.017453292F;
        }
        double sinThrowerYaw = MathHelper.sin(throwerYawRenderTick);
        double cosThrowerYaw = MathHelper.cos(throwerYawRenderTick);
        double handOffset = (double) handSide * 0.35D;
        double throwerSpotX;
        double throwerSpotY;
        double throwerSpotZ;
        double eyeHeight;

        if ((Minecraft.getMinecraft().getRenderManager().options == null || Minecraft.getMinecraft().getRenderManager().options.thirdPersonView <= 0) && thrower == Minecraft.getMinecraft().player) {
            float fov = Minecraft.getMinecraft().getRenderManager().options.fovSetting;
            fov = fov / 100.0F;
            Vec3d vec3d = new Vec3d((double) handSide * -0.36D * (double) fov, -0.045D * (double) fov, 0.4D);
            vec3d = vec3d.rotatePitch(-(thrower.prevRotationPitch + (thrower.rotationPitch - thrower.prevRotationPitch) * (float) partialTicks) * 0.017453292F);
            vec3d = vec3d.rotateYaw(-(thrower.prevRotationYaw + (thrower.rotationYaw - thrower.prevRotationYaw) * (float) partialTicks) * 0.017453292F);
            vec3d = vec3d.rotateYaw(sinSwing * 0.5F);
            vec3d = vec3d.rotatePitch(-sinSwing * 0.7F);
            throwerSpotX = thrower.prevPosX + (thrower.posX - thrower.prevPosX) * partialTicks + vec3d.x;
            throwerSpotY = thrower.prevPosY + (thrower.posY - thrower.prevPosY) * partialTicks + vec3d.y;
            throwerSpotZ = thrower.prevPosZ + (thrower.posZ - thrower.prevPosZ) * partialTicks + vec3d.z;
            eyeHeight = thrower.getEyeHeight() - 0.06;
        } else {
            throwerSpotX = thrower.prevPosX + (thrower.posX - thrower.prevPosX) * partialTicks - cosThrowerYaw * handOffset - sinThrowerYaw * 0.8D;
            throwerSpotY = thrower.prevPosY + (double) thrower.getEyeHeight() + (thrower.posY - thrower.prevPosY) * (double) partialTicks - 0.45D;
            throwerSpotZ = thrower.prevPosZ + (thrower.posZ - thrower.prevPosZ) * partialTicks - sinThrowerYaw * handOffset + cosThrowerYaw * 0.8D;
            eyeHeight = thrower.isSneaking() ? -0.1875D : 0.0D;
        }

        double projSpotX = bound.prevPosX + (bound.posX - bound.prevPosX) * partialTicks;
        double projSpotY = bound.prevPosY + bound.height / 2 + (bound.posY - bound.prevPosY) * partialTicks + 0.25D;
        double projSpotZ = bound.prevPosZ + (bound.posZ - bound.prevPosZ) * partialTicks;
        double lengthX = (throwerSpotX - projSpotX);
        double lengthY = (throwerSpotY - projSpotY) + eyeHeight;
        double lengthZ = (throwerSpotZ - projSpotZ);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        int maxVertices = 1;

        for (int vertexCount = 0; vertexCount <= maxVertices; ++vertexCount) {
            float vertexIncrement = (float) vertexCount / (float) maxVertices;
            bufferbuilder.pos(bound.posX + lengthX * (double) vertexIncrement, bound.posY + bound.height / 2 + lengthY * (double) (vertexIncrement * vertexIncrement + vertexIncrement) * 0.5D + 0.25D, bound.posZ + lengthZ * (double) vertexIncrement).color(0, 0, 0, 255).endVertex();
            //bufferbuilder.pos(bound.posX + (double) vertexIncrement, bound.posY + (double) (vertexIncrement * vertexIncrement + vertexIncrement) * 0.5D + 0.25D, bound.posZ + (double) vertexIncrement).color(0, 0, 0, 255).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
//        Vec3d vec = bound.getPositionEyes((float) partialTicks);
//        Vec3d pvec = thrower.getPositionEyes((float) partialTicks);
//        double vx = vec.x;
//        double vy = vec.y;
//        double vz = vec.z;
//        double px = pvec.x;
//        double py = pvec.y;
//        double pz = pvec.z;
//
//        GL11.glPushMatrix();
//        GL11.glDisable(GL11.GL_LIGHTING);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        GL11.glDisable(GL11.GL_DEPTH_TEST);
//
//
//        GL11.glLineWidth(2);
//        GL11.glTranslated(-px, -py, -pz);
//        GL11.glColor3f(0.5f, 0.5f, 0.5f);
//
//        GL11.glEnable(GL11.GL_LINE_SMOOTH);
//        GL11.glHint( GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST );
//        GL11.glBegin(GL11.GL_LINE_STRIP);
//
//        GL11.glVertex3d(px, py, pz);
//        GL11.glVertex3d(vx, vy, vz);
//
//        GL11.glEnd();
//        //GL11.glEnable(GL11.GL_LIGHTING);
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        //GL11.glDisable(GL11.GL_LINE_SMOOTH);
//        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public static void color(ColorHandlerEvent.Item e) {
        //e.getItemColors().registerItemColorHandler(ProjectileTinter.INSTANCE, TaoItems.prop);
    }

    /**
     * thank you based coolAlias
     */
    @SubscribeEvent
    public static void zTarget(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player == null) return;
            ITaoStatCapability cap = TaoCasterData.getTaoCap(player);
            if (cap.getForcedLookAt() != null) {
                Entity e = cap.getForcedLookAt();
                double dx = player.posX - e.posX;
                double dz = player.posZ - e.posZ;
                double angle = Math.atan2(dz, dx) * 180 / Math.PI;
                double pitch = Math.atan2((player.posY + player.getEyeHeight()) - (e.posY + (e.height / 2f)), Math.sqrt(dx * dx + dz * dz)) * 180 / Math.PI;
                double distance = player.getDistance(e);
                float rYaw = (float) (angle - player.rotationYaw);
                while (rYaw > 180) {
                    rYaw -= 360;
                }
                while (rYaw < -180) {
                    rYaw += 360;
                }
                rYaw += 90F;
                float rPitch = (float) pitch - (float) (10.0F / Math.sqrt(distance)) + (float) (distance * Math.PI / 90);
                player.turn(rYaw, -(rPitch - player.rotationPitch));
            }
        } else {
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
        }
    }

    @SubscribeEvent
    public static void handRaising(RenderSpecificHandEvent e) {
        if (e.getHand().equals(EnumHand.MAIN_HAND)) return;
        AbstractClientPlayer p = Minecraft.getMinecraft().player;
        //cancel event so two handed weapons give a visual cue to their two-handedness
//        if (p.getHeldItemMainhand().getItem() instanceof ITwoHanded) {
//            if (((TaoWeapon) p.getHeldItemMainhand().getItem()).isTwoHanded(p.getHeldItemMainhand())) {
//                e.setCanceled(true);
//
//                return;
//            }
//        }
        //force offhand to have some semblance of cooldown
        if (!(e.getItemStack().getItem() instanceof TaoWeapon) && !TaoCombatUtils.isValidCombatItem(e.getItemStack()) && !TaoCombatUtils.isShield(e.getItemStack()))
            return;
        e.setCanceled(true);
        ItemRenderer ir = Minecraft.getMinecraft().getItemRenderer();
        float f1 = p.prevRotationPitch + (p.rotationPitch - p.prevRotationPitch) * e.getPartialTicks();
        //MathHelper.clamp((!requipM ? f * f * f : 0.0F) - this.equippedProgressMainHand, -0.4F, 0.4F);//mainhand add per
        float cd = TaoCombatUtils.getCooledAttackStrengthOff(p, e.getPartialTicks());
        float f6 = 1 - (cd * cd * cd);
        ir.renderItemInFirstPerson(p, e.getPartialTicks(), f1, EnumHand.OFF_HAND, e.getSwingProgress(), p.getHeldItemOffhand(), f6);
    }

    @SubscribeEvent
    public static void displayCoolie(RenderGameOverlayEvent.Post event) {
        ScaledResolution sr = event.getResolution();
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS)) {
            //draw offhand cooldown, crosshair type
            {
                GameSettings gamesettings = mc.gameSettings;

                if (gamesettings.thirdPersonView == 0) {
                    int width = sr.getScaledWidth();
                    int height = sr.getScaledHeight();

                    EntityPlayerSP player = mc.player;
                    if (!gamesettings.showDebugInfo || gamesettings.hideGUI || player.hasReducedDebug() || gamesettings.reducedDebugInfo) {
                        if (mc.gameSettings.attackIndicator == 1) {
                            GlStateManager.enableAlpha();
                            float cooldown = TaoCombatUtils.getCooledAttackStrengthOff(player, 0f);
                            boolean hyperspeed = false;

                            if (mc.pointedEntity instanceof EntityLivingBase && cooldown >= 1.0F) {
                                hyperspeed = TaoCombatUtils.getCooldownPeriodOff(player) > 5.0F;
                                hyperspeed = hyperspeed & (mc.pointedEntity).isEntityAlive();
                            }

                            int y = height / 2 - 7 - 7;
                            int x = width / 2 - 8;

                            if (hyperspeed) {
                                mc.ingameGUI.drawTexturedModalRect(x, y, 68, 94, 16, 16);
                            } else if (cooldown < 1.0F) {
                                int k = (int) (cooldown * 17.0F);
                                mc.ingameGUI.drawTexturedModalRect(x, y, 36, 94, 16, 4);
                                mc.ingameGUI.drawTexturedModalRect(x, y, 52, 94, k, 4);
                            }
                        }
                    }
                }
            }
        }
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.HOTBAR)) {
            //draw offhand cooldown, hotbar type
            if (mc.getRenderViewEntity() instanceof EntityPlayer) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                EntityPlayer p = (EntityPlayer) mc.getRenderViewEntity();
                ItemStack itemstack = p.getHeldItemOffhand();
                EnumHandSide oppositeHand = p.getPrimaryHand().opposite();
                int halfOfScreen = sr.getScaledWidth() / 2;

                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                RenderHelper.enableGUIStandardItemLighting();

                if (mc.gameSettings.attackIndicator == 2) {
                    float strength = TaoCombatUtils.getCooledAttackStrengthOff(p, 0);

                    if (strength < 1.0F) {
                        int y = sr.getScaledHeight() - 20;
                        int x = halfOfScreen + 91 + 6;

                        if (oppositeHand == EnumHandSide.LEFT) {
                            x = halfOfScreen - 91 - 22;
                        }

                        mc.getTextureManager().bindTexture(Gui.ICONS);
                        int modStrength = (int) (strength * 19.0F);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        mc.ingameGUI.drawTexturedModalRect(x, y, 0, 94, 18, 18);
                        mc.ingameGUI.drawTexturedModalRect(x, y + 18 - modStrength, 18, 112 - modStrength, 18, modStrength);
                    }
                }

                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
            }
        }


        if (event.getType().equals(RenderGameOverlayEvent.ElementType.ALL))
            if (mc.getRenderViewEntity() instanceof EntityPlayer) {
                GameSettings gamesettings = mc.gameSettings;
                EntityPlayerSP player = mc.player;
                ITaoStatCapability cap = TaoCasterData.getTaoCap(player);
                int width = sr.getScaledWidth();
                int height = sr.getScaledHeight();
                //if (gamesettings.thirdPersonView == 0) {
                mc.getTextureManager().bindTexture(hud);
                float targetQiLevel = cap.getQi();
                boolean closeEnough = true;
                if (targetQiLevel > currentQiLevel) {
                    currentQiLevel += Math.min(0.1, (targetQiLevel - currentQiLevel) / 20);
                    closeEnough = false;
                }
                if (targetQiLevel < currentQiLevel) {
                    currentQiLevel -= Math.min(0.1, (currentQiLevel - targetQiLevel) / 20);
                    closeEnough = !closeEnough;
                }
                if (closeEnough)
                    currentQiLevel = targetQiLevel;
                int qi = (int) (currentQiLevel);
                float qiExtra = currentQiLevel - qi;
                //System.out.println(currentQiLevel);
                //System.out.println(qi);
                if (qi != 0 || qiExtra != 0f) {
                    //render qi bar
                    GlStateManager.pushMatrix();
                    //GlStateManager.bindTexture(mc.renderEngine.getTexture(qibar).getGlTextureId());
                    //bar
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.enableAlpha();
                    //int c = GRADIENTE[MathHelper.clamp((int) (qiExtra *(GRADIENTE.length)), 0, GRADIENTE.length - 1)];
                    //GlStateManager.color(red(c), green(c), blue(c));
                    GlStateManager.color(1, 1, 1, qi > 0 ? 1 : qiExtra);
                    mc.ingameGUI.drawTexturedModalRect(Math.min(HudConfig.client.qi.x, width - 64), Math.min(HudConfig.client.qi.y, height - 64), 0, 0, 64, 64);//+(int)(qiExtra*32)
                    GlStateManager.popMatrix();

                    if (qi > 0) {
                        //overlay
                        GlStateManager.pushMatrix();
                        GlStateManager.color(qiExtra, qiExtra, qiExtra, qiExtra);
                        //GlStateManager.bindTexture(mc.renderEngine.getTexture(qihud[qi]).getGlTextureId());
                        mc.ingameGUI.drawTexturedModalRect(Math.min(HudConfig.client.qi.x, width - 64), Math.min(HudConfig.client.qi.y, height - 64), ((qi + 1) * 64) % 256, Math.floorDiv((qi + 1), 4) * 64, 64, 64);
                        //GlStateManager.resetColor();
                        //mc.renderEngine.bindTexture();
                        GlStateManager.popMatrix();

                        //overlay layer 2
                        GlStateManager.pushMatrix();
                        GlStateManager.color(1f, 1f, 1f);
                        //GlStateManager.bindTexture(mc.renderEngine.getTexture(qihud[qi]).getGlTextureId());
                        mc.ingameGUI.drawTexturedModalRect(Math.min(HudConfig.client.qi.x, width - 64), Math.min(HudConfig.client.qi.y, height - 64), (qi * 64) % 256, Math.floorDiv(qi, 4) * 64, 64, 64);
                        //GlStateManager.resetColor();
                        //mc.renderEngine.bindTexture();
                        GlStateManager.popMatrix();
                    }
                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }

                //render posture bar if not full
                if (cap.getPosture() < cap.getMaxPosture() || cap.getDownTimer() > 0)
                    drawPostureBarreAt(player, width / 2, height - 57);
                Entity look = getEntityLookedAt(player);
                if (look instanceof EntityLivingBase && HudConfig.client.displayEnemyPosture && (TaoCasterData.getTaoCap((EntityLivingBase) look).getPosture() < TaoCasterData.getTaoCap((EntityLivingBase) look).getMaxPosture() || TaoCasterData.getTaoCap((EntityLivingBase) look).getDownTimer() > 0)) {
                    drawPostureBarreAt((EntityLivingBase) look, width / 2, 20);//Math.min(HudConfig.client.enemyPosture.x, width - 64), Math.min(HudConfig.client.enemyPosture.y, height - 64));
                }
                //}
            }
    }

    /**
     * Draws it with the coord as its center
     *
     * @param elb
     * @param atX
     * @param atY
     */
    private static void drawPostureBarreAt(EntityLivingBase elb, int atX, int atY) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(hood);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(elb);
        mc.mcProfiler.startSection("postureBar");
        float cap = itsc.getMaxPosture();
        int left = atX - 91;
        float posPerc = MathHelper.clamp(itsc.getPosture() / itsc.getMaxPosture(), 0, 1);
        int c = GRADIENTDOWN[(int) (posPerc * (GRADIENTDOWN.length - 1))];
        if (itsc.isProtected()) c = GRADIENTSSP[(int) (posPerc * (GRADIENTSSP.length - 1))];
        if (cap > 0) {
            short barWidth = 182;
            int filled = (int) (itsc.getPosture() / itsc.getMaxPosture() * (float) (barWidth));
            int invulTime = (int) ((float) itsc.getPosInvulTime() / (float) CombatConfig.ssptime * (float) (barWidth));
            mc.ingameGUI.drawTexturedModalRect(left, atY, 0, 64, barWidth, 5);
            if (filled > invulTime) {
                GlStateManager.color(red(c), green(c), blue(c));
                mc.ingameGUI.drawTexturedModalRect(left, atY, 0, 69, filled, 5);
            }
            if (itsc.getDownTimer() > 0) {
                invulTime = (int) (MathHelper.clamp((float) itsc.getDownTimer() / (float) TaoStatCapability.MAXDOWNTIME, 0, 1) * (float) (barWidth));
                GlStateManager.color(0, 0, 0);//, ((float) itsc.getPosInvulTime()) / (float) CombatConfig.ssptime);
                mc.ingameGUI.drawTexturedModalRect(left, atY, 0, 69, invulTime, 5);
            } else {
                GlStateManager.color(1, 225f / 255f, 0);//, ((float) itsc.getPosInvulTime()) / (float) CombatConfig.ssptime);
                mc.ingameGUI.drawTexturedModalRect(left, atY, 0, 69, invulTime, 5);
            }
            if (filled <= invulTime) {
                GlStateManager.color(red(c), green(c), blue(c));
                mc.ingameGUI.drawTexturedModalRect(left, atY, 0, 69, filled, 5);
            }
        }
        mc.mcProfiler.endSection();
//        mc.mcProfiler.startSection("postureNumber");
//        float postureNumber = ((int) (itsc.getPosture() * 100)) / 100f;
//        String text = "" + postureNumber;
//        int x = atX - (mc.fontRenderer.getStringWidth(text) / 2);
//        int y = atY - 1;
//        mc.fontRenderer.drawString(text, x + 1, y, 0);
//        mc.fontRenderer.drawString(text, x - 1, y, 0);
//        mc.fontRenderer.drawString(text, x, y + 1, 0);
//        mc.fontRenderer.drawString(text, x, y - 1, 0);
//        mc.fontRenderer.drawString(text, x, y, c.getRGB());
//        mc.mcProfiler.endSection();
        mc.getTextureManager().bindTexture(Gui.ICONS);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static Entity getEntityLookedAt(Entity e) {
        Entity foundEntity = null;

        final double finalDistance = 16;
        double distance = finalDistance;
        RayTraceResult pos = raycast(e, finalDistance);

        Vec3d positionVector = e.getPositionVector();
        if (e instanceof EntityPlayer)
            positionVector = positionVector.addVector(0, e.getEyeHeight(), 0);

        if (pos != null)
            distance = pos.hitVec.distanceTo(positionVector);

        Vec3d lookVector = e.getLookVec();
        Vec3d reachVector = positionVector.addVector(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance);

        Entity lookedEntity = null;
        List<Entity> entitiesInBoundingBox = e.getEntityWorld().getEntitiesWithinAABBExcludingEntity(e, e.getEntityBoundingBox().grow(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance).expand(1F, 1F, 1F));
        double minDistance = distance;

        for (Entity entity : entitiesInBoundingBox) {
            if (entity.canBeCollidedWith()) {
                float collisionBorderSize = entity.getCollisionBorderSize();
                AxisAlignedBB hitbox = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                RayTraceResult interceptPosition = hitbox.calculateIntercept(positionVector, reachVector);

                if (hitbox.contains(positionVector)) {
                    if (0.0D < minDistance || minDistance == 0.0D) {
                        lookedEntity = entity;
                        minDistance = 0.0D;
                    }
                } else if (interceptPosition != null) {
                    double distanceToEntity = positionVector.distanceTo(interceptPosition.hitVec);

                    if (distanceToEntity < minDistance || minDistance == 0.0D) {
                        lookedEntity = entity;
                        minDistance = distanceToEntity;
                    }
                }
            }

            if (lookedEntity != null && (minDistance < distance || pos == null))
                foundEntity = lookedEntity;
        }

        return foundEntity;
    }

    private static float red(int a) {
        return BinaryMachiavelli.getInteger(a, 16, 23) / 255f;
    }

    private static float green(int a) {
        return BinaryMachiavelli.getInteger(a, 8, 15) / 255f;
    }

	/*@SubscribeEvent
	public static void colorize(ColorHandlerEvent event){
		Taoism.logger.debug("this is being called");
	}*/

    /*public static void draw(int x, int y, int textureX, int textureY, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, (double)this.zLevel).tex((float)(textureX + 0) * 0.00390625F, (float)(textureY + height) * 0.00390625F).endVertex();
        bufferbuilder.pos(x + width, y + height, (double)this.zLevel).tex((float)(textureX + width) * 0.00390625F, (float)(textureY + height) * 0.00390625F).endVertex();
        bufferbuilder.pos(x + width, y + 0, (double)this.zLevel).tex((float)(textureX + width) * 0.00390625F, (float)(textureY + 0) * 0.00390625F).endVertex();
        bufferbuilder.pos(x, y + 0, (double)this.zLevel).tex((float)(textureX + 0) * 0.00390625F, (float)(textureY + 0) * 0.00390625F).endVertex();
        tessellator.draw();
    }*/

    private static float blue(int a) {
        return BinaryMachiavelli.getInteger(a, 0, 7) / 255f;
    }

    public static RayTraceResult raycast(Entity e, double len) {
        Vec3d vec = new Vec3d(e.posX, e.posY, e.posZ);
        if (e instanceof EntityPlayer)
            vec = vec.add(new Vec3d(0, e.getEyeHeight(), 0));

        Vec3d look = e.getLookVec();
        if (look == null)
            return null;

        return raycast(e.getEntityWorld(), vec, look, len);
    }

    public static RayTraceResult raycast(World world, Vec3d origin, Vec3d ray, double len) {
        Vec3d end = origin.add(ray.normalize().scale(len));
        RayTraceResult pos = world.rayTraceBlocks(origin, end);
        return pos;
    }

    private static void drawPostureBarAt(EntityLivingBase elb, int x, int y) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(hud);
        ITaoStatCapability cap = TaoCasterData.getTaoCap(elb);
        GlStateManager.pushMatrix();
        float posPerc = MathHelper.clamp(cap.getPosture() / cap.getMaxPosture(), 0, 1);
        int down = cap.getDownTimer();
        if (down <= 0) {
            //bar, not rendered if down because that don't make sense
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            int c = GRADIENTSSP[(int) (posPerc * (GRADIENTSSP.length - 1))];
            GlStateManager.color(red(c), green(c), blue(c));
            mc.ingameGUI.drawTexturedModalRect(x, y + (int) ((1 - posPerc) * 64), 128, 128, 64, (int) (posPerc * 64));//+(int)(qiExtra*32)
            GlStateManager.popMatrix();
            //base shield layer
            GlStateManager.pushMatrix();
            GlStateManager.color(1f, 1f, 1f);
            mc.ingameGUI.drawTexturedModalRect(x, y, 0, 192, 64, 64);
            GlStateManager.popMatrix();
            //ssp
            if (cap.isProtected()) {
                GlStateManager.pushMatrix();
                mc.ingameGUI.drawTexturedModalRect(x, y, 192, 128, 64, 64);
                GlStateManager.popMatrix();
            }
            //cracks (four variations)
            GlStateManager.pushMatrix();
            mc.ingameGUI.drawTexturedModalRect(x, y, 64, 192, 64, (int) Math.ceil((1 - posPerc) * 4) * 16);
            GlStateManager.popMatrix();
            //resolution
            if (cap.getPosInvulTime() > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1f, 1f, 1f, ((float) cap.getPosInvulTime()) / (float) CombatConfig.ssptime);
                mc.ingameGUI.drawTexturedModalRect(x, y, 192, 192, 64, 64);
                GlStateManager.popMatrix();
            }
        } else {
            //broken shield base
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.color(1f, 1f, 1f);
            mc.ingameGUI.drawTexturedModalRect(x, y, 128, 192, 64, 64);
            GlStateManager.popMatrix();
            //percentage to restore
            float downPercent = (float) down / (float) TaoStatCapability.MAXDOWNTIME;
            GlStateManager.pushMatrix();
            mc.ingameGUI.drawTexturedModalRect(x, y + (int) ((downPercent) * 64), 0, 192 + ((int) (downPercent * 64)), 64, (int) ((1 - downPercent) * 64));
            mc.ingameGUI.drawTexturedModalRect(x, y + (int) ((downPercent) * 64), 64, 192 + ((int) (downPercent * 64)), 64, (int) ((1 - downPercent) * 64));
            GlStateManager.disableAlpha();
            GlStateManager.popMatrix();
        }
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
        mc.getTextureManager().bindTexture(Gui.ICONS);
    }
}

/*
一元
两仪
三才
四象
五行
六合
七星
八卦
九宫

一闪
双煞
三皇
四相
五行
六道
七星
八卦
九鼎
 */
