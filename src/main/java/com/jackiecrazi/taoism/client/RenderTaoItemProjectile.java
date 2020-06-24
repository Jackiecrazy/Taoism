package com.jackiecrazi.taoism.client;

import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityThrownWeapon;
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

public class RenderTaoItemProjectile<T extends EntityThrownWeapon> extends Render<T> {
    private final int index;
    private final RenderItem itemRenderer;
    private final Vec3i rot;
    private final float scale;
    protected ItemStack item;

    RenderTaoItemProjectile(RenderManager renderManagerIn, int index, RenderItem itemRendererIn, Vec3i rotations, float scaleFactor) {
        super(renderManagerIn);
        this.index = index;
        this.itemRenderer = itemRendererIn;
        rot = rotations;
        scale=scaleFactor;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(T e, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y+(e.height>1?e.height/2:0), (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(e.rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-e.rotationPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rot.getY() + e.ySpin(), 0, 1, 0);
        GlStateManager.rotate(rot.getZ() + e.zSpin(), 0, 0, 1);
        GlStateManager.rotate(rot.getX() + e.xSpin(), 1, 0, 0);
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
        GlStateManager.popMatrix();
        super.doRender(e, x, y, z, entityYaw, partialTicks);
    }

    private ItemStack getStackToRender(T entityIn) {
        if (item == null) item = new ItemStack(TaoItems.prop, index);
        return item;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityThrownWeapon entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}