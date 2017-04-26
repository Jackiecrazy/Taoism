package com.Jackiecrazi.taoism.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import com.Jackiecrazi.taoism.common.block.tile.TileDing;
import com.Jackiecrazi.taoism.common.container.ContainerDing;
import com.Jackiecrazi.taoism.common.container.ContainerTPInv;
import com.Jackiecrazi.taoism.common.inventory.InventoryTPInv;

public class TaoisticGuiHandler implements IGuiHandler {
	public static final int DING=0,INV=1;
	public TaoisticGuiHandler() {
		
	}

	@Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == DING)
            return new ContainerDing((TileDing) world.getTileEntity(new BlockPos(x, y, z)),player.inventory);
        if (ID == INV){
            return new ContainerTPInv(player, player.inventory, new InventoryTPInv(player));
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == DING)
            return new GUIDing(player.inventory,(TileDing) world.getTileEntity(new BlockPos(x, y, z)));
        if(ID==INV)
        	return new GUITPInv(player, new InventoryTPInv(player));
        return null;
    }

}
