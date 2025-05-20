package suike.suikecherry.entity;

import suike.suikecherry.SuiKe;
import suike.suikecherry.entity.boat.*;
import suike.suikecherry.entity.boat.boat.*;
import suike.suikecherry.entity.boat.chestboat.*;
import suike.suikecherry.client.render.entity.boat.ModBoatRenderFactory;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;

@Mod.EventBusSubscriber
public class ModEntity {
    public static final Class<? extends ModEntityBoat> entityCherryBoat = CherryBoatEntity.class;
    public static final Class<? extends ModEntityBoat> entityCherryChestBoat = CherryChestBoatEntity.class;

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityEntry> event) {
        // 注册实体
        registerEntityBoat(event, 1, entityCherryBoat, "cherry_boat");
        registerEntityBoat(event, 2, entityCherryChestBoat, "cherry_chest_boat");
    }

    // 注册船实体
    public static void registerEntityBoat(RegistryEvent.Register<EntityEntry> event, int entityID, Class<? extends ModEntityBoat> entityBoat, String boatName) {
        event.getRegistry().register(EntityEntryBuilder.create().entity(entityBoat).id(new ResourceLocation(SuiKe.MODID, boatName), entityID).name(boatName).tracker(80, 3, true).build());
    }

    @SubscribeEvent
    public static void registryModel(ModelRegistryEvent event) {
        // 注册实体模型
        RenderingRegistry.registerEntityRenderingHandler(ModEntityBoat.class, new ModBoatRenderFactory());
    }
}