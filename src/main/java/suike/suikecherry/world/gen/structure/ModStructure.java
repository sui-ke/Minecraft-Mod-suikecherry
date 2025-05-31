package suike.suikecherry.world.gen.structure;

import java.util.Set;

import suike.suikecherry.world.gen.structure.oceanruins.*;
import suike.suikecherry.world.gen.structure.desertpyramid.*;

import net.minecraft.world.gen.structure.MapGenStructureIO;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModStructure {
    public static void registerStructure() {
        // 海底废墟
        MapGenStructureIO.registerStructure(OceanRuinsGen.OceanRuinsStart.class, "OceanRuins");
        MapGenStructureIO.registerStructureComponent(OceanRuins.class, "OceanRuinsComponent");
        GameRegistry.registerWorldGenerator(new OceanRuinsGen(), 9);

        // 沙漠水井
        MapGenStructureIO.registerStructure(DesertWellGen.DesertWellStart.class, "DesertWellTreasure");
        MapGenStructureIO.registerStructureComponent(DesertWell.class, "DesertWellTreasureComponent");
        GameRegistry.registerWorldGenerator(new DesertWellGen(), 11);

        // 沙漠神殿
        MapGenStructureIO.registerStructure(DesertPyramidTreasureRoomGen.DesertPyramidStart.class, "DesertPyramidTreasureRoom");
        MapGenStructureIO.registerStructureComponent(DesertPyramidTreasureRoom.class, "DesertPyramidTreasureRoomComponent");
        GameRegistry.registerWorldGenerator(new DesertPyramidTreasureRoomGen(), 10);
    }
}