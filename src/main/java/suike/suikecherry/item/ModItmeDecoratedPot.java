package suike.suikecherry.item;

import suike.suikecherry.block.ModBlockDecoratedPot;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;

// 陶罐物品类
public class ModItmeDecoratedPot extends ItemBlock {
    public ModItmeDecoratedPot(ModBlockDecoratedPot decoratedPot) {
        super(decoratedPot);
        this.setRegistryName(decoratedPot.getRegistryName());

        /*添加到考古学物品栏列表*/ModItemBrush.ARCHAEOLOGY_ITEMS.add(this);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == CreativeTabs.DECORATIONS || tab == CreativeTabs.REDSTONE) {
            items.add(new ItemStack(this));
        }
    }
}