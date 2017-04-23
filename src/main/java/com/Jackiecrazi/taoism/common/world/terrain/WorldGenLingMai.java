package com.Jackiecrazi.taoism.common.world.terrain;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.Jackiecrazi.taoism.api.SimplexNoise;

public class WorldGenLingMai extends WorldGenerator {

	public WorldGenLingMai() {
		
	}
	
	@Override
	public boolean generate(World w, Random r,
			int x, int y, int z) {
		System.out.println("generating new vein around "+x+" "+y+" "+z);
		
		int length=48;
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
			}
		return true;
	}
	private static int fastfloor(double x) {
		int xi = (int)x;
		return x<xi ? xi-1 : xi;
	}
	private static double[][][] posterize2(double[][][] inp,double threshold){
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
	}
}
