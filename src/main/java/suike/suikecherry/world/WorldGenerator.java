package suike.suikecherry.world;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sblock.*;
import suike.suikecherry.config.ConfigValue;

import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.BiomePlains;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.ChunkEvent;

@Mod.EventBusSubscriber(modid = SuiKe.MODID)
public class WorldGenerator {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (ConfigValue.spawnCherryTree && !event.getChunk().isTerrainPopulated()) {
            World world = event.getWorld();
            BlockPos chunkPos = event.getChunk().getPos().getBlock(0, 0, 0);

            if (world.getBiome(chunkPos) instanceof BiomePlains) {
                //只在平原生成
                generateTrees(world, world.getSeed(), chunkPos);
            }
        }
    }

    public static void generateTrees(World world, long seed, BlockPos pos) {
        Random rand = new Random(seed + pos.getX() * 13 + pos.getZ() * 71 + 37);

        // 根据种子概率生成
        if (
            //true ||
            ConfigValue.IgnoreSpawnProbability ||
            rand.nextInt(24) == 0
        ) {
            int treeCount = 5 + rand.nextInt(10); // 生成5到15棵树木
            List<BlockPos> generatedPositions = new ArrayList<>(); // 存储已生成的位置
            int am = 0;

            for (int i = 0; i < treeCount; i++) {
                BlockPos 生成中心 = world.getHeight(pos);
                BlockPos treePos = world.getHeight(pos);// 确保位置在地表上
                boolean generate = true;

                if (am == 0) {
                    treePos = world.getHeight(pos); // 第一个树的位置
                } else {
                    /*获取上一个树的生成位置*/BlockPos treeRandPos = generatedPositions.get(generatedPositions.size() - 1);
                    /*通过上棵树的位置获取Random*/Random treeRand = new Random(seed + treeRandPos.getX() * 5 + treeRandPos.getZ() * 7 + 3);

                    int o = 0;
                    do {

                        if (o == 3) {// 尝试3次
                            generate = false;
                            break;
                        }

                        //通过上一棵树的位置随机偏移
                        int x, z = 0;
                        x = (int) (treeRand.nextInt(4) + 5);
                        z = (int) (treeRand.nextInt(4) + 5);

                        //随机偏移轴方向
                        int axis = (int) treeRand.nextInt(3) + 1;
                        if (axis == 1) {
                            x = -x;
                        } else if (axis == 2) {
                            z = -z;
                        } else if (axis == 3) {
                            x = -x;
                            z = -z;
                        }

                        if (am % 3 == 0){
                            treePos = 生成中心 = 生成中心.add(x, 0, z);
                        } else {
                            treePos = 生成中心.add(x, 0, z);
                        }

                        treePos = world.getHeight(treePos);

                        o++;
                    } while (
                        //检查生成位置是否符合
                        isTooClose(world, treePos, generatedPositions)
                    );
                }

                if (generate && world.getBlockState(treePos.down()).getBlock() == Blocks.GRASS) {
                    generatedPositions.add(treePos);
                    am++;
                }
            }

            if (ConfigValue.IgnoreSpawnProbability || am > 4) {
                for (BlockPos treePos : generatedPositions) {
                    if (拥有空间生长(world, treePos) && 避开液体(world, treePos)) {
                        //生成樱花树
                        CherryTree.getCherryTree().generateCherryTree(world, treePos, rand);
                        CherryTree.放置落英(treePos, 10);
                    }
                }
            }
        }
    }

//检查距离
    public static boolean isTooClose(World world, BlockPos treePos, List<BlockPos> existingPositions) {
        if (world.getBiome(treePos) instanceof BiomePlains) {
            int r = 4; // 5x5x5 的半径为2
            for (BlockPos pos : existingPositions) {
                for (int x = -r; x <= r; x++) {
                    for (int z = -r; z <= r; z++) {
                        BlockPos newPos = treePos.add(x, 0, z);
                        newPos = world.getHeight(newPos);;
                        if (newPos.equals(pos)) {
                            return true;// 太近
                        }
                    }
                }
            }

            return false; // 合法位置
        }

        return true;// 生物群系不符合
    }

//检查拥有空间生长 3格上方5x*5z*3y
    public static boolean 拥有空间生长(World world, BlockPos pos) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 3; y <= 5; y++) {
                    /*获取方块*/Block block = world.getBlockState(pos.add(x, y, z)).getBlock();
                    if (block == BlockBase.CHERRY_LOG || //如果周围有其他树, 则不允许生长
                        block == Blocks.COBBLESTONE || // 远离村庄房屋
                        block == Blocks.STONE ||
                        block == Blocks.GRASS
                    ) {
                        return false;
                    }
                }
            }
        }

        return true; //允许生长
    }

//避开液体方法
    public static boolean 避开液体(World world, BlockPos pos) {
        for (int y = -5; y <= -1; y++) {
            /*获取方块*/Block oldBlock = world.getBlockState(pos.add(0, y, 0)).getBlock();
            if (oldBlock instanceof net.minecraft.block.BlockLiquid) {
                return false;
            }
        }
        return true; //允许生长
    }
}