package suike.suikecherry.config;

import java.io.File;

import suike.suikecherry.SuiKe;
import suike.suikecherry.config.Config;
import suike.suikecherry.config.ConfigValue;

import net.minecraftforge.common.config.Configuration;

public class ConfigRead {
    public static void config() {
        ConfigValue.cherrySaplingAndPetals = readConfig("Cherry", "cherrySaplingAndPetals", true);

        ConfigValue.spawnCherryBiome = readConfig("Cherry", "spawnCherryBiome", true);
        ConfigValue.cherryBiomeSpawnBamboo = readConfig("Cherry", "cherryBiomeSpawnBamboo", true);

        ConfigValue.spawnCherryTree = readConfig("Cherry", "spawnCherryTree", true);
        ConfigValue.IgnoreSpawnProbability = readConfig("Cherry", "IgnoreSpawnProbability", false);

        ConfigValue.saplingSpawnBee = readConfig("Cherry", "saplingSpawnBee", true);
        ConfigValue.spawnTreeSpawnBee = readConfig("Cherry", "spawnTreeSpawnBee", true);

        ConfigValue.blockFire = readConfig("Cherry", "blockFire", false);
    }

    public static boolean readConfig(String category, String key, boolean defaultValue) {
        return (boolean) readConfig(category, key, defaultValue, true);
    }

    public static Object readConfig(String category, String key, Object defaultValue, boolean a) {
        /*获取配置文件位置*/Configuration config = new Configuration(Config.configFile);

        if (defaultValue instanceof Boolean) {
            return (boolean) config.get(category, key, (Boolean) defaultValue).getBoolean();
        }

        return defaultValue;
    }
}