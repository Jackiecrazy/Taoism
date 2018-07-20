package com.jackiecrazi.taoism.client.stupidmodels;

import java.util.EnumMap;

import javax.vecmath.Vector3f;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

/**
 * just a class that moves things from {@link ForgeBlockStateV1} out so I can access them...
 * @author forge guys
 *
 */
public class UsefulTransformations {
	public static final ImmutableMap<String, IModelState> transforms;
	public static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s)
    {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(tx / 16, ty / 16, tz / 16),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
            new Vector3f(s, s, s),
            null));
    }
	
	private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);
	private static TRSRTransformation leftify(TRSRTransformation transform)
    {
        return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
    }
	
	static
    {
        ImmutableMap.Builder<String, IModelState> builder = ImmutableMap.builder();

        builder.put("identity", TRSRTransformation.identity());

        // block/block
        {
            EnumMap<TransformType, TRSRTransformation> map = new EnumMap<>(TransformType.class);
            TRSRTransformation thirdperson = get(0, 2.5f, 0, 75, 45, 0, 0.375f);
            map.put(TransformType.GUI,                     get(0, 0, 0, 30, 225, 0, 0.625f));
            map.put(TransformType.GROUND,                  get(0, 3, 0, 0, 0, 0, 0.25f));
            map.put(TransformType.FIXED,                   get(0, 0, 0, 0, 0, 0, 0.5f));
            map.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
            map.put(TransformType.THIRD_PERSON_LEFT_HAND,  leftify(thirdperson));
            map.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 45, 0, 0.4f));
            map.put(TransformType.FIRST_PERSON_LEFT_HAND,  get(0, 0, 0, 0, 225, 0, 0.4f));
            builder.put("block", new SimpleModelState(ImmutableMap.copyOf(map)));
        }

        // item/generated
        {
            EnumMap<TransformType, TRSRTransformation> map = new EnumMap<>(TransformType.class);
            TRSRTransformation thirdperson = get(0, 3, 1, 0, 0, 0, 0.55f);
            TRSRTransformation firstperson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
            map.put(TransformType.GROUND,                  get(0, 2, 0, 0, 0, 0, 0.5f));
            map.put(TransformType.HEAD,                    get(0, 13, 7, 0, 180, 0, 1));
            map.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
            map.put(TransformType.THIRD_PERSON_LEFT_HAND,  leftify(thirdperson));
            map.put(TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
            map.put(TransformType.FIRST_PERSON_LEFT_HAND,  leftify(firstperson));
            map.put(TransformType.FIXED,                   get(0, 0, 0, 0, 180, 0, 1));
            builder.put("item", new SimpleModelState(ImmutableMap.copyOf(map)));
        }

        // item/handheld
        {
            EnumMap<TransformType, TRSRTransformation> map = new EnumMap<>(TransformType.class);
            map.put(TransformType.GROUND,                  get(0, 2, 0, 0, 0, 0, 0.5f));
            map.put(TransformType.HEAD,                    get(0, 13, 7, 0, 180, 0, 1));
            map.put(TransformType.THIRD_PERSON_RIGHT_HAND, get(0, 4, 0.5f,         0, -90, 55, 0.85f));
            map.put(TransformType.THIRD_PERSON_LEFT_HAND,  get(0, 4, 0.5f,         0, 90, -55, 0.85f));
            map.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f));
            map.put(TransformType.FIRST_PERSON_LEFT_HAND,  get(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f));
            map.put(TransformType.FIXED,                   get(0, 0, 0, 0, 180, 0, 1));
            builder.put("held", new SimpleModelState(ImmutableMap.copyOf(map)));
        }

        transforms = builder.build();
    }
}
