package suike.suikecherry.config;

import java.util.List;
import java.util.ArrayList;

public class ConfigValue {
    /*配置文件版本*/public static final int modsConfigVersion = 24;

    /*樱花树苗生成粉红色花簇*/public static boolean cherrySaplingAndPetals = true;
    /*粉红色花簇放置限制取消*/public static boolean petalsUnlimitedPlace = false;

    /*樱花粒子概率*/public static int cherryParticleChance = 10;

    /*生成樱花树林生物群系*/public static boolean spawnCherryBiome = true;
    /*生物群系规模*/public static int cherryBiomeSize = 6;
    /*生物群系基础高度*/public static float cherryBiomeBaseHeight = 2.0f;
    /*生物群系高度起伏*/public static float cherryBiomeHeightVariation = 0.48f;
    /*生物群系生成竹子*/public static boolean cherryBiomeSpawnBamboo = true;
    /*使用的竹子*/public static boolean hasBamboo = false;
    /*使用的竹子*/public static List<String> bamboos = new ArrayList<>();;

    /*树苗生长时带蜂巢*/public static boolean saplingSpawnBee = true;
    /*樱花树生成时带蜂巢*/public static boolean spawnTreeSpawnBee = true;

    /*沙漠水井*/public static boolean genDesertWell = true;
    /*沙漠神殿*/public static boolean genDesertPyramid = true;
    /*海底废墟*/public static boolean genOceanRuins = true;
    /*古迹废墟*/public static boolean genTrailRuins = true;

    /*去皮*/public static boolean stripLog = true;
    /*书架需要精准采集*/public static boolean chiseledBookShelfNeedSilkTouch = false;
    /*方块燃烧*/public static boolean blockFire = false;

    /*添加原版船的箱船*/public static boolean addBoatChest = true;
}