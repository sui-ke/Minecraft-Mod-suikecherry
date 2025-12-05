package suike.suikecherry.data;

import java.util.*;
import java.util.stream.Collectors;

import suike.suikecherry.SuiKe;
import suike.suikecherry.config.*;
import suike.suikecherry.item.ItemBase;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TreasureData {
    private final String treasure;
    private final int meta;
    private final float chance;

    public TreasureData(String treasure, float chance) {
        this(treasure, 0, chance);
    }
    public TreasureData(Item treasure, float chance) {
        this(treasure, 0, chance);
    }
    public TreasureData(Item treasure, int meta, float chance) {
        this((treasure != null ? treasure.getRegistryName().toString() : ""), meta, chance);
    }
    public TreasureData(ItemStack treasure, float chance) {
        this(treasure.getItem(), treasure.getMetadata(), chance);
    }
    public TreasureData(String treasure, int meta, float chance) {
        this.treasure = treasure;
        this.meta = meta;
        this.chance = chance;
    }

    public String getTreasure() {
        return this.treasure;
    }

    public int getMeta() {
        return this.meta;
    }

    public float getChance() {
        return this.chance;
    }

    public boolean isValid() {
        boolean isValid = 
            !this.treasure.isEmpty() &&
            ItemBase.isValidItemStack(
                new ItemStack(Item.getByNameOrId(this.treasure), 1, this.meta)
            );

        if (!isValid) {
            // 将无效物品添加到集合中
            String invalidItem = this.treasure + (meta != 0 ? (":" + meta) : "");
            invalidItems.add(invalidItem);
        }

        return isValid;
    }

    public String getKey() {
        return this.treasure + "|" + this.meta;
    }

    public String toString() {
        return meta == 0 
            ? String.format("[宝藏: %s, 概率%.2f]", this.treasure, this.chance)
            : String.format("[宝藏: %s, 元数据%d, 概率%.2f]", this.treasure, this.meta, this.chance);
    }

// 添加宝藏方法
    public static boolean initializeChance = false;
    public static void addTreasure(Structure structure, TreasureData... treasureDataArray) {
        boolean listChanged = false;
        Set<String> existingKeys = structure.getTreasureListInside().stream()
            .map(TreasureData::getKey)
            .collect(Collectors.toSet());

        for (TreasureData data : treasureDataArray) {
            if (!data.isValid()) continue; // 跳过无效物品

            if (!existingKeys.contains(data.getKey())) {
                structure.getTreasureListInside().add(data);
                existingKeys.add(data.getKey()); // 更新键集合
                listChanged = true;
            }
        }

        // 重新计算概率
        if (listChanged && initializeChance) { // 初始化完成之前不需要重新计算概率
            structure.calculateCumulativeChances();
        }
    }

// 基础宝藏列表
    // 沙漠水井战利品
    private static List<TreasureData> desertWellTreasureList = createTreasureList(
        new TreasureData("suikecherry:arms_up_pottery_sherd", 0.2f), // 举臂纹样陶片
        new TreasureData("suikecherry:brewer_pottery_sherd", 0.2f),  // 佳酿纹样陶片
        new TreasureData("minecraft:brick", 0.5f),          // 红砖
        new TreasureData("minecraft:stick", 0.5f),          // 木棍
        new TreasureData("minecraft:clay_ball", 0.4f),      // 粘土
        new TreasureData("minecraft:cactus", 0.3f),         // 仙人掌
        new TreasureData("minecraft:glass_bottle", 0.3f),   // 玻璃瓶
        new TreasureData("minecraft:rotten_flesh", 0.2f),   // 腐肉
        new TreasureData("minecraft:gold_nugget", 0.2f),    // 金粒
        new TreasureData("minecraft:emerald", 0.1f)         // 绿宝石
    );

    // 沙漠神殿战利品
    private static List<TreasureData> desertPyramidTreasureList = createTreasureList(
        new TreasureData("suikecherry:archer_pottery_sherd", 0.3f),  // 弓箭纹样陶片
        new TreasureData("suikecherry:miner_pottery_sherd", 0.3f),   // 采矿纹样陶片
        new TreasureData("suikecherry:prize_pottery_sherd", 0.3f),   // 珍宝纹样陶片
        new TreasureData("suikecherry:skull_pottery_sherd", 0.3f),   // 头颅纹样陶片
        new TreasureData("minecraft:tnt", 1.0f),           // TNT
        new TreasureData("minecraft:gunpowder", 1.0f),     // 火药
        new TreasureData("minecraft:bone", 0.8f),          // 骨头
        new TreasureData("minecraft:leather", 0.8f),       // 皮革
        new TreasureData("minecraft:spider_eye", 0.6f),    // 蜘蛛眼
        new TreasureData("minecraft:saddle", 0.3f),        // 鞍
        new TreasureData("minecraft:golden_apple", 0.2f),  // 金苹果
        new TreasureData("minecraft:diamond", 0.4f),       // 钻石
        new TreasureData("minecraft:emerald", 0.4f),       // 绿宝石
        new TreasureData("minecraft:gold_nugget", 0.7f),   // 金粒
        new TreasureData("minecraft:gold_ingot", 0.3f),    // 金锭
        new TreasureData("minecraft:gold_block", 0.005f),  // 金块
        new TreasureData("minecraft:iron_nugget", 0.7f),   // 铁粒
        new TreasureData("minecraft:iron_ingot", 0.3f),    // 铁锭
        new TreasureData("minecraft:enchanted_book", 0.3f) // 附魔书
    );

    // 海底废墟战利品
    private static List<TreasureData> oceanRuinsTreasureList = createTreasureList(
        new TreasureData("suikecherry:mourner_pottery_sherd", 0.3f),   // 悲恸纹样陶片
        new TreasureData("suikecherry:plenty_pottery_sherd", 0.3f),    // 富饶纹样陶片
        new TreasureData("suikecherry:explorer_pottery_sherd", 0.3f),  // 探险纹样陶片
        new TreasureData("suikecherry:blade_pottery_sherd", 0.3f),     // 利刃纹样陶片
        new TreasureData("suikecherry:snort_pottery_sherd", 0.3f),     // 嗅探纹样陶片
        new TreasureData("suikecherry:shelter_pottery_sherd", 0.3f),   // 树荫纹样陶片
        new TreasureData("suikecherry:angler_pottery_sherd", 0.3f),    // 垂钓纹样陶片
        new TreasureData("suikecherry:flow_pottery_sherd", 0.3f),      // 涡流纹样陶片
        new TreasureData("suikecherry:sniffer_egg", 0.066f),           // 嗅探兽蛋
        new TreasureData("minecraft:bowl", 0.8f),             // 碗
        new TreasureData("minecraft:wheat", 0.8f),            // 小麦
        new TreasureData("minecraft:flint", 0.8f),            // 燧石
        new TreasureData("minecraft:string", 0.8f),           // 线
        new TreasureData("minecraft:leather", 0.8f),          // 皮革
        new TreasureData("minecraft:wooden_hoe", 0.8f),       // 木锄
        new TreasureData("minecraft:leather_boots", 0.8f),    // 皮革靴子
        new TreasureData("minecraft:coal", 0.7f),             // 煤炭
        new TreasureData("minecraft:gold_nugget", 0.7f),      // 金粒
        new TreasureData("minecraft:iron_nugget", 0.7f),      // 铁粒
        new TreasureData("minecraft:fish", 0.6f),             // 生鱼
        new TreasureData("minecraft:dye", 0.4f),              // 墨囊
        new TreasureData("minecraft:fishing_rod", 0.4f),      // 钓鱼竿
        new TreasureData("minecraft:prismarine_shard", 0.3f), // 海晶碎片
        new TreasureData("minecraft:iron_axe", 0.2f),         // 铁斧
        new TreasureData("minecraft:emerald", 0.1f),          // 绿宝石
        new TreasureData("minecraft:sponge", 1, 0.08f)        // 湿海绵
    );

    // 古迹废墟
    private static List<TreasureData> trailRuinsTreasureList = createTreasureList(
        new TreasureData(ItemBase.HOST_ATST, 0.8f),                     // 雇主纹饰
        new TreasureData(ItemBase.RAISER_ATST, 0.8f),                   // 牧民纹饰
        new TreasureData(ItemBase.SHAPER_ATST, 0.8f),                   // 塑造纹饰
        new TreasureData(ItemBase.WAYFINDER_ATST, 0.8f),                // 向导纹饰
        new TreasureData("suikecherry:heart_pottery_sherd", 0.5f),      // 爱心纹样陶片
        new TreasureData("suikecherry:heartbreak_pottery_sherd", 0.5f), // 心碎纹样陶片
        new TreasureData("suikecherry:burn_pottery_sherd", 0.5f),       // 烈焰纹样陶片
        new TreasureData("suikecherry:sheaf_pottery_sherd", 0.5f),      // 麦捆纹样陶片
        new TreasureData("suikecherry:howl_pottery_sherd", 0.5f),       // 狼嚎纹样陶片
        new TreasureData("suikecherry:danger_pottery_sherd", 0.5f),     // 危机纹样陶片
        new TreasureData("suikecherry:friend_pottery_sherd", 0.5f),     // 挚友纹样陶片
        new TreasureData("suikecherry:scrape_pottery_sherd", 0.5f),     // 刮削纹样陶片
        new TreasureData("suikecherry:guster_pottery_sherd", 0.5f),     // 旋风纹样陶片
        new TreasureData("suikecherry:music_disc_relic", 0.4f),         // 音乐唱片 (Relic)
        new TreasureData("suikecherry:oak_hanging_sign", 0.8f),         // 悬挂式橡木告示牌
        new TreasureData("suikecherry:spruce_hanging_sign", 0.8f),      // 悬挂式云杉木告示牌
        new TreasureData("minecraft:diamond", 0.4f),                // 钻石
        new TreasureData("minecraft:emerald", 0.6f),                // 绿宝石
        new TreasureData("minecraft:brick", 1.5f),                  // 红砖
        new TreasureData("minecraft:wheat", 1.5f),                  // 小麦
        new TreasureData("minecraft:clay_ball", 1.5f),              // 黏土
        new TreasureData("minecraft:wooden_hoe", 1.5f),             // 木锄
        new TreasureData("minecraft:coal", 1.0f),                   // 煤炭
        new TreasureData("minecraft:lead", 1.0f),                   // 拴绳
        new TreasureData("minecraft:string", 1.0f),                 // 线
        new TreasureData("minecraft:deadbush", 1.0f),               // 枯萎的灌木
        new TreasureData("minecraft:flower_pot", 1.0f),             // 花盆
        new TreasureData("minecraft:wheat_seeds", 1.0f),            // 小麦种子
        new TreasureData("minecraft:beetroot_seeds", 1.0f),         // 甜菜种子
        new TreasureData("minecraft:gold_nugget", 1.0f),            // 金粒
        new TreasureData("minecraft:stained_glass_pane", 2, 1.0f),  // 品红色
        new TreasureData("minecraft:stained_glass_pane", 3, 1.0f),  // 淡蓝色
        new TreasureData("minecraft:stained_glass_pane", 4, 1.0f),  // 黄色
        new TreasureData("minecraft:stained_glass_pane", 6, 1.0f),  // 粉色
        new TreasureData("minecraft:stained_glass_pane", 10, 1.0f), // 紫色
        new TreasureData("minecraft:stained_glass_pane", 11, 1.0f), // 蓝色
        new TreasureData("minecraft:stained_glass_pane", 14, 1.0f), // 红色
        new TreasureData("minecraft:dye", 4, 1.0f),                 // 蓝色染料/青金石
        new TreasureData("minecraft:dye", 11, 1.0f),                // 黄色染料
        new TreasureData("minecraft:dye", 12, 1.0f),                // 淡蓝色染料
        new TreasureData("minecraft:dye", 14, 1.0f),                // 橙色染料
        new TreasureData("minecraft:dye", 15, 1.0f)                 // 白色染料/骨粉
    );

    // 创建宝藏列表
    private static List<TreasureData> createTreasureList(TreasureData... datas) {
        List<TreasureData> list = new ArrayList<>();
        Collections.addAll(list, datas);
        return list;
    }

// 结构枚举
    private static final List<Structure> structures = new ArrayList<>();
    public static List<Structure> getStructureList() {
        return new ArrayList<>(structures);
    }

    public enum Structure {
        desertWell("desertWell", TreasureData.desertWellTreasureList),
        desertPyramid("desertPyramid", TreasureData.desertPyramidTreasureList),
        oceanRuins("oceanRuins", TreasureData.oceanRuinsTreasureList),
        trailRuins("trailRuins", TreasureData.trailRuinsTreasureList);

        private final String structureName;
        private List<TreasureData> treasureList;
        private float[] cumulativeChances;
        private float totalChance;

        Structure(String structureName, List<TreasureData> treasureList) {
            this.structureName = structureName;
            this.treasureList = treasureList;
            TreasureData.structures.add(this);
        }

        // 初始化
        public static void initAll() {
            Config.configReadTreasure(); // 读取配置文件

            for (Structure structure : values()) {
                initConfigTreasure(structure);
                structure.calculateCumulativeChances();
            }

            TreasureData.initializeChance = true; // 初始化完成
        }

        private static void initConfigTreasure(Structure structure) {
            Set<String> existingKeys = new HashSet<>();

            // 预生成现有列表的键
            for (TreasureData existing : structure.getTreasureListInside()) {
                existingKeys.add(existing.getKey());
            }

            List<TreasureData> combinedList = new ArrayList<>(structure.getTreasureListInside());
            for (TreasureData data : TreasureListRead.getTreasureList(structure.structureName)) {
                if (!data.isValid()) continue; // 跳过无效物品

                // 检查重复性
                String key = data.getKey();
                if (!existingKeys.contains(key)) {
                    combinedList.add(data);
                    existingKeys.add(key);
                }
            }

            // 更新结构列表
            structure.treasureList = combinedList;
        }

        // 计算概率: 物品概率 = 物品权重 / 总权重
        private void calculateCumulativeChances() {
            this.totalChance = 0f;
            // 计算总权重
            for (TreasureData data : treasureList) {
                this.totalChance += data.getChance();
            }

            // 计算累积概率
            this.cumulativeChances = new float[treasureList.size()];
            float cumulative = 0f;
            for (int i = 0; i < treasureList.size(); i++) {
                cumulative += treasureList.get(i).getChance();
                this.cumulativeChances[i] = cumulative;
            }
        }

        public TreasureData getRandomTreasure(Random rand) {
            if (treasureList.isEmpty() || this.totalChance <= 0) return null;

            float randomValue = rand.nextFloat() * this.totalChance;

            // 二分查找优化
            int index = Arrays.binarySearch(cumulativeChances, randomValue);
            if (index < 0) index = -index - 1;

            // 防止数组越界
            return treasureList.get(Math.min(index, treasureList.size() - 1));
        }

        private List<TreasureData> getTreasureListInside() {
            return this.treasureList;
        }

        public List<TreasureData> getTreasureList() {
            return new ArrayList<>(this.treasureList);
        }

        public float getTotalChance() {
            return this.totalChance;
        }
    }

// 无效物品
    private static Set<String> invalidItems = new HashSet<>();

    public static Set<String> getInvalidItems() {
        return new HashSet<>(invalidItems);
    }

    public static void clearInvalidItems() {
        invalidItems.clear();
    }
}