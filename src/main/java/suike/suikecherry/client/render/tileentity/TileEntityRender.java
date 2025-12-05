package suike.suikecherry.client.render.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class TileEntityRender<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
    public static final Minecraft MC = Minecraft.getMinecraft();

    @Override
    public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {}
}