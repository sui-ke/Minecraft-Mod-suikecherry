package suike.suikecherry.client.render.tileentity;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import suike.suikecherry.client.render.TexModelRenderer;
import suike.suikecherry.tileentity.ChiseledBookShelfTileEntity;

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

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import com.google.common.collect.ImmutableMap;

public class ChiseledBookShelfTileEntityRender extends TileEntityRender<ChiseledBookShelfTileEntity> {

    private static final Map<EnumFacing, ModelBooks> MODEL_MAP = ImmutableMap.<EnumFacing, ModelBooks>builder()
        .put(EnumFacing.NORTH, new ModelBooks(EnumFacing.NORTH))
        .put(EnumFacing.SOUTH, new ModelBooks(EnumFacing.SOUTH))
        .put(EnumFacing.EAST, new ModelBooks(EnumFacing.EAST))
        .put(EnumFacing.WEST, new ModelBooks(EnumFacing.WEST))
        .build();

    private static final ResourceLocation BOOKS_TEXTURE = new ResourceLocation("suikecherry", "textures/blocks/chiseled_bookshelf/chiseled_bookshelf_occupied.png");

    @Override
    public void render(ChiseledBookShelfTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || !tile.canRender()) return;

        this.renderBooks(tile, x, y, z, partialTicks);
    }

// 渲染书本
    private void renderBooks(ChiseledBookShelfTileEntity tile, double x, double y, double z, float partialTicks) {
        ModelBooks model = MODEL_MAP.get(tile.getParentBlockFacing());

        model.hasBookSlot = tile.getAllHasBookSlot();

        GlStateManager.pushMatrix();
        GlStateManager.enableCull();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-1.0F, -1.0F);
        GlStateManager.translate((float) x + 0.5, (float) y + 0.5, (float) z + 0.5);
        // 绑定贴图 并 渲染
        bindTexture(BOOKS_TEXTURE);
        model.root.render(0.0625F);

        GlStateManager.popMatrix();
    }

// 书本模型
    private static class ModelBooks extends ModelBase {
        private final ModelRenderer root;
        private final TexModelRenderer book_0;
        private final TexModelRenderer book_1;
        private final TexModelRenderer book_2;
        private final TexModelRenderer book_3;
        private final TexModelRenderer book_4;
        private final TexModelRenderer book_5;

        private IntSet hasBookSlot = new IntOpenHashSet();

        private ModelBooks(EnumFacing facing) {
            textureWidth = 16;
            textureHeight = 16;

            // 根部件
            this.root = new ModelRenderer(this);

            this.book_0 = new TexModelRenderer(this, 13, 1, () -> needRenderBook(0));
            this.book_1 = new TexModelRenderer(this, 2, 1, () -> needRenderBook(1));
            this.book_2 = new TexModelRenderer(this, 7, 1, () -> needRenderBook(2));
            this.book_3 = new TexModelRenderer(this, 7, 9, () -> needRenderBook(3));
            this.book_4 = new TexModelRenderer(this, 2, 9, () -> needRenderBook(4));
            this.book_5 = new TexModelRenderer(this, 13, 9, () -> needRenderBook(5));

            this.book_0.addBox(3.0F, -7.0F, 8.002F, 4, 6, 0);
            this.book_1.addBox(-2.0F, -7.0F, 8.002F, 4, 6, 0);
            this.book_2.addBox(-7.0F, -7.0F, 8.002F, 4, 6, 0);
            this.book_3.addBox(3.0F, 1.0F, 8.002F, 4, 6, 0);
            this.book_4.addBox(-2.0F, 1.0F, 8.002F, 4, 6, 0);
            this.book_5.addBox(-7.0F, 1.0F, 8.002F, 4, 6, 0);

            this.root.addChild(this.book_0);
            this.root.addChild(this.book_1);
            this.root.addChild(this.book_2);
            this.root.addChild(this.book_3);
            this.root.addChild(this.book_4);
            this.root.addChild(this.book_5);

            this.setRotation(facing);
        }

        private void setRotation(EnumFacing facing) {
            final float flatAngle = (float) Math.PI;
            final float rightAngle = flatAngle / 2;

            this.root.rotateAngleZ = flatAngle; // 180度旋转
            switch (facing) {
                case EAST:
                    this.root.rotateAngleY = rightAngle; // 90度旋转
                    break;
                case WEST:
                    this.root.rotateAngleY = -rightAngle; // -90度旋转
                    break;
                case SOUTH:
                    this.root.rotateAngleY = flatAngle; // 180度旋转
                    break;
                case NORTH:
                default:
                    break;
            }
        }

        private boolean needRenderBook(int slot) {
            return hasBookSlot.contains(slot);
        }
    }
}