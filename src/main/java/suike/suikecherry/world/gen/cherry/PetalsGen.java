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
        placePetals(world, pos, radius, Integer.MAX_VALUE, true, rand);
    }

    private static void placePetals(World world, BlockPos centerPos, int radius, int maxHeight, boolean isCherryBiomeGen, Random rand) {
        // 使用传入的随机数生成器或创建新的
        int centerY = centerPos.getY();
        Random random = rand != null ? rand : new Random();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // 检查是否在圆形区域内
                if (x * x + z * z > radius * radius) continue;

                BlockPos currentPos = centerPos.add(x, 0, z);
                currentPos = world.getHeight(currentPos); // 确保位置在地表上

                if (isCherryBiomeGen) {
                    // 检查生物群系
                    if (!(world.getBiome(currentPos) instanceof CherryBiome)) {
                        continue;
                    }
                }

                // 检查高度限制
                int currentY = currentPos.getY();
                if (currentY > (centerY + maxHeight) || currentY < (centerY - maxHeight)) continue;

                // 检查目标位置是否为空气
                if (world.getBlockState(currentPos).getBlock() != Blocks.AIR) continue;

                // 检查下方方块
                if (!ModBlockPetals.downBlockIsGrass(world, currentPos, "isGenerate")) continue;

                // 决定放置什么
                int chance = random.nextInt(isCherryBiomeGen ? 8 : 5);

                if (chance == 0) {
                    // 放置花瓣: 随机花瓣 数量 和 方向
                    int axis = random.nextInt(4) + 1;
                    int level = random.nextInt(4) + 1;

                    IBlockState state = BlockBase.PINK_PETALS.getDefaultState()
                        .withProperty(ModBlockPetals.AXIS, axis)
                        .withProperty(ModBlockPetals.LEVEL, level);

                    world.setBlockState(currentPos, state, 2);
                } else if (isCherryBiomeGen && chance >= 7) {
                    // 只在樱花生物群系中放置高草
                    IBlockState state = Blocks.TALLGRASS.getStateFromMeta(1);
                    world.setBlockState(currentPos, state, 2);
                }
            }
        }
    }
}