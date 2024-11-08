package suike.suikecherry.world;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.lang.reflect.Field;

import suike.suikecherry.sblock.*;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.config.ConfigValue;

import thedarkcolour.futuremc.block.villagepillage.BlockBamboo;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenLakes;

import net.minecraftforge.common.BiomeManager;

public class CherryBiome extends Biome {
    public CherryBiome() {
        super(new BiomeProperties("suikecherry:cherry_grove")
            .setTemperature(0.6f)// 温度
            .setRainfall(0.8f)// 湿度
            .setBaseHeight(0.8f) // 设置基准高度
            .setHeightVariation(0.14f) // 设置高度变化范围
            .setBaseBiome("forest"));// 基础生物群系

        /*禁用树木生成*/this.decorator.treesPerChunk = -1;

        /*设置生成权重*/BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(this, 10));
    }

    @Override
    public int getGrassColorAtPos(BlockPos pos) {
        //设置草地颜色
        return getModdedBiomeGrassColor(0x2EEC67);
    }

//seed  -4010465041474756583
//pos   /tp 3570 100 -4000

    @Override
    public void decorate(World world, Random rand, BlockPos pos) {
        super.decorate(world, rand, pos);
        rand = new Random(world.getSeed() + pos.getX() * 13 + pos.getZ() * 71 + 37);

        生成樱花树(world, rand, pos);

        BlockPos currentPos = pos.add(8, 0, 8);
        放置落英(world, rand, currentPos, 16);

        if (ConfigValue.cherryBiomeSpawnBamboo && Examine.FuturemcID) {
            生成竹子(world, rand, currentPos);
        }
    }

//放置落英
    public static void 放置落英(World world, Random rand, BlockPos pos, int r) {
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                BlockPos currentPos = pos.add(x, 0, z);
                currentPos = world.getHeight(currentPos);// 确保位置在地表上

                Block 旧方块 = world.getBlockState(currentPos).getBlock();

                if (旧方块 == Blocks.AIR) {
                    
                    Block 下方方块 = world.getBlockState(currentPos.down()).getBlock();

                    if (下方方块 != Blocks.GRASS) {
                        continue;
                    }

                    if (!(world.getBiome(currentPos) instanceof CherryBiome)) {
                        continue;
                    }

                    /*是否生成落英*/int a = rand.nextInt(8);
                    if (a == 0) {
                        int axis = rand.nextInt(4) + 1;
                        int level = rand.nextInt(4) + 1;

                        IBlockState state = BlockBase.PINK_PETALS.getDefaultState()
                            .withProperty(SBlockPetals.AXIS, axis)
                            .withProperty(SBlockPetals.LEVEL, level);

                        world.setBlockState(currentPos, state, 2);
                    }
                }
            }
        }
    }

//树&灰化土生成位置
    public static Set<int[]> posLists = new HashSet<>();
    public static Set<int[]> bambooPosLists = new HashSet<>();
    public static void 初始化生成位置() {
        posLists.add(new int[]{4, 2});
        posLists.add(new int[]{7, 7});
        posLists.add(new int[]{12, 3});
        posLists.add(new int[]{10, 11});
        posLists.add(new int[]{3, 12});

        bambooPosLists.add(new int[]{1, 0});
        bambooPosLists.add(new int[]{-1, 0});
        bambooPosLists.add(new int[]{0, 1});
        bambooPosLists.add(new int[]{0, -1});
        bambooPosLists.add(new int[]{1, 1});
        bambooPosLists.add(new int[]{-1, 1});
        bambooPosLists.add(new int[]{-1, -1});
        bambooPosLists.add(new int[]{1, -1});
    }

//生成樱花树
    public static void 生成樱花树(World world, Random rand, BlockPos pos) {
    //正常高度树
        for (int[] posList : posLists) {
            BlockPos treePos = pos.add(posList[0], 0, posList[1]);

            rand = new Random(world.getSeed() + treePos.getX() * 4 + treePos.getZ() * 7 + 1);

            // 1/3 概率不生成
            if (rand.nextInt(3) != 0) {
                /*随机偏移*/int x = rand.nextInt(4) - 2;
                /*随机偏移*/int z = rand.nextInt(4) - 2;

                treePos = treePos.add(x, 0, z);
                /*确保位置在地表上*/treePos = world.getHeight(treePos);

                if (
                    world.getBlockState(treePos.down()).getBlock() == Blocks.GRASS &&
                    避开液体(world, treePos)
                ) {
                    rand = new Random(world.getSeed() + treePos.getX() * 4 + treePos.getZ() * 7 + 1);
                    if (world.getBiome(treePos) instanceof CherryBiome) {
                        CherryTree.生成樱花树(world, treePos, rand, false);
                    }
                }
            }
        }

    //更高度树
        for (int i = 0; i <= 1; i++) {
            BlockPos treePos = pos;
            if (i == 0) {
                treePos = treePos.add(8, 0, 15);
            } else {
                treePos = treePos.add(0, 0, 7);
            }

            /*确保位置在地表上*/treePos = world.getHeight(treePos);
            rand = new Random(world.getSeed() + treePos.getX() * 4 + treePos.getZ() * 7 + 1);
            int chance = rand.nextInt(5);
            if (
                chance == 0 && 
                world.getBlockState(treePos.down()).getBlock() == Blocks.GRASS &&
                (world.getBiome(treePos) instanceof CherryBiome) &&
                避开液体(world, treePos)
            ) {
                /*升高 y 格*/int y = rand.nextInt(3) + 5;

                for (int o = 0; o <= y; o++) {
                    /*放置主干*/CherryTree.placeBlock(treePos, CherryTree.y, true);
                    /*升高主干*/treePos = treePos.add(0, 1, 0);
                }

                CherryTree.生成樱花树(world, treePos, rand, false);
            }
        }
    }

//生成竹子
    public static void 生成竹子(World world, Random rand, BlockPos pos) {
        /*随机偏移*/int x = rand.nextInt(15) - 7;
        /*随机偏移*/int z = rand.nextInt(15) - 7;

        pos = pos.add(x, 0, z);
        /*确保位置在地表上*/pos = world.getHeight(pos);

        if (
            world.getBlockState(pos.down()).getBlock() == Blocks.GRASS &&
            world.getBiome(pos) instanceof CherryBiome &&
            避开液体(world, pos)
        ) {
            for (int i = 3; i <= 16; i++) {
                BlockPos posUp = pos.up(i);
                Block block = world.getBlockState(posUp).getBlock();
                if (block != Blocks.AIR) {
                    return;
                }
            }

            rand = new Random(world.getSeed() + pos.getX() * 4 + pos.getZ() * 7 + 1);

            if (rand.nextInt(4) != 0) {
                /*生成灰化土*/生成灰化土(world, rand, pos.down());
            }

            /*获取方块的默认状态*/IBlockState state = 获取竹子状态("none");
            /*生成 y 格的竹子*/int y = rand.nextInt(6) + 12;
            for (int i = 0; i <= y; i++) {
                /*放置主干*/world.setBlockState(pos, state, 2);
                /*升高主干*/pos = pos.up();
            }

            //倒数第三层
            world.setBlockState(pos, 获取竹子状态("small"), 2);

            //倒数一二层
            world.setBlockState(pos.up(1), 获取竹子状态("large"), 2);
            world.setBlockState(pos.up(2), 获取竹子状态("large"), 2);
        }
    }

//生成灰化土
    public static void 生成灰化土(World world, Random rand, BlockPos pos) {
        /*通过注册名获取方块*/Block block = Block.getBlockFromItem(Item.getByNameOrId("minecraft:dirt"));
        /*获取灰化土*/IBlockState state = block.getStateFromMeta(2);

        /*放置灰化土*/world.setBlockState(pos, state, 2);

        List<int[]> bambooPos = new ArrayList<>(bambooPosLists);
        /*生成 am 个灰化土*/int am = rand.nextInt(5);

        for (int i = 1; i <= am; i++) {
            rand = new Random(world.getSeed() + pos.getX() * 4 + pos.getZ() * 7 + i);
            /*随机获取一个数组*/int posChance = rand.nextInt(bambooPos.size());
            int[] posList = bambooPos.get(posChance);

            BlockPos newPos = pos.add(posList[0], 0, posList[1]);

            if (world.getBlockState(newPos).getBlock() == Blocks.GRASS) {
                /*放置灰化土*/world.setBlockState(newPos, state, 2);
            } else if (world.getBlockState(newPos.up()).getBlock() == Blocks.GRASS) {
                /*放置灰化土*/world.setBlockState(newPos.up(), state, 2);
            } else if (world.getBlockState(newPos.down()).getBlock() == Blocks.GRASS) {
                /*放置灰化土*/world.setBlockState(newPos.down(), state, 2);
            }

            /*删除已使用的数组*/bambooPos.remove(posChance);
        }
    }

//获取竹子状态
    public static IBlockState 获取竹子状态(String stateName) {
        /*通过注册名获取方块*/Block block = Block.getBlockFromItem(Item.getByNameOrId("futuremc:bamboo"));
        /*获取方块的默认状态*/IBlockState state = block.getDefaultState();

        int meta = 3;
        if (stateName.equals("small")) {
            meta = 4; // LEAVES为small
        } else if (stateName.equals("large")) {
            meta = 5; // LEAVES为large
        }

        return block.getStateFromMeta(meta);
    }

//避开液体方法
    public static boolean 避开液体(World world, BlockPos pos) {
        for (int y = -5; y <= -1; y++) {
            /*获取方块*/Block block = world.getBlockState(pos.add(0, y, 0)).getBlock();
            if (block.getMaterial(null).isLiquid()) {
                return false;
            }
        }
        return true; //允许生长
    }
}