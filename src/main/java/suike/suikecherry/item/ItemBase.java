package suike.suikecherry.item;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.block.ModBlockSmithingTable;
import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.sound.ModSoundType;
import suike.suikecherry.sound.ModSoundEvent;
import suike.suikecherry.entity.boat.boat.*;
import suike.suikecherry.entity.boat.chestboat.*;
import suike.suikecherry.entity.sniffer.SnifferEntity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ItemBase {
// 物品
    public static final List<Item> ITEMS = new ArrayList<>();

    /*樱花籽*/public static ModItemSapling CHERRY_SEED = null;
    /*樱花树苗*/public static final ModItemSapling CHERRY_SAPLING = new ModItemSapling("cherry_sapling", BlockBase.CHERRY_SAPLING, BlockBase.CHERRY_SAPLING_POT, BlockBase.CHERRY_LEAVES);
    /*落英*/public static final ModItemPetals PINK_PETALS = BlockBase.PINK_PETALS.getItemPetals();
    /*樱花木门*/public static final ModItemDoor CHERRY_DOOR = new ModItemDoor("cherry_door", BlockBase.CHERRY_DOOR);
    /*樱花木台阶*/public static final ModItemSlab CHERRY_SLAB = new ModItemSlab("cherry_slab", BlockBase.CHERRY_SLAB, BlockBase.CHERRY_SLAB_DOUBLE);

    //*橡木告示牌*/public static final ModItemSign OAK_SIGN = new ModItemSign("oak_sign", BlockBase.OAK_SIGN, BlockBase.OAK_SIGN_WALL);
    /*悬挂式橡木告示牌*/public static final ModItemHangingSign OAK_HANGING_SIGN = new ModItemHangingSign("oak_hanging_sign", BlockBase.OAK_HANGING_SIGN, BlockBase.OAK_HANGING_SIGN_WALL, BlockBase.OAK_HANGING_SIGN_ATTACHED);
    /*樱花木告示牌*/public static final ModItemSign CHERRY_SIGN = new ModItemSign("cherry_sign", BlockBase.CHERRY_SIGN, BlockBase.CHERRY_SIGN_WALL);
    /*悬挂式樱花木告示牌*/public static final ModItemHangingSign CHERRY_HANGING_SIGN = new ModItemHangingSign("cherry_hanging_sign", BlockBase.CHERRY_HANGING_SIGN, BlockBase.CHERRY_HANGING_SIGN_WALL, BlockBase.CHERRY_HANGING_SIGN_ATTACHED);
    /*云杉木告示牌*/public static final ModItemSign SPRUCE_SIGN = new ModItemSign("spruce_sign", BlockBase.SPRUCE_SIGN, BlockBase.SPRUCE_SIGN_WALL);
    /*悬挂式云杉木告示牌*/public static final ModItemHangingSign SPRUCE_HANGING_SIGN = new ModItemHangingSign("spruce_hanging_sign", BlockBase.SPRUCE_HANGING_SIGN, BlockBase.SPRUCE_HANGING_SIGN_WALL, BlockBase.SPRUCE_HANGING_SIGN_ATTACHED);
    /*白桦木告示牌*/public static final ModItemSign BIRCH_SIGN = new ModItemSign("birch_sign", BlockBase.BIRCH_SIGN, BlockBase.BIRCH_SIGN_WALL);
    /*悬挂式白桦木告示牌*/public static final ModItemHangingSign BIRCH_HANGING_SIGN = new ModItemHangingSign("birch_hanging_sign", BlockBase.BIRCH_HANGING_SIGN, BlockBase.BIRCH_HANGING_SIGN_WALL, BlockBase.BIRCH_HANGING_SIGN_ATTACHED);
    /*丛林木告示牌*/public static final ModItemSign JUNGLE_SIGN = new ModItemSign("jungle_sign", BlockBase.JUNGLE_SIGN, BlockBase.JUNGLE_SIGN_WALL);
    /*悬挂式丛林木告示牌*/public static final ModItemHangingSign JUNGLE_HANGING_SIGN = new ModItemHangingSign("jungle_hanging_sign", BlockBase.JUNGLE_HANGING_SIGN, BlockBase.JUNGLE_HANGING_SIGN_WALL, BlockBase.JUNGLE_HANGING_SIGN_ATTACHED);
    /*金合欢木告示牌*/public static final ModItemSign ACACIA_SIGN = new ModItemSign("acacia_sign", BlockBase.ACACIA_SIGN, BlockBase.ACACIA_SIGN_WALL);
    /*悬挂式金合欢木告示牌*/public static final ModItemHangingSign ACACIA_HANGING_SIGN = new ModItemHangingSign("acacia_hanging_sign", BlockBase.ACACIA_HANGING_SIGN, BlockBase.ACACIA_HANGING_SIGN_WALL, BlockBase.ACACIA_HANGING_SIGN_ATTACHED);
    /*深色橡木告示牌*/public static final ModItemSign DARK_OAK_SIGN = new ModItemSign("dark_oak_sign", BlockBase.DARK_OAK_SIGN, BlockBase.DARK_OAK_SIGN_WALL);
    /*悬挂式深色橡木告示牌*/public static final ModItemHangingSign DARK_OAK_HANGING_SIGN = new ModItemHangingSign("dark_oak_hanging_sign", BlockBase.DARK_OAK_HANGING_SIGN, BlockBase.DARK_OAK_HANGING_SIGN_WALL, BlockBase.DARK_OAK_HANGING_SIGN_ATTACHED);
    /*竹告示牌*/public static ModItemSign BAMBOO_SIGN = null;
    /*悬挂式竹告示牌*/public static ModItemHangingSign BAMBOO_HANGING_SIGN = null;
    /*绯红木告示牌*/public static ModItemSign CRIMSON_SIGN = null;
    /*悬挂式绯红木告示牌*/public static ModItemHangingSign CRIMSON_HANGING_SIGN = null;
    /*诡异木告示牌*/public static ModItemSign WARPED_SIGN = null;
    /*悬挂式诡异木告示牌*/public static ModItemHangingSign WARPED_HANGING_SIGN = null;

    /*嗅探兽生物蛋*/public static final ModItemEntityEgg SNIFFER_SPAWN_EGG = new ModItemEntityEgg("sniffer_spawn_egg", SnifferEntity.class);

    /*樱花木船*/public static final ModItemBoat CHERRY_BOAT = new ModItemBoat("cherry_boat", CherryBoat.class);
    /*竹筏*/public static final ModItemBoat BAMBOO_RAFT = new ModItemBoat("bamboo_raft", BambooRaft.class);
    /*樱花木运输船*/public static ModItemBoat CHERRY_CHEST_BOAT = null;
    /*运输竹筏*/public static ModItemBoat BAMBOO_CHEST_RAFT = null;
    /*橡木运输船*/public static ModItemBoat OAK_CHEST_BOAT = null;
    /*云杉木运输船*/public static ModItemBoat SPRUCE_CHEST_BOAT = null;
    /*白桦木运输船*/public static ModItemBoat BIRCH_CHEST_BOAT = null;
    /*丛林木运输船*/public static ModItemBoat JUNGLE_CHEST_BOAT = null;
    /*金合欢木运输船*/public static ModItemBoat ACACIA_CHEST_BOAT = null;
    /*深色橡木运输船*/public static ModItemBoat DARK_OAK_CHEST_BOAT = null;

    /*刷子*/public static final ModItemBrush BRUSH = new ModItemBrush("brush");
    /*唱片-Relic*/public static final ModItemRecord MUSIC_DISC_RELIC = new ModItemRecord("music_disc_relic", "music_disc.relic");
    /*爱心纹样*/public static final ModItemPotterySherd HEART_POTTERY_SHERD = new ModItemPotterySherd("heart_pottery_sherd");
    /*心碎纹样*/public static final ModItemPotterySherd HEARTBREAK_POTTERY_SHERD = new ModItemPotterySherd("heartbreak_pottery_sherd");
    /*垂钓纹样*/public static final ModItemPotterySherd ANGLER_POTTERY_SHERD = new ModItemPotterySherd("angler_pottery_sherd");
    /*弓箭纹样*/public static final ModItemPotterySherd ARCHER_POTTERY_SHERD = new ModItemPotterySherd("archer_pottery_sherd");
    /*佳酿纹样*/public static final ModItemPotterySherd BREWER_POTTERY_SHERD = new ModItemPotterySherd("brewer_pottery_sherd");
    /*探险纹样*/public static final ModItemPotterySherd EXPLORER_POTTERY_SHERD = new ModItemPotterySherd("explorer_pottery_sherd");
    /*悲恸纹样*/public static final ModItemPotterySherd MOURNER_POTTERY_SHERD = new ModItemPotterySherd("mourner_pottery_sherd");
    /*嗅探纹样*/public static final ModItemPotterySherd SNORT_POTTERY_SHERD = new ModItemPotterySherd("snort_pottery_sherd");
    /*危机纹样*/public static final ModItemPotterySherd DANGER_POTTERY_SHERD = new ModItemPotterySherd("danger_pottery_sherd");
    /*富饶纹样*/public static final ModItemPotterySherd PLENTY_POTTERY_SHERD = new ModItemPotterySherd("plenty_pottery_sherd");
    /*珍宝纹样*/public static final ModItemPotterySherd PRIZE_POTTERY_SHERD = new ModItemPotterySherd("prize_pottery_sherd");
    /*麦捆纹样*/public static final ModItemPotterySherd SHEAF_POTTERY_SHERD = new ModItemPotterySherd("sheaf_pottery_sherd");
    /*头颅纹样*/public static final ModItemPotterySherd SKULL_POTTERY_SHERD = new ModItemPotterySherd("skull_pottery_sherd");
    /*举臂纹样*/public static final ModItemPotterySherd ARMS_UP_POTTERY_SHERD = new ModItemPotterySherd("arms_up_pottery_sherd");
    /*利刃纹样*/public static final ModItemPotterySherd BLADE_POTTERY_SHERD = new ModItemPotterySherd("blade_pottery_sherd");
    /*烈焰纹样*/public static final ModItemPotterySherd BURN_POTTERY_SHERD = new ModItemPotterySherd("burn_pottery_sherd");
    /*挚友纹样*/public static final ModItemPotterySherd FRIEND_POTTERY_SHERD = new ModItemPotterySherd("friend_pottery_sherd");
    /*狼嚎纹样*/public static final ModItemPotterySherd HOWL_POTTERY_SHERD = new ModItemPotterySherd("howl_pottery_sherd");
    /*采矿纹样*/public static final ModItemPotterySherd MINER_POTTERY_SHERD = new ModItemPotterySherd("miner_pottery_sherd");
    /*树荫纹样*/public static final ModItemPotterySherd SHELTER_POTTERY_SHERD = new ModItemPotterySherd("shelter_pottery_sherd");
    /*涡流纹样*/public static final ModItemPotterySherd FLOW_POTTERY_SHERD = new ModItemPotterySherd("flow_pottery_sherd");
    /*刮削纹样*/public static final ModItemPotterySherd SCRAPE_POTTERY_SHERD = new ModItemPotterySherd("scrape_pottery_sherd");
    /*旋风纹样*/public static final ModItemPotterySherd GUSTER_POTTERY_SHERD = new ModItemPotterySherd("guster_pottery_sherd");

    /*海岸纹饰*/public static ModItemSmithingTemplate COAST_ATST = null;
    /*沙丘纹饰*/public static ModItemSmithingTemplate DUNE_ATST = null;
    /*眼眸纹饰*/public static ModItemSmithingTemplate EYE_ATST = null;
    /*雇主纹饰*/public static ModItemSmithingTemplate HOST_ATST = null;
    /*牧民纹饰*/public static ModItemSmithingTemplate RAISER_ATST = null;
    /*肋骨纹饰*/public static ModItemSmithingTemplate RIB_ATST = null;
    /*哨兵纹饰*/public static ModItemSmithingTemplate SENTRY_ATST = null;
    /*塑造纹饰*/public static ModItemSmithingTemplate SHAPER_ATST = null;
    /*幽静纹饰*/public static ModItemSmithingTemplate SILENCE_ATST = null;
    /*猪鼻纹饰*/public static ModItemSmithingTemplate SNOUT_ATST = null;
    /*尖塔纹饰*/public static ModItemSmithingTemplate SPIRE_ATST = null;
    /*潮汐纹饰*/public static ModItemSmithingTemplate TIDE_ATST = null;
    /*恼鬼纹饰*/public static ModItemSmithingTemplate VEX_ATST = null;
    /*监守纹饰*/public static ModItemSmithingTemplate WARD_ATST = null;
    /*向导纹饰*/public static ModItemSmithingTemplate WAYFINDER_ATST = null;
    /*荒野纹饰*/public static ModItemSmithingTemplate WILD_ATST = null;
    /*镶铆纹饰*/public static ModItemSmithingTemplate BOLT_ATST = null;
    /*涡流纹饰*/public static ModItemSmithingTemplate FLOW_ATST = null;

    static {
        if (Examine.hasMixin()) {
            COAST_ATST = new ModItemSmithingTemplate("coast_armor_trim_smithing_template");
            DUNE_ATST = new ModItemSmithingTemplate("dune_armor_trim_smithing_template");
            EYE_ATST = new ModItemSmithingTemplate("eye_armor_trim_smithing_template");
            HOST_ATST = new ModItemSmithingTemplate("host_armor_trim_smithing_template");
            RAISER_ATST = new ModItemSmithingTemplate("raiser_armor_trim_smithing_template");
            RIB_ATST = new ModItemSmithingTemplate("rib_armor_trim_smithing_template");
            SENTRY_ATST = new ModItemSmithingTemplate("sentry_armor_trim_smithing_template", "raids", "chests/pillager_outpost");
            SHAPER_ATST = new ModItemSmithingTemplate("shaper_armor_trim_smithing_template");
            SILENCE_ATST = new ModItemSmithingTemplate("silence_armor_trim_smithing_template");
            SNOUT_ATST = new ModItemSmithingTemplate("snout_armor_trim_smithing_template");
            SPIRE_ATST = new ModItemSmithingTemplate("spire_armor_trim_smithing_template");
            TIDE_ATST = new ModItemSmithingTemplate("tide_armor_trim_smithing_template");
            VEX_ATST = new ModItemSmithingTemplate("vex_armor_trim_smithing_template");
            WARD_ATST = new ModItemSmithingTemplate("ward_armor_trim_smithing_template");
            WAYFINDER_ATST = new ModItemSmithingTemplate("wayfinder_armor_trim_smithing_template");
            WILD_ATST = new ModItemSmithingTemplate("wild_armor_trim_smithing_template");
            BOLT_ATST = new ModItemSmithingTemplate("bolt_armor_trim_smithing_template");
            FLOW_ATST = new ModItemSmithingTemplate("flow_armor_trim_smithing_template");
        }
        if (Examine.exnihilocreatioID) {
            CHERRY_SEED = new ModItemSapling("cherry_seed", BlockBase.CHERRY_SAPLING);
        }
        if (ConfigValue.hasBambo) {
            BAMBOO_SIGN = new ModItemSign("bamboo_sign", BlockBase.BAMBOO_SIGN, BlockBase.BAMBOO_SIGN_WALL);
            BAMBOO_HANGING_SIGN = new ModItemHangingSign("bamboo_hanging_sign", BlockBase.BAMBOO_HANGING_SIGN, BlockBase.BAMBOO_HANGING_SIGN_WALL, BlockBase.BAMBOO_HANGING_SIGN_ATTACHED);
        }
        if (Examine.UNBID) {
            CRIMSON_SIGN = new ModItemSign("crimson_sign", BlockBase.CRIMSON_SIGN, BlockBase.CRIMSON_SIGN_WALL);
            CRIMSON_HANGING_SIGN = new ModItemHangingSign("crimson_hanging_sign", BlockBase.CRIMSON_HANGING_SIGN, BlockBase.CRIMSON_HANGING_SIGN_WALL, BlockBase.CRIMSON_HANGING_SIGN_ATTACHED);
            WARPED_SIGN = new ModItemSign("warped_sign", BlockBase.WARPED_SIGN, BlockBase.WARPED_SIGN_WALL);
            WARPED_HANGING_SIGN = new ModItemHangingSign("warped_hanging_sign", BlockBase.WARPED_HANGING_SIGN, BlockBase.WARPED_HANGING_SIGN_WALL, BlockBase.WARPED_HANGING_SIGN_ATTACHED);
        }
        if (ConfigValue.addBoatChest) {
            CHERRY_CHEST_BOAT = new ModItemBoat("cherry_chest_boat", CherryChestBoat.class);
            BAMBOO_CHEST_RAFT = new ModItemBoat("bamboo_chest_raft", BambooChestRaft.class);
            OAK_CHEST_BOAT = new ModItemBoat("oak_chest_boat", OakChestBoat.class);
            SPRUCE_CHEST_BOAT = new ModItemBoat("spruce_chest_boat", SpruceChestBoat.class);
            BIRCH_CHEST_BOAT = new ModItemBoat("birch_chest_boat", BirchChestBoat.class);
            JUNGLE_CHEST_BOAT = new ModItemBoat("jungle_chest_boat", JungleChestBoat.class);
            ACACIA_CHEST_BOAT = new ModItemBoat("acacia_chest_boat", AcaciaChestBoat.class);
            DARK_OAK_CHEST_BOAT = new ModItemBoat("dark_oak_chest_boat", DarkOakChestBoat.class);
        }
    }

// 注册物品
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        for (Item item : ITEMS) {
            event.getRegistry().register(item);
        }
        event.getRegistry().registerAll(BlockBase.ITEMBLOCKS.toArray(new Item[0]));
        ModBlockSmithingTable.registerRecipe();
    }

// 注册模型
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Item item : ITEMS) {
            registerModels(item);
        }
    }
    public static void registerModels(Item item) {
        SuiKe.proxy.registerItemRenderer(item);
    }

// 通过 ItemStack 物品获取矿词
    public static String getItemOreDict(ItemStack stack) {
        return getItemOreDict(stack, null);
    }
    public static String getItemOreDict(ItemStack stack, String needOreDict) {
        return getItemOreDict(stack, needOreDict, false);
    }
    public static String getItemOreDict(ItemStack stack, String needOreDict, boolean contain) {
        if (!isValidItemStack(stack)) return null;

        int[] oreIDs = OreDictionary.getOreIDs(stack); // 物品矿词列表

        // 矿词列表为空
        if (oreIDs.length == 0) return null;

        // 如果没有指定要求的矿词直接返回第一个
        if (needOreDict == null) {
            return OreDictionary.getOreName(oreIDs[0]);
        }

        for (int id : oreIDs) {
            String oreDict = OreDictionary.getOreName(id); // 获取矿词

            // 包含
            if (contain && oreDict.contains(needOreDict)) {
                return oreDict;
            }
            // 正常对比
            else if (oreDict.equals(needOreDict)) {
                return oreDict;
            }
        }

        return null; // 未找到匹配的矿词
    }

// 检查 ItemStack
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
                return false; // 有一个无效, 返回false
            }
        }
        return true; // 全部有效, 返回true
    }

// 相同物品
    public static boolean itemsEqual(ItemStack item1, ItemStack item2) {
        return isValidItemStack(item1, item2)                // 有效物品
            && ItemStack.areItemsEqual(item1, item2)         // 相同 Item
            && ItemStack.areItemStackTagsEqual(item1, item2) // 相同 tag
            && item1.getMetadata() == item2.getMetadata();   // 相同 元数据
    }

// 给玩家物品物品
    public static void givePlayerItem(EntityPlayer player, ItemStack... itemStacks) {
        givePlayerItem(player, true, itemStacks);
    }
    public static void givePlayerItem(EntityPlayer player, boolean tryTeplace, ItemStack... itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            if (!isValidItemStack(itemStack)) continue;

            // 获取玩家背包
            InventoryPlayer inventory = player.field_71071_by;

            // 手部为空直接替换
            if (tryTeplace && inventory.getStackInSlot(inventory.currentItem).isEmpty()) {
                inventory.setInventorySlotContents(inventory.currentItem, itemStack);
                return;
            }

            // 创造模式判断
            if (player.field_71075_bZ.isCreativeMode) {
                for (ItemStack inBagItem : inventory.mainInventory) {
                    if (itemsEqual(inBagItem, itemStack) && inBagItem.getCount() >= inBagItem.getMaxStackSize()) {
                        return; // 背包有此物品 且到达堆叠上限
                    }
                }
            }

            // 尝试添加物品
            if (!player.func_191521_c(itemStack)) {
                // 添加失败, 生成物品
                Block.spawnAsEntity(
                    player.world,
                    player.getPosition().up(),
                    itemStack
                );
            }
        }
    }

// 获取更精确的玩家方向
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