package suike.suikecherry.entity;

import suike.suikecherry.SuiKe;
import suike.suikecherry.entity.boat.*;
import suike.suikecherry.entity.boat.boat.*;
import suike.suikecherry.entity.boat.chestboat.*;
import suike.suikecherry.entity.sniffer.EntitySniffer;
import suike.suikecherry.block.ModBlockBrushable.ModBlockBrushableFallingEntity;
import suike.suikecherry.client.render.entity.boat.ModBoatRenderFactory;
import suike.suikecherry.client.render.entity.sniffer.EntitySnifferRenderFactory;

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
    public static final Class<? extends EntityLiving> entitySniffer = EntitySniffer.class;
    public static final Class<? extends ModEntityBoat> entityCherryBoat = CherryBoatEntity.class;
    public static final Class<? extends ModEntityBoat> entityCherryChestBoat = CherryChestBoatEntity.class;
    // public static final Class<? extends Entity> blockBrushableFallingEntity = ModBlockBrushableFallingEntity.class;

    @SubscribeEvent
    public static void onRegisterEntity(RegistryEvent.Register<EntityEntry> event) {
        // 注册实体
        registerEntity(event, 1, entityCherryBoat, "cherry_boat");
        registerEntity(event, 2, entityCherryChestBoat, "cherry_chest_boat");
        registerEntity(event, 3, entitySniffer, "sniffer");
        // registerEntity(event, 4, blockBrushableFallingEntity, "block_Brushable_Falling_Entity");
    }

    public static void registerEntity(RegistryEvent.Register<EntityEntry> event, int entityID, Class<? extends Entity> entityClass, String entityName) {
        event.getRegistry().register(EntityEntryBuilder.create().entity(entityClass).id(new ResourceLocation(SuiKe.MODID, entityName), entityID).name(entityName).tracker(80, 3, true).build());
    }

    @SubscribeEvent
    public static void registryModel(ModelRegistryEvent event) {
        // 注册实体渲染
        RenderingRegistry.registerEntityRenderingHandler(ModEntityBoat.class, new ModBoatRenderFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntitySniffer.class, new EntitySnifferRenderFactory());
    }
}