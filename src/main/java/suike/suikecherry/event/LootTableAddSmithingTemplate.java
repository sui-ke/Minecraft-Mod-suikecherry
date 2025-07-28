package suike.suikecherry.event;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.item.ModItemSmithingTemplate;
import suike.suikecherry.world.gen.structure.LootTable;
import suike.suikecherry.expand.Examine;

import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.functions.*;
import net.minecraft.world.storage.loot.conditions.*;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.LootTableLoadEvent;

@Mod.EventBusSubscriber
public class LootTableAddSmithingTemplate {
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (!Examine.hasMixin()) return;

        /*沙丘纹饰*/addItem(ItemBase.DUNE_ATST    , 1, 2, 0.36f, "chests/desert_pyramid"      , event);
        /*眼眸纹饰*/addItem(ItemBase.EYE_ATST     , 1, 1, 0.26f, "chests/stronghold_corridor" , event);
        /*眼眸纹饰*/addItem(ItemBase.EYE_ATST     , 1, 2, 1.24f, "chests/stronghold_library"  , event);
        /*肋骨纹饰*/addItem(ItemBase.RIB_ATST     , 1, 1, 0.36f, "chests/nether_bridge"       , event);
        /*哨兵纹饰*/addItem(ItemBase.SENTRY_ATST  , 1, 2, 0.66f, "chests/village_blacksmith"  , event);
        /*幽静纹饰*/addItem(ItemBase.SILENCE_ATST , 1, 1, 0.16f, "chests/simple_dungeon"      , event);
        /*尖塔纹饰*/addItem(ItemBase.SPIRE_ATST   , 1, 1, 0.42f, "chests/end_city_treasure"   , event);
        /*潮汐纹饰*/addItem(ItemBase.TIDE_ATST    , 1, 1, 0.20f, "entities/elder_guardian"    , event);
        /*恼鬼纹饰*/addItem(ItemBase.VEX_ATST     , 1, 1, 0.42f, "chests/woodland_mansion"    , event);
        /*监守纹饰*/addItem(ItemBase.WARD_ATST    , 1, 1, 0.16f, "chests/simple_dungeon"      , event);
        /*荒野纹饰*/addItem(ItemBase.WILD_ATST    , 1, 1, 0.46f, "chests/jungle_temple"       , event);

        if (Loader.isModLoaded("oe")) {
            /*海岸纹饰*/addItem(ItemBase.COAST_ATST, 1, 2, 0.66f, new ResourceLocation("oe", "chests/shipwreck_map"), event);
            /*海岸纹饰*/addItem(ItemBase.COAST_ATST, 1, 2, 0.66f, new ResourceLocation("oe", "chests/shipwreck_supply"), event);
            /*海岸纹饰*/addItem(ItemBase.COAST_ATST, 1, 2, 0.66f, new ResourceLocation("oe", "chests/shipwreck_treasure"), event);
        }
        else {
            /*海岸纹饰*/addItem(ItemBase.COAST_ATST, 1, 2, 0.36f, LootTable.underwater_ruin, event);
            /*海岸纹饰*/addItem(ItemBase.COAST_ATST, 1, 2, 0.66f, LootTable.big_underwater_ruin, event);
        }

        if (Loader.isModLoaded("nb")) {
            /*猪鼻纹饰*/addItem(ItemBase.SNOUT_ATST, 1, 1, 0.32f, new ResourceLocation("nb", "pig_towers"), event);
            /*猪鼻纹饰*/addItem(ItemBase.SNOUT_ATST, 1, 1, 0.32f, new ResourceLocation("nb", "bastion_hold"), event);
            /*猪鼻纹饰*/addItem(ItemBase.SNOUT_ATST, 1, 1, 0.32f, new ResourceLocation("nb", "bastion_hold_futuremc"), event);
            /*猪鼻纹饰*/addItem(ItemBase.SNOUT_ATST, 1, 2, 0.52f, new ResourceLocation("nb", "bastion_treasure"), event);
            /*猪鼻纹饰*/addItem(ItemBase.SNOUT_ATST, 1, 2, 0.52f, new ResourceLocation("nb", "bastion_treasure_futuremc"), event);
        }
        else {
            /*猪鼻纹饰*/addItem(ItemBase.SNOUT_ATST, 1, 2, 0.42f, "chests/nether_bridge", event);
        }

        if (Loader.isModLoaded("deeperdepths")) {
            /*镶铆纹饰*/addItem(ItemBase.BOLT_ATST, 1, 1, 0.32f, new ResourceLocation("deeperdepths", "chamber_chest_loot"), event);
            /*镶铆纹饰*/addItem(ItemBase.BOLT_ATST, 1, 1, 0.62f, new ResourceLocation("deeperdepths", "vault"), event);
            /*涡流纹饰*/addItem(ItemBase.FLOW_ATST, 1, 1, 0.42f, new ResourceLocation("deeperdepths", "ominous_vault"), event);
        }
        else {
            /*镶铆纹饰*/addItem(ItemBase.BOLT_ATST, 1, 1, 0.26f, LootTable.underwater_ruin, event);
            /*涡流纹饰*/addItem(ItemBase.FLOW_ATST, 1, 1, 0.22f, LootTable.underwater_ruin, event);
            /*镶铆纹饰*/addItem(ItemBase.BOLT_ATST, 1, 1, 0.32f, LootTable.big_underwater_ruin, event);
            /*涡流纹饰*/addItem(ItemBase.FLOW_ATST, 1, 1, 0.26f, LootTable.big_underwater_ruin, event);
        }
    }

    private static void addItem(ModItemSmithingTemplate item, int minCount, int maxCount, float chance, Object targetTable, LootTableLoadEvent event) {
        if (!item.needAddLootTable) return;

        // 检查是否是目标战利品表
        if (targetTable instanceof String) {
            if (!event.getName().equals(item.getTargetTable((String) targetTable))) return;
        }
        else if (targetTable instanceof ResourceLocation) {
            if (!event.getName().equals((ResourceLocation) targetTable)) return;
        }
        else { return; }

        // 创建数量设置函数
        LootFunction[] countFunctions = new LootFunction[]{
            new SetCount(new LootCondition[0], new RandomValueRange(minCount, maxCount))
        };

        // 创建战利品条目
        LootEntryItem entry = new LootEntryItem(
            item,                 // 物品
            1,                    // 基础权重
            0,                    // 品质
            countFunctions,       // 数量函数
            new LootCondition[0], // 无附加条件
            "suikecherry.entry." + item.getPatternType()
        );

        // 创建概率条件
        LootCondition[] conditions = new LootCondition[]{new RandomChance(chance)};

        // 创建战利品池
        LootPool pool = new LootPool(
            new LootEntry[]{entry},  // 条目数组
            conditions,              // 概率条件
            new RandomValueRange(1), // 固定抽取1次
            new RandomValueRange(0), // 无额外抽取
            "suikecherry.pool." + item.getPatternType()
        );

        // 添加到战利品表
        event.getTable().addPool(pool);
    }
}