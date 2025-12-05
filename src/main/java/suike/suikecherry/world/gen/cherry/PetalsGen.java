package suike.suikecherry.world.gen.cherry;

import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.*;
import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.world.biome.CherryBiome;

import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

public class PetalsGen {
    public static final IBlockState grassState = Blocks.TALLGRASS.getStateFromMeta(1);
    public static final IBlockState petalsState = BlockBase.PINK_PETALS.getDefaultState();

    public static void placePetals(World world, BlockPos pos, int radius) {
        placePetals(world, pos, radius, 10, false, null);
    }
    public static void placePetals(World world, BlockPos pos, int radius, int maxHeight) {
        placePetals(world, pos, radius, maxHeight, false, null);
    }
    public static void plainsBiomePlacePetals(World world, Random rand, BlockPos pos, int radius) {
        placePetals(world, pos, radius, 10, false, rand);
    }
    public static void cherryBiomePlacePetals(World world, Random rand, BlockPos pos, int radius) {
        placePetals(world, pos.add(8, 0, 8), radius, null, true, rand);
    }

    private static void placePetals(World world, BlockPos centerPos, int radius, Integer maxHeight, boolean isCherryBiomeGen, Random rand) {
        int centerY = centerPos.getY();
        // 使用传入的随机数生成器 或 创建新的
        Random random = rand != null ? rand : new Random();
        int radiusSq = radius * radius; // 缓存半径平方
        int seaLevel = isCherryBiomeGen ? 64 : 0;
        int randomLevel = isCherryBiomeGen ? 8 : 5;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // 检查是否在圆形区域内
                if (x*x + z*z > radiusSq) continue;

                // 决定放置什么
                int chance = random.nextInt(randomLevel);
                if (chance != 0 && !(isCherryBiomeGen && chance >= 7)) continue;

                BlockPos currentPos = centerPos.add(x, seaLevel, z);

                // 检查生物群系
                if (isCherryBiomeGen && !(world.getBiome(currentPos) instanceof CherryBiome)) continue;

                currentPos = world.getHeight(currentPos); // 确保位置在地表上

                // 检查高度限制
                if (maxHeight != null && Math.abs(currentPos.getY() - centerY) > maxHeight) continue;

                // 检查目标位置是否为空气
                if (!world.isAirBlock(currentPos)) continue;

                // 检查下方方块
                if (!ModBlockPetals.downBlockIsGrass(world, currentPos, false)) continue;

                if (chance == 0) {
                    // 放置花瓣: 随机花瓣 数量 和 方向
                    int axis = random.nextInt(4) + 1;
                    int level = random.nextInt(4) + 1;

                    world.setBlockState(
                        currentPos,
                        petalsState.withProperty(ModBlockPetals.AXIS, axis)
                                   .withProperty(ModBlockPetals.LEVEL, level),
                        2
                    );
                } else {
                    // 只在樱花生物群系中放置草丛
                    world.setBlockState(currentPos, grassState, 2);
                }
            }
        }
    }
}