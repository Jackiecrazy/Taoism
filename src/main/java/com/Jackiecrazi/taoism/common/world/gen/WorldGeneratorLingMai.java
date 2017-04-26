package com.Jackiecrazi.taoism.common.world.gen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import com.Jackiecrazi.taoism.common.world.terrain.WorldGenLingMai;
public class WorldGeneratorLingMai implements IWorldGenerator
{
	
	private void generateSurface(World world, Random rand, int chunkX, int chunkZ)
	{
		if(rand.nextGaussian()+2.5<0.01){
			WorldGenLingMai tree = new WorldGenLingMai();
			int i = chunkX + rand.nextInt(16);
			int k = chunkZ + rand.nextInt(16);
			int j = world.getHeight(i, k);

			tree.generate(world, rand, i, j, k);
		}

	}
	private void generateNether(World world, Random random, int blockX, int blockZ)
	{
	}
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.getDimension())
		{
		case -1: generateNether(world, random, chunkX*16, chunkZ*16);
		case 0: generateSurface(world, random, chunkX*16, chunkZ*16);
		}
	}
}