package suike.suikecherry.entity;

import suike.suikecherry.SuiKe;
import suike.suikecherry.entity.boat.*;
import suike.suikecherry.entity.boat.boat.*;
import suike.suikecherry.entity.boat.chestboat.*;
import suike.suikecherry.entity.sniffer.SnifferEntity;
import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.client.render.entity.boat.ModBoatRenderFactory;
import suike.suikecherry.client.render.entity.sniffer.SnifferEntityRenderFactory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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
    @SubscribeEvent
    public static void onRegisterEntity(RegistryEvent.Register<EntityEntry> event) {
        // 注册实体
        registerEntity(event, 0, SnifferEntity.class, "sniffer");
        registerEntity(event, 1, CherryBoat.class, "cherry_boat");
        registerEntity(event, 2, BambooRaft.class, "bamboo_raft");
        if (ConfigValue.addBoatChest) {
            registerEntity(event, 3, CherryChestBoat.class, "cherry_chest_boat");
            registerEntity(event, 4, BambooChestRaft.class, "bamboo_chest_raft");
            registerEntity(event, 5, OakChestBoat.class, "oak_chest_boat");
            registerEntity(event, 6, SpruceChestBoat.class, "spruce_chest_boat");
            registerEntity(event, 7, BirchChestBoat.class, "birch_chest_boat");
            registerEntity(event, 8, JungleChestBoat.class, "jungle_chest_boat");
            registerEntity(event, 9, AcaciaChestBoat.class, "acacia_chest_boat");
            registerEntity(event, 10, DarkOakChestBoat.class, "dark_oak_chest_boat");
        }
    }

    public static void registerEntity(RegistryEvent.Register<EntityEntry> event, int entityID, Class<? extends Entity> entityClass, String entityName) {
        event.getRegistry().register(EntityEntryBuilder.create().entity(entityClass).id(new ResourceLocation(SuiKe.MODID, entityName), entityID).name(entityName).tracker(80, 3, true).build());
    }

    @SubscribeEvent
    public static void registryModel(ModelRegistryEvent event) {
        // 注册实体渲染
        RenderingRegistry.registerEntityRenderingHandler(ModEntityBoat.class, new ModBoatRenderFactory());
        RenderingRegistry.registerEntityRenderingHandler(SnifferEntity.class, new SnifferEntityRenderFactory());
    }
}