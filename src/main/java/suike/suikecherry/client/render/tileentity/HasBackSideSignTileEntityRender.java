package suike.suikecherry.client.render.tileentity;

import java.util.List;

import suike.suikecherry.data.RenderPosition;
import suike.suikecherry.block.ModBlockSignWall;
import suike.suikecherry.tileentity.HasBackSideSignTileEntity;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;

public class HasBackSideSignTileEntityRender extends TileEntityRender<HasBackSideSignTileEntity> {
    private static final double fixedY = 0.8343;
    private static final double inWallfixedY = 0.5218;
    private static final float TEXT_SCALE = 0.010416667F;
    private static final int[] totalSlot = new int[]{0, 1};

    @Override
    public void render(HasBackSideSignTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) return;

        // 获取有文字的面
        int textSides = tile.getTextSides();
        if (textSides == -1) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableRescaleNormal();

        // 仅渲染有文字的面
        for (int slot : totalSlot) {
            // 跳过不需要渲染的面
            if (!this.shouldRenderSide(textSides, slot)) continue;

            RenderPosition renderPos = tile.getTextRenderPosition(slot);

            this.renderText(tile, slot, renderPos);
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private boolean shouldRenderSide(int textSides, int slot) {
        return textSides == 2 || textSides == slot;
    }

    private void renderText(HasBackSideSignTileEntity tile, int slot, RenderPosition renderPos) {
        ITextComponent[] lines = tile.getTextForSlot(slot);

        GlStateManager.pushMatrix();
        applyTransforms(tile, renderPos);
        setupRenderingState();
        drawText(lines);
        GlStateManager.popMatrix();
    }

    private void applyTransforms(HasBackSideSignTileEntity tile, RenderPosition renderPos) {
        double y = tile.getParentBlock() instanceof ModBlockSignWall ? inWallfixedY : fixedY;

        GlStateManager.translate(renderPos.getX(), y, renderPos.getZ());
        GlStateManager.rotate(renderPos.getRotation(), 0, 1, 0);
        GlStateManager.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
    }

    private void setupRenderingState() {
        GlStateManager.glNormal3f(0, 0, -1);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    private void drawText(ITextComponent[] lines) {
        FontRenderer fr = getFontRenderer();
        
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] == null) continue;
            
            String text = formatText(lines[i], fr);
            int yPos = i * 10 - lines.length * 5;
            fr.drawString(text, -fr.getStringWidth(text)/2, yPos, 0);
        }
    }

    private String formatText(ITextComponent component, FontRenderer fr) {
        List<ITextComponent> split = GuiUtilRenderComponents.splitText(
            component, 90, fr, false, true
        );
        return split.isEmpty() ? "" : split.get(0).getFormattedText();
    }
}