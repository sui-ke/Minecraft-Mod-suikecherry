package suike.suikecherry.sitem;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.sblock.BlockBase;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ItemBase extends Item implements SItem {
//物品
    public static final List<Item> ITEMS = new ArrayList<>();

    /*樱花树苗*/public static final SItemSapling CHERRY_SAPLING = new SItemSapling("cherry_sapling", CreativeTabs.DECORATIONS, true);
    /*樱花籽*/public static final SItemSapling CHERRY_SEED = new SItemSapling("cherry_seed", CreativeTabs.MATERIALS, Examine.exnihilocreatioID);
    /*落英*/public static final SItemPetals PINK_PETALS = new SItemPetals("pink_petals", CreativeTabs.DECORATIONS);
    /*樱花木门*/public static final SItemDoor CHERRY_DOOR = new SItemDoor("cherry_door", CreativeTabs.REDSTONE);
    /*樱花木台阶*/public static final SItemSlab CHERRY_SLAB = new SItemSlab("cherry_slab", CreativeTabs.BUILDING_BLOCKS);
    /*樱花木船*/public static final Item CHERRY_BOAT = new SItemBoat("cherry_boat", CreativeTabs.TRANSPORTATION);
    /*樱花木告示牌*/public static final SItemSign CHERRY_SIGN = new SItemSign("cherry_sign", CreativeTabs.DECORATIONS);
    /*樱花木悬挂式告示牌*/public static final SItemHangingSign CHERRY_HANGING_SIGN = new SItemHangingSign("cherry_hanging_sign", CreativeTabs.DECORATIONS);

//构造函数
    public ItemBase(String name, CreativeTabs tabs) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/setCreativeTab(tabs);

        /*添加到ITEMS列表*/ITEMS.add(this);
    }

//设置物品对应的方块
    public static void setItemBlock() {
        /*落英*/ItemBase.PINK_PETALS.setBlock(BlockBase.PINK_PETALS);
        /*樱花木门*/ItemBase.CHERRY_DOOR.setBlock(BlockBase.CHERRY_DOOR);
        /*樱花木台阶*/ItemBase.CHERRY_SLAB.setBlock(BlockBase.CHERRY_SLAB, BlockBase.CHERRY_SLAB_DOUBLE);
        /*樱花树苗*/ItemBase.CHERRY_SEED.setBlock(BlockBase.CHERRY_SAPLING, null);
        /*樱花树苗*/ItemBase.CHERRY_SAPLING.setBlock(BlockBase.CHERRY_SAPLING, BlockBase.CHERRY_SAPLING_POT);
        /*樱花木告示牌*/ItemBase.CHERRY_SIGN.setBlock(BlockBase.CHERRY_SIGN, BlockBase.CHERRY_SIGN_WALL);
        /*樱花木悬挂式告示牌*/ItemBase.CHERRY_HANGING_SIGN.setBlock(BlockBase.CHERRY_HANGING_SIGN, BlockBase.CHERRY_HANGING_SIGN_WALL, BlockBase.CHERRY_HANGING_SIGN_ATTACHED);
    }

//注册物品
    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ITEMS.toArray(new Item[0]));
    }

//注册模型
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Item item : ITEMS) {
            registerModels(item);
        }
    }
    public static void registerModels(Item item) {
        SuiKe.proxy.registerItemRenderer(item, 0, "inventory");
    }

//检查ItemStack
    public static boolean isValidItemStack(ItemStack... itemStackList) {
        for (ItemStack itemStack : itemStackList) {
            if (itemStack.isEmpty()) {
                return false;
            }

            String stackString = itemStack.toString().replaceAll("^\\d+x", "");
            if (stackString.matches("item.null@\\d+") ||
                stackString.matches("tile.air@\\d+") ||
                stackString.matches("item.@\\d+") ||
                stackString.matches("tile.@\\d+") ||
                stackString.equals("item.") ||
                stackString.equals("tile.")) 
            {
                return false;//有一个无效, 返回false
            }
        }
        return true;//全部有效, 返回true
    }

//获取更精确的玩家方向
    public static int getPlayerFacing(float angle) {
        while (angle < 0) {
            angle = angle + 360;
        }
        angle = angle + 180;
        angle = angle % 360;

        if (angle > 11.25 && angle <= 33.75)
            return 1;
        if (angle > 33.75 && angle <= 56.25)
            return 2;
        if (angle > 56.25 && angle <= 78.75)
            return 3;
        if (angle > 78.75 && angle <= 101.25)
            return 4;
        if (angle > 101.25 && angle <= 123.75)
            return 5;
        if (angle > 123.75 && angle <= 146.25)
            return 6;
        if (angle > 146.25 && angle <= 168.75)
            return 7;
        if (angle > 168.75 && angle <= 191.25)
            return 8;
        if (angle > 191.25 && angle <= 213.75)
            return 9;
        if (angle > 213.75 && angle <= 236.25)
            return 10;
        if (angle > 236.25 && angle <= 258.75)
            return 11;
        if (angle > 258.75 && angle <= 281.25)
            return 12;
        if (angle > 281.25 && angle <= 303.75)
            return 13;
        if (angle > 303.75 && angle <= 326.25)
            return 14;
        if (angle > 326.25 && angle <= 348.75)
            return 15;
        return 0;
    }
}