package suike.suikecherry.client.render.tileentity;

import java.util.Map;
import java.util.List;

import suike.suikecherry.data.AxisPosition;
import suike.suikecherry.tileentity.HangingSignTileEntity;
import suike.suikecherry.tileentity.HasBackSideSignTileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

import com.google.common.collect.ImmutableMap;

// 悬挂告示牌渲染类
public class HangingSignTileEntityRender extends TileEntityRender<HangingSignTileEntity> {
    private static final double ItemFixedY = 0.3125;
    private static final double textFixedY = 0.2995;
    private static final int[] totalSlot = new int[]{0, 1};
    private static final int LINE_SPACING = 13;
    private static final float TEXT_SCALE = 0.009216667F;

    @Override
    public void render(HangingSignTileEntity hangingSignTile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (hangingSignTile == null) return;

        HasBackSideSignTileEntity signTile = (HasBackSideSignTileEntity) hangingSignTile;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableRescaleNormal();

        // 仅渲染有物品或文字的面
        for (int slot : totalSlot) {
            boolean shouldRenderText = shouldRenderText(signTile, slot);
            boolean shouldRenderItem = shouldRenderItem(hangingSignTile, slot);

            if (shouldRenderText) {
                this.renderText(signTile, slot);
            } else if (shouldRenderItem) {
                this.renderItem(hangingSignTile, slot);
            }
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

// 渲染
    private boolean shouldRenderText(HasBackSideSignTileEntity signTile, int slot) {
        int textSides = signTile.getTextSides();
        return (textSides == 2 || textSides == slot) && signTile.hasText(slot);
    }

    private boolean shouldRenderItem(HangingSignTileEntity hangingSignTile, int slot) {
        int displayedSides = hangingSignTile.getDisplayedSides();
        return (displayedSides == 2 || displayedSides == slot) && !hangingSignTile.getDisplayItem(slot).isEmpty();
    }
     
    private void renderText(HasBackSideSignTileEntity signTile, int slot) {
        AxisPosition renderPos = signTile.getTextRenderPosition(slot);
        ITextComponent[] lines = signTile.getTextForSlot(slot);

        GlStateManager.pushMatrix();
        applyTextTransforms(renderPos);
        setupTextRenderingState();
        drawTextLines(lines);
        GlStateManager.popMatrix();
    }

    private void renderItem(HangingSignTileEntity hangingSignTile, int slot) {
        ItemStack stack = hangingSignTile.getDisplayItem(slot);
        AxisPosition renderPos = hangingSignTile.getItemRenderPosition(slot);

        GlStateManager.pushMatrix();
        applyItemTransforms(renderPos);
        renderItemStack(stack);
        GlStateManager.popMatrix();
    }

    private void applyTextTransforms(AxisPosition renderPos) {
        GlStateManager.translate(renderPos.getX(), textFixedY, renderPos.getZ());
        GlStateManager.rotate(renderPos.getRotation(), 0, 1, 0);
        GlStateManager.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
    }

    private void applyItemTransforms(AxisPosition renderPos) {
        GlStateManager.translate(renderPos.getX(), ItemFixedY, renderPos.getZ());
        GlStateManager.rotate(renderPos.getRotation(), 0, 1, 0);
        GlStateManager.scale(0.5, 0.5, 0.5);
    }

    private void setupTextRenderingState() {
        GlStateManager.glNormal3f(0, 0, -1);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    private void drawTextLines(ITextComponent[] lines) {
        FontRenderer fr = MC.fontRenderer;
        int totalHeight = lines.length * LINE_SPACING;

        for (int i = 0; i < lines.length; i++) {
            if (lines[i] == null) continue;
            
            String text = formatTextLine(lines[i], fr);
            int yPos = i * LINE_SPACING - totalHeight / 2; // 保持垂直居中
            fr.drawString(text, -fr.getStringWidth(text)/2, yPos, 0);
        }
    }

    private String formatTextLine(ITextComponent component, FontRenderer fr) {
        List<ITextComponent> split = GuiUtilRenderComponents.splitText(
            component, 90, fr, false, true
        );
        return split.isEmpty() ? "" : split.get(0).getFormattedText();
    }

    private void renderItemStack(ItemStack stack) {
        MC.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
    }

// 渲染位置
    public static final Map<Integer, AxisPosition> ROTATION_MAP = ImmutableMap.<Integer, AxisPosition>builder()
        .put(0, new AxisPosition(0.5, 0.5781, 180f))
        .put(1, new AxisPosition(0.4701, 0.5722, 157.5f))
        .put(2, new AxisPosition(0.4448, 0.5552, 135f))
        .put(3, new AxisPosition(0.4278, 0.5299, 112.5f))
        .put(4, new AxisPosition(0.4219, 0.5, 90f))
        .put(5, new AxisPosition(0.4278, 0.4701, 67.5f))
        .put(6, new AxisPosition(0.4448, 0.4448, 45f))
        .put(7, new AxisPosition(0.4701, 0.4278, 22.5f))
        .put(8, new AxisPosition(0.5, 0.4219, 0f))
        .put(9, new AxisPosition(0.5299, 0.4278, -22.5f))
        .put(10, new AxisPosition(0.5552, 0.4448, -45f))
        .put(11, new AxisPosition(0.5722, 0.4701, -67.5f))
        .put(12, new AxisPosition(0.5781, 0.5, 270f))
        .put(13, new AxisPosition(0.5722, 0.5299, -112.5f))
        .put(14, new AxisPosition(0.5552, 0.5552, -135f))
        .put(15, new AxisPosition(0.5299, 0.5722, -157.5f))
        .build();
}