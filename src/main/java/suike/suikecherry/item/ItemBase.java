package suike.suikecherry.item;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.entity.ModEntity;
import suike.suikecherry.oredict.OreDict;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ItemBase extends Item {
//物品
    public static final List<Item> ITEMS = new ArrayList<>();

    /*樱花籽*/public static ModItemSapling CHERRY_SEED = null;
    /*樱花树苗*/public static final ModItemSapling CHERRY_SAPLING = new ModItemSapling("cherry_sapling", BlockBase.CHERRY_SAPLING, BlockBase.CHERRY_SAPLING_POT);
    /*落英*/public static final ModItemPetals PINK_PETALS = new ModItemPetals("pink_petals", BlockBase.PINK_PETALS);
    /*樱花木门*/public static final ModItemDoor CHERRY_DOOR = new ModItemDoor("cherry_door", BlockBase.CHERRY_DOOR);
    /*樱花木台阶*/public static final ModItemSlab CHERRY_SLAB = new ModItemSlab("cherry_slab", BlockBase.CHERRY_SLAB, BlockBase.CHERRY_SLAB_DOUBLE);
    /*樱花木船*/public static final ModItemBoat CHERRY_BOAT = new ModItemBoat("cherry_boat", ModEntity.entityCherryBoat);
    /*樱花木运输船*/public static final ModItemBoat CHERRY_CHEST_BOAT = new ModItemBoat("cherry_chest_boat", ModEntity.entityCherryChestBoat);
    /*樱花木告示牌*/public static final ModItemSign CHERRY_SIGN = new ModItemSign("cherry_sign", BlockBase.CHERRY_SIGN, BlockBase.CHERRY_SIGN_WALL);
    /*樱花木悬挂式告示牌*/public static final ModItemHangingSign CHERRY_HANGING_SIGN = new ModItemHangingSign("cherry_hanging_sign", BlockBase.CHERRY_HANGING_SIGN, BlockBase.CHERRY_HANGING_SIGN_WALL, BlockBase.CHERRY_HANGING_SIGN_ATTACHED);

    /*橡木悬挂式告示牌*/public static final ModItemHangingSign OAK_HANGING_SIGN = new ModItemHangingSign("oak_hanging_sign", BlockBase.OAK_HANGING_SIGN, BlockBase.OAK_HANGING_SIGN_WALL, BlockBase.OAK_HANGING_SIGN_ATTACHED);
    /*云杉木悬挂式告示牌*/public static final ModItemHangingSign SPRUCE_HANGING_SIGN = new ModItemHangingSign("spruce_hanging_sign", BlockBase.SPRUCE_HANGING_SIGN, BlockBase.SPRUCE_HANGING_SIGN_WALL, BlockBase.SPRUCE_HANGING_SIGN_ATTACHED);
    /*白桦木悬挂式告示牌*/public static final ModItemHangingSign BIRCH_HANGING_SIGN = new ModItemHangingSign("birch_hanging_sign", BlockBase.BIRCH_HANGING_SIGN, BlockBase.BIRCH_HANGING_SIGN_WALL, BlockBase.BIRCH_HANGING_SIGN_ATTACHED);
    /*丛林木悬挂式告示牌*/public static final ModItemHangingSign JUNGLE_HANGING_SIGN = new ModItemHangingSign("jungle_hanging_sign", BlockBase.JUNGLE_HANGING_SIGN, BlockBase.JUNGLE_HANGING_SIGN_WALL, BlockBase.JUNGLE_HANGING_SIGN_ATTACHED);
    /*金合欢木悬挂式告示牌*/public static final ModItemHangingSign ACACIA_HANGING_SIGN = new ModItemHangingSign("acacia_hanging_sign", BlockBase.ACACIA_HANGING_SIGN, BlockBase.ACACIA_HANGING_SIGN_WALL, BlockBase.ACACIA_HANGING_SIGN_ATTACHED);
    /*深色橡木悬挂式告示牌*/public static final ModItemHangingSign DARK_OAK_HANGING_SIGN = new ModItemHangingSign("dark_oak_hanging_sign", BlockBase.DARK_OAK_HANGING_SIGN, BlockBase.DARK_OAK_HANGING_SIGN_WALL, BlockBase.DARK_OAK_HANGING_SIGN_ATTACHED);
    /*竹悬挂式告示牌*/public static ModItemHangingSign BAMBOO_HANGING_SIGN = null;
    /*绯红木悬挂式告示牌*/public static ModItemHangingSign CRIMSON_HANGING_SIGN = null;
    /*诡异木悬挂式告示牌*/public static ModItemHangingSign WARPED_HANGING_SIGN = null;

//构造函数
    public ItemBase(String name, CreativeTabs tabs) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/this.setCreativeTab(tabs);

        /*添加到ITEMS列表*/ITEMS.add(this);
    }

    static {
        if (Examine.exnihilocreatioID) CHERRY_SEED = new ModItemSapling("cherry_seed", BlockBase.CHERRY_SAPLING);
        if (Examine.FuturemcID) BAMBOO_HANGING_SIGN = new ModItemHangingSign("bamboo_hanging_sign", BlockBase.BAMBOO_HANGING_SIGN, BlockBase.BAMBOO_HANGING_SIGN_WALL, BlockBase.BAMBOO_HANGING_SIGN_ATTACHED);
        if (Examine.UNBID) CRIMSON_HANGING_SIGN = new ModItemHangingSign("crimson_hanging_sign", BlockBase.CRIMSON_HANGING_SIGN, BlockBase.CRIMSON_HANGING_SIGN_WALL, BlockBase.CRIMSON_HANGING_SIGN_ATTACHED);
        if (Examine.UNBID) WARPED_HANGING_SIGN = new ModItemHangingSign("warped_hanging_sign", BlockBase.WARPED_HANGING_SIGN, BlockBase.WARPED_HANGING_SIGN_WALL, BlockBase.WARPED_HANGING_SIGN_ATTACHED);
    }

//注册物品
    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        ITEMS.addAll(BlockBase.ITEMBLOCKS);
        event.getRegistry().registerAll(ITEMS.toArray(new Item[0]));

        OreDict.oreDict();
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
            if (itemStack == null || itemStack.isEmpty()) {
                return false;
            }

            String stackString = itemStack.toString().replaceAll("^\\d+x", "");
            if (
                stackString.startsWith("item.null") ||
                stackString.startsWith("tile.air") ||
                stackString.equals("item.") ||
                stackString.equals("tile.")
               ) 
            {
                return false;//有一个无效, 返回false
            }
        }
        return true;//全部有效, 返回true
    }

//创造模式玩家增加物品
    public static void giveCreativePlayerItem(EntityPlayer player, ItemStack itemStack) {
        boolean giveItem = true;
        for (ItemStack inBagItem : player.field_71071_by.mainInventory) { // 使用混淆名获取背包
            if (ItemStack.areItemsEqual(inBagItem, itemStack)) {
                giveItem = false; // 背包有此物品不再添加物品
                break;
            }
        }
        if (giveItem) {
            player.func_191521_c(itemStack); // 使用混淆名添加物品
        }
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