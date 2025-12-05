package suike.suikecherry.client.render.tileentity;

import java.util.Map;
import java.util.HashMap;

import suike.suikecherry.SuiKe;
import suike.suikecherry.proxy.ClientProxy;
import suike.suikecherry.block.ModBlockDecoratedPot;
import suike.suikecherry.tileentity.DecoratedPotTileEntity;
import suike.suikecherry.tileentity.DecoratedPotTileEntity.PotTileClient;
import suike.suikecherry.client.render.TexModelRenderer;

import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

public class DecoratedPotTileEntityRender extends TileEntityRender<DecoratedPotTileEntity> {

    private static final Map<EnumFacing, ModelDecoratedPot> MODEL_MAP = ImmutableMap.<EnumFacing, ModelDecoratedPot>builder()
        .put(EnumFacing.NORTH, new ModelDecoratedPot(EnumFacing.NORTH))
        .put(EnumFacing.SOUTH, new ModelDecoratedPot(EnumFacing.SOUTH))
        .put(EnumFacing.EAST, new ModelDecoratedPot(EnumFacing.EAST))
        .put(EnumFacing.WEST, new ModelDecoratedPot(EnumFacing.WEST))
        .build();

    // 纹理资源
    public static final Map<String, ResourceLocation> PATTERN_TEXTURES = new HashMap<>();
    public static final ResourceLocation BASE_TEXTURE = new ResourceLocation("suikecherry", "textures/entity/decorated_pot/decorated_pot_base.png");
    public static final ResourceLocation SIDE_TEXTURE = new ResourceLocation("suikecherry", "textures/entity/decorated_pot/decorated_pot_side.png");

    @Override
    public void render(DecoratedPotTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) return;
        this.renderDecoratedPot((PotTileClient) tile, x, y, z, partialTicks);
    }

// 渲染方块陶罐
    private void renderDecoratedPot(PotTileClient tile, double x, double y, double z, float partialTicks) {
        // 获取方块方向
        EnumFacing facing = tile.getParentBlockFacing();

        // 获取模型
        ModelDecoratedPot model = MODEL_MAP.get(facing);

        // 获取各侧面纹饰
        model.currentSherdIDs = tile.getAllPotteryType();

        if (tile.wobbleProgress > 0) {
            this.decoratedPotwobble(tile, model, facing, partialTicks);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableCull();
        GlStateManager.translate((float) x + 0.5F, (float) y, (float) z + 0.5F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);

        // 绑定基础纹理并渲染
        bindTexture(BASE_TEXTURE);
        model.root.render(0.0625F);

        GlStateManager.popMatrix();

        // 恢复原始旋转角度
        if (tile.wobbleProgress > 0) {
            model.resetRotation();
        }
    }

// 应用动画
    private void decoratedPotwobble(PotTileClient tile, ModelDecoratedPot model, EnumFacing facing, float partialTicks) {
        float animRotateX = 0;
        float animRotateY = 0;
        float animRotateZ = 0;

        float progress = tile.wobbleProgress - (partialTicks / PotTileClient.WOBBLE_DURATION);
        if (progress < 0) progress = 0;

        // 成功动画 (复合旋转)
        if (tile.wobbleDirection > 0) {
            float k = progress * 2 * (float)Math.PI;
            // 计算世界坐标系中的旋转
            float tiltX = -1.5F * (MathHelper.cos(k) + 0.5F) * MathHelper.sin(k / 2.0F);
            float rotationX = tiltX * 0.015625F;
            float rotationZ = MathHelper.sin(k) * 0.015625F;

            // 根据方块朝向转换旋转轴
            switch (facing) {
                case EAST:
                    animRotateY = -rotationX;
                    animRotateZ = -rotationZ;
                    break;
                case WEST:
                    animRotateY = rotationX;
                    animRotateZ = rotationZ;
                    break;
                case NORTH:
                    animRotateX = rotationX;
                    animRotateZ = rotationZ;
                    break;
                default: // SOUTH
                    animRotateX = -rotationX;
                    animRotateZ = -rotationZ;
                    break;
            }
        }
        // 失败动画 (绕Y轴旋转)
        else {
            // 在基础旋转上叠加额外旋转
            float extraRotation = MathHelper.sin(-progress * 3.0F * (float)Math.PI) * 0.125F * (1.0F - progress);
            animRotateY = extraRotation;
        }

        // 应用动画旋转到模型
        model.root.rotateAngleX += animRotateX;
        model.root.rotateAngleY += animRotateY;
        model.root.rotateAngleZ += animRotateZ;
    }

// 获取侧面贴图
    private static ResourceLocation getPatternTexture(String patternType) {
        if ("brick".equals(patternType)) {
            return SIDE_TEXTURE;
        }

        return PATTERN_TEXTURES.computeIfAbsent(patternType, 
            k -> new ResourceLocation("suikecherry", "textures/entity/decorated_pot/pattern/" + k + ".png"));
    }

// 陶罐模型
    public static class ModelDecoratedPot extends ModelBase {
        public final ModelRenderer root;

        private final ModelRenderer hand;
        private final ModelRenderer neck;
        private final ModelRenderer top;
        private final ModelRenderer bottom;
        private final TexModelRenderer right;
        private final TexModelRenderer back;
        private final TexModelRenderer left;
        private final TexModelRenderer front;

        private final float originalRotateAngleX;
        private final float originalRotateAngleY;
        private final float originalRotateAngleZ;

        public String[] currentSherdIDs = new String[]{"brick", "brick", "brick", "brick"};

        public ModelDecoratedPot(EnumFacing facing) {
            textureWidth = 0;
            textureHeight = 0;

            // 根部件
            this.root = new ModelRenderer(this);

            // 头部模型
            this.hand = new ModelRenderer(this, 0, 0);
            this.hand.setTextureSize(32, 32);
            this.hand.addBox(-4.0F, -20.1F, -4.0F, 8, 3, 8);
            this.root.addChild(this.hand);

            // 颈部模型
            this.neck = new ModelRenderer(this, 0, 5);
            this.neck.setTextureSize(32, 32);
            this.neck.addBox(-3.0F, -17.0F, -3.0F, 6, 1, 6, 0.1F);
            this.root.addChild(this.neck);

            // 顶部模型
            this.top = new ModelRenderer(this, 0, 13);
            this.top.setTextureSize(32, 32);
            this.top.addBox(-7.0F, -16.0F, -7.0F, 14, 0, 14);
            this.root.addChild(this.top);

            // 底部模型
            this.bottom = new ModelRenderer(this, 4, 13);
            this.bottom.setTextureSize(32, 32);
            this.bottom.addBox(-7.0F, -0.002F, -7.0F, 14, 0, 14);
            this.root.addChild(this.bottom);

            // 四个侧面模型
            // 右边
            this.right = new TexModelRenderer(this, 1, 2, () -> {
                MC.getTextureManager().bindTexture(getPatternTexture(currentSherdIDs[0]));
            });
            this.right.setTextureSize(16, 16);
            this.right.addBox(-7.0F, -16.0F, -7.0F, 0, 16, 14);
            this.root.addChild(this.right);

            // 背面
            this.back = new TexModelRenderer(this, 3, 0, () -> {
                MC.getTextureManager().bindTexture(getPatternTexture(currentSherdIDs[1]));
            });
            this.back.setTextureSize(16, 16);
            this.back.addBox(-7.0F, -16.0F, 7.0F, 14, 16, 0);
            this.root.addChild(this.back);

            // 左边
            this.left = new TexModelRenderer(this, 3, 2, () -> {
                MC.getTextureManager().bindTexture(getPatternTexture(currentSherdIDs[2]));
            });
            this.left.setTextureSize(16, 16);
            this.left.addBox(7.0F, -16.0F, -7.0F, 0, 16, 14);
            this.root.addChild(this.left);

            // 前边
            this.front = new TexModelRenderer(this, 1, 0, () -> {
                MC.getTextureManager().bindTexture(getPatternTexture(currentSherdIDs[3]));
            });
            this.front.setTextureSize(16, 16);
            this.front.addBox(-7.0F, -16.0F, -7.0F, 14, 16, 0);
            this.root.addChild(this.front);

            // 设置方向默认旋转
            this.setRotation(facing);

            // 存储此方向默认角度
            this.originalRotateAngleX = this.root.rotateAngleX;
            this.originalRotateAngleY = this.root.rotateAngleY;
            this.originalRotateAngleZ = this.root.rotateAngleZ;
        }

        private void setRotation(EnumFacing facing) {
            final float flatAngle = (float) Math.PI;
            final float rightAngle = flatAngle / 2;

            switch (facing) {
                case EAST:
                    this.root.rotateAngleY = -rightAngle; // -90度旋转
                    break;
                case WEST:
                    this.root.rotateAngleY = rightAngle; // 90度旋转
                    break;
                case NORTH:
                    this.root.rotateAngleY = flatAngle; // 180度旋转
                    break;
                case SOUTH:
                default:
                    break;
            }
        }

        private void resetRotation() {
            this.root.rotateAngleY = this.originalRotateAngleY;
            this.root.rotateAngleX = this.originalRotateAngleX;
            this.root.rotateAngleZ = this.originalRotateAngleZ;
        }
    }
}