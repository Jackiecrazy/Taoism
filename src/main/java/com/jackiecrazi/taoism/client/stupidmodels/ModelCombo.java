package com.jackiecrazi.taoism.client.stupidmodels;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class ModelCombo implements IBakedModel {
	protected IBakedModel base;
	protected IBakedModel[] stuff;
	
	
	public ModelCombo(IBakedModel base, IBakedModel... combine) {
		this.base = base;
		//for(IBakedModel ibm:combine)
		stuff = combine.clone();
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>();
		quads.addAll(base.getQuads(state, side, rand));

		for (IBakedModel ibaraki : stuff) {
			quads.addAll(ibaraki.getQuads(state, side, rand));
		}

		return quads;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return base.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return base.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return base.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return base.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return base.getOverrides();
	}

}
