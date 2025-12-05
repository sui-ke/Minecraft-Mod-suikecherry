package suike.suikecherry.config;

import java.util.*;
import java.io.File;

import suike.suikecherry.SuiKe;
import suike.suikecherry.config.Config;
import suike.suikecherry.config.ConfigValue;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;

public class ConfigRead {
    public static void config() {
        ConfigValue.cherrySaplingAndPetals = readConfig("Cherry", "cherrySaplingAndPetals", true);
        ConfigValue.petalsUnlimitedPlace = readConfig("Cherry", "petalsUnlimitedPlace", true);

        ConfigValue.cherryParticleChance = readConfig("Cherry", "cherryParticleChance", 10, 100);

        ConfigValue.spawnCherryBiome = readConfig("Cherry", "spawnCherryBiome", true);
        ConfigValue.cherryBiomeSize = readConfig("Cherry", "cherryBiomeSize", 6, 128);
        ConfigValue.cherryBiomeBaseHeight = readConfig("Cherry", "cherryBiomeBaseHeight", 2.0F, 5);
        ConfigValue.cherryBiomeHeightVariation = readConfig("Cherry", "cherryBiomeHeightVariation", 0.48F, 1);
        ConfigValue.cherryBiomeSpawnBamboo = readConfig("Cherry", "cherryBiomeSpawnBamboo", true);

        ConfigValue.saplingSpawnBee = readConfig("Cherry", "saplingSpawnBee", true);
        ConfigValue.spawnTreeSpawnBee = readConfig("Cherry", "spawnTreeSpawnBee", true);

        ConfigValue.genDesertWell = readConfig("Cherry", "genDesertWell", true);
        ConfigValue.genDesertPyramid = readConfig("Cherry", "genDesertPyramid", true);
        ConfigValue.genOceanRuins = readConfig("Cherry", "genOceanRuins", true);
        ConfigValue.genTrailRuins = readConfig("Cherry", "genTrailRuins", true);

        ConfigValue.stripLog = readConfig("Cherry", "stripLog", true);
        ConfigValue.chiseledBookShelfNeedSilkTouch = readConfig("Cherry", "chiseledBookShelfNeedSilkTouch", false);
        ConfigValue.blockFire = readConfig("Cherry", "blockFire", false);

        ConfigValue.addBoatChest = readConfig("Cherry", "addBoatChest", true);

        readBamboConfig();
    }

    private static void readBamboConfig() {
        // 检查是否有竹子模组
        boolean hasFuturemc = Loader.isModLoaded("futuremc");
        boolean hasBiomesoplenty = Loader.isModLoaded("biomesoplenty");
        boolean hasSakura = Loader.isModLoaded("sakura");

        if (!hasFuturemc && !hasBiomesoplenty && !hasSakura) return;

        Map<Integer, List<String>> modPriorities = new TreeMap<>();
        if (hasFuturemc) {
            int priority = readConfig("Cherry.Bamboo", "futuremc", 0, 2);
            modPriorities.computeIfAbsent(priority, k -> new ArrayList<String>()).add("futuremc");
        }
        if (hasBiomesoplenty) {
            int priority = readConfig("Cherry.Bamboo", "biomesoplenty", 1, 2);
            modPriorities.computeIfAbsent(priority, k -> new ArrayList<String>()).add("biomesoplenty");
        }
        if (hasSakura) {
            int priority = readConfig("Cherry.Bamboo", "sakura", 2, 2);
            modPriorities.computeIfAbsent(priority, k -> new ArrayList<String>()).add("sakura");
        }

        if (!modPriorities.isEmpty()) {
            ConfigValue.hasBamboo = true;
            Map.Entry<Integer, List<String>> highestPriority = modPriorities.entrySet()
                .stream()
                .min(Map.Entry.comparingByKey())
                .orElse(null);
            ConfigValue.bamboos = highestPriority.getValue();
        }
    }

    private static int readConfig(String category, String key, int defaultValue, int max) {
        return (int) readConfigValue(category, key, defaultValue, max);
    }
    private static float readConfig(String category, String key, float defaultValue, int max) {
        return (float) readConfigValue(category, key, defaultValue, max);
    }
    private static boolean readConfig(String category, String key, boolean defaultValue) {
        return (boolean) readConfigValue(category, key, defaultValue, 0);
    }

    public static Object readConfigValue(String category, String key, Object defaultValue, int max) {
        /*获取配置文件位置*/Configuration config = new Configuration(Config.configFile);

        if (defaultValue instanceof Integer) {
            return (int) maximum(config.get(category, key, (Integer) defaultValue).getDouble(), max);
        } else if (defaultValue instanceof Float) {
            return roundToOneDecimal((float) maximum(config.get(category, key, (Float) defaultValue).getDouble(), max));
        } else if (defaultValue instanceof Boolean) {
            return (boolean) config.get(category, key, (Boolean) defaultValue).getBoolean();
        }

        return defaultValue;
    }

    // 保留一位小数
    public static float roundToOneDecimal(float floats) {
        return (float) (Math.floor(floats * 10) / 10);
    }

    // 最大值
    public static double maximum(double value, int max) {
        if (value > max) {
            //设置新的值
            value = max;
        }

        return value;
    }
}