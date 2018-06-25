package com.jackiecrazi.taoism.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;

public class TileWorkstation extends TilePedestal implements ITickable {

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onRightClick(EntityPlayer p, EnumHand hand, EnumFacing facing, float x, float y, float z) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		// TODO Auto-generated method stub
		return false;
	}/*
	public static final ArrayList<OperationMode> modes = new ArrayList<OperationMode>();

	int progress = 0;
	private OperationMode om;
	private boolean works;

	public TileWorkstation() {
		super();
	}

	private void setOpMode(OperationMode o) {
		om = o;
		if (o != null) o.start(this);
	}

	public OperationMode getOpMode() {
		return om;
	}

	public boolean works() {
		return works;
	}

	*//**
	 * grabs the first recipe that is doable with the placed item and the surrounding items
	 * @return
	 *//*
	public OperationMode modeFromIS() {
		System.out.println(modes.size());
		if (!isOperational()) {
			System.out.println("barely functional");
			return null;
		}
		ItemStack i = this.getStackInSlot(0);
		if (i == null) {
			System.out.println("null stack");
			return null;
		}
		for (OperationMode m : modes) {

			if (m.isCatalyst(i) && m.canActivate(this)) {
				System.out.println("found match");
			return m; }
		}
		System.out.println("cannot activate");
		return null;
	}

	public void refresh() {
		works = isOperational();
	}

	public boolean isOperational() {
		//check the eight dirs away and verify if there's tiles there
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (!(this.getWorld().getTileEntity(getPos().add(x, 0, z)) instanceof TileWorkstation)) { return false; }
			}
		}
		works = true;
		return true;
	}

	@Override
	public int getField(int id) {
		return progress;
	}

	@Override
	public void setField(int id, int value) {
		progress = value;
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	*//**
	 * 
	 * @param i
	 * @return whether a something was initiated
	 *//*
	public boolean placeItem(ItemStack i) {
		boolean ret = false;
		//		if (modeFromIS(i) != null) {
		//			setOpMode(modeFromIS(i));
		//			ret=true;
		//		}
		if (this.getStackInSlot(0) != null) drop();
		this.setInventorySlotContents(0, i.copy());
		return ret;

	}

	public void reset() {
		progress = 0;
		setOpMode(null);
		//		if (getStackInSlot(0) != null) {
		//			this.getWorld().spawnEntity(new EntityItem(getWorld(), this.getPos().getX(), this.getPos().getY() + 2, this.getPos().getZ(), getStackInSlot(0)));
		//			this.setInventorySlotContents(0, null);
		//		}
	}

	public void drop() {
		if (getStackInSlot(0) != null && !this.getWorld().isRemote) {
			this.getWorld().spawnEntity(new EntityItem(getWorld(), this.getPos().getX(), this.getPos().getY() + 2, this.getPos().getZ(), getStackInSlot(0)));
			this.setInventorySlotContents(0, null);
		}
	}

	public void incrementProgress() {
		setField(0, getField(0) + 1);
		if (getField(0) >= om.getRequiredCount(this)) {
			om.finish(this);
			finish();
		}

	}

	public ItemStack getMat() {
		return getStackInSlot(0);
	}

	*//**
	 * 
	 * @return an array of size 8, containing the itemstacks on nearby pedestals
	 *         in no particular order
	 *//*
	public ItemStack[] getSurroundingMats() {
		ItemStack[] ret = new ItemStack[8];
		int index = 0;
		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				if ((x != 0 || z != 0) && (this.getWorld().getTileEntity(getPos().add(x, 0, z)) instanceof TileWorkstation)) {
					//System.out.println("tile indeed "+x+ " "+z);
					if ((z != 0 || x != 0)) ret[index] = ((TileWorkstation) (this.getWorld().getTileEntity(getPos().add(x, 0, z)))).getMat();
					index++;
				}
			}
		}
		return ret;
	}

	private void finish() {
		//3x3 multiblock, each one can hold something. Upgrade=less complex recipe possible since it occupies one space

		//catalyst in center, constituents around it, commence the skill check, done
		//display some arrows or text on it to show what goes where
		//putting in a handle, blueprint, or chassis switches it to the appropriate "mode" so to speak. Restrict inv: print gets 1, paper urn gets 0

		//find a more sane minigame
		if (!this.getWorld().isRemote) this.getWorld().spawnEntity(new EntityItem(getWorld(), this.getPos().getX(), this.getPos().getY() + 2, this.getPos().getZ(), om.finish(this)));
		reset();
		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				if ((this.getWorld().getTileEntity(getPos().add(x, 0, z)) instanceof TileWorkstation)) {
					((TileWorkstation) (this.getWorld().getTileEntity(getPos().add(x, 0, z)))).setContent(null);
				}
			}
		}
	}

	public void update() {
		if (om != null && !om.tick(this)) {
			om.fail(this);
			this.setOpMode(null);
			this.reset();
		}
	}

	public EntityPlayer closestPlayerInRadius() {
		EntityPlayer ret = null;
		double shortest = 300;
		List<EntityPlayer> l = this.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.getPos()).expand(5, 5, 5));
		for (EntityPlayer p : l) {
			if (p.getDistanceSq(getPos()) <= shortest) {
				shortest = p.getDistanceSq(getPos());
				ret = p;
			}
		}
		return ret;
	}

	@Override
	public boolean onRightClick(EntityPlayer p, EnumHand hand, EnumFacing facing, float x, float y, float z) {
		this.refresh();
		//System.out.println(this.getWorld().isRemote);
		if (this.isUsableByPlayer(p)) {
			if (p.getHeldItem(hand).getItem() == Item.getItemFromBlock(Blocks.AIR)) {
				System.out.println("null");
				if (p.isSneaking()) {
					System.out.println("op");
					setOpMode(modeFromIS());
				} else {
					System.out.println("drop");
					drop();
				}
			} else {
				System.out.println("put");
				placeItem(p.getHeldItem(hand));
			}
			return true;
		}
		return false;
	}

*/}
