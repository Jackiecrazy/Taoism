package com.jackiecrazi.taoism.client.render;

import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityThrownWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class RenderTetheredTaoItemProjectile<T extends EntityThrownWeapon> extends RenderTaoItemProjectile<T> {
    private final boolean renderOnPrimaryHand;

    public RenderTetheredTaoItemProjectile(RenderManager renderManagerIn, int index, RenderItem itemRendererIn, Vec3i rotations, float scaleFactor, boolean main) {
        super(renderManagerIn, index, itemRendererIn, rotations, scaleFactor);
        renderOnPrimaryHand = main;
    }

    public RenderTetheredTaoItemProjectile(RenderManager renderManagerIn, int index, RenderItem itemRendererIn, Vec3i rotations, float scaleX, float scaleY, float scaleZ, boolean main) {
        super(renderManagerIn, index, itemRendererIn, rotations, scaleX, scaleY, scaleZ);
        renderOnPrimaryHand = main;
    }

    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityLivingBase thrower = entity.getThrower();
        if (thrower != null && !this.renderOutlines) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x, (float) y, (float) z);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            this.bindEntityTexture(entity);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            int handSide = (thrower.getPrimaryHand() == EnumHandSide.RIGHT) == (renderOnPrimaryHand) ? 1 : -1;

            float swingProgress = 0;//thrower.getSwingProgress(partialTicks);
            float sinSwing = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
            float throwerYawRenderTick = (thrower.prevRenderYawOffset + (thrower.renderYawOffset - thrower.prevRenderYawOffset) * partialTicks) * 0.017453292F;
            double sinThrowerYaw = MathHelper.sin(throwerYawRenderTick);
            double cosThrowerYaw = MathHelper.cos(throwerYawRenderTick);
            double handOffset = (double) handSide * 0.35D;
            double throwerSpotX;
            double throwerSpotY;
            double throwerSpotZ;
            double eyeHeight;

            if ((this.renderManager.options == null || this.renderManager.options.thirdPersonView <= 0) && thrower == Minecraft.getMinecraft().player) {
                float fov = this.renderManager.options.fovSetting;
                fov = fov / 100.0F;
                Vec3d vec3d = new Vec3d((double) handSide * -0.36D * (double) fov, -0.045D * (double) fov, 0.4D);
                vec3d = vec3d.rotatePitch(-(thrower.prevRotationPitch + (thrower.rotationPitch - thrower.prevRotationPitch) * partialTicks) * 0.017453292F);
                vec3d = vec3d.rotateYaw(-(thrower.prevRotationYaw + (thrower.rotationYaw - thrower.prevRotationYaw) * partialTicks) * 0.017453292F);
                vec3d = vec3d.rotateYaw(sinSwing * 0.5F);
                vec3d = vec3d.rotatePitch(-sinSwing * 0.7F);
                throwerSpotX = thrower.prevPosX + (thrower.posX - thrower.prevPosX) * (double) partialTicks + vec3d.x;
                throwerSpotY = thrower.prevPosY + (thrower.posY - thrower.prevPosY) * (double) partialTicks + vec3d.y;
                throwerSpotZ = thrower.prevPosZ + (thrower.posZ - thrower.prevPosZ) * (double) partialTicks + vec3d.z;
                eyeHeight = thrower.getEyeHeight()-0.06;
            } else {
                throwerSpotX = thrower.prevPosX + (thrower.posX - thrower.prevPosX) * (double) partialTicks - cosThrowerYaw * handOffset - sinThrowerYaw * 0.8D;
                throwerSpotY = thrower.prevPosY + (double) thrower.getEyeHeight() + (thrower.posY - thrower.prevPosY) * (double) partialTicks - 0.45D;
                throwerSpotZ = thrower.prevPosZ + (thrower.posZ - thrower.prevPosZ) * (double) partialTicks - sinThrowerYaw * handOffset + cosThrowerYaw * 0.8D;
                eyeHeight = thrower.isSneaking() ? -0.1875D : 0.0D;
            }

            double projSpotX = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
            double projSpotY = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + 0.25D;
            double projSpotZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
            double lengthX = (throwerSpotX - projSpotX);
            double lengthY = (throwerSpotY - projSpotY) + eyeHeight;
            double lengthZ = (throwerSpotZ - projSpotZ);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
            int maxVertices = 1;

            for (int vertexCount = 0; vertexCount <= maxVertices; ++vertexCount) {
                float f11 = (float) vertexCount / (float) maxVertices;
                bufferbuilder.pos(x + lengthX * (double) f11, y + lengthY * (double) (f11 * f11 + f11) * 0.5D + 0.25D, z + lengthZ * (double) f11).color(0, 0, 0, 255).endVertex();
            }

            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}
