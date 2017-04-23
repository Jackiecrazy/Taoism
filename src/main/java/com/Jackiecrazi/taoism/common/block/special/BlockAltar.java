package com.Jackiecrazi.taoism.common.block.special;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.block.tile.TileAltar;

public class BlockAltar extends Block implements ITileEntityProvider{
	private final String name = "altar";
	int paperAmount = 0;
	public BlockAltar()
	{
		super(Material.wood);
		setCreativeTab(Taoism.TabTaoistWeapon);
		this.setBlockName("altar");
		setHarvestLevel("axe", 2);
        isBlockContainer = true;
        setHardness(2);
        }
	public String getName()
	{
	return name;
	}
	public TileEntity createNewTileEntity(World world, int meta) {
        System.out.println("TE created");
        return new TileAltar();
    }

    @Override
    public void breakBlock(World world, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        super.breakBlock(world,p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_,p_149749_6_);
        world.removeTileEntity(p_149749_2_,p_149749_3_,p_149749_4_);
        System.out.println("Te removed");
    }

    
    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
		TileAltar a = (TileAltar) worldIn.getTileEntity(x,y,z);
    	if(playerIn.getHeldItem() != null && playerIn.getHeldItem().getItem() == Items.paper && playerIn.getHeldItem().stackSize >= 2 && !worldIn.isRemote){
    		int i = playerIn.getHeldItem().stackSize;
    		System.out.println(i);
    		playerIn.getHeldItem().stackSize-=2;
    		a.setPaper(a.getPaper()+1);
    		ChatComponentText component = new ChatComponentText("paper quantity: "+a.getPaper());
    		playerIn.addChatComponentMessage(component);
    	}
    	else TileAltar.runThatThing();
    		
    		return true;
    		
    }
    
}