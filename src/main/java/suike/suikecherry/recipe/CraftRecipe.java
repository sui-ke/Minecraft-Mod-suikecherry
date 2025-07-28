package suike.suikecherry.recipe;

import java.util.List;
import java.util.ArrayList;

import suike.suikecherry.SuiKe;
import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.BlockBase;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public class CraftRecipe {
    public static void register() {
        // 不生成樱花树时添加-樱花树苗-配方
        if (!ConfigValue.spawnCherryBiome) {
            OreIngredient treeSapling = new OreIngredient("treeSapling");
            // 樱花树苗
            GameRegistry.addShapedRecipe(
                new ResourceLocation(SuiKe.MODID, "cherry_sapling"),
                new ResourceLocation(SuiKe.MODID),
                new ItemStack(ItemBase.CHERRY_SAPLING),
                "AB",
                'A', treeSapling,
                'B', new ItemStack(Item.getByNameOrId("minecraft:dye"), 1, 9)
            );
        }

        // 樱花木板
        OreIngredient logCherry = new OreIngredient("logCherry");
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "cherry_planks"), 
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(BlockBase.CHERRY_PLANKS, 4),
            "A",
            'A', logCherry
        );

        // 樱花木头
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "cherry_wood"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(BlockBase.CHERRY_WOOD, 3),
            "AA",
            "AA",
            'A', new ItemStack(BlockBase.CHERRY_LOG)
        );

        // 樱花去皮木头
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "cherry_stripped_wood"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(BlockBase.CHERRY_STRIPPED_WOOD, 3),
            "AA",
            "AA",
            'A', new ItemStack(BlockBase.CHERRY_STRIPPED_LOG)
        );

        // 樱花楼梯
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "cherry_stairs"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(BlockBase.CHERRY_STAIRS, 4),
            "ANN",
            "AAN",
            "AAA",
            'A', new ItemStack(BlockBase.CHERRY_PLANKS),
            'N', ItemStack.EMPTY
        );

        // 樱花台阶
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "cherry_slab"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(ItemBase.CHERRY_SLAB, 6),
            "AAA",
            'A', new ItemStack(BlockBase.CHERRY_PLANKS)
        );

        // 樱花活板门
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "cherry_trapdoor"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(BlockBase.CHERRY_TRAPDOOR, 2),
            "AAA",
            "AAA",
            'A', new ItemStack(BlockBase.CHERRY_PLANKS)
        );

        // 樱花木栅栏
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "cherry_fence"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(BlockBase.CHERRY_FENCE, 3),
            "ABA",
            "ABA",
            'A', new ItemStack(BlockBase.CHERRY_PLANKS),
            'B', new ItemStack(Item.getByNameOrId("minecraft:stick"))
        );

        // 樱花木栅栏
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "cherry_fence_gate"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(BlockBase.CHERRY_FENCEGATE),
            "BAB",
            "BAB",
            'A', new ItemStack(BlockBase.CHERRY_PLANKS),
            'B', new ItemStack(Item.getByNameOrId("minecraft:stick"))
        );

        // 樱花木门
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "cherry_door"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(ItemBase.CHERRY_DOOR, 3),
            "AA",
            "AA",
            "AA",
            'A', new ItemStack(BlockBase.CHERRY_PLANKS)
        );

        // 樱花木按钮
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "cherry_button"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(BlockBase.CHERRY_BUTTON, 1),
            "A",
            'A', new ItemStack(BlockBase.CHERRY_PLANKS)
        );

        // 樱花木压力板
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "cherry_pressure_plate"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(BlockBase.CHERRY_PRESSURE_PLATE, 1),
            "AA",
            'A', new ItemStack(BlockBase.CHERRY_PLANKS)
        );

        // 粉红色染料
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "pink_petals"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(Item.getByNameOrId("minecraft:dye"), 1, 9),
            "A",
            'A', new ItemStack(ItemBase.PINK_PETALS)
        );

        // 阳光传感器
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "daylight_detector"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(Item.getByNameOrId("minecraft:daylight_detector")),
            "AAA",
            "BBB",
            "CCC",
            'A', new OreIngredient("blockGlass"),
            'B', new OreIngredient("gemQuartz"),
            'C', new OreIngredient("slabWood")
        );

        // 刷子
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "brush"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(ItemBase.BRUSH),
            "A",
            "B", 
            "C",
            'A', new ItemStack(Items.FEATHER),
            'B', new ItemStack(Items.GOLD_INGOT),
            'C', new ItemStack(Items.STICK)
        );

        // 雕纹书架
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "chiseled_bookshelf"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(BlockBase.CHISELED_BOOKSHELF),
            "AAA",
            "BBB", 
            "AAA",
            'A', new OreIngredient("plankWood"),
            'B', new OreIngredient("slabWood")
        );

        // 重新添加配方
        recipe();
    }

    //重新添加配方
    public static void recipe() {
        List<Item> itemList = new ArrayList<Item>();
        itemList.add(Item.getByNameOrId("minecraft:sign"));
        itemList.add(Item.getByNameOrId("minecraft:trapdoor"));
        itemList.add(Item.getByNameOrId("minecraft:wooden_button"));
        itemList.add(Item.getByNameOrId("minecraft:wooden_pressure_plate"));

        //获取当前注册的配方注册表
        IForgeRegistryModifiable<IRecipe> modRegistry = (IForgeRegistryModifiable<IRecipe>) GameRegistry.findRegistry(IRecipe.class);
        //删除配方
        int count = 0;
        for (IRecipe recipe : (Iterable<IRecipe>) modRegistry) {
            for (Item item : itemList) {
                if (ItemStack.areItemsEqual(recipe.getRecipeOutput(), new ItemStack(item))) {
                    modRegistry.remove(recipe.getRegistryName());
                    count++;
                }
                if (count == itemList.size()) {
                    break;
                }
            }
        }

        OreIngredient woodStack = new OreIngredient("plankWood");

        // 活板门
        GameRegistry.addShapedRecipe(
            new ResourceLocation("minecraft", "oak_trapdoor"),
            new ResourceLocation("minecraft"),
            new ItemStack(Item.getByNameOrId("minecraft:trapdoor"), 2),
            "AAA",
            "AAA",
            'A', woodStack
        );

        // 按钮
        GameRegistry.addShapedRecipe(
            new ResourceLocation("minecraft", "oak_button"),
            new ResourceLocation("minecraft"),
            new ItemStack(Item.getByNameOrId("minecraft:wooden_button"), 2),
            "A",
            'A', woodStack
        );

        // 压力板
        GameRegistry.addShapedRecipe(
            new ResourceLocation("minecraft", "oak_pressure_plate"),
            new ResourceLocation("minecraft"),
            new ItemStack(Item.getByNameOrId("minecraft:wooden_pressure_plate"), 2),
            "AA",
            'A', woodStack
        );

        // 告示牌
        GameRegistry.addShapedRecipe(
            new ResourceLocation("minecraft", "sign"),
            new ResourceLocation("minecraft"),
            new ItemStack(Item.getByNameOrId("minecraft:sign"), 3),
            "AAA",
            "AAA",
            "CBC",
            'A', woodStack,
            'B', new OreIngredient("stickWood"),
            'C', ItemStack.EMPTY
        );
    }
}