package com.jackiecrazi.taoism.client.render;

import com.jackiecrazi.taoism.common.entity.projectile.EntityTaoProjectile;
import com.jackiecrazi.taoism.common.item.TaoItems;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;

public class RenderTaoItemProjectile<T extends EntityTaoProjectile> extends Render<T> {
    private final int index;
    private final RenderItem itemRenderer;
    private final Vec3i rot;
    private final float sX, sY, sZ;
    protected ItemStack item;

    public RenderTaoItemProjectile(RenderManager renderManagerIn, int index, RenderItem itemRendererIn, Vec3i rotations, float scaleFactor) {
        this(renderManagerIn, index, itemRendererIn, rotations, scaleFactor, scaleFactor, scaleFactor);
    }

    public RenderTaoItemProjectile(RenderManager renderManagerIn, int index, RenderItem itemRendererIn, Vec3i rotations, float scaleX, float scaleY, float scaleZ) {
        super(renderManagerIn);
        this.index = index;
        this.itemRenderer = itemRendererIn;
        rot = rotations;
        sX = scaleX;
        sY = scaleY;
        sZ = scaleZ;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(T e, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + (e.height / 2), (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.rotate(e.rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-e.rotationPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rot.getY() + e.ySpin(), 0, 1, 0);
        GlStateManager.rotate(rot.getZ() + e.zSpin(), 0, 0, 1);
        GlStateManager.rotate(rot.getX() + e.xSpin(), 1, 0, 0);
        GlStateManager.scale(sX, sY, sZ);
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(e));
        }

        this.itemRenderer.renderItem(this.getStackToRender(e), ItemCameraTransforms.TransformType.GROUND);

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
        super.doRender(e, x, y, z, entityYaw, partialTicks);
    }

    private ItemStack getStackToRender(T entityIn) {
        if (item == null) {
            item = new ItemStack(TaoItems.prop, index);
            //item.setItemDamage(color);
        }
        return item;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityTaoProjectile entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}