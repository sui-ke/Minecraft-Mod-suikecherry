package suike.suikecherry.client.render.entity.sniffer;

import suike.suikecherry.entity.sniffer.EntitySniffer;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;

import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntitySnifferRenderFactory implements IRenderFactory<EntitySniffer> {
    @Override
    public Render<? super EntitySniffer> createRenderFor(RenderManager manager) {
        return new EntitySnifferRender(manager);
    }
}