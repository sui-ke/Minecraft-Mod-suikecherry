package suike.suikecherry.recipe;

import java.util.stream.IntStream;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.item.ModItemSmithingTemplate;

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
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class SmithingTemplateRecipe {
    public static void register(RegistryEvent.Register<IRecipe> event) {
        /*海岸纹饰*/addRecipe(ItemBase.COAST_ATST    , Blocks.COBBLESTONE, event);
        /*沙丘纹饰*/addRecipe(ItemBase.DUNE_ATST     , "sandstone", event);
        /*眼眸纹饰*/addRecipe(ItemBase.EYE_ATST      , Blocks.END_STONE, event);
        /*雇主纹饰*/addRecipe(ItemBase.HOST_ATST     , Blocks.STAINED_HARDENED_CLAY, 0, 15, event);
        /*牧民纹饰*/addRecipe(ItemBase.RAISER_ATST   , Blocks.STAINED_HARDENED_CLAY, 0, 15, event);
        /*肋骨纹饰*/addRecipe(ItemBase.RIB_ATST      , Blocks.NETHERRACK, event);
        /*哨兵纹饰*/addRecipe(ItemBase.SENTRY_ATST   , Blocks.COBBLESTONE, event);
        /*塑造纹饰*/addRecipe(ItemBase.SHAPER_ATST   , Blocks.STAINED_HARDENED_CLAY, 0, 15, event);
        /*幽静纹饰*/addRecipe(ItemBase.SILENCE_ATST  , "cobblestone", event);
        /*猪鼻纹饰*/addRecipe(ItemBase.SNOUT_ATST    , "cobblestone", event);
        /*尖塔纹饰*/addRecipe(ItemBase.SPIRE_ATST    , new ItemStack[]{new ItemStack(Blocks.PURPUR_BLOCK), new ItemStack(Blocks.PURPUR_PILLAR)}, event);
        /*潮汐纹饰*/addRecipe(ItemBase.TIDE_ATST     , Blocks.PRISMARINE, 0, 2, event);
        /*恼鬼纹饰*/addRecipe(ItemBase.VEX_ATST      , Blocks.COBBLESTONE, event);
        /*监守纹饰*/addRecipe(ItemBase.WARD_ATST     , "cobblestone", event);
        /*向导纹饰*/addRecipe(ItemBase.WAYFINDER_ATST, Blocks.STAINED_HARDENED_CLAY, 0, 15, event);
        /*荒野纹饰*/addRecipe(ItemBase.WILD_ATST     , Blocks.MOSSY_COBBLESTONE, event);
        /*镶铆纹饰*/addRecipe(ItemBase.BOLT_ATST     , Blocks.GOLD_BLOCK, event);
        /*涡流纹饰*/addRecipe(ItemBase.FLOW_ATST     , new ItemStack(Item.getByNameOrId("deeperdepths:material"), 1, 3), event);
    }

    private static void addRecipe(ModItemSmithingTemplate smithingTemplate, Object material, RegistryEvent.Register<IRecipe> event) {
        addRecipe(smithingTemplate, material, 0, event);
    }
    private static void addRecipe(ModItemSmithingTemplate smithingTemplate, Block block, int minMeta, int maxMeta, RegistryEvent.Register<IRecipe> event) {
        ItemStack[] stacks = IntStream.rangeClosed(minMeta, maxMeta)
            .mapToObj(meta -> new ItemStack(block, 1, meta))
            .toArray(ItemStack[]::new);
        addRecipe(smithingTemplate, stacks, 0, event);
    }
    private static void addRecipe(ModItemSmithingTemplate smithingTemplate, Object material, int mate, RegistryEvent.Register<IRecipe> event) {
        if (material instanceof String) {
            String oreDict = (String) material;
            if (OreDictionary.getOres(oreDict).isEmpty()) return;
            material = new OreIngredient(oreDict);
        }
        else if (material instanceof Block) {
            material = new ItemStack((Block) material, 1, mate);
            if (!ItemBase.isValidItemStack((ItemStack) material)) return;
        }
        else if (material instanceof ItemStack[]) {
            material = Ingredient.fromStacks((ItemStack[]) material);
        }
        else if (material instanceof ItemStack) {
            if (!ItemBase.isValidItemStack((ItemStack) material)) return;
        }
        else { return; }

        ResourceLocation res = new ResourceLocation(SuiKe.MODID, smithingTemplate.getPatternType());
        event.getRegistry().register(
            new ShapedOreRecipe(
                res,
                new ItemStack(smithingTemplate, 2),
                "BAB",
                "BCB",
                "BBB",
                'A', new ItemStack(smithingTemplate),
                'B', new ItemStack(Items.DIAMOND),
                'C', material
            ).setRegistryName(res)
        );
    }
}