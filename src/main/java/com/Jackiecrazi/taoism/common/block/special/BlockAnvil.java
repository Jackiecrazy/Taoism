package com.Jackiecrazi.taoism.common.block.special;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.block.TaoBlocks;
import com.Jackiecrazi.taoism.common.block.tile.TileAnvil;
import com.Jackiecrazi.taoism.common.block.tile.TileDummy;
import com.Jackiecrazi.taoism.common.items.TaoItems;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.lianQi.LianQiHandler;

public class BlockAnvil extends BlockContainer {

	public BlockAnvil(Material p_i45386_1_) {
		super(p_i45386_1_);
		this.setCreativeTab(Taoism.TabTaoistMaterials);
		this.setBlockName("TaoisticAnvil");
		this.setBlockTextureName("taoism:transparent");
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		boolean create=false;
		if(entity instanceof EntityPlayer)create=((EntityPlayer)entity).capabilities.isCreativeMode;
		int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            world.setBlockMetadataWithNotify(x, y, z, l, 2);
            System.out.println("set rot to "+l);
            int xx=x,yy=y,zz=z;
            switch(l){
            case 0:zz++;break;
            case 1:xx--;break;
            case 2:zz--;break;
            case 3:xx++;break;
            }
            if(world.getBlock(xx, yy, zz).isReplaceable(world, xx, yy, zz)){
				world.setBlock(xx, yy, zz, TaoBlocks.Dummy,l,3);
				world.setTileEntity(xx, yy, zz, new TileDummy().setX(x).setY(y).setZ(z));
				((TileDummy)world.getTileEntity(xx, yy, zz)).setIsSlave(true);
				if(!create)is.stackSize--;
			}
            else{
            	world.setBlock(x, y, z, Blocks.air);
    			if (entity instanceof EntityPlayer){
    				EntityPlayer player = (EntityPlayer)entity;
    				if(!create)
    				player.inventory.addItemStackToInventory(new ItemStack(TaoBlocks.Anvil));
    			}
    			return;
            }
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileAnvil();
	}
	public void onBlockClicked(World w, int x, int y, int z, EntityPlayer p) {
		System.out.println("wootz");
		TileAnvil te=(TileAnvil) w.getTileEntity(x, y, z);
		if(p.getHeldItem()!=null){
			if(p.getHeldItem().getItem()==TaoItems.Ding){
				if(te.isValidCombination())te.dropContent();
			}
			if(p.getHeldItem().getItem()==TaoItems.hammer&&w.rand.nextInt(1000)<=LianQiHandler.getThis(p).getLevel()){//get a REAL hammer
				te.requestFeeding();
				LianQiHandler.getThis(p).addXP(w.rand.nextFloat());
			}
		}
	}
	@Override
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int myth, float o, float lo, float gy)
	{
		TileAnvil te=(TileAnvil) w.getTileEntity(x, y, z);
		ItemStack heldItem = p.getHeldItem();
		if(heldItem!=null&&te.isItemValidForSlot(0, heldItem)){//&&!te.isSmithingTool()&&!te.isSmithingPart()
			if(heldItem.getItem()==TaoItems.QiPu){
				te.dropContent();
				if(te.getStackInSlot(0)!=null)return false;
				ItemStack i=heldItem;
				GenericTaoistWeapon cont=GenericTaoistWeapon.ListOfWeapons.get(i.getItemDamage());
				ItemStack put=new ItemStack(cont);
				for(String n:cont.getParts()){
					
					cont.setPart(n, put, new ItemStack(TaoItems.ResourceMetal,1,8000));
					
				}
				te.setAnvilContent(put);
				//te.setHasTemplate(true);
				te.setIndex(0);
				te.setSmithing(true);
				te.begin();
				//te.changeItemOrbit();
				te.requestFeeding();
			}
			
			
			
		
			
			
			/*if(heldItem.getItem() instanceof IModular){
				//System.out.println("initaiting tool");
				te.setSmithing(true);
				te.setAnvilContent(heldItem);
			}*/
		}
		else{
			ItemStack eaten=heldItem;
			if(eaten!=null){
				if(p.getHeldItem().getItem()==TaoItems.hammer&&w.rand.nextInt(10)<=p.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()){//higher prob?
					te.requestFeeding();
					LianQiHandler.getThis(p).addXP(w.rand.nextFloat());
					
				}
			}
			else{
			te.dropContent();//this shouldn't happen at all. Quench first, then it will auto-pop off
			LianQiHandler.getThis(p).addXP(w.rand.nextFloat());
			}
		}
		return false;
	}
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	public boolean isOpaqueCube()
	{
		return false;
	}
	@Override
    public void breakBlock(World world, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        super.breakBlock(world,p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_,p_149749_6_);
        world.removeTileEntity(p_149749_2_,p_149749_3_,p_149749_4_);
        //System.out.println("Te removed");
    }
}
