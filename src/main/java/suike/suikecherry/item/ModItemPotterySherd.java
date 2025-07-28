package suike.suikecherry.item;

import java.util.List;
import java.util.ArrayList;

import suike.suikecherry.SuiKe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;

// 陶片物品
public class ModItemPotterySherd extends Item {
    public ModItemPotterySherd(String name) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        this.potteryType = name.replace("sherd", "pattern");
        ALL_POTTERY_TYPE.add(this.potteryType);
        ALL_SHARDS_ITEM.add(new ItemStack(this));

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    private final String potteryType;
    public String getPotteryType() {
        return this.potteryType;
    }

    private static final List<String> ALL_POTTERY_TYPE = new ArrayList<>();
    public static List<String> getAllPotteryType() {
        return new ArrayList<>(ALL_POTTERY_TYPE);
    }

    private static final List<ItemStack> ALL_SHARDS_ITEM = new ArrayList<>();
    public static List<ItemStack> getAllSherdItme() {
        return new ArrayList<>(ALL_SHARDS_ITEM);
    }

    private static String[] DEFAULT_POTTERY_TYPES = null;
    public static String[] getDefaultPotteryTypes() {
        if (DEFAULT_POTTERY_TYPES == null) {
            DEFAULT_POTTERY_TYPES = getAllPotteryType().toArray(new String[0]);
        }
        return DEFAULT_POTTERY_TYPES;
    }
}