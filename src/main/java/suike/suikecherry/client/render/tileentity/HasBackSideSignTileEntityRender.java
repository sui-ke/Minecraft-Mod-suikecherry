package suike.suikecherry.client.render.tileentity;

import java.util.Map;
import java.util.List;

import suike.suikecherry.data.AxisPosition;
import suike.suikecherry.block.ModBlockSignWall;
import suike.suikecherry.tileentity.HasBackSideSignTileEntity;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;

import com.google.common.collect.ImmutableMap;

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

            AxisPosition renderPos = tile.getTextRenderPosition(slot);

            this.renderText(tile, slot, renderPos);
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private boolean shouldRenderSide(int textSides, int slot) {
        return textSides == 2 || textSides == slot;
    }

// 渲染
    private void renderText(HasBackSideSignTileEntity tile, int slot, AxisPosition renderPos) {
        ITextComponent[] lines = tile.getTextForSlot(slot);

        GlStateManager.pushMatrix();
        applyTransforms(tile, renderPos);
        setupRenderingState();
        drawText(lines);
        GlStateManager.popMatrix();
    }

    private void applyTransforms(HasBackSideSignTileEntity tile, AxisPosition renderPos) {
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

// 渲染位置
    public static final Map<Integer, AxisPosition> SIGN_ROTATION_MAP = ImmutableMap.<Integer, AxisPosition>builder()
        .put(0, new AxisPosition(0.5, 0.5408, 0f))
        .put(1, new AxisPosition(0.4844, 0.5377, -22.5f))
        .put(2, new AxisPosition(0.4711, 0.5289, -45f))
        .put(3, new AxisPosition(0.4617, 0.5146, -67.5f))
        .put(4, new AxisPosition(0.4592, 0.5, 270f))
        .put(5, new AxisPosition(0.4617, 0.4854, 247.5f))
        .put(6, new AxisPosition(0.4711, 0.4711, 225f))
        .put(7, new AxisPosition(0.4844, 0.4623, 202.5f))
        .put(8, new AxisPosition(0.5, 0.4592, 180f))
        .put(9, new AxisPosition(0.5156, 0.4623, 157.5f))
        .put(10, new AxisPosition(0.5289, 0.4711, 135f))
        .put(11, new AxisPosition(0.5383, 0.4854, 112.5f))
        .put(12, new AxisPosition(0.5408, 0.5, 90f))
        .put(13, new AxisPosition(0.5383, 0.5146, 67.5f))
        .put(14, new AxisPosition(0.5289, 0.5289, 45f))
        .put(15, new AxisPosition(0.5156, 0.5377, 22.5f))
        .build();

    public static final Map<Integer, AxisPosition> SIGN_ROTATION_WALL_MAP = ImmutableMap.<Integer, AxisPosition>builder()
        .put(0, new AxisPosition(0.5, 0.1035, 0f))
        .put(1, new AxisPosition(0.8965, 0.5, 270f))
        .put(2, new AxisPosition(0.5, 0.8965, 180f))
        .put(3, new AxisPosition(0.1035, 0.5, 90f))
        .build();

    public static final Map<Integer, AxisPosition> HANG_ING_SING_ROTATION_MAP = ImmutableMap.<Integer, AxisPosition>builder()
        .put(0, new AxisPosition(0.5, 0.563, 0f))
        .put(1, new AxisPosition(0.4759, 0.5582, -22.5f))
        .put(2, new AxisPosition(0.4554, 0.5446, -45f))
        .put(3, new AxisPosition(0.4418, 0.5241, -67.5f))
        .put(4, new AxisPosition(0.4370, 0.5, 270f))
        .put(5, new AxisPosition(0.4418, 0.4759, 247.5f))
        .put(6, new AxisPosition(0.4554, 0.4554, 225f))
        .put(7, new AxisPosition(0.4759, 0.4418, 202.5f))
        .put(8, new AxisPosition(0.5, 0.4370, 180f))
        .put(9, new AxisPosition(0.5241, 0.4418, 157.5f))
        .put(10, new AxisPosition(0.5446, 0.4554, 135f))
        .put(11, new AxisPosition(0.5582, 0.4759, 112.5f))
        .put(12, new AxisPosition(0.563, 0.5, 90f))
        .put(13, new AxisPosition(0.5582, 0.5241, 67.5f))
        .put(14, new AxisPosition(0.5446, 0.5446, 45f))
        .put(15, new AxisPosition(0.5241, 0.5582, 22.5f))
        .build();
}