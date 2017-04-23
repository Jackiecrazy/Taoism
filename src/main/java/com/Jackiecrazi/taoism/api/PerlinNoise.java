package com.Jackiecrazi.taoism.api;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class PerlinNoise
{
	
	public static void init() {
		Random random = new Random();

		double[][] noise1 = generateNoise(random, 2, 0, 0, 25, 25);
		double[][] noise2 = generateNoise(random, 1, 0, 0, 50, 50);
		double[][] noise3 = generateNoise(random, 1, 0, 0, 100, 100);
		double[][] noise4 = generateNoise(random, 1, 0, 0, 200, 200);
		final int width = 3200;
		final int octave=random.nextInt(8)+2;
		double[][] noise5 = generateNoise(random, 1, 0, 0, width, width);

		noise4 = interpolateBilinear(noise4, 200, 200);

		noise3 = interpolateBilinear(noise3, 100, 100);
		noise3 = interpolateBilinear(noise3, 200, 200);

		noise2 = interpolateBilinear(noise2, 50, 50);
		noise2 = interpolateBilinear(noise2, 100, 100);
		noise2 = interpolateBilinear(noise2, 200, 200);

		noise1 = interpolateBilinear(noise1, 25, 25);
		noise1 = interpolateBilinear(noise1, 50, 50);
		noise1 = interpolateBilinear(noise1, 100, 100);
		noise1 = interpolateBilinear(noise1, 200, 200);

		/*double[][] noisebase= generateNoise(random, octave, 0, 0, width, width);
		for(int x=0;x<Math.log(width/25)/Math.log(2);x++){
			int oct=random.nextInt(8)+2;
			double[][] noisy=
					generateNoise(random,oct,0,0,width/((int)Math.pow(2, x)),width/((int)Math.pow(2, x)));
			for(int y=x;y>0;y--){
				noisy=interpolateBilinear(noisy,width/((int)Math.pow(2, y)),width/((int)Math.pow(2, y)));
			}
			try {
				saveImage(noiseToImage(posterize2(noisebase,0.5), 16, 16), "./expnoise/", "perlin"+x);
				saveImage(noiseToImage(posterize2(noisy,0.5), 16, 16), "./expnoise/", "perlin"+x+" with "+oct+" octaves");
				
			} catch(IOException e) {
				e.printStackTrace();
			}
			System.out.println("this is cycle "+x+", the count of base is "+noisebase.length+" and the count of the addition is "+noisy.length);
			noisebase=combineArrays(noisebase,noisy,width,width);
		}
		try {
			saveImage(noiseToImage(posterize2(noisebase,0.5), 16, 16), "./expnoise/", "lastperlin");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		final int img = 80;
		//for(int octaves=0; octaves<20;octaves++)
			for(double thres=0;thres<1;thres+=0.01)
			//for(float rough=0.9f;rough<1f;rough+=0.01f)
			try {
				
				saveImage(noiseToImage(posterize2(SimplexNoise.generate2DOctavedSimplexNoise(img, img, 7, 0.4f, 0.05f),thres),img,img),"./expnoise/","simplex"+thres);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private static double[][] posterize2(double[][] inp,double threshold){
		double[][] ret=new double[inp.length][inp[0].length];
		for(int x=0;x<inp.length;x++)
		for(int y=0;y<inp[0].length;y++){
			double input=inp[x][y];
			if(input-fastfloor(input)>threshold)input=1;else input=0;
			ret[x][y]=input;
		}
		return ret;
	}
	
	private static int fastfloor(double x) {
		int xi = (int)x;
		return x<xi ? xi-1 : xi;
	}
	
	public static double[][] generateNoise(Random random, int octaves, int x, int y, int width, int height) {
		NoiseGeneratorPerlin noiseGenerator = new NoiseGeneratorPerlin(random, octaves);
		double[][] noiseArray = new double[width][height];

		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				noiseArray[i][j] = noiseGenerator.func_151601_a(x + i, y + j);
			}
		}

		return noiseArray;
	}

	public static BufferedImage noiseToImage(double[][] noise, int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				int hue = Math.abs((int) (255 * ((1D + noise[i][j]) / 2))%255);
				//System.out.println(hue);
				img.setRGB(i, j, new Color(hue, hue, hue).getRGB());
			}
		}

		return img;
	}

	public static void saveImage(BufferedImage img, String directory, String name) throws IOException {
		ImageIO.write(img, "png", new File(directory + name + ".png"));
	}

	public static void setAlpha(BufferedImage img, int alpha) {
		for(int i = 0; i < img.getWidth(); i++) {
			for(int j = 0; j < img.getHeight(); j++) {
				Color currentColor = new Color(img.getRGB(i, j));
				Color newColor = new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), alpha);

				img.setRGB(i, j, newColor.getRGB());
			}
		}
	}

	public static double[][] interpolateBilinear(double[][] array, int width, int height) {
		double[][] res = new double[width * 2][height * 2];

		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				res[i * 2][j * 2] = array[i][j];
			}
		}

		for(int i = 0; i < width * 2; i++) {
			for(int j = 0; j < height * 2; j++) {
				if(i == 0 || i % 2 == 0) {
					if((j + 1) % 2 == 0) {
						if(j != (width * 2) - 1) {
							res[i][j] = bilinearInterpolatePoints(res[i][j - 1], res[i][j + 1]);
						} else {
							res[i][j] = res[i][j - 2];
						}
					}
				} else {
					if(i != (height * 2) - 1) {
						res[i][j] = bilinearInterpolatePoints(res[i - 1][j], res[i + 1][j]);
					} else {
						res[i][j] = res[i - 2][j];
					}
				}
			}
		}

		return res;
	}

	public static double[][] combineArrays(double[][] array1, double[][] array2, int width, int height) {
		double[][] res = new double[width][height];

		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				res[i][j] = bilinearInterpolatePoints(array1[i][j], array2[i][j]);
			}
		}

		return res;
	}

	public static double cosineInterpolatePoints(double p1, double p2) {
		double mu2;

		mu2 = (1 - Math.cos(0.5 * Math.PI)) / 2; //0.5 is mu

		return(p1 * (1 - mu2) + p2 * mu2);
	}

	public static double bilinearInterpolatePoints(double p1, double p2) {
		return (p1 + p2) / 2;
	}
}

