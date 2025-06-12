package suike.suikecherry.item;

import java.util.List;
import java.util.ArrayList;

import suike.suikecherry.SuiKe;
import suike.suikecherry.recipe.RecipeDecoratedPot;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;

// 陶片物品
public class ModItemPotterySherd extends Item {
    public ModItemPotterySherd(String name) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        //*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.MATERIALS);
        this.potteryType = name.replace("sherd", "pattern");
        ALL_PATTERN_TYPE.add(this.potteryType);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
        /*添加到合成列表*/RecipeDecoratedPot.ALL_SHARDS.add(new ItemStack(this));
        /*添加到考古学物品栏列表*/ModItemBrush.ARCHAEOLOGY_ITEMS.add(this);
    }

    private static final List<String> ALL_PATTERN_TYPE = new ArrayList<>();
    public static List<String> getAllPotteryType() {
        return new ArrayList<>(ALL_PATTERN_TYPE);
    }

    private final String potteryType;
    public String getPotteryType() {
        return this.potteryType;
    }
}