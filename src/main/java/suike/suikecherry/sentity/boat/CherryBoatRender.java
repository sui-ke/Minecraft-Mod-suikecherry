package suike.suikecherry.sentity.boat;

import suike.suikecherry.SuiKe;

import net.minecraft.entity.item.EntityBoat;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.client.registry.IRenderFactory;
import suike.suikecherry.suikecherry.Tags;

//渲染类
public class CherryBoatRender {
    public static class RenderCherryBoatFactory implements IRenderFactory<CherryBoatEntity> {
        @Override
        public Render<? super CherryBoatEntity> createRenderFor(RenderManager manager) {
            return new RenderCherryBoat(manager);
        }
    }

    public static class RenderCherryBoat extends RenderBoat {
        public RenderCherryBoat(RenderManager renderManager) {
            super(renderManager);
        }

        @Override
        protected ResourceLocation getEntityTexture(EntityBoat boat) {
            return new ResourceLocation(Tags.MOD_ID, "textures/entity/cherry_boat.png");
        }
    }
}