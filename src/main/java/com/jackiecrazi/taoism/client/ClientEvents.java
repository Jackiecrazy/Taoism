package com.jackiecrazi.taoism.client;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.MoveCode;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITwoHanded;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.capability.TaoStatCapability;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import com.jackiecrazi.taoism.networking.PacketBeginParry;
import com.jackiecrazi.taoism.networking.PacketDodge;
import com.jackiecrazi.taoism.networking.PacketMakeMove;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.awt.*;
import java.lang.reflect.Field;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Taoism.MODID)
public class ClientEvents {

    //Reflection time!
    public static final Field zaHando = ObfuscationReflectionHelper.findField(ItemRenderer.class, "field_187471_h");
    public static final Field okuyasu = ObfuscationReflectionHelper.findField(ItemRenderer.class, "field_187472_i");
    private static final int ALLOWANCE = 7;
    private static final Color[] GRADIENT = {
            new Color(200, 37, 56),
            new Color(177, 52, 51),
            new Color(141, 71, 43),
            new Color(103, 94, 36),
            new Color(69, 115, 30),
            new Color(46, 127, 24),

    };
    private static final ResourceLocation hud = new ResourceLocation(Taoism.MODID, "textures/hud/spritesheet.png");
    /**
     * left, back, right
     */
    private static long[] lastTap = {0, 0, 0};
    private static boolean[] tapped = {false, false, false};
    private static boolean sneak = false;

    @SubscribeEvent
    public static void model(ModelRegistryEvent e) {
        for (Item i : TaoWeapon.listOfWeapons) {
            regWeap(i);
        }
    }

    private static void regWeap(Item i) {
        ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public static void down(RenderLivingEvent.Pre event) {
        Tuple<Float, Float> sizes = new Tuple<>(event.getEntity().width, event.getEntity().height);//TaoCasterData.getTaoCap(event.getEntity()).getPrevSizes();
        if (TaoCasterData.getTaoCap(event.getEntity()).getDownTimer() > 0 && event.getEntity().isEntityAlive()) {
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
            if (sizes.getFirst() > sizes.getSecond()) {//sizes.getFirst().equals(sizes.getSecond())&&sizes.getFirst()==0 //this means it didn't update, which happens when there's nothing to change, i.e. you're flat already
                GlStateManager.translate(event.getX(), event.getY(), event.getZ());
                //GlStateManager.rotate(180f, 0, 0, 0);
                //GlStateManager.rotate(180f, 0, 1, 0);
                //GlStateManager.rotate(event.getEntity().renderYawOffset, 0, 0, 1);
                //GlStateManager.rotate(event.getEntity().renderYawOffset, 0, 1, 0);
                GlStateManager.translate(-event.getX(), -event.getY() - event.getEntity().height / 2, -event.getZ());
            }
            //multi bois do nothing
        }
    }

    @SubscribeEvent
    public static void downEnd(RenderLivingEvent.Post event) {
        if (TaoCasterData.getTaoCap(event.getEntity()).getDownTimer() > 0 && event.getEntity().isEntityAlive()) {//TaoCasterData.getTaoCap(event.getEntity()).getDownTimer()>0
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public static void modelThePlayer(RenderPlayerEvent.Pre e) {
//        EntityPlayer p = e.getEntityPlayer();
//        e.getRenderer().getMainModel().bipedRightArm.rotateAngleX += 37;
        //what was I doing? I can't remember...
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void doju(InputUpdateEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        MovementInput mi = e.getMovementInput();
        if (mi.leftKeyDown && !tapped[0]) {
            if (mc.world.getTotalWorldTime() - lastTap[0] <= ALLOWANCE) {
                Taoism.net.sendToServer(new PacketDodge(0));
            }
            lastTap[0] = mc.world.getTotalWorldTime();
        }
        tapped[0] = mi.leftKeyDown;
        if (mi.backKeyDown && !tapped[1]) {
            if (mc.world.getTotalWorldTime() - lastTap[1] <= ALLOWANCE) {
                Taoism.net.sendToServer(new PacketDodge(1));
            }
            lastTap[1] = mc.world.getTotalWorldTime();
        }
        tapped[1] = mi.backKeyDown;
        if (mi.rightKeyDown && !tapped[2]) {
            if (mc.world.getTotalWorldTime() - lastTap[2] <= ALLOWANCE) {
                Taoism.net.sendToServer(new PacketDodge(2));
            }
            lastTap[2] = mc.world.getTotalWorldTime();
        }
        tapped[2] = mi.rightKeyDown;
        if (mi.sneak && !sneak) {
            //if(mc.world.getTotalWorldTime()-lastSneak<=ALLOWANCE){
            Taoism.net.sendToServer(new PacketBeginParry());
            //}
        }
        sneak = mi.sneak;

        if (TaoCasterData.getTaoCap(mc.player).getDownTimer() > 0) {
            //no moving while you're down! (except for a safety roll)
            KeyBinding.unPressAllKeys();
        }
    }

    @SubscribeEvent
    public static void updateMove(MouseEvent e) {
        if (!e.isButtonstate()) return;
        GameSettings gs = Minecraft.getMinecraft().gameSettings;
        MoveCode move = new MoveCode(true, gs.keyBindForward.isKeyDown(), gs.keyBindBack.isKeyDown(), gs.keyBindLeft.isKeyDown(), gs.keyBindRight.isKeyDown(), gs.keyBindJump.isKeyDown(), gs.keyBindSneak.isKeyDown(), e.getButton() == 0);
        Taoism.net.sendToServer(new PacketMakeMove(move));
    }

    @SubscribeEvent
    public static void handRaising(RenderSpecificHandEvent e) {
        if (e.getHand().equals(EnumHand.MAIN_HAND)) return;
        AbstractClientPlayer p = Minecraft.getMinecraft().player;
        //cancel event so two handed weapons give a visual cue to their two-handedness
        if (p.getHeldItemMainhand().getItem() instanceof ITwoHanded) {
            if (((TaoWeapon) p.getHeldItemMainhand().getItem()).isTwoHanded(p.getHeldItemMainhand())) {
                e.setCanceled(true);

                return;
            }
        }
        //force offhand to have some semblance of cooldown
        if (!(e.getItemStack().getItem() instanceof TaoWeapon)) return;
        e.setCanceled(true);
        ItemRenderer ir = Minecraft.getMinecraft().getItemRenderer();
        float f1 = p.prevRotationPitch + (p.rotationPitch - p.prevRotationPitch) * e.getPartialTicks();
        //MathHelper.clamp((!requipM ? f * f * f : 0.0F) - this.equippedProgressMainHand, -0.4F, 0.4F);//mainhand add per
        float cd = NeedyLittleThings.getCooledAttackStrengthOff(p, e.getPartialTicks());
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
                            float cooldown = NeedyLittleThings.getCooledAttackStrengthOff(player, 0f);
                            boolean hyperspeed = false;

                            if (mc.pointedEntity instanceof EntityLivingBase && cooldown >= 1.0F) {
                                hyperspeed = NeedyLittleThings.getCooldownPeriodOff(player) > 5.0F;
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
                    float strength = NeedyLittleThings.getCooledAttackStrengthOff(p, 0);

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
                if (gamesettings.thirdPersonView == 0) {
                    mc.getTextureManager().bindTexture(hud);
                    int qi = cap.getQiFloored();
                    float qiExtra = cap.getQi() - cap.getQiFloored();
                    if (qi != 0 || qiExtra != 0f) {
                        //render qi bar
                        GlStateManager.pushMatrix();
                        //GlStateManager.bindTexture(mc.renderEngine.getTexture(qibar).getGlTextureId());
                        //bar
                        GlStateManager.pushMatrix();
                        GlStateManager.enableAlpha();
                        Color c = GRADIENT[(int) (qiExtra * (GRADIENT.length))];
                        GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
                        mc.ingameGUI.drawTexturedModalRect(width - 96, height / 2 - 64 + (int) ((1 - qiExtra) * 64), 128, 128, 64, (int) (qiExtra * 64));//+(int)(qiExtra*32)
                        GlStateManager.popMatrix();

                        //overlay
                        GlStateManager.pushMatrix();
                        GlStateManager.color(1f, 1f, 1f);
                        //GlStateManager.bindTexture(mc.renderEngine.getTexture(qihud[qi]).getGlTextureId());
                        mc.ingameGUI.drawTexturedModalRect(width - 96, height / 2 - 64, (qi * 64) % 256, Math.floorDiv(qi, 4) * 64, 64, 64);
                        //GlStateManager.resetColor();
                        //mc.renderEngine.bindTexture();
                        GlStateManager.disableAlpha();
                        GlStateManager.popMatrix();
                        GlStateManager.popMatrix();
                    }

                    //render posture bar
                    GlStateManager.pushMatrix();
                    float posPerc = cap.getPosture() / cap.getMaxPosture();
                    int down = cap.getDownTimer();
                    if (down <= 0) {
                        //bar, not rendered if down because that don't make sense
                        GlStateManager.pushMatrix();
                        GlStateManager.enableAlpha();
                        Color c = GRADIENT[(int) (posPerc * (GRADIENT.length - 1))];
                        GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
                        mc.ingameGUI.drawTexturedModalRect(width - 96, height / 2 + (int) ((1 - posPerc) * 64), 128, 128, 64, (int) (posPerc * 64));//+(int)(qiExtra*32)
                        GlStateManager.popMatrix();
                        //base shield layer
                        GlStateManager.pushMatrix();
                        GlStateManager.color(1f, 1f, 1f);
                        mc.ingameGUI.drawTexturedModalRect(width - 96, height / 2, 0, 192, 64, 64);
                        GlStateManager.popMatrix();
                        //ssp
                        if (cap.isProtected()) {
                            GlStateManager.pushMatrix();
                            mc.ingameGUI.drawTexturedModalRect(width - 96, height / 2, 192, 128, 64, 64);
                            GlStateManager.popMatrix();
                        }
                        //cracks (four variations)
                        GlStateManager.pushMatrix();
                        mc.ingameGUI.drawTexturedModalRect(width - 96, height / 2, 64, 192, 64, (int) Math.ceil((1 - posPerc) * 4) * 16);
                        GlStateManager.popMatrix();
                        //resolution
                        if (cap.getPosInvulTime() > 0) {
                            GlStateManager.pushMatrix();
                            mc.ingameGUI.drawTexturedModalRect(width - 96, height / 2, 192, 192, 64, 64);
                            GlStateManager.popMatrix();
                        }
                    } else {
                        //broken shield base
                        GlStateManager.pushMatrix();
                        GlStateManager.color(1f, 1f, 1f);
                        mc.ingameGUI.drawTexturedModalRect(width - 96, height / 2, 128, 192, 64, 64);
                        GlStateManager.popMatrix();
                        //percentage to restore
                        float downPercent = (float) down / (float) TaoStatCapability.MAXDOWNTIME;
                        GlStateManager.pushMatrix();
                        mc.ingameGUI.drawTexturedModalRect(width - 96, height / 2 + (int) ((downPercent) * 64), 0, 192 + ((int) (downPercent * 64)), 64, (int) ((1 - downPercent) * 64));
                        mc.ingameGUI.drawTexturedModalRect(width - 96, height / 2 + (int) ((downPercent) * 64), 64, 192 + ((int) (downPercent * 64)), 64, (int) ((1 - downPercent) * 64));
                        GlStateManager.popMatrix();
                    }
                    GlStateManager.disableAlpha();
                    GlStateManager.popMatrix();
                    mc.getTextureManager().bindTexture(Gui.ICONS);
                }
            }
    }

	/*@SubscribeEvent
	public static void colorize(ColorHandlerEvent event){
		Taoism.logger.debug("this is being called");
	}*/

    /*public static void drawDrawFullscreenImage(int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, (double)height, -90.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV((double)width, (double)height, -90.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV((double)width, 0.0D, -90.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
        tessellator.draw();
    }*/
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
