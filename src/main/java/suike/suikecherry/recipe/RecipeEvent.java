package suike.suikecherry.recipe;

import suike.suikecherry.oredict.OreDict;

import net.minecraft.item.crafting.IRecipe;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.RegistryEvent;

@Mod.EventBusSubscriber
public class RecipeEvent {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        OreDict.oreDict();
        SignRecipe.register();
        FurnaceRecipe.register();
        BoatRecipe.register(event);
        SmithingTemplateRecipe.register(event);
        CraftRecipe.register();
    }
}