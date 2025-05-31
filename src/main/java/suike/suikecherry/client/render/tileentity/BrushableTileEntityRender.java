package suike.suikecherry.client.render.tileentity;

import java.util.Map;

import suike.suikecherry.data.RenderPosition;
import suike.suikecherry.tileentity.BrushableTileEntity;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import org.lwjgl.opengl.GL11;
import com.google.common.collect.ImmutableMap;

public class BrushableTileEntityRender extends TileEntityRender<BrushableTileEntity> {

    private static final Map<EnumFacing, Float> TEXTURES_FACING_ROTATIONS = ImmutableMap.<EnumFacing, Float>builder()
        .put(EnumFacing.NORTH, 180.0F)
        .put(EnumFacing.EAST, 90.0F)
        .put(EnumFacing.SOUTH, 0.0F)
        .put(EnumFacing.WEST, 270.0F)
        .build();

    private static final Map<EnumFacing, Float> ITEM_FACING_ROTATIONS = ImmutableMap.<EnumFacing, Float>builder()
        .put(EnumFacing.NORTH, 90.0F)
        .put(EnumFacing.EAST, 180.0F)
        .put(EnumFacing.SOUTH, 270.0F)
        .put(EnumFacing.WEST, 0.0F)
        .build();

    @Override
    public void render(BrushableTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || !tile.canRender()) return;

        RenderPosition renderPos = this.getItemRenderPosition(tile);
        if (renderPos == null) return;

        renderMask(tile, x, y, z, partialTicks);
        this.renderItem(
            tile.getTreasure(),
            x + renderPos.getX(),
            y + renderPos.getY(),
            z + renderPos.getZ(),
            renderPos.getRotation()
        );
    }

// 渲染遮罩
    private void renderMask(BrushableTileEntity tile, double x, double y, double z, float partialTicks) {
        EnumFacing facing = tile.getBrushFacing();
        if (facing == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        switch (facing) {
            case UP:
                GlStateManager.rotate(-90.0F, 1, 0, 0); // 绕 x 轴旋转-90度，使面朝上
                GlStateManager.translate(0, 0, 0.5001);  // 沿旋转后的Z轴偏移
                break;
            case DOWN:
                GlStateManager.rotate(90.0F, 1, 0, 0);  // 绕 x 轴旋转90度，使面朝下
                GlStateManager.translate(0, 0, 0.5001);
                break;
            default:
                float rotation = TEXTURES_FACING_ROTATIONS.getOrDefault(facing, 0.0F);
                GlStateManager.rotate(rotation, 0, 1, 0);
                GlStateManager.translate(0, 0, 0.5001);
        }

        // 配置渲染状态
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        // 绑定贴图
        TextureAtlasSprite sprite = MC.getTextureMapBlocks().getAtlasSprite(
            this.getSuspiciousTextures(tile).toString()
        );
        MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        // 绘制面
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        // 顶点
        double halfSize = 0.5;
        buffer.pos(-halfSize, -halfSize, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        buffer.pos(halfSize, -halfSize, 0).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        buffer.pos(halfSize, halfSize, 0).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        buffer.pos(-halfSize, halfSize, 0).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();

        tessellator.draw();

        GlStateManager.depthMask(true);
        // GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private ResourceLocation getSuspiciousTextures(BrushableTileEntity tile) {
        return tile.getParentBlock().getSuspiciousTextures(tile.getDusted());
    }

// 渲染物品
    private void renderItem(ItemStack stack, double x, double y, double z, float rotation) {
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(rotation, 0, 1, 0);
        GlStateManager.scale(0.5, 0.5, 0.5);

        MC.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

        GlStateManager.popMatrix();
    }

    public RenderPosition getItemRenderPosition(BrushableTileEntity tile) {
        EnumFacing brushFacing = tile.getBrushFacing();
        if (brushFacing == null) return null;

        // 根据方向和刷取等级计算偏移
        float[] offset = this.getTranslation(brushFacing, tile.getDusted());
        float rotation = ITEM_FACING_ROTATIONS.getOrDefault(brushFacing, 0.0F);

        // 从 方块中心点开始 偏移
        double dx = 0.5 + offset[0];
        double dy = 0.5 + offset[1];
        double dz = 0.5 + offset[2];

        return new RenderPosition(dx, dy, dz, rotation);
    }

    // 移植自 1.20 的偏移计算 (调整为 1.12 坐标系)
    private float[] getTranslation(EnumFacing direction, int dusted) {
        float[] fs = new float[3];
        float f = (float) dusted / 10.0F * 0.75F;

        switch(direction) {
            case EAST:
                fs[0] = 0.3F + f;
                break;
            case WEST:
                fs[0] = -0.3F - f;
                break;
            case UP:
                fs[1] = 0.32F + f;
                break;
            case DOWN:
                fs[1] = -0.32F - f;
                break;
            case NORTH:
                fs[2] = -0.3F - f;
                break;
            case SOUTH:
                fs[2] = 0.3F + f;
                break;
        }
        return fs;
    }
}