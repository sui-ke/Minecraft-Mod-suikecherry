package suike.suikecherry.config;

public class ConfigValue {
    /*配置文件版本*/public static final int modsConfigVersion = 12;

    /*樱花树苗生成粉红色花簇*/public static boolean cherrySaplingAndPetals = true;
    /*粉红色花簇放置限制取消*/public static boolean petalsUnlimitedPlace = false;

    /*生成樱花树林生物群系*/public static boolean spawnCherryBiome = true;
    /*生物群系规模*/public static int cherryBiomeSize = 10;
    /*生物群系基础高度*/public static float cherryBiomeBaseHeight = 2.0f;
    /*生物群系高度起伏*/public static float cherryBiomeHeightVariation = 0.48f;
    /*生物群系生成竹子*/public static boolean cherryBiomeSpawnBamboo = true;

    /*草原生成小型樱花树林*/public static boolean spawnCherryTree = true;
    /*忽略草原生成概率*/public static boolean IgnoreSpawnProbability = false;

    /*树苗生长时带蜂巢*/public static boolean saplingSpawnBee = true;
    /*樱花树生成时带蜂巢*/public static boolean spawnTreeSpawnBee = true;

    /*方块燃烧*/public static boolean blockFire = false;
}