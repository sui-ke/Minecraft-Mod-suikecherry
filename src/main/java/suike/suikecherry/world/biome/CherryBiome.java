package suike.suikecherry.world.biome;

import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.function.Predicate;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.*;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.world.gen.PetalsGen;
import suike.suikecherry.world.gen.CherryTreeGen;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;

import net.minecraftforge.common.BiomeManager;

// 樱花树林
public class CherryBiome extends Biome {

    private Set<ICherryBiomeDecorate> CHERRYBIOMEGENS = new HashSet<>();

    public CherryBiome() {
        super(new BiomeProperties("suikecherry:cherry_grove")
            .setTemperature(0.5f) // 温度
            .setRainfall(0.2f) // 降雨量
            .setBaseHeight(ConfigValue.cherryBiomeBaseHeight) // 设置基准高度
            .setHeightVariation(ConfigValue.cherryBiomeHeightVariation) // 设置高度变化范围
            .setBaseBiome("forest")
        );

        // 设置生成权重
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(this, ConfigValue.cherryBiomeSize));

        // 按顺序添加装饰器
        this.CHERRYBIOMEGENS.add(new CherryTreeGenerator());
        this.CHERRYBIOMEGENS.add(new PetalsGenerator());
        this.CHERRYBIOMEGENS.add(new OreGenerator(Blocks.EMERALD_ORE, 5, 3));
        if (Examine.FuturemcID && ConfigValue.cherryBiomeSpawnBamboo) this.CHERRYBIOMEGENS.add(new BambooGenerator());

        // 禁用原树木生成
        this.decorator.treesPerChunk = -1;
        // 禁用瀑布
        this.decorator.generateFalls = false;
        // 禁用水坑
        // this.decorator.waterlakesPerChunk = false;
    }

// 地形装饰
    @Override
    public void decorate(World world, Random random, BlockPos pos) {
        super.decorate(world, random, pos);

        // 使用种子和坐标获取 random
        random = new Random(world.getSeed() + pos.getX() * 13 + pos.getZ() * 71 + 37);

        // 遍历并启用装饰器
        for (ICherryBiomeDecorate cherryDecorate : this.CHERRYBIOMEGENS) {
            cherryDecorate.decorate(world, pos, random);
        }
    }

// 颜色
    @Override
    public int getGrassColorAtPos(BlockPos pos) {
        //设置草地颜色
        return getModdedBiomeGrassColor(0xB6DB61);
    }
    @Override
    public int getFoliageColorAtPos(BlockPos pos) {
        //设置树叶颜色
        return getModdedBiomeGrassColor(0xB6DB61);
    }
    @Override
    public int getWaterColorMultiplier() {
        //设置水体颜色
        return getModdedBiomeGrassColor(0x5DB7EF);
    }

// 装饰器接口
    private interface ICherryBiomeDecorate {
        void decorate(World world, BlockPos pos, Random random);
    }

// 花簇生成器
    private class PetalsGenerator implements ICherryBiomeDecorate {
        private final int radius; // 生成半径

        public PetalsGenerator() {
            this.radius = 16;
        }

        @Override
        public void decorate(World world, BlockPos pos, Random random) {
            PetalsGen.cherryBiomePlacePetals(world, random, pos.add(8, 0, 8), radius);
        }
    }

// 樱花树生成器
    private static Set<int[]> GENERATEDPOS = new HashSet<>(); // 存储已生成的位置 全局使用
    private class CherryTreeGenerator implements ICherryBiomeDecorate {
        private final List<int[]> TREEGENPOS = new ArrayList<>();

        public CherryTreeGenerator() {
            this.TREEGENPOS.add(new int[]{4, 2});
            this.TREEGENPOS.add(new int[]{7, 7});
            this.TREEGENPOS.add(new int[]{12, 3});
            this.TREEGENPOS.add(new int[]{10, 11});
            this.TREEGENPOS.add(new int[]{3, 12});
        }

        private final int MAX_CACHE_SIZE = 24;
        private final int HALF_CACHE_SIZE = MAX_CACHE_SIZE / 2;

        // 清理过期的生成位置记录
        private void managePositionCache() {
            if (GENERATEDPOS.size() >= MAX_CACHE_SIZE) {
                Set<int[]> newSet = GENERATEDPOS.stream()
                    .skip(HALF_CACHE_SIZE)
                    .collect(Collectors.toCollection(HashSet::new));
                GENERATEDPOS = newSet;
            }
        }

        @Override
        public void decorate(World world, BlockPos pos, Random random) {
            // 清理过期的生成位置记录
            this.managePositionCache();

            int treesGenerated = 0; // 当前区块已生成的树数量

            for (int i = 0; i < this.TREEGENPOS.size(); i++) {
                // 一个区块最多两棵树
                if (treesGenerated >= 2) return;

                // 随机获取位置
                int[] posList = this.TREEGENPOS.get(random.nextInt(this.TREEGENPOS.size()));
                BlockPos treePos = pos.add(posList[0], 0, posList[1]);

                // 为每个位置创建一个随机数生成器
                Random posRandom = new Random(world.getSeed() + treePos.getX() * 4 + treePos.getZ() * 7 + 1);

                // 2/5 概率不生成, 但保底一棵
                if (treesGenerated != 0 && posRandom.nextInt(5) < 2) continue;

                // 应用随机偏移（-3到+2范围）
                treePos = treePos.add(
                    posRandom.nextInt(6) - 3, 
                    0, 
                    posRandom.nextInt(6) - 3
                );

                // 检查是否太靠近其他树
                if (this.isTooCloseToOtherTrees(treePos)) continue;

                // 确保位置在地表上
                treePos = world.getHeight(treePos); 

                if (this.isValidTreePosition(world, treePos)) {
                    // 生成樱花树
                    CherryTreeGen.getCherryTree().generateCherryTree(world, treePos, random);

                    // 记录生成位置
                    GENERATEDPOS.add(new int[]{treePos.getX(), treePos.getZ()});
                    treesGenerated++;
                }
            }
        }

        // 检查是否太靠近其他树
        private boolean isTooCloseToOtherTrees(BlockPos treePos) {
            // 检查5x5范围内是否有其他树
            for (int[] generatedPos : GENERATEDPOS) {
                int diffX = Math.abs(generatedPos[0] - treePos.getX());
                int diffZ = Math.abs(generatedPos[1] - treePos.getZ());

                if (diffX <= 5 && diffZ <= 5) {
                    return true;
                }
            }
            return false;
        }

        // 验证树位置是否有效
        private boolean isValidTreePosition(World world, BlockPos pos) {
            return world.getBlockState(pos.down()).getBlock() == Blocks.GRASS && // 下方是草方块
                world.getBiome(pos) instanceof CherryBiome &&        // 位置是樱花树林
                CherryTreeGen.growthSpace(world, pos) &&                 // 有生长空间
                avoidFluid(world, pos);                              // 避开液体
        }
    }

// 竹子生成器
    private class BambooGenerator implements ICherryBiomeDecorate {
        private final Block bambooBlock;
        private final IBlockState dirtState;
        private Set<int[]> DIRTPOSLIST = new HashSet<>();

        public BambooGenerator() {
            this.DIRTPOSLIST.add(new int[]{1, 0});
            this.DIRTPOSLIST.add(new int[]{-1, 0});
            this.DIRTPOSLIST.add(new int[]{0, 1});
            this.DIRTPOSLIST.add(new int[]{0, -1});
            this.DIRTPOSLIST.add(new int[]{1, 1});
            this.DIRTPOSLIST.add(new int[]{-1, 1});
            this.DIRTPOSLIST.add(new int[]{-1, -1});
            this.DIRTPOSLIST.add(new int[]{1, -1});

            this.bambooBlock = Block.getBlockFromItem(Item.getByNameOrId("futuremc:bamboo"));
            this.dirtState = Block.getBlockFromItem(Item.getByNameOrId("minecraft:dirt")).getStateFromMeta(2);
        }

        @Override
        public void decorate(World world, BlockPos pos, Random random) {
            // 确保位置在地表上
            pos = world.getHeight(
                pos.add(8, 0, 8) // 将中心设置到区块中心
                .add(
                    random.nextInt(11) - 5, // 使用区块 random 应用随机偏移
                    0,
                    random.nextInt(11) - 5
                )
            );

            // 通过种子和位置生成该位置 random
            Random posRandom = new Random(world.getSeed() + pos.getX() * 4 + pos.getZ() * 7 + 1);

            // 概率生成
            if (posRandom.nextInt(6) > 1 && this.isValidBambooPosition(world, pos)) {
                // 生成灰化土
                if (posRandom.nextInt(4) != 0) generateDirtSimple(world, posRandom, pos.down());

                // 放置竹子
                this.generateBamboo(world, pos, random);
            }
        }

        // 生成竹子主逻辑
        private void generateBamboo(World world, BlockPos pos, Random random) {
            IBlockState defaultState = bambooBlock.getStateFromMeta(3);
            IBlockState smallLeaves = bambooBlock.getStateFromMeta(4);
            IBlockState largeLeaves = bambooBlock.getStateFromMeta(5);

            // 生成一颗高度 10 ~ 16 的竹子
            int height = random.nextInt(7) + 10;
            BlockPos currentPos = pos;

            for (int i = 0; i < height; i++) {
                world.setBlockState(currentPos, defaultState, 2);
                currentPos = currentPos.up(); // 放置竹子主干
            }

            // 放置顶部带树叶竹子
            world.setBlockState(currentPos, smallLeaves, 2);
            world.setBlockState(currentPos.up(), largeLeaves, 2);
            world.setBlockState(currentPos.up(2), largeLeaves, 2);
        }

        // 生成灰化土
        private void generateDirtSimple(World world, Random random, BlockPos pos) {
            // 放置灰化土
            world.setBlockState(pos, dirtState, 2);

            // 生成 am 个灰化土
            int am = random.nextInt(5);

            if (am == 0) return;

            List<int[]> bambooPos = new ArrayList<>(this.DIRTPOSLIST);

            // 额外放置灰化土
            for (int i = 1; i <= am; i++) {
                random = new Random(world.getSeed() + pos.getX() * 4 + pos.getZ() * 7 + i);
                // 随机获取一个数组
                int posChance = random.nextInt(bambooPos.size());

                int[] posList = bambooPos.get(posChance);

                BlockPos newPos = pos.add(posList[0], 0, posList[1]);

                if (world.getBlockState(newPos).getBlock() == Blocks.GRASS) {
                    world.setBlockState(newPos, dirtState, 2);
                } else if (world.getBlockState(newPos.up()).getBlock() == Blocks.GRASS) {
                    world.setBlockState(newPos.up(), dirtState, 2);
                } else if (world.getBlockState(newPos.down()).getBlock() == Blocks.GRASS) {
                    world.setBlockState(newPos.down(), dirtState, 2);
                }

                // 删除已使用的数组
                bambooPos.remove(posChance);
            }
        }

        // 验证竹子位置是否有效
        private boolean isValidBambooPosition(World world, BlockPos pos) {
            return world.getBlockState(pos.down()).getBlock() == Blocks.GRASS && // 下方是草方块
                world.getBiome(pos) instanceof CherryBiome &&        // 位置是樱花树林
                avoidFluid(world, pos) &&                            // 避开液体
                hasVerticalSpace(world, pos);                        // 上方无遮挡
        }

        private final int MIN_SPACE_ABOVE = 5;
        private final int MAX_SPACE_ABOVE = 16;
        private boolean hasVerticalSpace(World world, BlockPos pos) {
            for (int y = pos.getY() + MIN_SPACE_ABOVE; y <= pos.getY() + MAX_SPACE_ABOVE; y++) {
                if (!world.isAirBlock(new BlockPos(pos.getX(), y, pos.getZ()))) {
                    return false;
                }
            }
            return true;
        }
    }

// 矿石生成器
    private class OreGenerator implements ICherryBiomeDecorate {
        private final IBlockState oreBlock; // 矿石
        private final int veinsPerChunk; // 尝试生成次数
        private final int veinSize; // 矿脉大小
        private final Integer richHeight; // 富集高度 (可为null表示无富集区域)
        private final int richRange = 10; // 富集区域范围
        private final int minHeight; // 最小高度
        private final int maxHeight; // 最大高度

        public OreGenerator(Block block, int veinsPerChunk, int veinSize) {
            this(block, veinsPerChunk, veinSize, null);
        }
        public OreGenerator(Block block, int veinsPerChunk, int veinSize, Integer richHeight) {
            this(block, veinsPerChunk, veinSize, richHeight, 30, 140);
        }
        public OreGenerator(Block block, int veinsPerChunk, int veinSize, Integer richHeight, int minHeight, int maxHeight) {
            this.oreBlock = block.getDefaultState();
            this.veinsPerChunk = veinsPerChunk;
            this.veinSize = veinSize;
            this.richHeight = richHeight;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }

        @Override
        public void decorate(World world, BlockPos pos, Random random) {
            // 获取世界坐标
            int baseX = pos.getX();
            int baseZ = pos.getZ();

            for (int i = 0; i < veinsPerChunk; i++) {
                // 生成坐标
                int x = baseX + random.nextInt(16);
                int z = baseZ + random.nextInt(16);
                int y = getRandomHeight(random);
                BlockPos genPos = new BlockPos(x, y, z);

                // 三维坐标验证
                if (!this.isValidPosition(world, genPos)) continue;

                // 矿石生成
                this.generateOreVein(world, random, genPos);
            }
        }

        // 获取生成高度
        private int getRandomHeight(Random random) {
            if (richHeight != null && random.nextFloat() < 0.6f) { // 60%概率生成在富集区域
                int richMin = Math.max(minHeight, richHeight - richRange);
                int richMax = Math.min(maxHeight, richHeight + richRange);
                return richMin + random.nextInt(richMax - richMin + 1);
            }
            return minHeight + random.nextInt(maxHeight - minHeight + 1);
        }

        private boolean isValidPosition(World world, BlockPos pos) {
            // 基础高度检查
            if (pos.getY() < 1 || pos.getY() > 255) return false;
            
            // 目标不为可替换方块
            if (!this.canReplaceableStone(world.getBlockState(pos))) {
                return false;
            }

            return true;
        }

        private void generateOreVein(World world, Random random, BlockPos pos) {
            // 第一个矿石直接放置在 pos
            world.setBlockState(pos, this.oreBlock, 2);

            // 随机生成更多矿石数量（1 ~ this.veinSize）
            int oreCount = random.nextInt(this.veinSize);

            // 通过随机 EnumFacing 放置
            for (int i = 1; i < oreCount; i++) {
                EnumFacing direction = EnumFacing.random(random);
                BlockPos targetPos = pos.offset(direction);
                if (this.canReplaceableStone(world.getBlockState(targetPos))) {
                    world.setBlockState(targetPos, this.oreBlock, 2);
                }
            }
        }

        private boolean canReplaceableStone(IBlockState state) {
            Block block = state.getBlock();
            return block == Blocks.STONE ||
                    block == Blocks.STONE.getStateFromMeta(1).getBlock() || // 花岗岩 // 必须都检查不然三废石替换失效
                    block == Blocks.STONE.getStateFromMeta(3).getBlock() || // 闪长岩
                    block == Blocks.STONE.getStateFromMeta(4).getBlock() || // 安山岩
                    block == Blocks.GRAVEL;
        }
    }

// 避开液体方法
    private static boolean avoidFluid(World world, BlockPos pos) {
        for (int y = -5; y <= -1; y++) {
            Block oldBlock = world.getBlockState(pos.add(0, y, 0)).getBlock();
            if (oldBlock instanceof net.minecraft.block.BlockLiquid) {
                return false;
            }
        }
        return true; // 允许生长
    }
}