package com.jackiecrazi.taoism.client.stupidmodels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.WeaponPerk;
import com.jackiecrazi.taoism.api.WeaponStatWrapper;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.config.TaoConfigs;

public class ModelWeapon implements IBakedModel {
	//arrange png files such that:
	//heads have 32x32 textures and should cross 16, 16 as its rough center, make all "lenticular" (e.g. blades, spears, but not spears or clubs) end on the same pixel for guard purposes
	//shaft and long shaft adjust such that they touch the head when they are normal size and the heads are scaled down by one half and on the top right
	//IE make them end at the 48,48 point
	//plan guards such that they are on the handle at normal size and at the top of the shaft at 1/2 scale
	//pommels should be in the same location everytime

	//TODO essentially, guards and blades have it so that the top right part of a long weapon looks like a short weapon. Handles and pommels do not scale

	//FIXME shaft/long shaft are too thicc compared to the head/guard due to scaling, put everything except guard on a 0.5 diet
	//yes you need to pretty much use the old textures -.-
	IBakedModel base;
	public ModelWeapon(IBakedModel orig) {
		//super(ImmutableList.copyOf(new ArrayList<BakedQuad>()), null, ImmutableMap.copyOf(new HashMap<TransformType, TRSRTransformation>()), WeaponOverride.inst);
		base=orig;
	}

	public static class WeaponOverride extends ItemOverrideList {
		public static final WeaponOverride inst = new WeaponOverride();
		private Cache<CacheKey, IBakedModel> othercache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES).build();

		//private HashMap<CacheKey, IBakedModel> cache = new HashMap<CacheKey, IBakedModel>();

		private WeaponOverride() {
			super(new ArrayList<ItemOverride>());
		}

		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
			CacheKey ck = new CacheKey(stack);
			IBakedModel ret = othercache.getIfPresent(ck);
			if (ret == null) {
				//System.out.println("the key " + ck.toString() + " cannot be found!");

				ret = retrieve(originalModel, stack, world, entity);
				othercache.put(ck, ret);
				//System.out.println("put " + ck.toString() + " into cache");
			}
			/*try {
			      ret = othercache.get(ck, () -> retrieve(original,stack, world, entity));
			    } catch(ExecutionException e) {
			      // do nothing, return original model
			    }*/

			return ret;

		}

		private IBakedModel retrieve(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

			IBakedModel[] a = new IBakedModel[TaoConfigs.weapc.enabledPartsByType.size()];
			//int b = 0;
			//System.out.println("a");
			/*for (PartData p : TaoItems.weap.getParts(stack).values()) {
				if (p != null) {
					//System.out.println(p.toString());
					a[b] = scaleAndTranslateQuads(Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(p.toIS(), world, entity).getQuads(null, null, 0), getTranslation(stack,p),1f);
					b++;
				}
			}*/
			ModelComboWeapon ret = new ModelComboWeapon(super.handleItemState(originalModel, stack, world, entity), a, stack);
			return ret;
		}
	}

	protected static class CacheKey {

		final NBTTagCompound data;

		protected CacheKey(ItemStack stack) {
			this.data = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) { return true; }
			if (o == null || getClass() != o.getClass()) { return false; }

			CacheKey cacheKey = (CacheKey) o;

			/*if(parent != null ? parent != cacheKey.parent : cacheKey.parent != null) {
			  return false;
			}*/
			return data != null ? data.equals(cacheKey.data) : cacheKey.data == null;

		}

		@Override
		public int hashCode() {
			//int result = parent != null ? parent.hashCode() : 0;
			int result = 0;
			result = 31 * result + (data != null ? data.hashCode() : 0);
			return result;
		}
	}

	protected static class ModelComboWeapon extends ModelCombo {
		protected ItemStack weap;
		private List<BakedQuad> cache;

		public ModelComboWeapon(IBakedModel base, IBakedModel[] combine, ItemStack is) {
			super(base, combine);
			weap = is.copy();
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType t) {
			//TODO fix all this crap
			PartData pd = TaoItems.weap.getPart(weap, StaticRefs.HANDLE);
			if (pd == null) return Pair.of(this, null);
			WeaponStatWrapper wsw = TaoConfigs.weapc.lookup(pd.getPart(), pd.getOrdinal());
			if (wsw == null) return Pair.of(this, null);
			Matrix4f ret = new Matrix4f();
			ret.setIdentity();
			if (t != TransformType.GUI) {
				//ret.transform(new Vector3f(10f,0f,0f));
				//ret.rotX((float) (Math.PI / 2));
				ret.rotY((float) (-Math.PI / 2));
				//ret.rotZ((float) (-Math.PI / 2));
				

				for (WeaponPerk wp : wsw.getPerks()) {
					if (wp.equals(WeaponPerk.SHORT)) {
						//do nothing, just in case it's horizontal
					}
					if (wp.equals(WeaponPerk.MEDIUM)) {
						ret.mul(1.5f);
						ret.m33=1f;
						break;
					}
					if (wp.equals(WeaponPerk.LONG)) {
						ret.mul(2f);
						ret.m33=1f;
						break;
					}
					if (wp.equals(WeaponPerk.HORIZONTAL)) {
						ret.rotY((float) (Math.PI));
						break;
					}
				}
			} else {
				/*for (WeaponPerk wp : wsw.getPerks()) {
					if (wp.equals(WeaponPerk.HORIZONTAL)) {
						//ret.rotZ((float) (-Math.PI / 2));
						break;
					}
				}*/
			}
			return Pair.of(this, ret);
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			//System.out.println(state);
			if (cache != null) return cache;
			ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>();
			quads.addAll(base.getQuads(state, side, rand));

			/*for (IBakedModel ibaraki : stuff) {
				for (BakedQuad shuten : ibaraki.getQuads(state, side, rand)) {
					
					quads.add(new BakedQuad(shuten.getVertexData(), shuten.getTintIndex(), shuten.getFace(), shuten.getSprite(), shuten.shouldApplyDiffuseLighting(), shuten.getFormat()));
				}
			}*/
			

			for (PartData p : TaoItems.weap.getParts(weap).values())
				if (side != null) {
					//System.out.println(p);
					//System.out.println(Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(p.toStack(), null, null));
					//missingno not turning up with rabbit foot but there is still no texture because it suddenly has no quads...
					//Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, worldIn, entitylivingbaseIn);
					IBakedModel quaddy = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(p.toStack(), null, null);
					//System.out.println(side==null);
					//quads.addAll(quaddy.getQuads(state, null, rand));
					quads.addAll(scaleAndTranslateQuads(quaddy.getQuads(state, null, rand), getTranslation(weap, p), getScale(weap, p)));
				}
			cache = quads;
			return quads;
		}

	}

	private static Vec3d getTranslation(ItemStack i, PartData p) {
		switch (p.getPart()) {
		//these don't need moving
		case StaticRefs.POMMEL:
			return new Vec3d(0, 0, 0);
		case StaticRefs.HANDLE:
			return new Vec3d(0, 0, 0);
		default:
			break;
		}
		try {
			//System.out.println("call");
			for (WeaponPerk wp : TaoConfigs.weapc.lookup(StaticRefs.HANDLE, TaoItems.weap.getPart(i, StaticRefs.HANDLE).getOrdinal()).getPerks()) {
				if (wp == WeaponPerk.LONG) {
					return new Vec3d(1, 1, 0.5);
				} else if (wp == WeaponPerk.MEDIUM) {
					return new Vec3d(1, 1, 0.5);
				} else if (wp == WeaponPerk.SHORT) { return new Vec3d(0, 0, 0); }
				//FIXME fine tune these values, and vec3i means INTEGER yo
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new Vec3d(0, 0, 0);
		}
		return new Vec3d(0, 0, 0);
	}

	private static float getScale(ItemStack i, PartData p) {
		switch (p.getPart()) {
		//these don't need scaling... right?
		case StaticRefs.POMMEL:
			return 1;
		case StaticRefs.HANDLE:
			return 1;
		default:
			break;
		}
		try {
			//System.out.println("call");
			if (p.getPart().equals(StaticRefs.GUARD) || p.getPart().equals(StaticRefs.HEAD)) for (WeaponPerk wp : TaoConfigs.weapc.lookup(StaticRefs.HANDLE, TaoItems.weap.getPart(i, StaticRefs.HANDLE).getOrdinal()).getPerks()) {
				if (wp == WeaponPerk.LONG) {
					return 0.5f;
				} else if (wp == WeaponPerk.MEDIUM) {
					return 0.5f;
				} else if (wp == WeaponPerk.SHORT) { return 1f; }
				//FIXME fine tune these values
			}

		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
		return 1;
	}

	private static List<BakedQuad> scaleAndTranslateQuads(List<BakedQuad> q, Vec3d t, float s) {
		ArrayList<BakedQuad> ret = new ArrayList<BakedQuad>();
		for (int x = 0; x < q.size(); x++) {
			ret.add(scaleAndTranslateQuad(q.get(x), t, s));
		}
		return ret;
	}

	private static BakedQuad scaleAndTranslateQuad(BakedQuad q, Vec3d t, float s) {
		//System.out.println(q.getFace());
		int[] v = q.getVertexData().clone();
		// leftRigh, upDown, frontBack
		double lr, ud, fb;

		// A quad has four verticies
		// indices of x values of vertices are 0, 7, 14, 21
		// indices of y values of vertices are 1, 8, 15, 22
		// indices of z values of vertices are 2, 9, 16, 23

		// east: x
		// south: z
		// up: y

		switch (q.getFace()) {
		case UP:
			// Quad up is towards north
			lr = t.x;
			ud = t.z;
			fb = t.y;

			v[0] = transform(v[0], lr, s);
			v[7] = transform(v[7], lr, s);
			v[14] = transform(v[14], lr, s);
			v[21] = transform(v[21], lr, s);

			v[1] = transform(v[1], fb, s);
			v[8] = transform(v[8], fb, s);
			v[15] = transform(v[15], fb, s);
			v[22] = transform(v[22], fb, s);

			v[2] = transform(v[2], ud, s);
			v[9] = transform(v[9], ud, s);
			v[16] = transform(v[16], ud, s);
			v[23] = transform(v[23], ud, s);
			break;

		case DOWN:
			// Quad up is towards south
			lr = t.x;
			ud = t.z;
			fb = t.y;

			v[0] = transform(v[0], lr, s);
			v[7] = transform(v[7], lr, s);
			v[14] = transform(v[14], lr, s);
			v[21] = transform(v[21], lr, s);

			v[1] = transform(v[1], fb, s);
			v[8] = transform(v[8], fb, s);
			v[15] = transform(v[15], fb, s);
			v[22] = transform(v[22], fb, s);

			v[2] = transform(v[2], -ud, s);
			v[9] = transform(v[9], -ud, s);
			v[16] = transform(v[16], -ud, s);
			v[23] = transform(v[23], -ud, s);
			break;

		case WEST:
			lr = t.z;
			ud = t.y;
			fb = t.x;

			v[0] = transform(v[0], fb, s);
			v[7] = transform(v[7], fb, s);
			v[14] = transform(v[14], fb, s);
			v[21] = transform(v[21], fb, s);

			v[1] = transform(v[1], ud, s);
			v[8] = transform(v[8], ud, s);
			v[15] = transform(v[15], ud, s);
			v[22] = transform(v[22], ud, s);

			v[2] = transform(v[2], lr, s);
			v[9] = transform(v[9], lr, s);
			v[16] = transform(v[16], lr, s);
			v[23] = transform(v[23], lr, s);
			break;

		case EAST:
			lr = t.z;
			ud = t.y;
			fb = t.x;

			v[0] = transform(v[0], fb, s);
			v[7] = transform(v[7], fb, s);
			v[14] = transform(v[14], fb, s);
			v[21] = transform(v[21], fb, s);

			v[1] = transform(v[1], ud, s);
			v[8] = transform(v[8], ud, s);
			v[15] = transform(v[15], ud, s);
			v[22] = transform(v[22], ud, s);

			v[2] = transform(v[2], lr, s);
			v[9] = transform(v[9], lr, s);
			v[16] = transform(v[16], lr, s);
			v[23] = transform(v[23], lr, s);
			break;

		case NORTH:
			lr = t.x;
			ud = t.y;
			fb = t.z;

			v[0] = transform(v[0], lr, s);
			v[7] = transform(v[7], lr, s);
			v[14] = transform(v[14], lr, s);
			v[21] = transform(v[21], lr, s);

			v[1] = transform(v[1], ud, s);
			v[8] = transform(v[8], ud, s);
			v[15] = transform(v[15], ud, s);
			v[22] = transform(v[22], ud, s);

			v[2] = transform(v[2], fb, s);
			v[9] = transform(v[9], fb, s);
			v[16] = transform(v[16], fb, s);
			v[23] = transform(v[23], fb, s);
			break;

		case SOUTH:
			// Case where quad is aligned with world coordinates
			lr = t.x;
			ud = t.y;
			fb = t.z;

			v[0] = transform(v[0], lr, s);
			v[7] = transform(v[7], lr, s);
			v[14] = transform(v[14], lr, s);
			v[21] = transform(v[21], lr, s);

			v[1] = transform(v[1], ud, s);
			v[8] = transform(v[8], ud, s);
			v[15] = transform(v[15], ud, s);
			v[22] = transform(v[22], ud, s);

			v[2] = transform(v[2], fb, s);
			v[9] = transform(v[9], fb, s);
			v[16] = transform(v[16], fb, s);
			v[23] = transform(v[23], fb, s);
			break;

		default:
			System.out.println("Unexpected face=" + q.getFace());
			break;
		}

		return new BakedQuad(v, q.getTintIndex(), q.getFace(), q.getSprite(), q.shouldApplyDiffuseLighting(), q.getFormat());
	}

	private static int transform(int i, double t, float s) {
		float f = Float.intBitsToFloat(i);
		f = (float) ((f + t) * s);
		return Float.floatToRawIntBits(f);
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return base.getQuads(state, side, rand);
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
		return WeaponOverride.inst;
	}
}
