package suike.suikecherry.recipe;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.config.ConfigValue;

import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class BoatRecipe {
    public static void register(RegistryEvent.Register<IRecipe> event) {
        boatRecipe(Blocks.PLANKS, 0, Items.BOAT         , ItemBase.OAK_CHEST_BOAT     , "oak"     , event);
        boatRecipe(Blocks.PLANKS, 1, Items.SPRUCE_BOAT  , ItemBase.SPRUCE_CHEST_BOAT  , "spruce"  , event);
        boatRecipe(Blocks.PLANKS, 2, Items.BIRCH_BOAT   , ItemBase.BIRCH_CHEST_BOAT   , "birch"   , event);
        boatRecipe(Blocks.PLANKS, 3, Items.JUNGLE_BOAT  , ItemBase.JUNGLE_CHEST_BOAT  , "jungle"  , event);
        boatRecipe(Blocks.PLANKS, 4, Items.ACACIA_BOAT  , ItemBase.ACACIA_CHEST_BOAT  , "acacia"  , event);
        boatRecipe(Blocks.PLANKS, 5, Items.DARK_OAK_BOAT, ItemBase.DARK_OAK_CHEST_BOAT, "dark_oak", event);
        boatRecipe(BlockBase.CHERRY_PLANKS, 0, ItemBase.CHERRY_BOAT, ItemBase.CHERRY_CHEST_BOAT, "cherry", event);

        if (ConfigValue.hasBamboo) {
            boatRecipeOre("plankWoodBamboo", ItemBase.BAMBOO_RAFT, ItemBase.BAMBOO_CHEST_RAFT, "bamboo", event);
        }
    }

    private static void boatRecipe(Block block, int meta, Item boat, Item chestBoat, String type, RegistryEvent.Register<IRecipe> event) {
        // 防止重复添加原版
        if (block != Blocks.PLANKS) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(SuiKe.MODID, type + "_boat"),
                new ResourceLocation(SuiKe.MODID),
                new ItemStack(boat),
                "ANA",
                "AAA",
                'A', new ItemStack(block),
                'N', ItemStack.EMPTY
            );
        }

        if (chestBoat == null) return;
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, type + "_chest_boat"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(chestBoat),
            "ABA",
            "AAA",
            'A', new ItemStack(block, 1, meta),
            'B', new ItemStack(Blocks.CHEST)
        );

        chestBoatShapelessRecipe(chestBoat, type, boat, event);
    }

    private static void boatRecipeOre(String inputOreDict, Item boat, Item chestBoat, String type, RegistryEvent.Register<IRecipe> event) {
        OreIngredient inputStack = new OreIngredient(inputOreDict);
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, type + "_boat"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(boat),
            "ANA",
            "AAA",
            'A', inputStack,
            'N', ItemStack.EMPTY
        );

        if (chestBoat == null) return;
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, type + "_chest_boat"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(chestBoat),
            "ABA",
            "AAA",
            'A', inputStack,
            'B', new ItemStack(Blocks.CHEST)
        );

        chestBoatShapelessRecipe(chestBoat, type, boat, event);
    }

    private static void chestBoatShapelessRecipe(Item chestBoat, String type, Item boat, RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(
            new ChestBoatRecipe(chestBoat, type, boat).setRegistryName(
                new ResourceLocation(SuiKe.MODID, "boat>" + type + "_chest_boat")
            )
        );
    }

    private static class ChestBoatRecipe extends ShapelessOreRecipe {
        private ChestBoatRecipe(Item chestBoat, String type, Item boat) {
            super(
                new ResourceLocation(SuiKe.MODID, type + "_boat" + "_to_" + type + "_chest_boat"),
                new ItemStack(chestBoat),
                boat,
                Ingredient.fromItems(Item.getItemFromBlock(Blocks.CHEST))
            );
        }
    }
}