package suike.suikecherry.world.biome;

import java.util.*;

import suike.suikecherry.inter.IBiomes;
import suike.suikecherry.config.ConfigValue;

import rtg.api.RTGAPI;
import biomesoplenty.api.enums.BOPClimates;

import net.minecraft.world.biome.Biome;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.common.BiomeDictionary;

public class Biomes {
    public static void register() {
        List<Biome> biomes = new ArrayList<>();
        boolean BOPID = Loader.isModLoaded("biomesoplenty");
        boolean RTGID = Loader.isModLoaded("rtg") || Loader.isModLoaded("rtgc");

        if (ConfigValue.spawnCherryBiome) {
            biomes.add(new CherryBiome().setRegistryName("suikecherry:suike_cherry_grove"));
        }

        for (Biome biome : biomes) {
            // 注册生物群系
            ForgeRegistries.BIOMES.register(biome);
            BiomeDictionary.addTypes(
                biome, ((IBiomes) biome).getDictType()
            );

            if (BOPID) {
                // 添加到超多生物群系生成
                addBiomeToBOP(biome, ((IBiomes) biome).getWeight());
            }

            if (RTGID) {
                // 添加到真实地形生成
                RTGAPI.addRTGBiomes(new BiomeRTG(biome));
            }
        }
    }

    private static void addBiomeToBOP(Biome biome, int weight) {
        try {
            String climates = ((IBiomes) biome).getBOPType();
            // 处理多个气候带的情况
            String[] climateArray = climates.split(",");

            for (String climateStr : climateArray) {
                climateStr = climateStr.trim().toUpperCase();
                // 根据字符串找到对应的 BOPClimates 枚举
                BOPClimates climate = BOPClimates.valueOf(climateStr);
                // 添加到气候带
                climate.addBiome(weight, biome);
            }
        } catch (Exception e) {}
    }
}