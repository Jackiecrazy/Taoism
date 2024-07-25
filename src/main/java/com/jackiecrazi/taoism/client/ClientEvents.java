package com.jackiecrazi.taoism.client;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.BinaryMachiavelli;
import com.jackiecrazi.taoism.api.MoveCode;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    private static final ResourceLocation hud = new ResourceLocation(Taoism.MODID, "textures/hud/yeet.png");
    private static final ResourceLocation newdark = new ResourceLocation(Taoism.MODID, "textures/hud/mega.png");
    private static float currentQiLevel = 0;

    private static float blue(int a) {
        return BinaryMachiavelli.getInteger(a, 0, 7) / 255f;
    }

    @SubscribeEvent
    public static void color(ColorHandlerEvent.Item e) {
        //e.getItemColors().registerItemColorHandler(ProjectileTinter.INSTANCE, TaoItems.prop);
    }

    @SubscribeEvent
    public static void squish(ItemTooltipEvent e){
        List<String> tooltip=e.getToolTip();
        if(TaoCombatUtils.isValidCombatItem(e.getItemStack())) {
            if (KeyBindOverlord.isShiftDown()) {
                tooltip.add(TextFormatting.YELLOW + I18n.format("taoism.weaponDefMult", TaoCombatUtils.postureDef(e.getEntityPlayer(), null, e.getItemStack(), 0)) + TextFormatting.RESET);
                tooltip.add(TextFormatting.RED + I18n.format("taoism.weaponAttMult", TaoCombatUtils.postureAtk(null, e.getEntityPlayer(), e.getItemStack(), 1)) + TextFormatting.RESET);
                if(TaoCombatUtils.isShield(e.getItemStack()))
                    tooltip.add(TextFormatting.GREEN + I18n.format("weapon.shield"));

            } else {
                tooltip.add(I18n.format(TextFormatting.ITALIC + "weapon.shift"+TextFormatting.RESET));
            }
        }
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
                    drawNewDarkPostureBarAt(player, width / 2, height - 57);
                Entity look = getEntityLookedAt(player);
                if (look instanceof EntityLivingBase && HudConfig.client.displayEnemyPosture) {
                    ITaoStatCapability cappy = TaoCasterData.getTaoCap((EntityLivingBase) look);
                    if (cappy.getPosture() < cappy.getMaxPosture() || cappy.getDownTimer() > 0)
                        drawNewDarkPostureBarAt((EntityLivingBase) look, width / 2, 20);//Math.min(HudConfig.client.enemyPosture.x, width - 64), Math.min(HudConfig.client.enemyPosture.y, height - 64));
                }
                //}
            }
    }

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

    private static void drawNewDarkPostureBarAt(EntityLivingBase elb, int width, int height) {
        int atX = width;
        int atY = height;
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(newdark);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(elb);
        mc.mcProfiler.startSection("postureBar");
        float cap = itsc.getMaxPosture();
        //182
        //so we want the full size to be 240, 25 to be 125, and every 5 max posture changes this by 20 pixels
        int halfBarWidth = Math.min(240, (int) (Math.sqrt(itsc.getMaxPosture()) * 25)) / 2;
        //divvy by 2 for two-pronged approach
        int flexBarWidth = halfBarWidth + 3;
        final int barHeight = 12;
        //double shatter = MathHelper.clamp(itsc.getBarrier() / itsc.getMaxBarrier(), 0, 1);
        if (cap > 0) {
            //draw working bracket
            final int barY = atY - barHeight / 2;
            mc.ingameGUI.drawTexturedModalRect(atX, barY, 243 - flexBarWidth, 0, flexBarWidth, barHeight);
            mc.ingameGUI.drawTexturedModalRect(atX - flexBarWidth, barY, 0, 0, flexBarWidth, barHeight);
            //grayscale and change width if staggered
            if (itsc.getDownTimer() > 0) {
                flexBarWidth = (int) ((itsc.getDownTimer()) * flexBarWidth / (float) TaoStatCapability.MAXDOWNTIME) + 3;
                mc.ingameGUI.drawTexturedModalRect(atX, barY, 243 - flexBarWidth, 24, flexBarWidth, barHeight);
                mc.ingameGUI.drawTexturedModalRect(atX - flexBarWidth, barY, 0, 24, flexBarWidth, barHeight);
            }/*else if (itsc.getStunTime() > 0) {
                flexBarWidth = (int) ((itsc.getStunTime()) * flexBarWidth / (float) itsc.getMaxStunTime()) + 3;
                mc.ingameGUI.drawTexturedModalRect(atX, barY, 243 - flexBarWidth, 24, flexBarWidth, barHeight);
                mc.ingameGUI.drawTexturedModalRect(atX - flexBarWidth, barY, 0, 24, flexBarWidth, barHeight);
            }*/ else {
                //otherwise draw normal posture
                flexBarWidth = (int) ((itsc.getMaxPosture() - itsc.getPosture()) * halfBarWidth / itsc.getMaxPosture()) + 3;
                mc.ingameGUI.drawTexturedModalRect(atX, barY, 243 - flexBarWidth, 12, flexBarWidth, barHeight);
                mc.ingameGUI.drawTexturedModalRect(atX - flexBarWidth, barY, 0, 12, flexBarWidth, barHeight);
            }
            // render fracture if present
            /*if (itsc.getMaxFracture() > 0) {
                if (itsc.getFractureCount() > 0) {//shattering
                    float otemp = (float) itsc.getFractureCount() / itsc.getMaxFracture();
                    int fini = (int) (otemp * halfBarWidth);
                    int shatterV = Math.min(36 + (int) (otemp * 2.8) * 12, 60);
                    //gold that stretches out to the edges before disappearing
                    mc.ingameGUI.drawTexturedModalRect(atX + 5, atY - barHeight / 2, 243 - fini, shatterV, fini, barHeight);
                    mc.ingameGUI.drawTexturedModalRect(atX - fini - 5, atY - barHeight / 2, 0, shatterV, fini, barHeight);
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                }
            }*/
            //render insignia
            /* {
                int insigniaWH = 24;
                int iconW = 12, iconH = 10;
                //normal, use green
                int statusU = 0, statusV = 83;
                int iconU = 0, iconV = 72;
                //unsteady override
               if (elb.hasEffect(FootworkEffects.UNSTEADY.get()))
                    iconU = 12;
                //danger, use red
                if (itsc.getDownTimer() > 0 && itsc.getDownCount() > 0) {
                    statusU = 36;
                }
                if (itsc.isVulnerable()) {
                    statusU = 48;
                    if (itsc.isStunned())
                        iconU = 24;
                    if (itsc.isExposed())
                        iconU = 36;
                }
                //recharge, use yellow
                else if (itsc.getDownTimer() != 0 || itsc.getDownCount() != 0) {
                    statusU = 48;
                    iconU = 12;
                }
                //draw status color
                mc.ingameGUI.drawTexturedModalRect(atX - insigniaWH / 2, atY - insigniaWH / 2, statusU, statusV, insigniaWH, insigniaWH);
                //double evasionPerc = Math.min(1, itsc.getEvade() * 1d / CombatCapability.EVADE_CHARGE);
                //draw status icon
                mc.ingameGUI.drawTexturedModalRect(atX - iconW / 2, atY - iconH / 2, iconU, iconV, iconW, iconH);
                //draw evasion
//                statusU = 72;
//                mc.ingameGUI.drawTexturedModalRect(atX - insigniaWH / 2, atY + insigniaWH / 2 - (int) (insigniaWH * evasionPerc), statusU, statusV + insigniaWH - (int) (insigniaWH * evasionPerc), insigniaWH, (int) (insigniaWH * evasionPerc));
                //draw evasion animation when needed
                if (you) {
                    //filled up, start animation frames
                    if (scurrentEvasion != (float) evasionPerc && (evasionPerc == 1 || evasionPerc < scurrentEvasion)) {
                        snewDarkAnimFrames = (int) (56 * (evasionPerc - 0.5));
                    }
                    scurrentEvasion = (float) evasionPerc;
                    statusV = 107;
                    if (snewDarkAnimFrames > 0) {
                        statusU = (4 - (snewDarkAnimFrames / 3)) * 24;
                        mc.ingameGUI.drawTexturedModalRect(atX - insigniaWH / 2, atY - insigniaWH / 2, statusU, statusV, insigniaWH, insigniaWH);
                        snewDarkAnimFrames--;
                    } else if (snewDarkAnimFrames < 0) {
                        statusU = (7 + (snewDarkAnimFrames / 4)) * 24;
                        statusV = 131;
                        mc.ingameGUI.drawTexturedModalRect(atX - insigniaWH / 2, atY - insigniaWH / 2, statusU, statusV, insigniaWH, insigniaWH);
                        snewDarkAnimFrames++;
                    }
                } else {
                    //filled up, start animation frames
                    if (lcurrentEvasion != (float) evasionPerc && (evasionPerc == 1 || evasionPerc < lcurrentEvasion)) {
                        lnewDarkAnimFrames = (int) (56 * (evasionPerc - 0.5));
                    }
                    lcurrentEvasion = (float) evasionPerc;
                    statusV = 107;
                    if (lnewDarkAnimFrames > 0) {
                        statusU = (4 - (lnewDarkAnimFrames / 3)) * 24;
                        mc.ingameGUI.drawTexturedModalRect(atX - insigniaWH / 2, atY - insigniaWH / 2, statusU, statusV, insigniaWH, insigniaWH);
                        lnewDarkAnimFrames--;
                    } else if (lnewDarkAnimFrames < 0) {
                        statusU = (7 + (lnewDarkAnimFrames / 4)) * 24;
                        statusV = 131;
                        mc.ingameGUI.drawTexturedModalRect(atX - insigniaWH / 2, atY - insigniaWH / 2, statusU, statusV, insigniaWH, insigniaWH);
                        lnewDarkAnimFrames++;
                    }
                }
            }*/
        }
        mc.mcProfiler.endSection();
        mc.getTextureManager().bindTexture(Gui.ICONS);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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


    public static Entity getEntityLookedAt(Entity e) {
        return getEntityLookedAt(e, 16);
    }

    public static Entity getEntityLookedAt(Entity e, double finalDistance) {
        Entity foundEntity = null;

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

    private static float green(int a) {
        return BinaryMachiavelli.getInteger(a, 8, 15) / 255f;
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

    @SubscribeEvent
    public static void model(ModelRegistryEvent e) {
        for (Item i : TaoWeapon.listOfWeapons) {
            regWeap(i);
        }
        regWeap(TaoItems.prop);
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

    private static float red(int a) {
        return BinaryMachiavelli.getInteger(a, 16, 23) / 255f;
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

    private static void regWeap(Item i) {
        ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory"));
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
            InputEvents.leftClickAt = InputEvents.rightClickAt = 0;
        }
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
