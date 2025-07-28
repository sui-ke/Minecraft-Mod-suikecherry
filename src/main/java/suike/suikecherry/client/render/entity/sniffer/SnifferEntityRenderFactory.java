package suike.suikecherry.client.render.entity.sniffer;

import suike.suikecherry.entity.sniffer.SnifferEntity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;

import net.minecraftforge.fml.client.registry.IRenderFactory;

public class SnifferEntityRenderFactory implements IRenderFactory<SnifferEntity> {
    @Override
    public Render<? super SnifferEntity> createRenderFor(RenderManager manager) {
        return new SnifferEntityRender(manager);
    }
}