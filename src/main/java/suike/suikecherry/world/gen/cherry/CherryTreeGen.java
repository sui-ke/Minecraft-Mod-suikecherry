package suike.suikecherry.world.gen.cherry;

import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.function.Consumer;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.*;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.expand.futuremc.PlaceBeeHive;
import suike.suikecherry.config.ConfigValue;

import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

//樱花树
public class CherryTreeGen extends WorldGenAbstractTree {

    private World world;
    private Random random;
    private BlockPos treePos;
    private boolean isMirror;
    private boolean isSapling;
    private EnumFacing treeFacing;
    private static CherryTreeGen cherryTree = new CherryTreeGen();

    private static final EnumFacing[] DIRECTIONS = {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};

    public static final IBlockState leaves = BlockBase.CHERRY_LEAVES.getDefaultState();
    public static final IBlockState logY = BlockBase.CHERRY_LOG.getDefaultState().withProperty(ModBlockLog.AXIS, BlockLog.EnumAxis.Y);
    public static final IBlockState logX = BlockBase.CHERRY_LOG.getDefaultState().withProperty(ModBlockLog.AXIS, BlockLog.EnumAxis.X);
    public static final IBlockState logZ = BlockBase.CHERRY_LOG.getDefaultState().withProperty(ModBlockLog.AXIS, BlockLog.EnumAxis.Z);

    public CherryTreeGen() {
        super(false);
        this.clearWorld();
    }
    private CherryTreeGen(World world, BlockPos treePos, Random random, boolean isSapling) {
        super(true);
        this.world = world;
        this.treePos = treePos;
        this.isSapling = isSapling;
        this.random = isSapling ? random : new Random(world.getSeed() + treePos.getX() * 745 + 164 + treePos.getZ() * 357 + 256);
        this.treeFacing = DIRECTIONS[this.random.nextInt(DIRECTIONS.length)];
        this.isMirror = this.random.nextBoolean();
    }

    protected void setCherryTree() {
        CherryTreeGen.cherryTree = this;
    }

    public static CherryTreeGen getCherryTree() {
        return cherryTree;
    }

    public void generateCherryTree(World world, BlockPos treePos, Random random) {
        this.generateCherryTree(world, treePos, random, false);
    }
    public void generateCherryTree(World world, BlockPos treePos, Random random, boolean isSapling) {
        new CherryTreeGen(world, treePos, random, isSapling).generateCherryTree();
    }

    private void generateCherryTree() {
        this.generate(this.world, this.random, this.treePos);
    }

    @Override
    public boolean generate(World world, Random random, BlockPos treePos) {
        if (world == null) return false;

        // 随机一棵树
        int treeNumber = this.random.nextInt(cherryTreeList.size());
        // treeNumber = this.固定顺序生长(); // 测试每一棵树

        // 生成樱花树
        cherryTreeList.get(treeNumber).accept(this.treePos.down());

        // 生成蜂巢
        if (Examine.FuturemcID) this.placebeeHive();

        this.clearWorld();

        return true;
    }

    private void clearWorld() {
        this.world = null;
    }

/*
    // 固定顺序
    // 测试状态计数器
    private static int testCycle = 0;
    private static int currentTreeIndex = 0;
    private static int currentDirectionIndex = 0;
    private static boolean currentMirrorState = false;

    private int 固定顺序生长() {
        // 计算总测试组合数
        int totalTrees = cherryTreeList.size();
        int totalDirections = DIRECTIONS.length;
        int totalMirrorStates = 2; // true/false
        int totalCombinations = totalTrees * totalDirections * totalMirrorStates;

        // 获取当前测试组合
        currentTreeIndex = testCycle % totalTrees;
        currentDirectionIndex = (testCycle / totalTrees) % totalDirections;
        currentMirrorState = (testCycle / (totalTrees * totalDirections)) % 2 == 1;

        // 设置当前测试状态
        this.isMirror = currentMirrorState;
        this.treeFacing = DIRECTIONS[currentDirectionIndex];

        // 打印详细测试信息
        System.out.println("\n===== 测试组合 " + (testCycle + 1) + "/" + totalCombinations + " =====");
        System.out.println("树型: 第 " + (currentTreeIndex + 1) + " 种 (共 " + totalTrees + " 种)");
        System.out.println("方向: " + this.treeFacing + " (" + currentDirectionIndex + ")");
        System.out.println("镜像: " + this.isMirror);

        // 递增测试计数器
        testCycle++;

        // 循环提示
        if (testCycle >= totalCombinations) {
            System.out.println("\n注意: 已完成所有组合测试，将从头开始循环测试");
            testCycle = 0;
        }

        return currentTreeIndex;
    }
    //*/

// 樱花树列表
    private final List<Consumer<BlockPos>> cherryTreeList = Arrays.asList(
        this::cherryTree_1,
        this::cherryTree_2,
        this::cherryTree_3,
        this::cherryTree_4
    );

// 放置方块
    private void placeBlock(BlockPos pos) {
        this.placeBlock(pos, leaves);
    }
    private void placeBlock(BlockPos pos, IBlockState state) {
        this.placeBlock(pos, state, false);
    }
    private void placeBlock(BlockPos pos, IBlockState state, boolean a) {
        BlockPos rotatePos = rotateToFacing(this.treePos, pos, this.isMirror, this.treeFacing);
        Block oldBlock = world.getBlockState(rotatePos).getBlock();

        // 是否可以放置
        if (a || this.canReplaceableBlock(oldBlock, state, rotatePos)) {

            state = rotateToFacing(state, this.treeFacing);

            this.setBlockAndNotifyAdequately(this.world, rotatePos, state);
        }
    }

    private boolean canReplaceableBlock(Block oldBlock, IBlockState state, BlockPos rotatePos) {
        Block block = state.getBlock();

        // 方块相同不放置
        if (oldBlock == state.getBlock()) return false;

        // 不是树苗时检查是否是可替换方块
        if (!this.isSapling && oldBlock.isReplaceable(this.world, rotatePos)) return true;

        // 是空气或者樱花树叶
        return this.world.isAirBlock(rotatePos) || oldBlock == BlockBase.CHERRY_LEAVES;
    }

// 计算镜像和旋转方向
    // 坐标旋转
    public static BlockPos rotateToFacing(BlockPos origin, BlockPos pos, boolean isMirror, EnumFacing facing) {
        // 计算相对于原点的坐标
        int x = pos.getX() - origin.getX();
        int z = pos.getZ() - origin.getZ();

        // 根据朝向计算旋转后的坐标
        switch (facing) {
            case SOUTH: // 顺时针旋转90°（+Z 方向）
                if (isMirror) z = -z; // 南北镜像
                return new BlockPos(origin.getX() - z, pos.getY(), origin.getZ() + x);
            case WEST:  // 180° 旋转（-X 方向）
                if (isMirror) x = -x; // 东西镜像
                return new BlockPos(origin.getX() - x, pos.getY(), origin.getZ() - z);
            case NORTH: // 逆时针旋转90°（-Z 方向）
                if (isMirror) z = -z; // 南北镜像
                return new BlockPos(origin.getX() + z, pos.getY(), origin.getZ() - x);
            default:
                if (isMirror) x = -x; // 东西镜像
                return pos;
        }
    }

    // 方块旋转
    public static IBlockState rotateToFacing(IBlockState state, EnumFacing facing) {
        if (facing == EnumFacing.SOUTH || facing == EnumFacing.NORTH) {
            if (state == logX) {
                return logZ;
            } else if (state == logZ) {
                return logX;
            }
        }
        return state;
    }

// 生成蜂巢和蜜蜂
    private void placebeeHive() {
        if ((this.isSapling && ConfigValue.saplingSpawnBee) ||
            (!this.isSapling && ConfigValue.spawnTreeSpawnBee)) {

            //概率生成蜂巢
            if  ((random.nextInt(20)) == 0) PlaceBeeHive.place(this.world, this.treePos, this.random);
            // PlaceBeeHive.place(this.world, this.treePos, this.random); // 测试蜂巢生成
        }
    }

// 樱花树1
    private void cherryTree_1(BlockPos position) {
        //主干
        this.placeBlock(position.add(0, 1, 0), logY, true);
        this.placeBlock(position.add(0, 2, 0), logY);
        this.placeBlock(position.add(0, 3, 0), logY);
        this.placeBlock(position.add(0, 4, 0), logY);
        this.placeBlock(position.add(0, 5, 0), logY);
        this.placeBlock(position.add(0, 6, 0), logY);
        //直角支干
        this.placeBlock(position.add(-1, 4, 0), logX);
        this.placeBlock(position.add(-2, 4, 0), logX);
        this.placeBlock(position.add(-3, 4, 0), logX);
        this.placeBlock(position.add(-3, 5, 0), logY);
        this.placeBlock(position.add(-3, 6, 0), logY);
        this.placeBlock(position.add(-3, 7, 0), logY);
        //曲折支干
        this.placeBlock(position.add(1, 5, 0), logX);
        this.placeBlock(position.add(2, 5, 0), logX);
        this.placeBlock(position.add(2, 6, 0), logY);
        this.placeBlock(position.add(3, 6, 0), logX);
        this.placeBlock(position.add(3, 7, 0), logY);

        this.placeBlock(position.add(-1, 3, 1));
        this.placeBlock(position.add(-1, 4, 1));

        //第一层树叶
        BlockPos pos = position.add(-4, 5, -1);
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 3; z++) {
                this.placeBlock(pos.add(x, 0, z));
            }
        }

        //精修一层
        this.placeBlock(position.add(-3, 5, -2));
        this.placeBlock(position.add(-2, 5, -2));
        this.placeBlock(position.add(1, 5, -2));
        this.placeBlock(position.add(2, 5, -2));
        this.placeBlock(position.add(3, 5, -2));
        this.placeBlock(position.add(-3, 5, 2));
        this.placeBlock(position.add(-2, 5, 2));
        this.placeBlock(position.add(-1, 5, 2));
        this.placeBlock(position.add(2, 5, 2));
        this.placeBlock(position.add(3, 5, 2));
        this.placeBlock(position.add(2, 4, 2));//下垂
        this.placeBlock(position.add(4, 4, 1));//下垂
        this.placeBlock(position.add(1, 4, -2));//下垂
        this.placeBlock(position.add(-2, 4, -1));//下垂
        this.placeBlock(position.add(-3, 4, 2));//下垂

        //第二层树叶
        pos = position.add(-4, 6, -2);
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 5; z++) {
                this.placeBlock(pos.add(x, 0, z));
            }
        }

        //精修第二层
        this.placeBlock(position.add(-5, 6, -1));
        this.placeBlock(position.add(-5, 6, 0));
        this.placeBlock(position.add(5, 6, -1));
        this.placeBlock(position.add(5, 6, 1));
        this.placeBlock(position.add(-3, 6, -3));
        this.placeBlock(position.add(-2, 6, -3));
        this.placeBlock(position.add(-1, 6, -3));
        this.placeBlock(position.add(2, 6, -3));
        this.placeBlock(position.add(3, 6, -3));
        this.placeBlock(position.add(-3, 6, 3));
        this.placeBlock(position.add(-2, 6, 3));
        this.placeBlock(position.add(1, 6, 3));
        this.placeBlock(position.add(2, 6, 3));
        this.placeBlock(position.add(3, 6, 3));

        //第三层树叶
        pos = position.add(-4, 7, -2);
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 5; z++) {
                if ((x == 3 && z == 0) ||
                    (x == 4 && z == 0) ||
                    (x == 5 && z == 0) || 
                    (x == 3 && z == 4) ||
                    (x == 4 && z == 4) ||
                    (x == 5 && z == 4)) {
                    continue;
                }
                this.placeBlock(pos.add(x, 0, z));
            }
        }

        //精修第三层
        this.placeBlock(position.add(-5, 7, 0));
        this.placeBlock(position.add(-5, 7, 1));
        this.placeBlock(position.add(5, 7, -1));
        this.placeBlock(position.add(5, 7, 0));
        this.placeBlock(position.add(5, 7, 1));

        //第四层树叶负X
        this.placeBlock(position.add(-1, 8, 0));
        this.placeBlock(position.add(-2, 8, -1));
        this.placeBlock(position.add(-2, 8, 0));
        this.placeBlock(position.add(-2, 8, 1));
        this.placeBlock(position.add(-3, 8, -1));
        this.placeBlock(position.add(-3, 8, 0));
        this.placeBlock(position.add(-3, 8, 1));
        this.placeBlock(position.add(-4, 8, 0));
        this.placeBlock(position.add(-4, 8, 1));

        //第四层树叶正X
        this.placeBlock(position.add(1, 8, 0));
        this.placeBlock(position.add(2, 8, -1));
        this.placeBlock(position.add(2, 8, 0));
        this.placeBlock(position.add(2, 8, 1));
        this.placeBlock(position.add(3, 8, -1));
        this.placeBlock(position.add(3, 8, 0));
        this.placeBlock(position.add(3, 8, 1));
        this.placeBlock(position.add(4, 8, -1));
        this.placeBlock(position.add(4, 8, 0));
        this.placeBlock(position.add(4, 8, 1));

        //第五层树叶负X
        this.placeBlock(position.add(-2, 9, 0));
        this.placeBlock(position.add(-3, 9, 0));

        //第五层树叶正X
        this.placeBlock(position.add(2, 9, 0));
        this.placeBlock(position.add(3, 9, 0));
    }

// 樱花树2
    private void cherryTree_2(BlockPos position) {
        //主干
        this.placeBlock(position.add(0, 1, 0), logY, true);
        this.placeBlock(position.add(0, 2, 0), logY);
        this.placeBlock(position.add(0, 3, 0), logY);
        this.placeBlock(position.add(1, 3, 0), logX);
        this.placeBlock(position.add(1, 4, 0), logY);
        this.placeBlock(position.add(2, 4, 0), logX);
        this.placeBlock(position.add(2, 5, 0), logY);
        this.placeBlock(position.add(2, 6, 0), logY);
        this.placeBlock(position.add(2, 7, 0), logY);
        this.placeBlock(position.add(2, 8, 0), logY);

        //树叶主体
        BlockPos pos = position.add(0, 6, -2);
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    if ((x == 0 && y == 0 && z == 0) ||
                        (x == 0 && y == 3 && z == 0) ||
                        (x == 0 && y == 0 && z == 4) ||
                        (x == 4 && y == 0 && z == 4) ||
                        (x == 0 && y == 3 && z == 4) ||
                        (x == 4 && y == 0 && z == 0) ||
                        (x == 4 && y == 3 && z == 0) ||
                        (x == 4 && y == 3 && z == 4)) {
                        continue;
                    }
                    this.placeBlock(pos.add(x, y, z));
                }
            }
        }

        //下垂
        this.placeBlock(position.add(1, 4, -1));
        this.placeBlock(position.add(0, 5, 1));
        this.placeBlock(position.add(4, 5, -1));
        this.placeBlock(position.add(1, 5, -1));
        this.placeBlock(position.add(3, 5, 2));

        //西侧
        this.placeBlock(position.add(-1, 7, 0));
        this.placeBlock(position.add(-1, 8, -1));
        this.placeBlock(position.add(-1, 8, 0));
        this.placeBlock(position.add(-1, 8, 1));

        //北侧
        this.placeBlock(position.add(1, 7, -3));
        this.placeBlock(position.add(2, 7, -3));
        this.placeBlock(position.add(1, 8, -3));
        this.placeBlock(position.add(2, 8, -3));
        this.placeBlock(position.add(3, 8, -3));

        //东侧
        this.placeBlock(position.add(5, 7, -1));
        this.placeBlock(position.add(5, 7, 0));
        this.placeBlock(position.add(5, 8, 0));
        this.placeBlock(position.add(5, 8, 1));

        //南侧
        this.placeBlock(position.add(1, 7, 3));
        this.placeBlock(position.add(3, 7, 3));
        this.placeBlock(position.add(1, 8, 3));
        this.placeBlock(position.add(2, 8, 3));
        this.placeBlock(position.add(3, 8, 3));

        //顶部
        this.placeBlock(position.add(2, 10, -1));
        this.placeBlock(position.add(1, 10, 0));
        this.placeBlock(position.add(2, 10, 0));
        this.placeBlock(position.add(3, 10, 0));
        this.placeBlock(position.add(1, 10, 1));
        this.placeBlock(position.add(2, 10, 1));
        this.placeBlock(position.add(3, 10, 1));
    }

// 樱花树3
    private void cherryTree_3(BlockPos position) {
        //主干
        this.placeBlock(position.add(0, 1, 0), logY, true);
        this.placeBlock(position.add(0, 2, 0), logY);
        this.placeBlock(position.add(0, 3, 0), logY);
        this.placeBlock(position.add(0, 4, 0), logY);

        //矮支干
        this.placeBlock(position.add(-1, 4, 0), logX);
        this.placeBlock(position.add(-2, 4, 0), logX);
        this.placeBlock(position.add(-3, 4, 0), logX);
        this.placeBlock(position.add(-3, 5, 0), logY);
        this.placeBlock(position.add(-3, 6, 0), logY);
        this.placeBlock(position.add(-3, 7, 0), logY);

        //高支干
        this.placeBlock(position.add(1, 3, 0), logX);
        this.placeBlock(position.add(2, 3, 0), logX);
        this.placeBlock(position.add(3, 3, 0), logX);
        this.placeBlock(position.add(3, 4, 0), logY);
        this.placeBlock(position.add(4, 4, 0), logX);
        this.placeBlock(position.add(4, 5, 0), logY);
        this.placeBlock(position.add(4, 6, 0), logY);
        this.placeBlock(position.add(4, 7, 0), logY);
        this.placeBlock(position.add(4, 8, 0), logY);

        //高树叶主体
        BlockPos pos = position.add(2, 6, -2);
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    if ((x == 0 && y == 0 && z == 0) ||
                        (x == 4 && y == 3 && z == 4) ||
                        (x == 4 && y == 0 && z == 0) ||
                        (x == 0 && y == 3 && z == 0) ||
                        (x == 4 && y == 3 && z == 0) ||
                        (x == 0 && y == 0 && z == 4) ||
                        (x == 0 && y == 3 && z == 4)) {
                        continue;
                    }
                    this.placeBlock(pos.add(x, y, z));
                }
            }
        }

        //下垂
        this.placeBlock(position.add(4, 5, -2));
        this.placeBlock(position.add(5, 5, 2));
        this.placeBlock(position.add(6, 5, 1));
        this.placeBlock(position.add(3, 5, 1));
        this.placeBlock(position.add(3, 4, 1));
        this.placeBlock(position.add(5, 5, -1));
        this.placeBlock(position.add(5, 4, -1));
        this.placeBlock(position.add(2, 5, -1));

        //精修南方
        this.placeBlock(position.add(4, 7, 3));
        this.placeBlock(position.add(5, 7, 3));
        this.placeBlock(position.add(3, 8, 3));
        this.placeBlock(position.add(4, 8, 3));
        this.placeBlock(position.add(5, 8, 3));

        //精修东方
        this.placeBlock(position.add(7, 7, -1));
        this.placeBlock(position.add(7, 7, 1));
        this.placeBlock(position.add(7, 8, -1));
        this.placeBlock(position.add(7, 8, 0));
        this.placeBlock(position.add(7, 8, 1));

        //精修北方
        this.placeBlock(position.add(3, 6, -3));
        this.placeBlock(position.add(3, 7, -3));
        this.placeBlock(position.add(4, 7, -3));
        this.placeBlock(position.add(4, 8, -3));
        this.placeBlock(position.add(5, 8, -3));

        //精修西方
        this.placeBlock(position.add(1, 7, 0));
        this.placeBlock(position.add(1, 8, -1));
        this.placeBlock(position.add(1, 8, 0));
        this.placeBlock(position.add(1, 8, 1));

        //顶
        this.placeBlock(position.add(3, 10, -1));
        this.placeBlock(position.add(4, 10, -1));
        this.placeBlock(position.add(5, 10, -1));
        this.placeBlock(position.add(3, 10, 0));
        this.placeBlock(position.add(4, 10, 0));
        this.placeBlock(position.add(5, 10, 0));
        this.placeBlock(position.add(4, 10, 1));

        //矮树叶主体
        pos = position.add(-5, 5, -2);
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    if ((x == 0 && y == 0 && z == 0) ||
                        (x == 0 && y == 3 && z == 0) ||
                        (x == 0 && y == 3 && z == 4) ||
                        (x == 4 && y == 3 && z == 0) ||
                        (x == 4 && y == 0 && z == 0) ||
                        (x == 4 && y == 3 && z == 4)) {
                        continue;
                    }
                    this.placeBlock(pos.add(x, y, z));
                }
            }
        }

        //下垂
        this.placeBlock(position.add(-3, 4, 1));
        this.placeBlock(position.add(-3, 3, 1));
        this.placeBlock(position.add(-2, 4, 2));
        this.placeBlock(position.add(-4, 4, 2));
        this.placeBlock(position.add(-5, 4, 1));
        this.placeBlock(position.add(-3, 4, -2));
        this.placeBlock(position.add(-1, 4, -1));
        this.placeBlock(position.add(-1, 3, -1));

        //精修南方
        this.placeBlock(position.add(-2, 5, 3));
        this.placeBlock(position.add(-4, 6, 3));
        this.placeBlock(position.add(-3, 6, 3));
        this.placeBlock(position.add(-2, 6, 3));
        this.placeBlock(position.add(-4, 7, 3));
        this.placeBlock(position.add(-3, 7, 3));

        //精修东方
        this.placeBlock(position.add(0, 5, 1));
        this.placeBlock(position.add(0, 6, 1));
        this.placeBlock(position.add(0, 6, 0));
        this.placeBlock(position.add(0, 6, -1));
        this.placeBlock(position.add(0, 7, 1));
        this.placeBlock(position.add(0, 7, 0));
        this.placeBlock(position.add(0, 7, -1));

        //精修北方
        this.placeBlock(position.add(-2, 6, -3));
        this.placeBlock(position.add(-4, 6, -3));
        this.placeBlock(position.add(-3, 7, -3));
        this.placeBlock(position.add(-4, 7, -3));

        //精修西方
        this.placeBlock(position.add(-6, 6, 0));
        this.placeBlock(position.add(-6, 7, -1));
        this.placeBlock(position.add(-6, 7, 0));
        this.placeBlock(position.add(-6, 7, 1));

        //顶
        this.placeBlock(position.add(-4, 9, -1));
        this.placeBlock(position.add(-3, 9, -1));
        this.placeBlock(position.add(-2, 9, -1));
        this.placeBlock(position.add(-4, 9, 0));
        this.placeBlock(position.add(-3, 9, 0));
        this.placeBlock(position.add(-2, 9, 0));
        this.placeBlock(position.add(-4, 9, 1));
        this.placeBlock(position.add(-3, 9, 1));
    }

// 樱花树4
    private void cherryTree_4(BlockPos position) {
        //主干
        this.placeBlock(position.add(0, 1, 0), logY, true);
        this.placeBlock(position.add(0, 2, 0), logY);
        this.placeBlock(position.add(0, 3, 0), logY);
        //高支干
        this.placeBlock(position.add(1, 3, 0), logX);
        this.placeBlock(position.add(1, 4, 0), logY);
        this.placeBlock(position.add(1, 5, 0), logY);
        this.placeBlock(position.add(2, 5, 0), logX);
        this.placeBlock(position.add(2, 6, 0), logY);
        this.placeBlock(position.add(2, 7, 0), logY);
        //矮支干
        this.placeBlock(position.add(-1, 4, 0), logX);
        this.placeBlock(position.add(-2, 4, 0), logX);
        this.placeBlock(position.add(-2, 5, 0), logY);
        this.placeBlock(position.add(-3, 5, 0), logX);
        this.placeBlock(position.add(-4, 5, 0), logX);
        this.placeBlock(position.add(-4, 6, 0), logY);

        //主体树叶
        BlockPos pos = position.add(-5, 5, -1);
        for (int x = 0; x <= 9; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = 0; z <= 2; z++) {
                    if ((x == 3 && y == 0 && z == 0) ||
                        (x == 8 && y == 0 && z == 3) ||
                        (x == 9 && y == 0 && z == 0) ||
                        (x == 9 && y == 0 && z == 1) ||
                        (x == 9 && y == 0 && z == 2)) {
                        continue;
                    }
                    this.placeBlock(pos.add(x, y, z));
                }
            }
        }

        //顶层主体树叶
        pos = position.add(0, 8, -1);
        for (int x = 0; x <= 4; x++) {
            for (int z = 0; z <= 2; z++) {
                this.placeBlock(pos.add(x, 0, z));
            }
        }
        this.placeBlock(position.add(-1, 8, 0));

        //下垂
        this.placeBlock(position.add(1, 4, 1));
        this.placeBlock(position.add(2, 4, 1));
        this.placeBlock(position.add(2, 3, 1));
        this.placeBlock(position.add(1, 4, -1));
        this.placeBlock(position.add(-3, 4, -1));
        this.placeBlock(position.add(-4, 4, -1));
        this.placeBlock(position.add(-4, 3, -1));
        this.placeBlock(position.add(-3, 4, 0));
        this.placeBlock(position.add(-4, 4, 0));
        this.placeBlock(position.add(-5, 4, 0));
        this.placeBlock(position.add(-3, 4, 1));
        this.placeBlock(position.add(-3, 3, 1));
        this.placeBlock(position.add(-4, 4, 1));

        //精修南面
        this.placeBlock(position.add(-1, 4, 2));
        this.placeBlock(position.add(-3, 5, 2));
        this.placeBlock(position.add(-2, 5, 2));
        this.placeBlock(position.add(-1, 5, 2));
        this.placeBlock(position.add(1, 5, 2));
        this.placeBlock(position.add(-5, 6, 2));
        this.placeBlock(position.add(-4, 6, 2));
        this.placeBlock(position.add(-3, 6, 2));
        this.placeBlock(position.add(-2, 6, 2));
        this.placeBlock(position.add(-1, 6, 2));
        this.placeBlock(position.add(0, 6, 2));
        this.placeBlock(position.add(1, 6, 2));
        this.placeBlock(position.add(2, 6, 2));
        this.placeBlock(position.add(3, 6, 2));
        this.placeBlock(position.add(-4, 7, 2));
        this.placeBlock(position.add(-3, 7, 2));
        this.placeBlock(position.add(-2, 7, 2));
        this.placeBlock(position.add(1, 7, 2));
        this.placeBlock(position.add(2, 7, 2));
        this.placeBlock(position.add(3, 7, 2));
        this.placeBlock(position.add(4, 7, 2));
        this.placeBlock(position.add(2, 8, 2));
        this.placeBlock(position.add(3, 8, 2));

        //精修东面
        this.placeBlock(position.add(5, 7, -1));
        this.placeBlock(position.add(5, 7, 0));
        this.placeBlock(position.add(5, 7, 1));

        //精修北面
        this.placeBlock(position.add(-4, 5, -2));
        this.placeBlock(position.add(-1, 5, -2));
        this.placeBlock(position.add(-1, 4, -2));
        this.placeBlock(position.add(0, 5, -2));
        this.placeBlock(position.add(-5, 6, -2));
        this.placeBlock(position.add(-4, 6, -2));
        this.placeBlock(position.add(-3, 6, -2));
        this.placeBlock(position.add(-2, 6, -2));
        this.placeBlock(position.add(-1, 6, -2));
        this.placeBlock(position.add(0, 6, -2));
        this.placeBlock(position.add(1, 6, -2));
        this.placeBlock(position.add(3, 6, -2));
        this.placeBlock(position.add(-4, 7, -2));
        this.placeBlock(position.add(-3, 7, -2));
        this.placeBlock(position.add(1, 7, -2));
        this.placeBlock(position.add(2, 7, -2));
        this.placeBlock(position.add(3, 7, -2));
        this.placeBlock(position.add(4, 7, -2));
        this.placeBlock(position.add(2, 8, -2));
        this.placeBlock(position.add(3, 8, -2));

        //精修西面
        this.placeBlock(position.add(-6, 6, 0));
        this.placeBlock(position.add(-6, 6, 1));
        this.placeBlock(position.add(-6, 6, -1));

        //顶
        this.placeBlock(position.add(-4, 8, -1));
        this.placeBlock(position.add(-4, 8, 0));
        this.placeBlock(position.add(-3, 8, 0));
        this.placeBlock(position.add(-3, 8, 1));
        this.placeBlock(position.add(1, 9, 0));
        this.placeBlock(position.add(2, 9, -1));
        this.placeBlock(position.add(2, 9, 0));
        this.placeBlock(position.add(2, 9, 1));
        this.placeBlock(position.add(3, 9, -1));
        this.placeBlock(position.add(3, 9, 0));
        this.placeBlock(position.add(3, 9, 1));
    }

// 检查拥有空间生长 3格上方5x*5z*3y
    public static boolean growthSpace(World world, BlockPos pos) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 3; y <= 5; y++) {
                    BlockPos rotatePos = pos.add(x, y, z);
                    Block block = world.getBlockState(rotatePos).getBlock();
                    if (!block.isReplaceable(world, rotatePos) && block != BlockBase.CHERRY_LEAVES) {
                        return false;
                    }
                }
            }
        }

        return true; //允许生长
    }
}