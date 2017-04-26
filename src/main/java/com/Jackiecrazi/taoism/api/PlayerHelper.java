package com.Jackiecrazi.taoism.api;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class PlayerHelper {

	public static boolean decrease(int i, EntityPlayer player) {
		boolean didit = false;
		if(player.capabilities.isCreativeMode)return true;
		if (player.getHeldItemMainhand() != null) {
			ItemStack playerItem = player.getHeldItemMainhand();
			int jack = playerItem.getCount();
			if (jack > i) {
				playerItem.setCount(playerItem.getCount()-i);
				didit = true;
			} else if (jack == i) {
				player.setHeldItem(player.getActiveHand(), null);
				didit = true;
			}
			
		}
		return didit;
	}

	public static String getUsernameFromPlayer(EntityPlayer player)
    {
        return player.getEntityWorld().isRemote ? "" : UsernameCache.getLastKnownUsername(getUUIDFromPlayer(player));
    }

    public static EntityPlayer getPlayerFromUsername(String username)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return null;

        List<EntityPlayerMP> allPlayers = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
        for (EntityPlayerMP player : allPlayers) {
            if (player.getDisplayNameString()==username) {
                return player;
            }
        }
        return null;
    }

    public static EntityPlayer getPlayerFromUUID(String uuid)
    {
        return getPlayerFromUsername(getUsernameFromUUID(uuid));
    }

    public static EntityPlayer getPlayerFromUUID(UUID uuid)
    {
        return getPlayerFromUsername(getUsernameFromUUID(uuid));
    }

    public static UUID getUUIDFromPlayer(EntityPlayer player)
    {
        return player.getGameProfile().getId();
    }

    public static String getUsernameFromUUID(String uuid)
    {
        return UsernameCache.getLastKnownUsername(UUID.fromString(uuid));
    }

    public static String getUsernameFromUUID(UUID uuid)
    {
        return UsernameCache.getLastKnownUsername(uuid);
    }

    /*public static String getUsernameFromStack(ItemStack stack)
    {
        stack = NBTHelper.checkNBT(stack);

        return stack.getTagCompound().getString(Constants.NBT.OWNER_NAME);
    }*/

    
}
