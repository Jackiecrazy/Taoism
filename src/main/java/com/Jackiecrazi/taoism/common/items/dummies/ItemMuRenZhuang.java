package com.Jackiecrazi.taoism.common.items.dummies;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityMuRenZhuang;

public class ItemMuRenZhuang extends Item {
	public ItemMuRenZhuang(){
		this.setMaxStackSize(1);
		this.setCreativeTab(Taoism.TabTaoistWeapon);
		this.setUnlocalizedName("murenzhuang");
	}
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer p, World world, int x, int y, int z, int facing, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
        if(!world.isRemote&&facing==1) {
        	//TODO get position of player and compare with pos clicked
            EntityMuRenZhuang entity = new EntityMuRenZhuang(world);
//            entity.setPosition(x + 0.5, y+1, z + 0.5);
            entity.setPositionAndRotation(x+0.5, y+1, z+0.5, p.rotationYaw, 0);
            world.spawnEntityInWorld(entity);
            stack.stackSize--;
        }

        return super.onItemUse(stack, p, world, x, y, z, facing, p_77648_8_, p_77648_9_, p_77648_10_);
    }
}
