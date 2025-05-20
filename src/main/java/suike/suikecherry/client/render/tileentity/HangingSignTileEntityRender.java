package suike.suikecherry.client.render.tileentity;

import suike.suikecherry.block.ModBlockHangingSign;
import suike.suikecherry.block.ModBlockHangingSignAttached;
import suike.suikecherry.tileentity.HangingSignTileEntity;
import suike.suikecherry.tileentity.HasBackSideSignTileEntity.RenderPosition;

import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

import org.apache.commons.lang3.tuple.Pair;

public class HangingSignTileEntityRender extends TileEntityRender<HangingSignTileEntity> {
    private static final double fixedY = 0.3125;

    @Override
    public void render(HangingSignTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) return;

        // 获取有物品的面
        int displayedSides = tile.getDisplayedSides();
        if (displayedSides == -1) return;

        // 仅渲染有物品的面
        for (int slot = 0; slot < 2; slot++) {
            // 跳过不需要渲染的槽位
            if ((displayedSides == 0 && slot != 0) || 
                (displayedSides == 1 && slot != 1)) {
                continue;
            }

            ItemStack stack = tile.getDisplayItem(slot);

            // 获取渲染位置和旋转
            RenderPosition renderPos = tile.getItemRenderPosition(slot);

            // 渲染物品
            this.renderItem(stack, 
                x + renderPos.getX(), 
                y + fixedY, 
                z + renderPos.getZ(), 
                renderPos.getRotation()
            );
        }
    }

    private static final Minecraft MC = Minecraft.getMinecraft();
    private void renderItem(ItemStack stack, double x, double y, double z, float rotation) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(rotation, 0, 1, 0);
        GlStateManager.scale(0.5, 0.5, 0.5);

        RenderHelper.enableStandardItemLighting();
        MC.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        RenderHelper.disableStandardItemLighting();
            
        GlStateManager.popMatrix();
    }
}