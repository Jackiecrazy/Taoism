package com.Jackiecrazi.taoism.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.TaoisticPotions;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ICustomRange;
import com.Jackiecrazi.taoism.common.taoistichandlers.PlayerResourceStalker;
import com.Jackiecrazi.taoism.networking.PacketExtendThyReach;
import com.Jackiecrazi.taoism.networking.PacketOpenTPInv;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {
	public ClientEventHandler(){
		
	}
	Object[] binds;
	protected Minecraft mc;
	@SubscribeEvent
	public void pressAButton(KeyInputEvent e){
		mc=Minecraft.getMinecraft();
		binds=KeyBinding.getKeybinds().toArray();
		/*if (Mouse.isKeyDown(mc.gameSettings.keyBindAttack.getKeyCode())
				&& mc.thePlayer.getHeldItem() != null
				&& mc.thePlayer.getHeldItem().getItem() instanceof GenericTaoistWeapon) {
			EntityPlayer player=mc.thePlayer;
			ItemStack stack=mc.thePlayer.getHeldItem();
			AnimationStalker.getThis(player)
			.updateAnimation(
					true,
					false,
					stack,
					(player.isAirBorne ? 2
							: player.isSneaking() ? 1 : 0),
					player.inventory.currentItem);
		}*///Well, the mouse is not part of the keyboard...
		if(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));//detected pressing the jump key in air. You can use this to break concentration or double-jump
		if(Keyboard.isKeyDown(mc.gameSettings.keyBindInventory.getKeyCode())&&mc.player.isSneaking()){
			Taoism.net.sendToServer(new PacketOpenTPInv(mc.player,mc.currentScreen==null));
		}
	}
	
	@SubscribeEvent
	public void clickAButton(MouseEvent e) {
		if (e.isButtonstate() && e.getButton() == 0) // Note: without the
											// swingprogressint will
											// automatically attack. Potential
											// feature?
		{
			
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player != null) {
				//System.out.println("I broke your concentration");
				if(PlayerResourceStalker.get(player).getIsMeditating()){
					PlayerResourceStalker.get(player).setIsMeditating(false);
					//System.out.println("I broke your concentration");
					return;
				}
				/*if (player.attackTime != 0&&WayofConfig.CDEnabled){
					e.setCanceled(true);
					return;
				}*/
				ItemStack itemstack = player.getHeldItemMainhand();
				ICustomRange rangedItem = null;
				if (itemstack != null) {
					if (itemstack.getItem() instanceof ICustomRange) {
						rangedItem = (ICustomRange) itemstack.getItem();
					}

					if (rangedItem != null) {
						float reach = rangedItem.getRange(player, itemstack);
						RayTraceResult mov = ExtendThyReachHelper
								.getMouseOver(0, reach);

						if (mov != null && mov.entityHit != null
								&& mov.entityHit != player
								&& mov.entityHit.hurtResistantTime == 0) {
							Taoism.net.sendToServer(new PacketExtendThyReach(
									mov.entityHit.getEntityId(),true));
						}
					}
				}
			}
		}
		if(e.isButtonstate()&&e.getButton()==1){
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player != null) {
				if(player.isSneaking()&&player.onGround&&!PlayerResourceStalker.get(player).getIsMeditating()&&player.getHeldItem(player.swingingHand)==null){
					PlayerResourceStalker.get(player).setIsMeditating(true);
					//System.out.println("start meditate");
				}
			}
		}
	}

	@SubscribeEvent
	public void renderPlayerShenanigansStart(RenderPlayerEvent.Pre e) {
		//RenderPlayer rp = e.getRenderer();
		//RenderHalper.renderColoredSphere((int)(Math.sin(NeedyLittleThings.rad(e.entityPlayer.ticksExisted))+1)*125,(int) e.entityPlayer.cameraYaw%255, (int) e.entityPlayer.cameraPitch%255, 0.4f, 0, 0, 0, 3f);
		if(e.getEntityPlayer().isPotionActive(TaoisticPotions.Hide)){
			//
			int amp=e.getEntityPlayer().getActivePotionEffect(TaoisticPotions.Hide).getAmplifier();
			//System.out.println(amp);
			if(amp<-3)e.setCanceled(true);
		}
		
		/*RenderPlayer rp = e.renderer;
		if (e.entityPlayer.getHeldItem() != null
				&& e.entityPlayer.getHeldItem().getItem() instanceof GenericTaoistWeapon) {
			
			rp.modelBipedMain.bipedLeftArm.isHidden = true;
			rp.modelBipedMain.bipedRightArm.isHidden = true;
			// TODO one day, I'll fix the third person render.
		}
		else{
			rp.modelBipedMain.bipedLeftArm.isHidden=false;
			rp.modelBipedMain.bipedRightArm.isHidden=false;
		}*/
		//do a full body rotate when holding polearms
	}
	@SubscribeEvent
	public void renderPlayerShenanigansEnd(RenderPlayerEvent.Post e) {
		//RenderPlayer rp=e.getRenderer();
			//boolean frlse = false;

			/*rp.modelArmor.bipedBody.isHidden=frlse;
			rp.modelArmorChestplate.bipedBody.isHidden=frlse;
			rp.modelArmorChestplate.bipedHeadwear.isHidden=frlse;
			rp.modelArmorChestplate.bipedRightLeg.isHidden=frlse;
			rp.modelArmorChestplate.bipedLeftLeg.isHidden=frlse;
			rp.modelArmorChestplate.bipedLeftArm.isHidden=frlse;
			rp.modelArmorChestplate.bipedRightArm.isHidden=frlse;
			rp.modelArmor.bipedLeftLeg.isHidden=frlse;
			rp.modelArmor.bipedRightLeg.isHidden=frlse;
			rp.modelArmor.bipedHeadwear.isHidden=frlse;
			rp.modelArmor.bipedHead.isHidden=frlse;
			rp.modelArmor.bipedEars.isHidden=frlse;
			rp.modelArmorChestplate.bipedHead.isHidden=frlse;*/
			
	}
	/*@SubscribeEvent
	  public void renderOverlay(RenderGameOverlayEvent.Post event)
	  {
	    if ((event.isCanceled()) || (event.type != RenderGameOverlayEvent.ElementType.HELMET) || (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)) {
	      return;
	    }
	      drawOverlay();
	  }
	public static void drawOverlay()
	  {
		
	  }*/
	
}
