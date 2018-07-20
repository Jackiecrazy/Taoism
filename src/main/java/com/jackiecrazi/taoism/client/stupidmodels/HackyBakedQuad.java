package com.jackiecrazi.taoism.client.stupidmodels;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

public class HackyBakedQuad extends BakedQuad {

	public HackyBakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn, boolean applyDiffuseLighting, VertexFormat format) {
		super(vertexDataIn, tintIndexIn, faceIn, spriteIn, applyDiffuseLighting, format);
		// TODO Auto-generated constructor stub
	}

	public int getTintIndex()
    {
        return this.tintIndex;
    }

}
