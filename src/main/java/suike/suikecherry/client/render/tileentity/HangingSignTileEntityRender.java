package suike.suikecherry.client.render.tileentity;

import java.util.List;
import java.util.Arrays;

import suike.suikecherry.data.AxisPosition;
import suike.suikecherry.tileentity.HangingSignTileEntity;
import suike.suikecherry.tileentity.HasBackSideSignTileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

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
}