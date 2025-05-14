package suike.suikecherry.config;

import java.io.File;

import suike.suikecherry.SuiKe;
import suike.suikecherry.config.Config;
import suike.suikecherry.config.ConfigValue;

import net.minecraftforge.common.config.Configuration;

public class ConfigRead {
    public static void config() {
        ConfigValue.cherrySaplingAndPetals = readConfig("Cherry", "cherrySaplingAndPetals", true);
        ConfigValue.petalsUnlimitedPlace = readConfig("Cherry", "petalsUnlimitedPlace", true);

        ConfigValue.spawnCherryBiome = readConfig("Cherry", "spawnCherryBiome", true);
        ConfigValue.cherryBiomeSize = readConfig("Cherry", "cherryBiomeSize", 10, 16);
        ConfigValue.cherryBiomeBaseHeight = readConfig("Cherry", "cherryBiomeBaseHeight", 2.2f, 5);
        ConfigValue.cherryBiomeHeightVariation = readConfig("Cherry", "cherryBiomeHeightVariation", 0.54f, 1);
        ConfigValue.cherryBiomeSpawnBamboo = readConfig("Cherry", "cherryBiomeSpawnBamboo", true);

        ConfigValue.spawnCherryTree = readConfig("Cherry", "spawnCherryTree", true);
        ConfigValue.IgnoreSpawnProbability = readConfig("Cherry", "IgnoreSpawnProbability", false);

        ConfigValue.saplingSpawnBee = readConfig("Cherry", "saplingSpawnBee", true);
        ConfigValue.spawnTreeSpawnBee = readConfig("Cherry", "spawnTreeSpawnBee", true);

        ConfigValue.blockFire = readConfig("Cherry", "blockFire", false);
    }

    public static int readConfig(String category, String key, int defaultValue, int max) {
        return (int) readConfigValue(category, key, defaultValue, max);
    }
    public static float readConfig(String category, String key, float defaultValue, int max) {
        return (float) readConfigValue(category, key, defaultValue, max);
    }
    public static boolean readConfig(String category, String key, boolean defaultValue) {
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
        return (float) (Math.floor( floats * 10) / 10);
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