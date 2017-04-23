package com.Jackiecrazi.taoism.common.zhencomponents.shape;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.api.TaoistPosition;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenEffectShape;

public class ShapeSphere extends ZhenEffectShape {

	public ShapeSphere(Block b, int priceA) {
		super(b, priceA);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TaoistPosition[] performEffectBlock(World w, TaoistPosition origin,
			int minx, int miny, int minz, int maxx, int maxy, int maxz) {
		int a=maxx-maxy,b=maxy-miny,c=maxz-minz;
		List<TaoistPosition> ret=new ArrayList<TaoistPosition>();
		for(int x=minx;x<=maxx;x++){
			for(int y=miny;y<=maxy;y++){
				for(int z=minz;z<=maxz;z++){
					if((x/a)*(x/a)+(y/b)*(y/b)+(z/c)*(z/c)==1){
						ret.add(new TaoistPosition(x,y,z,w));
					}
				}
			}
		}
		return (TaoistPosition[]) ret.toArray();
	}

	@Override
	public Entity[] performEffectEntity(World w, TaoistPosition origin,
			int minx, int miny, int minz, int maxx, int maxy, int maxz) {
		@SuppressWarnings("unchecked")
		List<Entity> ret=w.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(minx, miny, minz, maxx, maxy, maxz));
		for(Entity e:ret){
			//ellipsoid formula here. Use shortest side for height
			if( Math.pow((e.posX-origin.getX())/((maxx-minx)/2),2)+ Math.pow((e.posY-origin.getY())/((maxy-miny)/2),2)+ Math.pow((e.posZ-origin.getZ())/((maxz-minz)/2),2) <=1){
				ret.remove(e);
			}
		}
		return (Entity[]) ret.toArray();
	}

}
