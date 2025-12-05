package suike.suikecherry.world.biome;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Predicate;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.*;
import suike.suikecherry.inter.IBiomes;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.world.gen.cherry.PetalsGen;
import suike.suikecherry.world.gen.cherry.CherryTreeGen;

import biomesoplenty.api.enums.BOPTrees;
import biomesoplenty.api.block.BOPBlocks;
import biomesoplenty.common.world.generator.tree.GeneratorBulbTree;

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
import net.minecraftforge.common.BiomeDictionary;

// 樱花树林
public class CherryBiome extends Biome implements IBiomes {

    private final List<ICherryBiomeDecorate> CHERRY_BIOME_GENS = new ArrayList<>();

    public CherryBiome() {
        super(new BiomeProperties("suikecherry:cherry_grove")
            .setRainfall(0.2f)    // 湿度
            .setTemperature(0.5f) // 温度
            .setBaseHeight(ConfigValue.cherryBiomeBaseHeight)           // 设置基准高度
            .setHeightVariation(ConfigValue.cherryBiomeHeightVariation) // 设置高度变化范围
            .setBaseBiome("forest")
        );

        // 设置生成权重
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(this, this.getWeight()));

        // 按顺序添加装饰器
        this.CHERRY_BIOME_GENS.add(new CherryTreeGenerator());
        this.CHERRY_BIOME_GENS.add(new PetalsGenerator());
        this.CHERRY_BIOME_GENS.add(new OreGenerator(Blocks.EMERALD_ORE, 5, 3));
        if (ConfigValue.cherryBiomeSpawnBamboo && ConfigValue.hasBamboo) {
            this.CHERRY_BIOME_GENS.add(new BambooGenerator());
        }

        // 禁用原树木生成
        this.decorator.treesPerChunk = -1;
        // 禁用瀑布
        this.decorator.generateFalls = false;
    }

// IBiomes 接口方法
    @Override
    public int getWeight() {
        return ConfigValue.cherryBiomeSize;
    }

    @Override
    public String getBOPType() {
        return "WARM_TEMPERATE";
    }

    @Override
    public BiomeDictionary.Type[] getDictType() {
        return new BiomeDictionary.Type[] {
            BiomeDictionary.Type.FOREST,
            BiomeDictionary.Type.MOUNTAIN
        };
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

// 地形装饰
    @Override
    public void decorate(World world, Random random, BlockPos pos) {
        super.decorate(world, random, pos);
        // 遍历并启用装饰器
        for (ICherryBiomeDecorate cherryDecorate : this.CHERRY_BIOME_GENS) {
            cherryDecorate.decorate(world, pos, random);
        }
    }

// 装饰器接口
    private interface ICherryBiomeDecorate {
        void decorate(World world, BlockPos pos, Random random);
    }

// 花簇生成器
    private class PetalsGenerator implements ICherryBiomeDecorate {
        private final int radius; // 生成半径

        public PetalsGenerator() {
            this.radius = 16; // 稍微重叠其他区块以提高密度
        }

        @Override
        public void decorate(World world, BlockPos pos, Random random) {
            PetalsGen.cherryBiomePlacePetals(world, random, pos, this.radius);
        }
    }

// 樱花树生成器
    private static List<int[]> GENERATEDPOS = new ArrayList<>();  // 存储已生成的位置 全局使用
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
                List<int[]> newSet = GENERATEDPOS.stream()
                    .skip(HALF_CACHE_SIZE)
                    .collect(Collectors.toCollection(ArrayList::new));
                GENERATEDPOS = newSet;
            }
        }

        @Override
        public void decorate(World world, BlockPos pos, Random random) {
            // 清理过期的生成位置记录
            this.managePositionCache();

            int treesGenerated = 0;              // 当前区块已生成的树数量

            // 打乱位置列表避免重复尝试
            List<int[]> shuffledPos = new ArrayList<>(TREEGENPOS);
            Collections.shuffle(shuffledPos, random);

            for (int[] posList : shuffledPos) {
                // 一个区块最多两棵树
                if (treesGenerated >= 2) return;

                BlockPos treePos = pos.add(posList[0], 0, posList[1]);

                // 2/5 概率不生成, 但保底一棵
                if (treesGenerated != 0 && random.nextInt(5) < 2) continue;

                // 应用随机偏移（-3到+2范围）
                treePos = treePos.add(
                    random.nextInt(6) - 3, 
                    64, 
                    random.nextInt(6) - 3
                );

                // 位置是樱花树林
                if (!(world.getBiome(treePos) instanceof CherryBiome)) continue;

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
            int treeX = treePos.getX();
            int treeZ = treePos.getZ();
            // 检查5x5范围内是否有其他树
            for (int[] pos : GENERATEDPOS) {
                if (Math.abs(pos[0] - treeX) <= 5 && 
                    Math.abs(pos[1] - treeZ) <= 5) {
                    return true;
                }
            }
            return false;
        }

        // 验证树位置是否有效
        private boolean isValidTreePosition(World world, BlockPos pos) {
            return world.getBlockState(pos.down()).getBlock() == Blocks.GRASS && // 下方是草方块
                CherryTreeGen.growthSpace(world, pos) &&             // 有生长空间
                avoidFluid(world, pos);                              // 避开液体
        }
    }

// 竹子生成器
    private class BambooGenerator implements ICherryBiomeDecorate {
        private final IBlockState dirtState;
        private final boolean needRandomGen;
        private Set<int[]> DIRTPOSLIST = new HashSet<>();

        private final List<BambooGen> BAMBOO_GENS = new ArrayList<>();

        public BambooGenerator() {
            // 灰化土
            this.dirtState = Block.getBlockFromName("minecraft:dirt").getStateFromMeta(2);
            this.DIRTPOSLIST.add(new int[]{1, 0});
            this.DIRTPOSLIST.add(new int[]{-1, 0});
            this.DIRTPOSLIST.add(new int[]{0, 1});
            this.DIRTPOSLIST.add(new int[]{0, -1});
            this.DIRTPOSLIST.add(new int[]{1, 1});
            this.DIRTPOSLIST.add(new int[]{-1, 1});
            this.DIRTPOSLIST.add(new int[]{-1, -1});
            this.DIRTPOSLIST.add(new int[]{1, -1});

            // 竹子
            for (String bambooType : ConfigValue.bamboos) {
                switch (bambooType) {
                    case "futuremc":
                        BAMBOO_GENS.add(new BambooGen(
                            "futuremc:bamboo",
                            3, 4, 5,
                            false
                        ));
                        break;
                    case "sakura":
                        BAMBOO_GENS.add(new BambooGen(
                            "sakura:bamboo",
                            0, 0, 0,
                            false
                        ));
                        break;
                    case "biomesoplenty":
                        BAMBOO_GENS.add(new BambooGen(null, 0, 0, 0, true));
                        break;
                }
            }

            this.needRandomGen = BAMBOO_GENS.size() > 1;
        }

        @Override
        public void decorate(World world, BlockPos pos, Random random) {
            // 概率生成
            int a = random.nextInt(6);
            if (a <= 1) return;

            // 确保位置在地表上
            pos = world.getHeight(
                pos.add(8, 0, 8) // 将中心设置到区块中心
                .add(
                    random.nextInt(11) - 5, // 应用随机偏移
                    64,
                    random.nextInt(11) - 5
                )
            );

            // 位置有效性检查
            if (!this.isValidBambooPosition(world, pos)) return;

            // 概率生成灰化土
            if (a > 4) {
                this.generateDirtSimple(world, random, pos.down());
            }

            // 放置竹子
            BambooGen bambooGen = this.needRandomGen
                ? BAMBOO_GENS.get(random.nextInt(BAMBOO_GENS.size()))
                : BAMBOO_GENS.get(0);
            bambooGen.gen(world, pos, random);
        }

        // 生成竹子主逻辑
        private class BambooGen {
            private final IBlockState defaultBamboo;
            private final IBlockState smallLeaves;
            private final IBlockState largeLeaves;
            private final Object BOPBambooGenerator;

            private BambooGen(String blockName, int defaultMate, int smallLeavesMate, int largeLeavesMate, boolean isBiomesoplenty) {
                if (!isBiomesoplenty) {
                    Block bambooBlock = Block.getBlockFromName(blockName);
                    this.defaultBamboo = bambooBlock.getStateFromMeta(defaultMate);
                    this.smallLeaves = bambooBlock.getStateFromMeta(smallLeavesMate);
                    this.largeLeaves = bambooBlock.getStateFromMeta(largeLeavesMate);
                    this.BOPBambooGenerator = null;
                } else {
                    this.defaultBamboo = null;
                    this.smallLeaves = null;
                    this.largeLeaves = null;
                    this.BOPBambooGenerator = new GeneratorBulbTree.Builder().minHeight(10).maxHeight(20).log(BOPBlocks.bamboo.getDefaultState()).leaves(BOPTrees.BAMBOO).create();
                }
            }

            private void gen(World world, BlockPos pos, Random random) {
                if (this.BOPBambooGenerator != null) {
                    ((GeneratorBulbTree) this.BOPBambooGenerator).generate(world, random, pos);
                    return;
                }
                if (this.defaultBamboo == null) return;

                // 生成一颗高度 10 ~ 16 的竹子
                int height = random.nextInt(7) + 10;
                BlockPos currentPos = pos;

                for (int i = 0; i < height; i++) {
                    world.setBlockState(currentPos, this.defaultBamboo, 2);
                    currentPos = currentPos.up(); // 放置竹子主干
                }

                // 放置顶部带树叶竹子
                world.setBlockState(currentPos, this.smallLeaves, 2);
                world.setBlockState(currentPos.up(), this.largeLeaves, 2);
                world.setBlockState(currentPos.up(2), this.largeLeaves, 2);
            }
        }

        // 生成灰化土
        private void generateDirtSimple(World world, Random random, BlockPos pos) {
            // 放置灰化土
            world.setBlockState(pos, dirtState, 2);
            int am = random.nextInt(5);
            if (am == 0) return;

            List<int[]> positions = new ArrayList<>(DIRTPOSLIST);
            Collections.shuffle(positions, random);

            // 额外放置灰化土
            for (int i = 0; i < Math.min(am, positions.size()); i++) {
                int[] offset = positions.get(i);
                BlockPos newPos = pos.add(offset[0], 0, offset[1]);

                // 分层检查
                for (int yOffset : new int[]{0, 1, -1}) {
                    BlockPos checkPos = newPos.add(0, yOffset, 0);
                    if (world.getBlockState(checkPos).getBlock() == Blocks.GRASS) {
                        world.setBlockState(checkPos, dirtState, 2);
                        break;
                    }
                }
            }
        }

        // 验证竹子位置是否有效
        private boolean isValidBambooPosition(World world, BlockPos pos) {
            return world.getBlockState(pos.down()).getBlock() == Blocks.GRASS // 下方是草方块
                && world.getBiome(pos) instanceof CherryBiome // 位置是樱花树林
                && avoidFluid(world, pos)                     // 避开液体
                && hasVerticalSpace(world, pos);              // 上方无遮挡
        }

        private boolean hasVerticalSpace(World world, BlockPos pos) {
            for (int y = pos.getY() + 5; y <= pos.getY() + 46; y++) {
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
        private final int veinsPerChunk;    // 尝试生成次数
        private final int veinSize;         // 矿脉大小
        private final Integer richHeight;   // 富集高度 (可为null表示无富集区域)
        private final int richRange = 10;   // 富集区域范围
        private final int minHeight;        // 最小高度
        private final int maxHeight;        // 最大高度

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

            // 随机生成更多矿石数量 (Max: this.veinSize)
            int oreCount = random.nextInt(this.veinSize);

            // 预生成方向列表
            List<EnumFacing> directions = new ArrayList<>(6);
            for (int i = 0; i < oreCount; i++) {
                directions.add(EnumFacing.random(random));
            }

            // 通过随机 EnumFacing 放置
            for (EnumFacing dir : directions) {
                BlockPos target = pos.offset(dir);
                if (canReplaceableStone(world.getBlockState(target))) {
                    world.setBlockState(target, oreBlock, 2);
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
            if (world.getBlockState(pos.add(0, y, 0)).getMaterial().isLiquid()) {
                return false;
            }
        }
        return true; // 允许生长
    }
}