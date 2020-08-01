package com.jackiecrazi.taoism.common.block.tile;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.common.block.TaoBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Credits to CorosUS for writing most of this!
 */
public class TileTempExplosion extends TileEntity implements ITickable {
    private IBlockState orig_blockState;
    private float orig_hardness = 1;
    private float orig_explosionResistance = 1;

    /*private int ticksRepairCount;
    private int ticksRepairMax = 20*60*5;*/
    private long timeToRepairAt = 0;

    public static TileTempExplosion replaceBlockAndBackup(World world, BlockPos pos) {
        return replaceBlockAndBackup(world, pos, 60);
    }

    /**
     * Some mod blocks might require getting data only while their block is still around, so we get it here and save it rather than on the fly later
     *
     * @param world
     * @param pos
     */
    public static TileTempExplosion replaceBlockAndBackup(World world, BlockPos pos, int ticksToRepair) {
        IBlockState oldState = world.getBlockState(pos);
        float oldHardness = oldState.getBlockHardness(world, pos);
        float oldExplosionResistance = 1;
        try {
            oldExplosionResistance = oldState.getBlock().getExplosionResistance(world, pos, null, null);
        } catch (Exception ex) {

        }

        world.setBlockState(pos, TaoBlocks.temp.getDefaultState());
        TileEntity tEnt = world.getTileEntity(pos);
        if (tEnt instanceof TileTempExplosion) {
            IBlockState state = world.getBlockState(pos);
            //CULog.dbg("set repairing block for pos: " + pos + ", " + oldState.getBlock());
            TileTempExplosion repairing = ((TileTempExplosion) tEnt);
            repairing.setBlockData(oldState);
            repairing.setOrig_hardness(oldHardness);
            repairing.setOrig_explosionResistance(oldExplosionResistance);
            repairing.timeToRepairAt = world.getTotalWorldTime() + ticksToRepair;
            //world.scheduleBlockUpdate(pos, state.getBlock(), 20*5, 1);
            return (TileTempExplosion) tEnt;
        } else {
            Taoism.logger.warn("failed to set repairing block for pos: " + pos);
            return null;
        }
    }

    public void setBlockData(IBlockState state) {
        //System.out.println(this + " - setting orig block as " + state);
        this.orig_blockState = state;
    }

    public IBlockState getOrig_blockState() {
        return orig_blockState;
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
    	return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 3, getPos().getZ() + 1);
    }*/

    @Override
    public void readFromNBT(NBTTagCompound var1) {
        super.readFromNBT(var1);
        timeToRepairAt = var1.getLong("timeToRepairAt");
        try {
            Block block = Block.getBlockFromName(var1.getString("orig_blockName"));
            if (block != null) {
                int meta = var1.getInteger("orig_blockMeta");
                this.orig_blockState = block.getStateFromMeta(meta);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            this.orig_blockState = Blocks.AIR.getDefaultState();
        }


        orig_hardness = var1.getFloat("orig_hardness");
        orig_explosionResistance = var1.getFloat("orig_explosionResistance");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound var1) {
        if (orig_blockState != null) {
            String str = Block.REGISTRY.getNameForObject(this.orig_blockState.getBlock()).toString();
            var1.setString("orig_blockName", str);
            var1.setInteger("orig_blockMeta", this.orig_blockState.getBlock().getMetaFromState(this.orig_blockState));
        }
        var1.setLong("timeToRepairAt", timeToRepairAt);

        var1.setFloat("orig_hardness", orig_hardness);
        var1.setFloat("orig_explosionResistance", orig_explosionResistance);

        return super.writeToNBT(var1);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    public float getOrig_hardness() {
        return orig_hardness;
    }

    public void setOrig_hardness(float orig_hardness) {
        this.orig_hardness = orig_hardness;
    }

    public float getOrig_explosionResistance() {
        return orig_explosionResistance;
    }

    public void setOrig_explosionResistance(float orig_explosionResistance) {
        this.orig_explosionResistance = orig_explosionResistance;
    }

    @Override
    public void update() {
        updateScheduledTick();
    }

    public void updateScheduledTick() {
        if (!world.isRemote) {
            if (orig_blockState == null || orig_blockState == this.getBlockType().getDefaultState()) {
                Taoism.logger.warn("invalid state for repairing block, removing, orig_blockState: " + orig_blockState + " vs " + this.getBlockType().getDefaultState());
                getWorld().setBlockState(this.getPos(), Blocks.AIR.getDefaultState());
            } else {
                if (world.getTotalWorldTime() > timeToRepairAt) {
                    AxisAlignedBB aabb = this.getBlockType().getDefaultState().getBoundingBox(this.getWorld(), this.getPos());
                    aabb = Block.FULL_BLOCK_AABB;
                    aabb = aabb.offset(this.getPos());
                    List<EntityLivingBase> listTest = this.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, aabb);
                    if (listTest.isEmpty() && (orig_blockState.getBlock().isAir(orig_blockState, world, getPos()) || orig_blockState.getBlock().canPlaceBlockAt(world, getPos()))) {
                        //System.out.println("restoring "+orig_blockState);
                        restoreBlock();
                    }
                }
            }
        }
    }

    public void restoreBlock() {
        //CULog.dbg("restoring block to state: " + orig_blockState + " at " + this.getPos());
        getWorld().setBlockState(this.getPos(), orig_blockState);

        //try to untrigger leaf decay for those large trees too far from wood source//also undo it for neighbors around it
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos posFix = pos.add(x, y, z);
                    IBlockState state = world.getBlockState(posFix);
                    if (state.getBlock() instanceof BlockLeaves) {
                        try {
                            //CULog.dbg("restoring leaf to non decay state at pos: " + posFix);
                            world.setBlockState(posFix, state.withProperty(BlockLeaves.CHECK_DECAY, false), 4);
                        } catch (Exception ex) {
                            //must be a modded block that doesnt use decay
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }


        //getWorld().setBlockState(this.getPos(), Blocks.STONE.getDefaultState());
    }
}
