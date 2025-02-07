package suike.suikecherry.sentity;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sentity.boat.CherryBoatEntity;
import suike.suikecherry.sentity.boat.CherryBoatRender;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;

@Mod.EventBusSubscriber(modid = SuiKe.MODID)
public class SEntity {
    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityEntry> event) {
        //注册船的实体
        event.getRegistry().register(
            EntityEntryBuilder.create()
                .entity(CherryBoatEntity.class)
                .id(new ResourceLocation(SuiKe.MODID, "cherry_boat"), 1)
                .name("cherry_boat")
                .tracker(80, 3, true)
                .build()
        );
    }

    @SubscribeEvent
    public static void registryModel(ModelRegistryEvent event) {
        /*注册船的实体模型*/RenderingRegistry.registerEntityRenderingHandler(CherryBoatEntity.class, new CherryBoatRender.RenderCherryBoatFactory());
    }
}