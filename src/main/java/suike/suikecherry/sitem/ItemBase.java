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

    /*樱花树苗*/public static final Item CHERRY_SAPLING = new SItemSapling("cherry_sapling", CreativeTabs.DECORATIONS, true);
    /*樱花籽*/public static final Item CHERRY_SEED = new SItemSapling("cherry_seed", CreativeTabs.MATERIALS, Examine.exnihilocreatioID);
    /*落英*/public static final Item PINK_PETALS = new SItemPetals("pink_petals", CreativeTabs.DECORATIONS);
    /*樱花木门*/public static final Item CHERRY_DOOR = new SItemDoor("cherry_door", CreativeTabs.REDSTONE);
    /*樱花木台阶*/public static final Item CHERRY_SLAB = new SItemSlab("cherry_slab", CreativeTabs.BUILDING_BLOCKS);
    /*樱花木船*/public static final Item CHERRY_BOAT = new SItemBoat("cherry_boat", CreativeTabs.TRANSPORTATION);

//构造函数
    public ItemBase(String name, CreativeTabs tabs) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/setCreativeTab(tabs);

        /*添加到ITEMS列表*/ITEMS.add(this);
    }

//设置物品对应的方块
    public static void setItemBlock() {
        /*设置树苗方块*/((SItemSapling) ItemBase.CHERRY_SAPLING).setBlock(BlockBase.CHERRY_SAPLING);
        /*设置树苗方块*/((SItemSapling) ItemBase.CHERRY_SEED).setBlock(BlockBase.CHERRY_SAPLING);
        /*设置落英方块*/((SItemPetals) ItemBase.PINK_PETALS).setBlock(BlockBase.PINK_PETALS);
        /*设置门方块*/((SItemDoor) ItemBase.CHERRY_DOOR).setBlock(BlockBase.CHERRY_DOOR);
        /*设置台阶的方块*/((SItemSlab) ItemBase.CHERRY_SLAB).setBlock(BlockBase.CHERRY_SLAB);
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
}