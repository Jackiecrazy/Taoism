package com.Jackiecrazi.taoism.common.world.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.Jackiecrazi.taoism.api.NeedyLittleThings;
import com.Jackiecrazi.taoism.api.SimplexNoise;
import com.Jackiecrazi.taoism.api.TaoistPosition;
import com.Jackiecrazi.taoism.common.block.TaoBlocks;
import com.Jackiecrazi.taoism.common.entity.mobs.hostile.EntityLingJing;

public class WorldGenLingMai extends WorldGenerator {

	public WorldGenLingMai() {

	}

	@Override
	public boolean generate(World w, Random r,
			int x, int y, int z) {
		System.out.println("generating new vein around "+x+" "+y+" "+z);
		Block b=TaoBlocks.LingOre;
		int bound=100,variation=4;
		/*int length=48;
		double[][][] coord=posterize2(SimplexNoise.generate3DOctavedSimplexNoise(length, length, length, 7, 0.4f, 0.05f,0.1),0.2);
		for(int xa=0;xa<coord.length;xa++)
			for(int ya=0;ya<coord[0].length;ya++){
				for(int za=0;za<coord[0][0].length;za++){
				double input=coord[xa][ya][za];
				if(input==1){
					w.setBlock(x+xa, y+ya, z+za, Blocks.planks);
					//System.out.println("set");
				}

				}
			}*/
		TaoistPosition tp=new TaoistPosition(x,y,z,w);
		TaoistPosition[] temp=new TaoistPosition[r.nextInt(4)+3];
		int rad=r.nextInt(4)+2;
		//determine the next point
		for(int a=0;a<temp.length;a++){
			dot(w,tp,rad,b,false);
			temp[a]=tp.clone();
			//System.out.println("recording "+a+" as "+tp.getX()+" "+tp.getY()+" "+tp.getZ());
			tp=tp.offsetX(r.nextInt(bound)-bound/2).offsetY(r.nextInt(bound)-bound/2).offsetZ(r.nextInt(bound)-bound/2);
			rad+=r.nextInt(variation)-(variation/2);
			rad=Math.max(Math.abs(rad),4);
			if(a!=0){
				//connect points
				//System.out.println("connecting node "+a+" to its predecessors");
				connect(w,temp[a-1],temp[a],Math.abs(rad),b);
			}
		}
		//generate offshoot generation 1
		TaoistPosition[] temp1=new TaoistPosition[temp.length*2+r.nextInt(temp.length*2)];
		int parentindex=0;
		variation=3;bound=25;
		for(int k=0;k<temp1.length;k++){
				//System.out.println("recording "+a+" as "+tp.getX()+" "+tp.getY()+" "+tp.getZ());
				tp=temp[parentindex].clone().offsetX(r.nextInt(bound)-bound/2).offsetY(r.nextInt(bound)-bound/2).offsetZ(r.nextInt(bound)-bound/2);
				rad+=r.nextInt(variation)-(variation/2);
				rad=Math.max(Math.abs(rad),2);
				dot(w,tp,rad,b,false);
				temp1[k]=tp.clone();
				//connect points
				//System.out.println("connecting node "+a+" to its predecessors");
				connect(w,temp[parentindex],temp1[k],Math.abs(rad),b);
				if(r.nextBoolean())parentindex++;
				if(parentindex>=temp.length)break;
		}
		//generation 2?
		
		//spawn the spirit
		tp=temp[temp.length/2];
		w.spawnEntityInWorld(new EntityLingJing(w,tp.getX(),tp.getY(),tp.getZ()));

		return true;
	}
	/*private static int fastfloor(double x) {
		int xi = (int)x;
		return x<xi ? xi-1 : xi;
	}*/
	private void dot(World w,TaoistPosition center,int radius,Block block,boolean hollow){
		for(int x=-radius;x<=radius;x++){
			for(int y=-radius;y<=radius;y++){
				for(int z=-radius;z<=radius;z++){
					TaoistPosition ne=center.clone().offsetX(x).offsetY(y).offsetZ(z);
					if(hollow){
						if((int)pythagoras(center,ne)==radius)setBlock(w,ne,block);
					}
					else if(pythagoras(center,ne)<radius)setBlock(w,ne,block);
				}
			}
		}
	}
	private void setBlock(World w, TaoistPosition tp,Block b){
		//if(w.getBlock(tp.getX(), tp.getY(), tp.getZ()).getBlockHardness(w, tp.getX(), tp.getY(), tp.getZ())<0&&b.getMaterial().equals(w.getBlock(tp.getX(), tp.getY(), tp.getZ())))
		w.setBlock(tp.getX(), tp.getY(), tp.getZ(), b);
	}
	private double pythagoras(TaoistPosition one,TaoistPosition two){
		int x1=one.getX(),x2=two.getX(),y1=one.getY(),y2=two.getY(),z1=one.getZ(),z2=two.getZ();
		int x=x2-x1,y=y2-y1,z=z2-z1;
		return Math.sqrt(x*x+y*y+z*z);
	}
	private void connect(World w,TaoistPosition from, TaoistPosition to, int radius,Block b){
		//System.out.println(from.getX()+"   "+from.getY()+"   "+from.getZ()+"   "+to.getX()+"   "+to.getY()+"   "+to.getZ()+"   ");
		TaoistPosition[] tp=NeedyLittleThings.bresenham(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
		double[][][] simplex=SimplexNoise.generate3DOctavedSimplexNoise(tp.length, tp.length, tp.length, 4, 0.7f, 0.003f, 1);
		Block place=b;
		boolean rep=true;
		int scale=radius;
		for(int k=0;k<2;k++){

			for(int a=0;a<tp.length;a++){
				TaoistPosition p=tp[a].clone();
				//System.out.println("bresenham on point "+p.getX()+" "+p.getY()+" "+p.getZ());
				p.offsetX((int)((1+simplex[a][a][a])*(1+simplex[a][a][a])*scale))
				.offsetY((int)((1+simplex[a][a][a])*(1+simplex[a][a][a])*scale))
				.offsetZ((int)((1+simplex[a][a][a])*(1+simplex[a][a][a])*scale));
				dot(w,p,radius,place,rep);

			}
			place=Blocks.air;
			radius-=1;
			rep=false;
		}
	}

	/*private static double[][][] posterize2(double[][][] inp,double threshold){
		double[][][] ret=new double[inp.length][inp[0].length][inp[0][0].length];
		for(int x=0;x<inp.length;x++)
			for(int y=0;y<inp[0].length;y++){
				for(int z=0;z<inp[0][0].length;z++){
					double input=inp[x][y][z];
					if(input-fastfloor(input)>threshold)input=1;else input=0;
					ret[x][y][z]=input;
				}
			}
		return ret;
	}*/
}
