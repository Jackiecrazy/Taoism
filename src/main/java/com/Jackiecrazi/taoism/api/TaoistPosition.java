package com.Jackiecrazi.taoism.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TaoistPosition {
	private int x,y,z;
	private World dimid;
	public TaoistPosition clone(){
		return new TaoistPosition(x,y,z,dimid);
	}
	public TaoistPosition(int posx,int posy, int posz,World dimid) {
		x=posx;
		y=posy;
		z=posz;
		this.dimid=dimid;
	}
	public TaoistPosition(BlockPos b) {
		this(b.getX(),b.getY(),b.getZ());
	}
	public TaoistPosition(BlockPos b,World w) {
		this(b.getX(),b.getY(),b.getZ(),w);
	}
	public TaoistPosition(int[] xyz,World dimid) {
		x=xyz[0];
		y=xyz[1];
		z=xyz[2];
		this.dimid=dimid;
	}
	public TaoistPosition(int posx,int posy, int posz) {
		x=posx;
		y=posy;
		z=posz;
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
	public World getDim(){
		return dimid;
	}
	public TaoistPosition offsetX(int offset){
		x+=offset;
		return this;
	}
	public TaoistPosition offsetY(int offset){
		y+=offset;
		return this;
	}
	public TaoistPosition offsetZ(int offset){
		z+=offset;
		return this;
	}
	public boolean equals(Object obj) {
		if(!(obj instanceof TaoistPosition))
        return false;
		TaoistPosition tp=(TaoistPosition)obj;
		return(tp.x==this.x&&tp.y==this.y&&tp.z==this.z&&tp.dimid==this.dimid);
    }
	public int[] toArray(){
		return new int[]{x,y,z};
	}
	public BlockPos toBlockPos(){
		return new BlockPos(x,y,z);
	}
}
