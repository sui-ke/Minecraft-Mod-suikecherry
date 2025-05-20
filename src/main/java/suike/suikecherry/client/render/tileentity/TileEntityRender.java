package suike.suikecherry.client.render.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public abstract class TileEntityRender<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
    @Override
    public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {}
}