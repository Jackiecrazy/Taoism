package com.Jackiecrazi.taoism.api;

import net.minecraft.world.World;

public class TaoistPosition {
	private int x,y,z;
	private World dimid;
	public TaoistPosition(int posx,int posy, int posz,World dimid) {
		x=posx;
		y=posy;
		z=posz;
		this.dimid=dimid;
	}
	public TaoistPosition(int[] xyz,World dimid) {
		x=xyz[0];
		y=xyz[1];
		z=xyz[2];
		this.dimid=dimid;
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
	public boolean equals(Object obj) {
		if(!(obj instanceof TaoistPosition))
        return false;
		TaoistPosition tp=(TaoistPosition)obj;
		return(tp.x==this.x&&tp.y==this.y&&tp.z==this.z&&tp.dimid==this.dimid);
    }
	public int[] toArray(){
		return new int[]{x,y,z};
	}
}
