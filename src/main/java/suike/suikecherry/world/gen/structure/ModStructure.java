package suike.suikecherry.world.gen.structure;

import java.util.Set;

import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.world.gen.structure.oceanruins.*;
import suike.suikecherry.world.gen.structure.trailruins.*;
import suike.suikecherry.world.gen.structure.desertwell.*;
import suike.suikecherry.world.gen.structure.desertpyramid.*;

import net.minecraft.world.gen.structure.MapGenStructureIO;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModStructure {
    public static void registerStructure() {
        if (ConfigValue.genOceanRuins) {
            // 海底废墟
            MapGenStructureIO.registerStructure(OceanRuinsGen.OceanRuinsStart.class, "OceanRuins");
            MapGenStructureIO.registerStructureComponent(OceanRuins.class, "OceanRuinsComponent");
            GameRegistry.registerWorldGenerator(new OceanRuinsGen(), 9);
        }

        if (ConfigValue.genDesertWell) {
            // 沙漠水井
            MapGenStructureIO.registerStructure(DesertWellGen.DesertWellStart.class, "DesertWellTreasure");
            MapGenStructureIO.registerStructureComponent(DesertWell.class, "DesertWellTreasureComponent");
            GameRegistry.registerWorldGenerator(new DesertWellGen(), 11);
        }

        if (ConfigValue.genDesertPyramid) {
            // 沙漠神殿
            MapGenStructureIO.registerStructure(DesertPyramidTreasureRoomGen.DesertPyramidStart.class, "DesertPyramidTreasureRoom");
            MapGenStructureIO.registerStructureComponent(DesertPyramidTreasureRoom.class, "DesertPyramidTreasureRoomComponent");
            GameRegistry.registerWorldGenerator(new DesertPyramidTreasureRoomGen(), 10);
        }

        if (ConfigValue.genTrailRuins) {
            // 古迹废墟
            MapGenStructureIO.registerStructure(TrailRuinsGen.TrailRuinsStart.class, "TrailRuins");
            MapGenStructureIO.registerStructureComponent(TrailRuins.class, "TrailRuinsComponent");
            GameRegistry.registerWorldGenerator(new TrailRuinsGen(), 16);
        }
    }
}