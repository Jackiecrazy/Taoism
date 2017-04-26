package com.Jackiecrazi.taoism.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExtendThyReachHelper {
	private static Minecraft mc = FMLClientHandler.instance().getClient();

	/**
	 * This method will return the entity or tile the mouse is hovering over up
	 * to the distance provided. It is more or less a copy/paste of the default
	 * minecraft version.
	 * 
	 * @return
	 */
		public static RayTraceResult getMouseOver(float frame, float dist)
		{
			RayTraceResult mop = null;
			final Entity renderViewEntity = mc.getRenderViewEntity();
			if (renderViewEntity != null)
			{
				if (mc.world != null)
				{
					double var2 = dist;
					mop = mc.getRenderViewEntity().rayTrace(var2, frame);
					double calcdist = var2;
					Vec3d pos = mc.getRenderViewEntity().getPositionVector();
					var2 = calcdist;
					if (mop != null)
					{
						calcdist = mop.hitVec.distanceTo(pos);
					}
					
					Vec3d lookvec = mc.getRenderViewEntity().getLook(frame);
					Vec3d var8 = pos.addVector(lookvec.xCoord * var2, lookvec.yCoord * var2, lookvec.zCoord * var2);
					Entity pointedEntity = null;
					float var9 = 1.0F;
					List<Entity> list = mc.world.getEntitiesWithinAABBExcludingEntity(renderViewEntity, renderViewEntity.getCollisionBoundingBox().addCoord(lookvec.xCoord * var2, lookvec.yCoord * var2, lookvec.zCoord * var2).expand(var9, var9, var9));
					double d = calcdist;
					
					for (Entity entity : list)
					{
						if (entity.canBeCollidedWith())
						{
							float bordersize = entity.getCollisionBorderSize();
							AxisAlignedBB aabb = entity.getCollisionBoundingBox().expand(bordersize, bordersize, bordersize);
							RayTraceResult mop0 = aabb.calculateIntercept(pos, var8);
							
							if (aabb.isVecInside(pos))
							{
								if (0.0D < d || d == 0.0D)
								{
									pointedEntity = entity;
									d = 0.0D;
								}
							} else if (mop0 != null)
							{
								double d1 = pos.distanceTo(mop0.hitVec);
								
								if (d1 < d || d == 0.0D)
								{
									pointedEntity = entity;
									d = d1;
								}
							}
						}
					}
					
					if (pointedEntity != null && (d < calcdist || mop == null))
					{
						mop = new RayTraceResult(pointedEntity);
					}
				}
			}
			return mop;
		}

}
