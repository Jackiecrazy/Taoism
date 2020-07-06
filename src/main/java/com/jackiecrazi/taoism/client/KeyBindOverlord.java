package com.jackiecrazi.taoism.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyBindOverlord {
	public static KeyBinding combatMode= new KeyBinding("key.taoism.combat", Keyboard.KEY_BACKSLASH, "key.categories.movement");

	public static boolean isShiftDown() {

	    return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	  }

	  public static boolean isControlDown() {

	    return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
	  }

	  public static boolean isAltDown() {

	    return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
	  }
}
