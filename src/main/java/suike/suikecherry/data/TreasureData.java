package suike.suikecherry.data;

import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.config.TreasureListRead;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TreasureData {
    private final String treasure;
    private final int meta;
    private final float chance;

    public TreasureData(String treasure, float chance) {
        this(treasure, 0, chance);
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
        return ItemBase.isValidItemStack(
            new ItemStack(Item.getByNameOrId(this.treasure), 1, this.meta)
        );
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
        Set<String> existingKeys = structure.getTreasureList().stream()
            .map(TreasureData::getKey)
            .collect(Collectors.toSet());

        for (TreasureData data : treasureDataArray) {
            if (!data.isValid()) continue; // 跳过无效物品

            if (!existingKeys.contains(data.getKey())) {
                structure.getTreasureList().add(data);
                existingKeys.add(data.getKey()); // 更新键集合
                listChanged = true;
            }
        }

        // 重新计算概率
        if (listChanged && initializeChance) { // 初始化完成之前不需要初始化
            structure.calculateCumulativeChances();
        }
    }

// 基础宝藏列表
    // 沙漠水井战利品
    private static List<TreasureData> desertWellTreasureList = createTreasureList(
        // new TreasureData("suikecherry:arms_up_pottery_sherd", 0.066f), // 举臂纹样陶片
        // new TreasureData("suikecherry:brewer_pottery_sherd", 0.083f), // 佳酿纹样陶片
        // new TreasureData("suikecherry:heartbreak_pottery_sherd", 0.066f), // 心碎纹样陶片
        // new TreasureData("suikecherry:heart_pottery_sherd", 0.066f), // 爱心纹样陶片
        new TreasureData("minecraft:brick", 0.5f),          // 红砖 (常见)
        new TreasureData("minecraft:stick", 0.5f),          // 木棍 (常见)
        new TreasureData("minecraft:clay_ball", 0.4f),      // 粘土
        new TreasureData("minecraft:cactus", 0.3f),         // 仙人掌
        new TreasureData("minecraft:glass_bottle", 0.3f),   // 玻璃瓶 (代表水瓶)
        new TreasureData("minecraft:rotten_flesh", 0.2f),   // 腐肉 (代表沙漠生物)
        new TreasureData("minecraft:gold_nugget", 0.2f),    // 金粒
        new TreasureData("minecraft:emerald", 0.1f)         // 绿宝石 (稀有)
    );

    // 沙漠神殿战利品
    private static List<TreasureData> desertPyramidTreasureList = createTreasureList(
        // new TreasureData("suikecherry:archer_pottery_sherd", 0.125f),  // 弓箭纹样陶片
        // new TreasureData("suikecherry:miner_pottery_sherd", 0.125f),   // 采矿纹样陶片
        // new TreasureData("suikecherry:prize_pottery_sherd", 0.125f),   // 珍宝纹样陶片
        // new TreasureData("suikecherry:skull_pottery_sherd", 0.125f),   // 头颅纹样陶片
        // new TreasureData("suikecherry:blade_pottery_sherd", 0.066f),   // 利刃纹样陶片
        // new TreasureData("suikecherry:relic_music_disc", 0.083f),      // 音乐唱片 (Relic)
        new TreasureData("minecraft:gunpowder", 1.0f),     // 火药  (常见)
        new TreasureData("minecraft:tnt", 1.0f),           // TNT (常见)
        new TreasureData("minecraft:bone", 0.8f),          // 骨头
        new TreasureData("minecraft:spider_eye", 0.6f),    // 蜘蛛眼
        new TreasureData("minecraft:gold_ingot", 0.3f),    // 金锭
        new TreasureData("minecraft:gold_nugget", 0.7f),   // 金粒
        new TreasureData("minecraft:gold_block", 0.005f),  // 金块
        new TreasureData("minecraft:iron_ingot", 0.3f),    // 铁锭
        new TreasureData("minecraft:iron_nugget", 0.7f),   // 铁粒
        new TreasureData("minecraft:saddle", 0.3f),        // 鞍
        new TreasureData("minecraft:golden_apple", 0.2f),  // 金苹果 (稀有)
        new TreasureData("minecraft:diamond", 0.4f),       // 钻石
        new TreasureData("minecraft:emerald", 0.4f),       // 绿宝石
        new TreasureData("minecraft:enchanted_book", 0.3f),// 附魔书 (随机附魔)
        new TreasureData("minecraft:leather", 0.8f)        // 皮革
    );

    // 海底废墟战利品
    private static List<TreasureData> oceanRuinsTreasureList = createTreasureList(
        new TreasureData("suikecherry:sniffer_egg", 0.066f), // 嗅探兽蛋 (极其稀有)
        // new TreasureData("suikecherry:angler_pottery_sherd", 0.066f), // 垂钓纹样陶片
        // new TreasureData("suikecherry:shelter_pottery_sherd", 0.066f), // 树荫纹样陶片
        // new TreasureData("suikecherry:snort_pottery_sherd", 0.066f),  // 嗅探纹样陶片
        // new TreasureData("suikecherry:mourner_pottery_sherd", 0.066f),// 悲恸纹样陶片
        // new TreasureData("suikecherry:plenty_pottery_sherd", 0.066f), // 富饶纹样陶片
        new TreasureData("minecraft:wheat", 0.8f),          // 小麦 (常见)
        new TreasureData("minecraft:wooden_hoe", 0.8f),     // 木锄 (常见)
        new TreasureData("minecraft:coal", 0.7f),           // 煤炭
        new TreasureData("minecraft:gold_nugget", 0.7f),    // 金粒
        new TreasureData("minecraft:iron_nugget", 0.7f),    // 铁粒
        new TreasureData("minecraft:fish", 0.6f),           // 生鱼
        new TreasureData("minecraft:dye", 0.4f),            // 墨囊
        new TreasureData("minecraft:prismarine_shard", 0.3f), // 海晶碎片
        new TreasureData("minecraft:sponge", 1, 0.08f),     // 湿海绵 (极其稀有)
        new TreasureData("minecraft:emerald", 0.1f),        // 绿宝石 (非常稀有)
        new TreasureData("minecraft:fishing_rod", 0.4f),    // 钓鱼竿
        new TreasureData("minecraft:flint", 0.8f),          // 燧石
        new TreasureData("minecraft:string", 0.8f),         // 线
        new TreasureData("minecraft:bowl", 0.8f),           // 碗
        new TreasureData("minecraft:leather", 0.8f),        // 皮革
        new TreasureData("minecraft:leather_boots", 0.8f)   // 皮革靴子
    );

    // 创建宝藏列表
    private static List<TreasureData> createTreasureList(TreasureData... datas) {
        List<TreasureData> list = new ArrayList<>();
        Collections.addAll(list, datas);
        return list;
    }

// 结构枚举
    public static final Structure[] structures = {Structure.desertWell, Structure.desertPyramid, Structure.oceanRuins};
    public enum Structure {
        desertWell("desertWell", TreasureData.desertWellTreasureList),
        desertPyramid("desertPyramid", TreasureData.desertPyramidTreasureList),
        oceanRuins("oceanRuins", TreasureData.oceanRuinsTreasureList);

        private String structureName;
        private List<TreasureData> treasureList;
        private float[] cumulativeChances;
        private float totalChance;

        Structure(String structureName, List<TreasureData> treasureList) {
            this.structureName = structureName;
            this.treasureList = treasureList;
        }

        // 初始化
        public static void initAll() {
            for (Structure structure : values()) {
                SuiKe.LOGGER.info("=========== {} ===========", structure);
                initConfigTreasure(structure);
                structure.calculateCumulativeChances();

                log(structure);
            }
            TreasureData.initializeChance = true; // 初始化完成
        }

        private static void log(Structure structure) {
            for (int i = 0; i < structure.treasureList.size(); i++) {
                TreasureData data = structure.treasureList.get(i);
                float chance = data.getChance() / structure.totalChance * 100;

                if (data.isValid()) SuiKe.LOGGER.info("○○○: {}. {} - {} - ({}%)", i+1, data.getTreasure(), data.getMeta(), String.format("%.2f", chance));
                else SuiKe.LOGGER.info("×××: {}. {} - {} - ({}%)", i+1, data.getTreasure(), data.getMeta(), String.format("%.2f", chance));
            }
        }

        private static void initConfigTreasure(Structure structure) {
            Set<String> existingKeys = new HashSet<>();

            // 预生成现有列表的键
            for (TreasureData existing : structure.getTreasureList()) {
                existingKeys.add(existing.getKey());
            }

            List<TreasureData> combinedList = new ArrayList<>(structure.getTreasureList());
            for (TreasureData data : TreasureListRead.getTreasureList(structure.structureName)) {
                // 检查物品有效性
                if (!data.isValid()) {
                    SuiKe.LOGGER.info("×××: {} - {}", data.getTreasure(), data.getMeta());
                    continue;
                }

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

        private List<TreasureData> getTreasureList() {
            return this.treasureList;
        }
    }
}