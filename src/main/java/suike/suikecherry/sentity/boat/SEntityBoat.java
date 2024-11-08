package suike.suikecherry.sentity.boat;

import suike.suikecherry.SuiKe;

import net.minecraft.entity.item.EntityBoat;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@Mod.EventBusSubscriber(modid = SuiKe.MODID)
public class SEntityBoat {
    @SubscribeEvent
    public static void registryModel(ModelRegistryEvent event) {
        /*注册船的实体*/EntityRegistry.registerModEntity(new ResourceLocation(SuiKe.MODID, "cherry_boat"), CherryBoatEntity.class, "cherry_boat", 1, SuiKe.instance, 80, 3, true);
        /*注册渲染*/RenderingRegistry.registerEntityRenderingHandler(CherryBoatEntity.class, new RenderCherryBoatFactory());
    }

//渲染类
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
            return new ResourceLocation(SuiKe.MODID, "textures/entity/cherry_boat.png");
        }
    }
}