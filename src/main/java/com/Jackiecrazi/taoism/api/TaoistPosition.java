package com.jackiecrazi.taoism.api;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TaoistPosition {
	private int x, y, z;
	private World dimid;

	public TaoistPosition clone() {
		return new TaoistPosition(x, y, z, dimid);
	}

	public TaoistPosition(int posx, int posy, int posz, World dimid) {
		x = posx;
		y = posy;
		z = posz;
		this.dimid = dimid;
	}
	
	public TaoistPosition(BlockPos p){
		this(p.getX(),p.getY(),p.getZ());
	}
	
	public TaoistPosition(Entity e) {
		this(e.posX,e.posY,e.posZ,e.world);
	}
	
	public TaoistPosition(Vec3d v) {
		this(v.x,v.y,v.z);
	}

	public TaoistPosition(int[] xyz, World dimid) {
		x = xyz[0];
		y = xyz[1];
		z = xyz[2];
		this.dimid = dimid;
	}

	public TaoistPosition(int posx, int posy, int posz) {
		x = posx;
		y = posy;
		z = posz;
	}

	public TaoistPosition(double posX, double posY, double posZ, World worldObj) {
		this((int)posX,(int)posY, (int)posZ, worldObj);
	}

	public TaoistPosition(double xCoord, double yCoord, double zCoord) {
		this((int)xCoord,(int)yCoord,(int)zCoord);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public World getDim() {
		return dimid;
	}

	public TaoistPosition offsetX(int offset) {
		x += offset;
		return this;
	}

	public TaoistPosition offsetY(int offset) {
		y += offset;
		return this;
	}

	public TaoistPosition offsetZ(int offset) {
		z += offset;
		return this;
	}

	public TaoistPosition offset(TaoistPosition p) {
		return this.offsetX(p.x).offsetY(p.y).offsetZ(p.z);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof TaoistPosition)) return false;
		TaoistPosition tp = (TaoistPosition) obj;
		return (tp.x == this.x && tp.y == this.y && tp.z == this.z && tp.dimid == this.dimid);
	}

	public int[] toArray() {
		return new int[] { x, y, z };
	}

	@Override
	public String toString() {
		return "x:" + x + " y:" + y + " z:" + z;
	}

	public double distTo(TaoistPosition to) {
		return distTo(to.x, to.y, to.z);
	}

	public double distTo(int x, int y, int z) {
		int rx=this.x-x,ry=this.y-y,rz=this.z-z;
		return Math.sqrt((rx*rx)+(ry*ry)+(rz*rz));
	}
	
	public BlockPos toBlockPos(){
		return new BlockPos(x,y,z);
	}
}
