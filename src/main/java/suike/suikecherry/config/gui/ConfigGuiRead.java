package suike.suikecherry.config.gui;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.config.gui.SimpleConfigElement.RequiresConfigElement;

import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;

public class ConfigGuiRead {
    public static List<IConfigElement> getConfigElements() {
        List<IConfigElement> elements = new ArrayList<>();

        // 樱花树苗生成粉红色花簇
        elements.add(new SimpleConfigElement(
            "cherrySaplingAndPetals",
            ConfigValue.cherrySaplingAndPetals,
            true
        ));
        // 粉红色花簇放置限制取消
        elements.add(new SimpleConfigElement(
            "petalsUnlimitedPlace",
            ConfigValue.petalsUnlimitedPlace,
            false
        ));

        // 樱花树苗生长时带蜂巢
        elements.add(new SimpleConfigElement(
            "saplingSpawnBee",
            ConfigValue.saplingSpawnBee,
            true
        ));
        // 樱花树生成时带蜂巢
        elements.add(new SimpleConfigElement(
            "spawnTreeSpawnBee",
            ConfigValue.spawnTreeSpawnBee,
            true
        ));

        // 去皮
        elements.add(new SimpleConfigElement(
            "stripLog",
            ConfigValue.stripLog,
            true
        ));
        // 书架需要精准采集
        elements.add(new SimpleConfigElement(
            "chiseledBookShelfNeedSilkTouch",
            ConfigValue.chiseledBookShelfNeedSilkTouch,
            false
        ));

        // 樱花粒子概率
        elements.add(new SimpleConfigElement(
            "cherryParticleChance",
            ConfigValue.cherryParticleChance,
            10, 0, 100,
            true
        ));

        return elements;
    }

    public static List<IConfigElement> getRestartRequiredElements() {
        List<IConfigElement> elements = new ArrayList<>();

        // 生成樱花树林生物群系
        elements.add(new RequiresConfigElement(
            "spawnCherryBiome",
            ConfigValue.spawnCherryBiome,
            true
        ));
        // 樱花树林生物群系规模
        elements.add(new RequiresConfigElement(
            "cherryBiomeSize",
            ConfigValue.cherryBiomeSize,
            6, 1, 128,
            true
        ));
        // 樱花树林生物群系生成竹子
        elements.add(new RequiresConfigElement(
            "cherryBiomeSpawnBamboo",
            ConfigValue.cherryBiomeSpawnBamboo,
            true,
            true
        ));

        /*/ 樱花树林生物群系基础高度
        elements.add(new RequiresConfigElement(
            "cherryBiomeBaseHeight",
            ConfigValue.cherryBiomeBaseHeight,
            2.0F, 0.0F, 5.0F,
            true
        ));
        // 樱花树林生物群系高度起伏
        elements.add(new RequiresConfigElement(
            "cherryBiomeHeightVariation",
            ConfigValue.cherryBiomeHeightVariation,
            0.48F, 0.0F, 1.0F,
            true
        ));//*/

        // 结构生成
        elements.add(new RequiresConfigElement(
            "genDesertWell",
            ConfigValue.genDesertWell,
            true
        ));
        elements.add(new RequiresConfigElement(
            "genDesertPyramid",
            ConfigValue.genDesertPyramid,
            true
        ));
        elements.add(new RequiresConfigElement(
            "genOceanRuins",
            ConfigValue.genOceanRuins,
            true
        ));
        elements.add(new RequiresConfigElement(
            "genTrailRuins",
            ConfigValue.genTrailRuins,
            true
        ));

        // 方块燃烧
        elements.add(new RequiresConfigElement(
            "blockFire",
            ConfigValue.blockFire,
            false
        ));

        // 添加箱船
        elements.add(new RequiresConfigElement(
            "addBoatChest",
            ConfigValue.addBoatChest,
            true
        ));

        return elements;
    }
}