package suike.suikecherry.recipe;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sblock.BlockBase;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.registry.GameRegistry;
//熔炉配方
public class FurnaceRecipe {
    public static void register() {
        register(new ItemStack(BlockBase.CHERRY_LOG), new ItemStack(Items.COAL, 1, 1));
        register(new ItemStack(BlockBase.CHERRY_WOOD), new ItemStack(Items.COAL, 1, 1));
        register(new ItemStack(BlockBase.CHERRY_STRIPPED_LOG), new ItemStack(Items.COAL, 1, 1));
        register(new ItemStack(BlockBase.CHERRY_STRIPPED_WOOD), new ItemStack(Items.COAL, 1, 1));
    }

//注册配方
    public static void register(ItemStack inputStack, ItemStack outputStack) {
        /*熔炉-配方注册*/GameRegistry.addSmelting(inputStack, outputStack, 0.15f);
    }
}