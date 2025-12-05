package suike.suikecherry.client.render.tileentity;

import java.util.Map;

import suike.suikecherry.data.AxisPosition;
import suike.suikecherry.tileentity.BrushableTileEntity;
import suike.suikecherry.client.render.TexModelRenderer;

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
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import com.google.common.collect.ImmutableMap;

public class BrushableTileEntityRender extends TileEntityRender<BrushableTileEntity> {

    private static final Map<EnumFacing, Float> ITEM_FACING_ROTATIONS = ImmutableMap.<EnumFacing, Float>builder()
        .put(EnumFacing.NORTH, 90.0F)
        .put(EnumFacing.EAST, 180.0F)
        .put(EnumFacing.SOUTH, 270.0F)
        .put(EnumFacing.WEST, 0.0F)
        .build();

    private static final Map<EnumFacing, ModelMask> MODEL_MAP = ImmutableMap.<EnumFacing, ModelMask>builder()
        .put(EnumFacing.UP, new ModelMask(EnumFacing.UP))
        .put(EnumFacing.DOWN, new ModelMask(EnumFacing.DOWN))
        .put(EnumFacing.NORTH, new ModelMask(EnumFacing.NORTH))
        .put(EnumFacing.SOUTH, new ModelMask(EnumFacing.SOUTH))
        .put(EnumFacing.EAST, new ModelMask(EnumFacing.EAST))
        .put(EnumFacing.WEST, new ModelMask(EnumFacing.WEST))
        .build();

    @Override
    public void render(BrushableTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || !tile.canRender()) return;

        AxisPosition renderPos = this.getItemRenderPosition(tile);
        if (renderPos == null) return;

        this.renderMask(tile, x, y, z, partialTicks);

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

        ModelMask model = MODEL_MAP.get(facing);

        GlStateManager.pushMatrix();
        GlStateManager.enableCull();

        // 获取并设置方块位置的光照值
        /*World world = tile.getWorld();
        BlockPos pos = tile.getPos();
        if (world != null && pos != null) {
            int light = world.getCombinedLight(pos, 0);
            OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit, 
                (light & 0xFFFF), 
                (light >> 16 & 0xFFFF)
            );
        }*/

        GlStateManager.translate((float) x + 0.5, (float) y + 0.5, (float) z + 0.5);
        // 绑定贴图
        bindTexture(this.getSuspiciousTextures(tile));
        model.root.render(0.0625F);

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

// 获取渲染位置
    public AxisPosition getItemRenderPosition(BrushableTileEntity tile) {
        EnumFacing brushFacing = tile.getBrushFacing();
        if (brushFacing == null) return null;

        // 根据方向和刷取等级计算偏移
        float[] offset = this.getTranslation(brushFacing, tile.getDusted());
        float rotation = ITEM_FACING_ROTATIONS.getOrDefault(brushFacing, 0.0F);

        // 从 方块中心点开始 偏移
        double dx = 0.5 + offset[0];
        double dy = 0.5 + offset[1];
        double dz = 0.5 + offset[2];

        return new AxisPosition(dx, dy, dz, rotation);
    }

    // 物品位置偏移
    private float[] getTranslation(EnumFacing direction, int dusted) {
        float[] fs = new float[3];
        float f = 0;

        switch (direction) {
            case EAST:
                f = (float) dusted / 10.0F * 1.25F;
                fs[0] = 0.26F + f;
                break;
            case WEST:
                f = (float) dusted / 10.0F * 1.25F;
                fs[0] = -0.26F - f;
                break;
            case UP:
                f = (float) dusted / 10.0F * 1.15F;
                fs[1] = 0.3F + f;
                break;
            case DOWN:
                f = (float) dusted / 10.0F * 1.15F;
                fs[1] = -0.3F - f;
                break;
            case NORTH:
                f = (float) dusted / 10.0F * 1.25F;
                fs[2] = -0.26F - f;
                break;
            case SOUTH:
                f = (float) dusted / 10.0F * 1.25F;
                fs[2] = 0.26F + f;
                break;
        }
        return fs;
    }

// 遮罩模型
    public static class ModelMask extends ModelBase {
        private final ModelRenderer root;
        private final ModelRenderer mask;

        private ModelMask(EnumFacing facing) {
            textureWidth = 16;
            textureHeight = 16;

            // 根部件
            this.root = new ModelRenderer(this);

            this.mask = new ModelRenderer(this, 0, 0);
            this.mask.addBox(-8.0F, 0.0F, -8.0F, 16, 0, 16);
            this.root.addChild(this.mask);

            this.setRotationAngles(facing);
        }

        private void setRotationAngles(EnumFacing facing) {
            final float flatAngle = (float) Math.PI;
            final float rightAngle = flatAngle / 2;

            switch (facing) {
                case DOWN:
                    this.root.offsetY = -0.50006f; // 向下移动
                    break;
                case UP:
                    this.root.offsetY = 0.50006f;        // 向上移动
                    this.root.rotateAngleX = flatAngle; // 180度翻转
                    break;
                case NORTH:
                    this.root.rotateAngleX = -rightAngle; // -90度旋转
                    this.root.rotateAngleY = flatAngle;   // 180度翻转
                    this.root.offsetZ = -0.50006f;
                    break;
                case SOUTH:
                    this.root.rotateAngleX = -rightAngle; // -90度旋转
                    this.root.offsetZ = 0.50006f;
                    break;
                case EAST:
                    this.root.rotateAngleZ = rightAngle; // 90度旋转
                    this.root.rotateAngleY = rightAngle; // 90度旋转
                    this.root.offsetX = 0.50006f;
                    break;
                case WEST:
                    this.root.rotateAngleZ = -rightAngle; // -90度旋转
                    this.root.rotateAngleY = -rightAngle; // -90度旋转
                    this.root.offsetX = -0.50006f;
                    break;
            }
        }
    }
}