package suike.suikecherry.recipe;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.BlockBase;

import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SignRecipe {
    public static void register() {
        signRecipe();
        hangingSignRecipe();
    }

    // 告示牌配方
    public static void signRecipe() {
        signRecipe(Blocks.PLANKS, 1, ItemBase.SPRUCE_SIGN, "spruce");
        signRecipe(Blocks.PLANKS, 2, ItemBase.BIRCH_SIGN, "birch");
        signRecipe(Blocks.PLANKS, 3, ItemBase.JUNGLE_SIGN, "jungle");
        signRecipe(Blocks.PLANKS, 4, ItemBase.ACACIA_SIGN, "acacia");
        signRecipe(Blocks.PLANKS, 5, ItemBase.DARK_OAK_SIGN, "dark_oak");
        signRecipe(BlockBase.CHERRY_PLANKS, 0, ItemBase.CHERRY_SIGN, "cherry");
        if (Examine.FuturemcID || Examine.sakuraID) {
            signRecipe("plankWoodBamboo", 0, ItemBase.BAMBOO_SIGN, "bamboo");
        }
        if (Examine.UNBID) {
            signRecipe(Block.getBlockFromName("nb:crimson_planks"), 0, ItemBase.CRIMSON_SIGN, "crimson");
            signRecipe(Block.getBlockFromName("nb:warped_planks"), 0, ItemBase.WARPED_SIGN, "warped");
        }
    }

    // 悬挂式告示牌配方
    public static void hangingSignRecipe() {
        // 樱花木悬挂式告示牌
        Item iron = Examine.FuturemcID ? Item.getByNameOrId("futuremc:chain") : Items.IRON_INGOT;

        String cherryLog = ConfigValue.stripLog ? "logStrippedCherry" : "logCherry";
        hangingSignRecipe(iron, cherryLog, 0, ItemBase.CHERRY_HANGING_SIGN, "cherry");

        Block block = ConfigValue.stripLog && Examine.FuturemcID ? Block.getBlockFromName("futuremc:stripped_oak_log") : Blocks.LOG;
        hangingSignRecipe(iron, block, 0, ItemBase.OAK_HANGING_SIGN, "oak");

        block = ConfigValue.stripLog && Examine.FuturemcID ? Block.getBlockFromName("futuremc:stripped_spruce_log") : Blocks.LOG;
        hangingSignRecipe(iron, block, 1, ItemBase.SPRUCE_HANGING_SIGN, "spruce");

        block = ConfigValue.stripLog && Examine.FuturemcID ? Block.getBlockFromName("futuremc:stripped_birch_log") : Blocks.LOG;
        hangingSignRecipe(iron, block, 2, ItemBase.BIRCH_HANGING_SIGN, "birch");

        block = ConfigValue.stripLog && Examine.FuturemcID ? Block.getBlockFromName("futuremc:stripped_jungle_log") : Blocks.LOG;
        hangingSignRecipe(iron, block, 3, ItemBase.JUNGLE_HANGING_SIGN, "jungle");

        block = ConfigValue.stripLog && Examine.FuturemcID ? Block.getBlockFromName("futuremc:stripped_acacia_log") : Blocks.LOG2;
        hangingSignRecipe(iron, block, 0, ItemBase.ACACIA_HANGING_SIGN, "acacia");

        block = ConfigValue.stripLog && Examine.FuturemcID ? Block.getBlockFromName("futuremc:stripped_dark_oak_log") : Blocks.LOG2;
        hangingSignRecipe(iron, block, 1, ItemBase.DARK_OAK_HANGING_SIGN, "dark_oak");

        if (ConfigValue.hasBamboo) {
            hangingSignRecipe(iron, "plankWoodBamboo", 0, ItemBase.BAMBOO_HANGING_SIGN, "bamboo");
        }

        if (Examine.UNBID) {
            block = Block.getBlockFromName("nb:crimson_stem");
            hangingSignRecipe(iron, block, 0, ItemBase.CRIMSON_HANGING_SIGN, "crimson");
    
            block = Block.getBlockFromName("nb:warped_stem");
            hangingSignRecipe(iron, block, 0, ItemBase.WARPED_HANGING_SIGN, "warped");
        }
    }

    public static void signRecipe(Object block, int meta, Item sign, String signType) {
        if (block == null) return;

        Object planks;
        if (block instanceof Block) {
            planks = new ItemStack((Block) block, 1, meta);
            if (!ItemBase.isValidItemStack((ItemStack) planks)) return;
        }
        else if (block instanceof String) {
            planks = new OreIngredient((String) block);
        }
        else {return;}

        ItemStack signStack = new ItemStack(sign, 3);

        if (!ItemBase.isValidItemStack(signStack)) return;

        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, signType + "_sign"),
            new ResourceLocation(SuiKe.MODID),
            signStack,
            "AAA",
            "AAA",
            "CBC",
            'A', planks,
            'B', new OreIngredient("stickWood"),
            'C', ItemStack.EMPTY
        );
    }

    public static void hangingSignRecipe(Item item, Object block, int meta, Item hangingSign, String hangingSignType) {
        if (block == null) return;
        if (Examine.FuturemcID) meta = 0;

        Object log;
        if (block instanceof Block) {
            log = new ItemStack((Block) block, 1, meta);
            if (!ItemBase.isValidItemStack((ItemStack) log)) return;
        }
        else if (block instanceof String) {
            log = new OreIngredient((String) block);
        }
        else {return;}

        ItemStack iron = new ItemStack(item);
        ItemStack hangingSignStack = new ItemStack(hangingSign, 6);

        if (!ItemBase.isValidItemStack(iron, hangingSignStack)) return;

        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, hangingSignType + "_hanging_sign"),
            new ResourceLocation(SuiKe.MODID),
            hangingSignStack,
            "ACA",
            "BBB",
            "BBB",
            'A', iron,
            'B', log,
            'C', ItemStack.EMPTY
        );
    }
}