package suike.suikecherry.world;

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sblock.*;
import suike.suikecherry.expand.*;
import suike.suikecherry.config.ConfigValue;

import thedarkcolour.futuremc.block.buzzybees.BeeHiveBlock;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//樱花树
public class CherryTree {

    private static World world;
    private static Random random;
    private static boolean isSapling;
    private static CherryTree cherryTree = new CherryTree();

    private static final IBlockState y = BlockBase.CHERRY_LOG.getDefaultState().withProperty(SLog.AXIS, BlockLog.EnumAxis.Y);
    private static final IBlockState x = BlockBase.CHERRY_LOG.getDefaultState().withProperty(SLog.AXIS, BlockLog.EnumAxis.X);
    private static final IBlockState z = BlockBase.CHERRY_LOG.getDefaultState().withProperty(SLog.AXIS, BlockLog.EnumAxis.Z);
    private static final IBlockState leaves = BlockBase.CHERRY_LEAVES.getDefaultState();
    private static final IBlockState air = Blocks.AIR.getDefaultState();

    public CherryTree() {}
    public static CherryTree getCherryTree() {
        return cherryTree;
    }
    protected void setCherryTree() {
        CherryTree.cherryTree = this;
    }

    public void generateCherryTree(World world, BlockPos pos, Random random) {
        this.生成樱花树(world, pos, random, false);
    }
    public void generateCherryTree(World world, BlockPos pos, Random random, boolean isSapling) {
        this.生成樱花树(world, pos, random, isSapling);
    }

    private static void 生成樱花树(World world, BlockPos pos, Random random, boolean isSapling) {
        CherryTree.world = world;
        CherryTree.isSapling = isSapling;
        if (!isSapling)
            CherryTree.random = random;

        random = isSapling ? new Random() : new Random(world.getSeed() + pos.getX() * 745 + 164 + pos.getZ() * 357 + 256);
        int treeNumber = random.nextInt(cherryTreeList.size()); // 随机一棵树

        //treeNumber = 固定顺序生长();

        //创建位置数组
        positions[1] = pos;
        for (int i = 2; i < positions.length; i++) {
            positions[i] = pos.up(i - 1); //从 positions[2] 开始向上移动
        }

        /*生成樱花树*/cherryTreeList.get(treeNumber).run();

        if (isSapling && ConfigValue.cherrySaplingAndPetals) {
            放置落英(pos, 5);
        }
    }

//固定顺序
    /*
    private static int generateTreeNumber = 0;
    private static int 固定顺序生长() {
        if (generateTreeNumber >= cherryTreeList.size()) 
            generateTreeNumber = 0;

        generateTreeNumber++;
        System.out.println("生长第: " + generateTreeNumber + "棵树");
        return generateTreeNumber;
    }*/

//樱花树列表
    private static BlockPos[] positions = new BlockPos[12];
    private static List<Runnable> cherryTreeList = Arrays.asList(
            () -> 樱花树1x(positions),
            () -> 樱花树1x(positions),
            () -> 樱花树1z(positions),
            () -> 樱花树1z(positions),
            () -> 樱花树2正x(positions),
            () -> 樱花树2负x(positions),
            () -> 樱花树2正z(positions),
            () -> 樱花树2负z(positions),
            () -> 樱花树3正x(positions),
            () -> 樱花树3负x(positions),
            () -> 樱花树3正z(positions),
            () -> 樱花树3负z(positions),
            () -> 樱花树4正x(positions),
            () -> 樱花树4负x(positions),
            () -> 樱花树4正z(positions),
            () -> 樱花树4负z(positions)
        );

//  //pos
    //.up()朝 Y 方向（正方向）
    //.add(1, 0, 0)//朝 X 方向（正方向）
    //.add(-1, 0, 0)//朝 X 方向（负方向）
    //.add(0, 0, 1)//朝 Z 方向（正方向）
    //.add(0, 0, -1)//朝 Z 方向（负方向）

//樱花树1x
    private static void 樱花树1x(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        placeBlock(positions[4], y);
        placeBlock(positions[5], y);
        placeBlock(positions[6], y);
        //直角支干
        placeBlock(positions[4].add(-1, 0, 0), x);
        placeBlock(positions[4].add(-2, 0, 0), x);
        placeBlock(positions[4].add(-3, 0, 0), x);
        placeBlock(positions[5].add(-3, 0, 0), y);
        placeBlock(positions[6].add(-3, 0, 0), y);
        placeBlock(positions[7].add(-3, 0, 0), y);
        //曲折支干
        placeBlock(positions[5].add(1, 0, 0), x);
        placeBlock(positions[5].add(2, 0, 0), x);
        placeBlock(positions[6].add(2, 0, 0), y);
        placeBlock(positions[6].add(3, 0, 0), x);
        placeBlock(positions[7].add(3, 0, 0), y);

        //第一层树叶
        BlockPos pos = positions[5].add(-4, 0, -1);
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 3; z++) {
                placeBlock(pos.add(x, 0, z), leaves);
            }
        }

        //精修一层
        placeBlock(positions[5].add(-3, 0, -2), leaves);
        placeBlock(positions[5].add(-2, 0, -2), leaves);
        placeBlock(positions[5].add(1, 0, -2), leaves);
        placeBlock(positions[5].add(2, 0, -2), leaves);
        placeBlock(positions[5].add(3, 0, -2), leaves);
        placeBlock(positions[5].add(-3, 0, 2), leaves);
        placeBlock(positions[5].add(-2, 0, 2), leaves);
        placeBlock(positions[5].add(-1, 0, 2), leaves);
        placeBlock(positions[5].add(2, 0, 2), leaves);
        placeBlock(positions[5].add(3, 0, 2), leaves);
        placeBlock(positions[4].add(2, 0, 2), leaves);//下垂
        placeBlock(positions[4].add(4, 0, 1), leaves);//下垂
        placeBlock(positions[4].add(1, 0, -2), leaves);//下垂
        placeBlock(positions[4].add(-2, 0, -1), leaves);//下垂
        placeBlock(positions[4].add(-3, 0, 2), leaves);//下垂

        //第二层树叶
        pos = positions[6].add(-4, 0, -2);
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 5; z++) {
                placeBlock(pos.add(x, 0, z), leaves);
            }
        }

        //精修第二层
        placeBlock(positions[6].add(-5, 0, -1), leaves);
        placeBlock(positions[6].add(-5, 0, 0), leaves);
        placeBlock(positions[6].add(5, 0, -1), leaves);
        placeBlock(positions[6].add(5, 0, 1), leaves);
        placeBlock(positions[6].add(-3, 0, -3), leaves);
        placeBlock(positions[6].add(-2, 0, -3), leaves);
        placeBlock(positions[6].add(-1, 0, -3), leaves);
        placeBlock(positions[6].add(2, 0, -3), leaves);
        placeBlock(positions[6].add(3, 0, -3), leaves);
        placeBlock(positions[6].add(-3, 0, 3), leaves);
        placeBlock(positions[6].add(-2, 0, 3), leaves);
        placeBlock(positions[6].add(1, 0, 3), leaves);
        placeBlock(positions[6].add(2, 0, 3), leaves);
        placeBlock(positions[6].add(3, 0, 3), leaves);

        //第三层树叶
        pos = positions[7].add(-4, 0, -2);
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 5; z++) {
                placeBlock(pos.add(x, 0, z), leaves);
            }
        }

        //精修第三层
        placeBlock(positions[7].add(-1, 0, -2), air);
        placeBlock(positions[7].add(0, 0, -2), air);
        placeBlock(positions[7].add(1, 0, -2), air);
        placeBlock(positions[7].add(-1, 0, 2), air);
        placeBlock(positions[7].add(0, 0, 2), air);
        placeBlock(positions[7].add(1, 0, 2), air);
        placeBlock(positions[7].add(-5, 0, 0), leaves);
        placeBlock(positions[7].add(-5, 0, 1), leaves);
        placeBlock(positions[7].add(5, 0, -1), leaves);
        placeBlock(positions[7].add(5, 0, 0), leaves);
        placeBlock(positions[7].add(5, 0, 1), leaves);

        //第四层树叶负X
        placeBlock(positions[8].add(-1, 0, 0), leaves);
        placeBlock(positions[8].add(-2, 0, -1), leaves);
        placeBlock(positions[8].add(-2, 0, 0), leaves);
        placeBlock(positions[8].add(-2, 0, 1), leaves);
        placeBlock(positions[8].add(-3, 0, -1), leaves);
        placeBlock(positions[8].add(-3, 0, 0), leaves);
        placeBlock(positions[8].add(-3, 0, 1), leaves);
        placeBlock(positions[8].add(-4, 0, 0), leaves);
        placeBlock(positions[8].add(-4, 0, 1), leaves);

        //第五层树叶负X
        placeBlock(positions[9].add(-2, 0, 0), leaves);
        placeBlock(positions[9].add(-3, 0, 0), leaves);

        //第四层树叶正X
        placeBlock(positions[8].add(1, 0, 0), leaves);
        placeBlock(positions[8].add(2, 0, -1), leaves);
        placeBlock(positions[8].add(2, 0, 0), leaves);
        placeBlock(positions[8].add(2, 0, 1), leaves);
        placeBlock(positions[8].add(3, 0, -1), leaves);
        placeBlock(positions[8].add(3, 0, 0), leaves);
        placeBlock(positions[8].add(3, 0, 1), leaves);
        placeBlock(positions[8].add(4, 0, -1), leaves);
        placeBlock(positions[8].add(4, 0, 0), leaves);
        placeBlock(positions[8].add(4, 0, 1), leaves);

        //第五层树叶正X
        placeBlock(positions[9].add(2, 0, 0), leaves);
        placeBlock(positions[9].add(3, 0, 0), leaves);

        //生成蜂巢
        placebeeNest(positions[4].add(1, 0, 0));
    }

//樱花树1z
    private static void 樱花树1z(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        placeBlock(positions[4], y);
        placeBlock(positions[5], y);
        placeBlock(positions[6], y);
        //直角支干
        placeBlock(positions[4].add(0, 0, -1), z);
        placeBlock(positions[4].add(0, 0, -2), z);
        placeBlock(positions[4].add(0, 0, -3), z);
        placeBlock(positions[5].add(0, 0, -3), y);
        placeBlock(positions[6].add(0, 0, -3), y);
        placeBlock(positions[7].add(0, 0, -3), y);
        //曲折支干
        placeBlock(positions[5].add(0, 0, 1), z);
        placeBlock(positions[5].add(0, 0, 2), z);
        placeBlock(positions[6].add(0, 0, 2), y);
        placeBlock(positions[6].add(0, 0, 3), z);
        placeBlock(positions[7].add(0, 0, 3), y);

        //第一层树叶
        BlockPos pos = positions[5].add(-1, 0, -4);
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 9; z++) {
                placeBlock(pos.add(x, 0, z), leaves);
            }
        }

        //精修一层
        placeBlock(positions[5].add(-2, 0, -3), leaves);
        placeBlock(positions[5].add(-2, 0, -2), leaves);
        placeBlock(positions[5].add(-2, 0, 1), leaves);
        placeBlock(positions[5].add(-2, 0, 2), leaves);
        placeBlock(positions[5].add(-2, 0, 3), leaves);
        placeBlock(positions[5].add(2, 0, -3), leaves);
        placeBlock(positions[5].add(2, 0, -2), leaves);
        placeBlock(positions[5].add(2, 0, -1), leaves);
        placeBlock(positions[5].add(2, 0, 2), leaves);
        placeBlock(positions[5].add(2, 0, 3), leaves);
        placeBlock(positions[4].add(2, 0, 2), leaves);//下垂
        placeBlock(positions[4].add(1, 0, 4), leaves);//下垂
        placeBlock(positions[4].add(-2, 0, 1), leaves);//下垂
        placeBlock(positions[4].add(-1, 0, -2), leaves);//下垂
        placeBlock(positions[4].add(2, 0, -3), leaves);//下垂

        //第二层树叶
        pos = positions[6].add(-2, 0, -4);
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 9; z++) {
                placeBlock(pos.add(x, 0, z), leaves);
            }
        }

        //精修第二层
        placeBlock(positions[6].add(-1, 0, -5), leaves);
        placeBlock(positions[6].add(0, 0, -5), leaves);
        placeBlock(positions[6].add(-1, 0, 5), leaves);
        placeBlock(positions[6].add(1, 0, 5), leaves);
        placeBlock(positions[6].add(-3, 0, -3), leaves);
        placeBlock(positions[6].add(-3, 0, -2), leaves);
        placeBlock(positions[6].add(-3, 0, -1), leaves);
        placeBlock(positions[6].add(-3, 0, 2), leaves);
        placeBlock(positions[6].add(-3, 0, 3), leaves);
        placeBlock(positions[6].add(3, 0, -3), leaves);
        placeBlock(positions[6].add(3, 0, -2), leaves);
        placeBlock(positions[6].add(3, 0, 1), leaves);
        placeBlock(positions[6].add(3, 0, 2), leaves);
        placeBlock(positions[6].add(3, 0, 3), leaves);

        //第三层树叶
        pos = positions[7].add(-2, 0, -4);
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 9; z++) {
                placeBlock(pos.add(x, 0, z), leaves);
            }
        }

        //精修第三层
        placeBlock(positions[7].add(-2, 0, -1), air);
        placeBlock(positions[7].add(-2, 0, 0), air);
        placeBlock(positions[7].add(-2, 0, 1), air);
        placeBlock(positions[7].add(2, 0, -1), air);
        placeBlock(positions[7].add(2, 0, 0), air);
        placeBlock(positions[7].add(2, 0, 1), air);
        placeBlock(positions[7].add(0, 0, -5), leaves);
        placeBlock(positions[7].add(1, 0, -5), leaves);
        placeBlock(positions[7].add(-1, 0, 5), leaves);
        placeBlock(positions[7].add(0, 0, 5), leaves);
        placeBlock(positions[7].add(1, 0, 5), leaves);

        //第四层树叶负X
        placeBlock(positions[8].add(0, 0, -1), leaves);
        placeBlock(positions[8].add(-1, 0, -2), leaves);
        placeBlock(positions[8].add(0, 0, -2), leaves);
        placeBlock(positions[8].add(1, 0, -2), leaves);
        placeBlock(positions[8].add(-1, 0, -3), leaves);
        placeBlock(positions[8].add(0, 0, -3), leaves);
        placeBlock(positions[8].add(1, 0, -3), leaves);
        placeBlock(positions[8].add(0, 0, -4), leaves);
        placeBlock(positions[8].add(1, 0, -4), leaves);

        //第五层树叶负X
        placeBlock(positions[9].add(0, 0, -2), leaves);
        placeBlock(positions[9].add(0, 0, -3), leaves);

        //第四层树叶正X
        placeBlock(positions[8].add(0, 0, 1), leaves);
        placeBlock(positions[8].add(-1, 0, 2), leaves);
        placeBlock(positions[8].add(0, 0, 2), leaves);
        placeBlock(positions[8].add(1, 0, 2), leaves);
        placeBlock(positions[8].add(-1, 0, 3), leaves);
        placeBlock(positions[8].add(0, 0, 3), leaves);
        placeBlock(positions[8].add(1, 0, 3), leaves);
        placeBlock(positions[8].add(-1, 0, 4), leaves);
        placeBlock(positions[8].add(0, 0, 4), leaves);
        placeBlock(positions[8].add(1, 0, 4), leaves);

        //第五层树叶正X
        placeBlock(positions[9].add(0, 0, 2), leaves);
        placeBlock(positions[9].add(0, 0, 3), leaves);

        //生成蜂巢
        placebeeNest(positions[4].add(0, 0, 1));
    }

//樱花树2正x
    private static void 樱花树2正x(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        placeBlock(positions[3].add(1, 0, 0), x);
        placeBlock(positions[4].add(1, 0, 0), y);
        placeBlock(positions[4].add(2, 0, 0), x);
        placeBlock(positions[5].add(2, 0, 0), y);
        placeBlock(positions[6].add(2, 0, 0), y);
        placeBlock(positions[7].add(2, 0, 0), y);
        placeBlock(positions[8].add(2, 0, 0), y);

        //支干
        placeBlock(positions[7].add(1, 0, 0), x);
        placeBlock(positions[7].add(3, 0, 0), x);
        placeBlock(positions[7].add(2, 0, -1), z);
        placeBlock(positions[7].add(2, 0, 1), z);
        placeBlock(positions[7].add(0, 0, 0), x);
        placeBlock(positions[7].add(4, 0, 0), x);
        placeBlock(positions[7].add(2, 0, -2), z);
        placeBlock(positions[7].add(2, 0, 2), z);

        //树叶主体
        BlockPos pos = positions[6].add(0, 0, -2);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 5; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }

        //精修
        placeBlock(positions[6].add(4, 0, -2), air);
        placeBlock(positions[9].add(0, 0, 2), air);

        //下垂
        placeBlock(positions[5].add(0, 0, 1), leaves);
        placeBlock(positions[4].add(0, 0, 1), leaves);
        placeBlock(positions[5].add(4, 0, -1), leaves);
        placeBlock(positions[4].add(4, 0, -1), leaves);
        placeBlock(positions[5].add(1, 0, -1), leaves);
        placeBlock(positions[5].add(3, 0, 2), leaves);

        //西侧
        placeBlock(positions[7].add(-1, 0, -2), leaves);
        placeBlock(positions[7].add(-1, 0, 0), leaves);
        placeBlock(positions[7].add(-1, 0, 2), leaves);
        placeBlock(positions[8].add(-1, 0, -2), leaves);
        placeBlock(positions[8].add(-1, 0, -1), leaves);
        placeBlock(positions[8].add(-1, 0, 0), leaves);
        placeBlock(positions[8].add(-1, 0, 1), leaves);

        //北侧
        placeBlock(positions[7].add(0, 0, -3), leaves);
        placeBlock(positions[7].add(1, 0, -3), leaves);
        placeBlock(positions[7].add(2, 0, -3), leaves);
        placeBlock(positions[7].add(4, 0, -3), leaves);
        placeBlock(positions[6].add(4, 0, -3), leaves);//上一个的下垂
        placeBlock(positions[8].add(1, 0, -3), leaves);
        placeBlock(positions[8].add(2, 0, -3), leaves);
        placeBlock(positions[8].add(3, 0, -3), leaves);
        placeBlock(positions[8].add(4, 0, -3), leaves);

        //东侧
        placeBlock(positions[7].add(5, 0, -2), leaves);
        placeBlock(positions[7].add(5, 0, -1), leaves);
        placeBlock(positions[7].add(5, 0, 0), leaves);
        placeBlock(positions[7].add(5, 0, 1), leaves);
        placeBlock(positions[8].add(5, 0, -1), leaves);
        placeBlock(positions[8].add(5, 0, 0), leaves);
        placeBlock(positions[8].add(5, 0, 1), leaves);
        placeBlock(positions[8].add(5, 0, 2), leaves);

        //南侧
        placeBlock(positions[7].add(0, 0, 3), leaves);
        placeBlock(positions[7].add(1, 0, 3), leaves);
        placeBlock(positions[7].add(2, 0, 3), leaves);
        placeBlock(positions[7].add(3, 0, 3), leaves);
        placeBlock(positions[8].add(0, 0, 3), leaves);
        placeBlock(positions[8].add(1, 0, 3), leaves);
        placeBlock(positions[8].add(2, 0, 3), leaves);
        placeBlock(positions[8].add(3, 0, 3), leaves);
        placeBlock(positions[8].add(4, 0, 3), leaves);

        //顶部
        placeBlock(positions[10].add(2, 0, -1), leaves);
        placeBlock(positions[10].add(1, 0, 0), leaves);
        placeBlock(positions[10].add(2, 0, 0), leaves);
        placeBlock(positions[10].add(3, 0, 0), leaves);
        placeBlock(positions[10].add(1, 0, 1), leaves);
        placeBlock(positions[10].add(2, 0, 1), leaves);
        placeBlock(positions[10].add(3, 0, 1), leaves);

        //生成蜂巢
        placebeeNest(positions[5].add(2, 0, -1));
    }

//樱花树2负x
    private static void 樱花树2负x(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        placeBlock(positions[3].add(-1, 0, 0), x);
        placeBlock(positions[4].add(-1, 0, 0), y);
        placeBlock(positions[4].add(-2, 0, 0), x);
        placeBlock(positions[5].add(-2, 0, 0), y);
        placeBlock(positions[6].add(-2, 0, 0), y);
        placeBlock(positions[7].add(-2, 0, 0), y);
        placeBlock(positions[8].add(-2, 0, 0), y);

        //支干
        placeBlock(positions[7].add(-1, 0, 0), x);
        placeBlock(positions[7].add(-3, 0, 0), x);
        placeBlock(positions[7].add(-2, 0, 1), z);
        placeBlock(positions[7].add(-2, 0, -1), z);
        placeBlock(positions[7].add(0, 0, 0), x);
        placeBlock(positions[7].add(-4, 0, 0), x);
        placeBlock(positions[7].add(-2, 0, 2), z);
        placeBlock(positions[7].add(-2, 0, -2), z);

        //树叶主体
        BlockPos pos = positions[6].add(-4, 0, -2);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 5; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }

        //精修
        placeBlock(positions[6].add(-4, 0, 2), air);
        placeBlock(positions[9].add(0, 0, -2), air);

        //下垂
        placeBlock(positions[5].add(0, 0, -1), leaves);
        placeBlock(positions[4].add(0, 0, -1), leaves);
        placeBlock(positions[5].add(-4, 0, 1), leaves);
        placeBlock(positions[4].add(-4, 0, 1), leaves);
        placeBlock(positions[5].add(-1, 0, 1), leaves);
        placeBlock(positions[5].add(-3, 0, -2), leaves);

        //西侧
        placeBlock(positions[7].add(1, 0, 2), leaves);
        placeBlock(positions[7].add(1, 0, 0), leaves);
        placeBlock(positions[7].add(1, 0, -2), leaves);
        placeBlock(positions[8].add(1, 0, 2), leaves);
        placeBlock(positions[8].add(1, 0, 1), leaves);
        placeBlock(positions[8].add(1, 0, 0), leaves);
        placeBlock(positions[8].add(1, 0, -1), leaves);

        //北侧
        placeBlock(positions[7].add(0, 0, 3), leaves);
        placeBlock(positions[7].add(-1, 0, 3), leaves);
        placeBlock(positions[7].add(-2, 0, 3), leaves);
        placeBlock(positions[7].add(-4, 0, 3), leaves);
        placeBlock(positions[6].add(-4, 0, 3), leaves);//上一个的下垂
        placeBlock(positions[8].add(-1, 0, 3), leaves);
        placeBlock(positions[8].add(-2, 0, 3), leaves);
        placeBlock(positions[8].add(-3, 0, 3), leaves);
        placeBlock(positions[8].add(-4, 0, 3), leaves);

        //东侧
        placeBlock(positions[7].add(-5, 0, 2), leaves);
        placeBlock(positions[7].add(-5, 0, 1), leaves);
        placeBlock(positions[7].add(-5, 0, 0), leaves);
        placeBlock(positions[7].add(-5, 0, -1), leaves);
        placeBlock(positions[8].add(-5, 0, 1), leaves);
        placeBlock(positions[8].add(-5, 0, 0), leaves);
        placeBlock(positions[8].add(-5, 0, -1), leaves);
        placeBlock(positions[8].add(-5, 0, -2), leaves);

        //南侧
        placeBlock(positions[7].add(0, 0, -3), leaves);
        placeBlock(positions[7].add(-1, 0, -3), leaves);
        placeBlock(positions[7].add(-2, 0, -3), leaves);
        placeBlock(positions[7].add(-3, 0, -3), leaves);
        placeBlock(positions[8].add(0, 0, -3), leaves);
        placeBlock(positions[8].add(-1, 0, -3), leaves);
        placeBlock(positions[8].add(-2, 0, -3), leaves);
        placeBlock(positions[8].add(-3, 0, -3), leaves);
        placeBlock(positions[8].add(-4, 0, -3), leaves);

        //顶部
        placeBlock(positions[10].add(-2, 0, 1), leaves);
        placeBlock(positions[10].add(-1, 0, 0), leaves);
        placeBlock(positions[10].add(-2, 0, 0), leaves);
        placeBlock(positions[10].add(-3, 0, 0), leaves);
        placeBlock(positions[10].add(-1, 0, -1), leaves);
        placeBlock(positions[10].add(-2, 0, -1), leaves);
        placeBlock(positions[10].add(-3, 0, -1), leaves);

        //生成蜂巢
        placebeeNest(positions[5].add(-2, 0, 1));
    }

//樱花树2正z
    private static void 樱花树2正z(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        placeBlock(positions[3].add(0, 0, 1), z);
        placeBlock(positions[4].add(0, 0, 1), y);
        placeBlock(positions[4].add(0, 0, 2), z);
        placeBlock(positions[5].add(0, 0, 2), y);
        placeBlock(positions[6].add(0, 0, 2), y);
        placeBlock(positions[7].add(0, 0, 2), y);
        placeBlock(positions[8].add(0, 0, 2), y);

        //支干
        placeBlock(positions[7].add(0, 0, 1), z);
        placeBlock(positions[7].add(0, 0, 3), z);
        placeBlock(positions[7].add(-1, 0, 2), x);
        placeBlock(positions[7].add(1, 0, 2), x);
        placeBlock(positions[7].add(0, 0, 0), z);
        placeBlock(positions[7].add(0, 0, 4), z);
        placeBlock(positions[7].add(-2, 0, 2), x);
        placeBlock(positions[7].add(2, 0, 2), x);

        //树叶主体
        BlockPos pos = positions[6].add(-2, 0, 0);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 5; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }

        //精修
        placeBlock(positions[6].add(-2, 0, 4), air);
        placeBlock(positions[9].add(2, 0, 0), air);

        //下垂
        placeBlock(positions[5].add(1, 0, 0), leaves);
        placeBlock(positions[4].add(1, 0, 0), leaves);
        placeBlock(positions[5].add(-1, 0, 4), leaves);
        placeBlock(positions[4].add(-1, 0, 4), leaves);
        placeBlock(positions[5].add(-1, 0, 1), leaves);
        placeBlock(positions[5].add(2, 0, 3), leaves);

        //西侧
        placeBlock(positions[7].add(-2, 0, -1), leaves);
        placeBlock(positions[7].add(0, 0, -1), leaves);
        placeBlock(positions[7].add(2, 0, -1), leaves);
        placeBlock(positions[8].add(-2, 0, -1), leaves);
        placeBlock(positions[8].add(-1, 0, -1), leaves);
        placeBlock(positions[8].add(0, 0, -1), leaves);
        placeBlock(positions[8].add(1, 0, -1), leaves);

        //北侧
        placeBlock(positions[7].add(-3, 0, 0), leaves);
        placeBlock(positions[7].add(-3, 0, 1), leaves);
        placeBlock(positions[7].add(-3, 0, 2), leaves);
        placeBlock(positions[7].add(-3, 0, 4), leaves);
        placeBlock(positions[6].add(-3, 0, 4), leaves);//上一个的下垂
        placeBlock(positions[8].add(-3, 0, 1), leaves);
        placeBlock(positions[8].add(-3, 0, 2), leaves);
        placeBlock(positions[8].add(-3, 0, 3), leaves);
        placeBlock(positions[8].add(-3, 0, 4), leaves);

        //东侧
        placeBlock(positions[7].add(-2, 0, 5), leaves);
        placeBlock(positions[7].add(-1, 0, 5), leaves);
        placeBlock(positions[7].add(0, 0, 5), leaves);
        placeBlock(positions[7].add(1, 0, 5), leaves);
        placeBlock(positions[8].add(-1, 0, 5), leaves);
        placeBlock(positions[8].add(0, 0, 5), leaves);
        placeBlock(positions[8].add(1, 0, 5), leaves);
        placeBlock(positions[8].add(2, 0, 5), leaves);

        //南侧
        placeBlock(positions[7].add(3, 0, 0), leaves);
        placeBlock(positions[7].add(3, 0, 1), leaves);
        placeBlock(positions[7].add(3, 0, 2), leaves);
        placeBlock(positions[7].add(3, 0, 3), leaves);
        placeBlock(positions[8].add(3, 0, 0), leaves);
        placeBlock(positions[8].add(3, 0, 1), leaves);
        placeBlock(positions[8].add(3, 0, 2), leaves);
        placeBlock(positions[8].add(3, 0, 3), leaves);
        placeBlock(positions[8].add(3, 0, 4), leaves);

        //顶部
        placeBlock(positions[10].add(-1, 0, 2), leaves);
        placeBlock(positions[10].add(0, 0, 1), leaves);
        placeBlock(positions[10].add(0, 0, 2), leaves);
        placeBlock(positions[10].add(0, 0, 3), leaves);
        placeBlock(positions[10].add(1, 0, 1), leaves);
        placeBlock(positions[10].add(1, 0, 2), leaves);
        placeBlock(positions[10].add(1, 0, 3), leaves);

        //生成蜂巢
        placebeeNest(positions[5].add(-1, 0, 2));
    }

//樱花树2负z
    private static void 樱花树2负z(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        placeBlock(positions[3].add(0, 0, -1), z);
        placeBlock(positions[4].add(0, 0, -1), y);
        placeBlock(positions[4].add(0, 0, -2), z);
        placeBlock(positions[5].add(0, 0, -2), y);
        placeBlock(positions[6].add(0, 0, -2), y);
        placeBlock(positions[7].add(0, 0, -2), y);
        placeBlock(positions[8].add(0, 0, -2), y);

        //支干
        placeBlock(positions[7].add(0, 0, -1), z);
        placeBlock(positions[7].add(0, 0, -3), z);
        placeBlock(positions[7].add(1, 0, -2), x);
        placeBlock(positions[7].add(-1, 0, -2), x);
        placeBlock(positions[7].add(0, 0, 0), z);
        placeBlock(positions[7].add(0, 0, -4), z);
        placeBlock(positions[7].add(2, 0, -2), x);
        placeBlock(positions[7].add(-2, 0, -2), x);

        //树叶主体
        BlockPos pos = positions[6].add(-2, 0, -4);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 5; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }

        //精修
        placeBlock(positions[6].add(2, 0, -4), air);
        placeBlock(positions[9].add(-2, 0, 0), air);

        //下垂
        placeBlock(positions[5].add(-1, 0, 0), leaves);
        placeBlock(positions[4].add(-1, 0, 0), leaves);
        placeBlock(positions[5].add(1, 0, -4), leaves);
        placeBlock(positions[4].add(1, 0, -4), leaves);
        placeBlock(positions[5].add(1, 0, -1), leaves);
        placeBlock(positions[5].add(-2, 0, -3), leaves);

        //西侧
        placeBlock(positions[7].add(2, 0, 1), leaves);
        placeBlock(positions[7].add(0, 0, 1), leaves);
        placeBlock(positions[7].add(-2, 0, 1), leaves);
        placeBlock(positions[8].add(2, 0, 1), leaves);
        placeBlock(positions[8].add(1, 0, 1), leaves);
        placeBlock(positions[8].add(0, 0, 1), leaves);
        placeBlock(positions[8].add(-1, 0, 1), leaves);

        //北侧
        placeBlock(positions[7].add(3, 0, 0), leaves);
        placeBlock(positions[7].add(3, 0, -1), leaves);
        placeBlock(positions[7].add(3, 0, -2), leaves);
        placeBlock(positions[7].add(3, 0, -4), leaves);
        placeBlock(positions[6].add(3, 0, -4), leaves);//上一个的下垂
        placeBlock(positions[8].add(3, 0, -1), leaves);
        placeBlock(positions[8].add(3, 0, -2), leaves);
        placeBlock(positions[8].add(3, 0, -3), leaves);
        placeBlock(positions[8].add(3, 0, -4), leaves);

        //东侧
        placeBlock(positions[7].add(2, 0, -5), leaves);
        placeBlock(positions[7].add(1, 0, -5), leaves);
        placeBlock(positions[7].add(0, 0, -5), leaves);
        placeBlock(positions[7].add(-1, 0, -5), leaves);
        placeBlock(positions[8].add(1, 0, -5), leaves);
        placeBlock(positions[8].add(0, 0, -5), leaves);
        placeBlock(positions[8].add(-1, 0, -5), leaves);
        placeBlock(positions[8].add(-2, 0, -5), leaves);

        //南侧
        placeBlock(positions[7].add(-3, 0, 0), leaves);
        placeBlock(positions[7].add(-3, 0, -1), leaves);
        placeBlock(positions[7].add(-3, 0, -2), leaves);
        placeBlock(positions[7].add(-3, 0, -3), leaves);
        placeBlock(positions[8].add(-3, 0, 0), leaves);
        placeBlock(positions[8].add(-3, 0, -1), leaves);
        placeBlock(positions[8].add(-3, 0, -2), leaves);
        placeBlock(positions[8].add(-3, 0, -3), leaves);
        placeBlock(positions[8].add(-3, 0, -4), leaves);

        //顶部
        placeBlock(positions[10].add(1, 0, -2), leaves);
        placeBlock(positions[10].add(0, 0, -1), leaves);
        placeBlock(positions[10].add(0, 0, -2), leaves);
        placeBlock(positions[10].add(0, 0, -3), leaves);
        placeBlock(positions[10].add(-1, 0, -1), leaves);
        placeBlock(positions[10].add(-1, 0, -2), leaves);
        placeBlock(positions[10].add(-1, 0, -3), leaves);

        //生成蜂巢
        placebeeNest(positions[5].add(1, 0, -2));
    }

//樱花树3正x
    private static void 樱花树3正x(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        placeBlock(positions[4], y);
        //矮支干
        placeBlock(positions[4].add(-1, 0, 0), x);
        placeBlock(positions[4].add(-2, 0, 0), x);
        placeBlock(positions[4].add(-3, 0, 0), x);
        placeBlock(positions[5].add(-3, 0, 0), y);
        placeBlock(positions[6].add(-3, 0, 0), y);
        placeBlock(positions[7].add(-3, 0, 0), y);
        //矮支干延伸
        placeBlock(positions[7].add(-2, 0, 0), x);
        placeBlock(positions[7].add(-1, 0, 0), x);
        placeBlock(positions[7].add(-4, 0, 0), x);
        placeBlock(positions[7].add(-5, 0, 0), x);
        placeBlock(positions[7].add(-3, 0, 1), z);
        placeBlock(positions[7].add(-3, 0, 2), z);
        placeBlock(positions[7].add(-3, 0, -1), z);
        placeBlock(positions[7].add(-3, 0, -2), z);
        //高支干
        placeBlock(positions[3].add(1, 0, 0), x);
        placeBlock(positions[3].add(2, 0, 0), x);
        placeBlock(positions[3].add(3, 0, 0), x);
        placeBlock(positions[4].add(3, 0, 0), y);
        placeBlock(positions[4].add(4, 0, 0), x);
        placeBlock(positions[5].add(4, 0, 0), y);
        placeBlock(positions[6].add(4, 0, 0), y);
        placeBlock(positions[7].add(4, 0, 0), y);
        placeBlock(positions[8].add(4, 0, 0), y);
        //高支干延伸
        placeBlock(positions[8].add(3, 0, 0), x);
        placeBlock(positions[8].add(2, 0, 0), x);
        placeBlock(positions[8].add(5, 0, 0), x);
        placeBlock(positions[8].add(6, 0, 0), x);
        placeBlock(positions[8].add(4, 0, 1), z);
        placeBlock(positions[8].add(4, 0, 2), z);
        placeBlock(positions[8].add(4, 0, -1), z);
        placeBlock(positions[8].add(4, 0, -2), z);

        //高树叶主体
        BlockPos pos = positions[6].add(2, 0, -2);
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[9].add(2, 0, 2), air);
        placeBlock(positions[9].add(6, 0, 2), air);
        //下垂
        placeBlock(positions[5].add(4, 0, -2), leaves);
        placeBlock(positions[5].add(5, 0, 2), leaves);
        placeBlock(positions[5].add(6, 0, 1), leaves);
        placeBlock(positions[5].add(3, 0, 1), leaves);
        placeBlock(positions[4].add(3, 0, 1), leaves);
        placeBlock(positions[5].add(5, 0, -1), leaves);
        placeBlock(positions[4].add(5, 0, -1), leaves);
        //精修南方
        placeBlock(positions[7].add(2, 0, 3), leaves);
        placeBlock(positions[7].add(4, 0, 3), leaves);
        placeBlock(positions[7].add(5, 0, 3), leaves);
        placeBlock(positions[8].add(2, 0, 3), leaves);
        placeBlock(positions[8].add(3, 0, 3), leaves);
        placeBlock(positions[8].add(4, 0, 3), leaves);
        placeBlock(positions[8].add(5, 0, 3), leaves);
        placeBlock(positions[8].add(6, 0, 3), leaves);
        //精修东方
        placeBlock(positions[7].add(7, 0, -1), leaves);
        placeBlock(positions[7].add(7, 0, 1), leaves);
        placeBlock(positions[8].add(7, 0, -2), leaves);
        placeBlock(positions[8].add(7, 0, -1), leaves);
        placeBlock(positions[8].add(7, 0, 0), leaves);
        placeBlock(positions[8].add(7, 0, 1), leaves);
        placeBlock(positions[8].add(7, 0, 2), leaves);
        //精修北方
        placeBlock(positions[6].add(3, 0, -3), leaves);
        placeBlock(positions[7].add(3, 0, -3), leaves);
        placeBlock(positions[7].add(4, 0, -3), leaves);
        placeBlock(positions[7].add(6, 0, -3), leaves);
        placeBlock(positions[8].add(2, 0, -3), leaves);
        placeBlock(positions[8].add(3, 0, -3), leaves);
        placeBlock(positions[8].add(4, 0, -3), leaves);
        placeBlock(positions[8].add(5, 0, -3), leaves);
        placeBlock(positions[8].add(6, 0, -3), leaves);
        //精修西方
        placeBlock(positions[7].add(1, 0, -2), leaves);
        placeBlock(positions[7].add(1, 0, 0), leaves);
        placeBlock(positions[8].add(1, 0, -2), leaves);
        placeBlock(positions[8].add(1, 0, -1), leaves);
        placeBlock(positions[8].add(1, 0, 0), leaves);
        placeBlock(positions[8].add(1, 0, 1), leaves);
        //顶
        placeBlock(positions[10].add(3, 0, -1), leaves);
        placeBlock(positions[10].add(4, 0, -1), leaves);
        placeBlock(positions[10].add(5, 0, -1), leaves);
        placeBlock(positions[10].add(3, 0, 0), leaves);
        placeBlock(positions[10].add(4, 0, 0), leaves);
        placeBlock(positions[10].add(5, 0, 0), leaves);
        placeBlock(positions[10].add(4, 0, 1), leaves);

        //矮树叶主体
        pos = positions[5].add(-5, 0, -2);
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[5].add(-1, 0, 2), air);
        placeBlock(positions[5].add(-1, 0, -2), air);
        //下垂
        placeBlock(positions[4].add(-2, 0, 2), leaves);
        placeBlock(positions[4].add(-4, 0, 2), leaves);
        placeBlock(positions[4].add(-5, 0, 1), leaves);
        placeBlock(positions[4].add(-3, 0, -2), leaves);
        placeBlock(positions[4].add(-1, 0, -1), leaves);
        placeBlock(positions[3].add(-1, 0, -1), leaves);
        //精修南方
        placeBlock(positions[5].add(-2, 0, 3), leaves);
        placeBlock(positions[6].add(-5, 0, 3), leaves);
        placeBlock(positions[6].add(-4, 0, 3), leaves);
        placeBlock(positions[6].add(-3, 0, 3), leaves);
        placeBlock(positions[6].add(-2, 0, 3), leaves);
        placeBlock(positions[7].add(-5, 0, 3), leaves);
        placeBlock(positions[7].add(-4, 0, 3), leaves);
        placeBlock(positions[7].add(-3, 0, 3), leaves);
        placeBlock(positions[7].add(-2, 0, 3), leaves);
        //精修东方
        placeBlock(positions[5].add(0, 0, 1), leaves);
        placeBlock(positions[6].add(0, 0, 1), leaves);
        placeBlock(positions[6].add(0, 0, 0), leaves);
        placeBlock(positions[6].add(0, 0, -1), leaves);
        placeBlock(positions[6].add(0, 0, -2), leaves);
        placeBlock(positions[7].add(0, 0, 2), leaves);
        placeBlock(positions[7].add(0, 0, 1), leaves);
        placeBlock(positions[7].add(0, 0, 0), leaves);
        placeBlock(positions[7].add(0, 0, -1), leaves);
        placeBlock(positions[7].add(0, 0, -2), leaves);
        //精修北方
        placeBlock(positions[6].add(-2, 0, -3), leaves);
        placeBlock(positions[6].add(-4, 0, -3), leaves);
        placeBlock(positions[6].add(-5, 0, -3), leaves);
        placeBlock(positions[7].add(-1, 0, -3), leaves);
        placeBlock(positions[7].add(-2, 0, -3), leaves);
        placeBlock(positions[7].add(-3, 0, -3), leaves);
        placeBlock(positions[7].add(-4, 0, -3), leaves);
        //精修西方
        placeBlock(positions[6].add(-6, 0, -1), leaves);
        placeBlock(positions[6].add(-6, 0, 0), leaves);
        placeBlock(positions[6].add(-6, 0, 1), leaves);
        placeBlock(positions[7].add(-6, 0, -2), leaves);
        placeBlock(positions[7].add(-6, 0, -1), leaves);
        placeBlock(positions[7].add(-6, 0, 0), leaves);
        placeBlock(positions[7].add(-6, 0, 1), leaves);
        placeBlock(positions[7].add(-6, 0, 2), leaves);
        //顶
        placeBlock(positions[9].add(-4, 0, -1), leaves);
        placeBlock(positions[9].add(-3, 0, -1), leaves);
        placeBlock(positions[9].add(-2, 0, -1), leaves);
        placeBlock(positions[9].add(-4, 0, 0), leaves);
        placeBlock(positions[9].add(-3, 0, 0), leaves);
        placeBlock(positions[9].add(-2, 0, 0), leaves);
        placeBlock(positions[9].add(-4, 0, 1), leaves);
        placeBlock(positions[9].add(-3, 0, 1), leaves);

        //生成蜂巢
        placebeeNest(positions[4].add(1, 0, 0));
    }

//樱花树3负x
    private static void 樱花树3负x(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        placeBlock(positions[4], y);
        //矮支干
        placeBlock(positions[4].add(1, 0, 0), x);
        placeBlock(positions[4].add(2, 0, 0), x);
        placeBlock(positions[4].add(3, 0, 0), x);
        placeBlock(positions[5].add(3, 0, 0), y);
        placeBlock(positions[6].add(3, 0, 0), y);
        placeBlock(positions[7].add(3, 0, 0), y);
        //矮支干延伸
        placeBlock(positions[7].add(2, 0, 0), x);
        placeBlock(positions[7].add(1, 0, 0), x);
        placeBlock(positions[7].add(4, 0, 0), x);
        placeBlock(positions[7].add(5, 0, 0), x);
        placeBlock(positions[7].add(3, 0, -1), z);
        placeBlock(positions[7].add(3, 0, -2), z);
        placeBlock(positions[7].add(3, 0, 1), z);
        placeBlock(positions[7].add(3, 0, 2), z);
        //高支干
        placeBlock(positions[3].add(-1, 0, 0), x);
        placeBlock(positions[3].add(-2, 0, 0), x);
        placeBlock(positions[3].add(-3, 0, 0), x);
        placeBlock(positions[4].add(-3, 0, 0), y);
        placeBlock(positions[4].add(-4, 0, 0), x);
        placeBlock(positions[5].add(-4, 0, 0), y);
        placeBlock(positions[6].add(-4, 0, 0), y);
        placeBlock(positions[7].add(-4, 0, 0), y);
        placeBlock(positions[8].add(-4, 0, 0), y);
        //高支干延伸
        placeBlock(positions[8].add(-3, 0, 0), x);
        placeBlock(positions[8].add(-2, 0, 0), x);
        placeBlock(positions[8].add(-5, 0, 0), x);
        placeBlock(positions[8].add(-6, 0, 0), x);
        placeBlock(positions[8].add(-4, 0, -1), z);
        placeBlock(positions[8].add(-4, 0, -2), z);
        placeBlock(positions[8].add(-4, 0, 1), z);
        placeBlock(positions[8].add(-4, 0, 2), z);

        //高树叶主体
        BlockPos pos = positions[6].add(-6, 0, -2);
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[9].add(-2, 0, -2), air);
        placeBlock(positions[9].add(-6, 0, -2), air);
        //下垂
        placeBlock(positions[5].add(-4, 0, 2), leaves);
        placeBlock(positions[5].add(-5, 0, -2), leaves);
        placeBlock(positions[5].add(-6, 0, -1), leaves);
        placeBlock(positions[5].add(-3, 0, -1), leaves);
        placeBlock(positions[4].add(-3, 0, -1), leaves);
        placeBlock(positions[5].add(-5, 0, 1), leaves);
        placeBlock(positions[4].add(-5, 0, 1), leaves);
        //精修南方
        placeBlock(positions[7].add(-2, 0, -3), leaves);
        placeBlock(positions[7].add(-4, 0, -3), leaves);
        placeBlock(positions[7].add(-5, 0, -3), leaves);
        placeBlock(positions[8].add(-2, 0, -3), leaves);
        placeBlock(positions[8].add(-3, 0, -3), leaves);
        placeBlock(positions[8].add(-4, 0, -3), leaves);
        placeBlock(positions[8].add(-5, 0, -3), leaves);
        placeBlock(positions[8].add(-6, 0, -3), leaves);
        //精修东方
        placeBlock(positions[7].add(-7, 0, 1), leaves);
        placeBlock(positions[7].add(-7, 0, -1), leaves);
        placeBlock(positions[8].add(-7, 0, 2), leaves);
        placeBlock(positions[8].add(-7, 0, 1), leaves);
        placeBlock(positions[8].add(-7, 0, 0), leaves);
        placeBlock(positions[8].add(-7, 0, -1), leaves);
        placeBlock(positions[8].add(-7, 0, -2), leaves);
        //精修北方
        placeBlock(positions[6].add(-3, 0, 3), leaves);
        placeBlock(positions[7].add(-3, 0, 3), leaves);
        placeBlock(positions[7].add(-4, 0, 3), leaves);
        placeBlock(positions[7].add(-6, 0, 3), leaves);
        placeBlock(positions[8].add(-2, 0, 3), leaves);
        placeBlock(positions[8].add(-3, 0, 3), leaves);
        placeBlock(positions[8].add(-4, 0, 3), leaves);
        placeBlock(positions[8].add(-5, 0, 3), leaves);
        placeBlock(positions[8].add(-6, 0, 3), leaves);
        //精修西方
        placeBlock(positions[7].add(-1, 0, 2), leaves);
        placeBlock(positions[7].add(-1, 0, 0), leaves);
        placeBlock(positions[8].add(-1, 0, 2), leaves);
        placeBlock(positions[8].add(-1, 0, 1), leaves);
        placeBlock(positions[8].add(-1, 0, 0), leaves);
        placeBlock(positions[8].add(-1, 0, -1), leaves);
        //顶
        placeBlock(positions[10].add(-3, 0, 1), leaves);
        placeBlock(positions[10].add(-4, 0, 1), leaves);
        placeBlock(positions[10].add(-5, 0, 1), leaves);
        placeBlock(positions[10].add(-3, 0, 0), leaves);
        placeBlock(positions[10].add(-4, 0, 0), leaves);
        placeBlock(positions[10].add(-5, 0, 0), leaves);
        placeBlock(positions[10].add(-4, 0, -1), leaves);

        //矮树叶主体
        pos = positions[5].add(1, 0, -2);
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[5].add(1, 0, -2), air);
        placeBlock(positions[5].add(1, 0, 2), air);
        //下垂
        placeBlock(positions[4].add(2, 0, -2), leaves);
        placeBlock(positions[4].add(4, 0, -2), leaves);
        placeBlock(positions[4].add(5, 0, -1), leaves);
        placeBlock(positions[4].add(3, 0, 2), leaves);
        placeBlock(positions[4].add(1, 0, 1), leaves);
        placeBlock(positions[3].add(1, 0, 1), leaves);
        //精修南方
        placeBlock(positions[5].add(2, 0, -3), leaves);
        placeBlock(positions[6].add(5, 0, -3), leaves);
        placeBlock(positions[6].add(4, 0, -3), leaves);
        placeBlock(positions[6].add(3, 0, -3), leaves);
        placeBlock(positions[6].add(2, 0, -3), leaves);
        placeBlock(positions[7].add(5, 0, -3), leaves);
        placeBlock(positions[7].add(4, 0, -3), leaves);
        placeBlock(positions[7].add(3, 0, -3), leaves);
        placeBlock(positions[7].add(2, 0, -3), leaves);
        //精修东方
        placeBlock(positions[5].add(0, 0, -1), leaves);
        placeBlock(positions[6].add(0, 0, -1), leaves);
        placeBlock(positions[6].add(0, 0, 0), leaves);
        placeBlock(positions[6].add(0, 0, 1), leaves);
        placeBlock(positions[6].add(0, 0, 2), leaves);
        placeBlock(positions[7].add(0, 0, -2), leaves);
        placeBlock(positions[7].add(0, 0, -1), leaves);
        placeBlock(positions[7].add(0, 0, 0), leaves);
        placeBlock(positions[7].add(0, 0, 1), leaves);
        placeBlock(positions[7].add(0, 0, 2), leaves);
        //精修北方
        placeBlock(positions[6].add(2, 0, 3), leaves);
        placeBlock(positions[6].add(4, 0, 3), leaves);
        placeBlock(positions[6].add(5, 0, 3), leaves);
        placeBlock(positions[7].add(1, 0, 3), leaves);
        placeBlock(positions[7].add(2, 0, 3), leaves);
        placeBlock(positions[7].add(3, 0, 3), leaves);
        placeBlock(positions[7].add(4, 0, 3), leaves);
        //精修西方
        placeBlock(positions[6].add(6, 0, 1), leaves);
        placeBlock(positions[6].add(6, 0, 0), leaves);
        placeBlock(positions[6].add(6, 0, -1), leaves);
        placeBlock(positions[7].add(6, 0, 2), leaves);
        placeBlock(positions[7].add(6, 0, 1), leaves);
        placeBlock(positions[7].add(6, 0, 0), leaves);
        placeBlock(positions[7].add(6, 0, -1), leaves);
        placeBlock(positions[7].add(6, 0, -2), leaves);
        //顶
        placeBlock(positions[9].add(4, 0, 1), leaves);
        placeBlock(positions[9].add(3, 0, 1), leaves);
        placeBlock(positions[9].add(2, 0, 1), leaves);
        placeBlock(positions[9].add(4, 0, 0), leaves);
        placeBlock(positions[9].add(3, 0, 0), leaves);
        placeBlock(positions[9].add(2, 0, 0), leaves);
        placeBlock(positions[9].add(4, 0, -1), leaves);
        placeBlock(positions[9].add(3, 0, -1), leaves);
        
        //生成蜂巢
        placebeeNest(positions[4].add(-1, 0, 0));
    }

//樱花树3正z
    private static void 樱花树3正z(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        placeBlock(positions[4], y);
        //矮支干
        placeBlock(positions[4].add(0, 0, -1), z);
        placeBlock(positions[4].add(0, 0, -2), z);
        placeBlock(positions[4].add(0, 0, -3), z);
        placeBlock(positions[5].add(0, 0, -3), y);
        placeBlock(positions[6].add(0, 0, -3), y);
        placeBlock(positions[7].add(0, 0, -3), y);
        //矮支干延伸
        placeBlock(positions[7].add(0, 0, -2), z);
        placeBlock(positions[7].add(0, 0, -1), z);
        placeBlock(positions[7].add(0, 0, -4), z);
        placeBlock(positions[7].add(0, 0, -5), z);
        placeBlock(positions[7].add(1, 0, -3), x);
        placeBlock(positions[7].add(2, 0, -3), x);
        placeBlock(positions[7].add(-1, 0, -3), x);
        placeBlock(positions[7].add(-2, 0, -3), x);
        //高支干
        placeBlock(positions[3].add(0, 0, 1), z);
        placeBlock(positions[3].add(0, 0, 2), z);
        placeBlock(positions[3].add(0, 0, 3), z);
        placeBlock(positions[4].add(0, 0, 3), y);
        placeBlock(positions[4].add(0, 0, 4), z);
        placeBlock(positions[5].add(0, 0, 4), y);
        placeBlock(positions[6].add(0, 0, 4), y);
        placeBlock(positions[7].add(0, 0, 4), y);
        placeBlock(positions[8].add(0, 0, 4), y);
        //高支干延伸
        placeBlock(positions[8].add(0, 0, 3), z);
        placeBlock(positions[8].add(0, 0, 2), z);
        placeBlock(positions[8].add(0, 0, 5), z);
        placeBlock(positions[8].add(0, 0, 6), z);
        placeBlock(positions[8].add(1, 0, 4), x);
        placeBlock(positions[8].add(2, 0, 4), x);
        placeBlock(positions[8].add(-1, 0, 4), x);
        placeBlock(positions[8].add(-2, 0, 4), x);

        //高树叶主体
        BlockPos pos = positions[6].add(-2, 0, 2);
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[9].add(2, 0, 2), air);
        placeBlock(positions[9].add(2, 0, 6), air);
        //下垂
        placeBlock(positions[5].add(-2, 0, 4), leaves);
        placeBlock(positions[5].add(2, 0, 5), leaves);
        placeBlock(positions[5].add(1, 0, 6), leaves);
        placeBlock(positions[5].add(1, 0, 3), leaves);
        placeBlock(positions[4].add(1, 0, 3), leaves);
        placeBlock(positions[5].add(-1, 0, 5), leaves);
        placeBlock(positions[4].add(-1, 0, 5), leaves);
        //精修南方
        placeBlock(positions[7].add(3, 0, 2), leaves);
        placeBlock(positions[7].add(3, 0, 4), leaves);
        placeBlock(positions[7].add(3, 0, 5), leaves);
        placeBlock(positions[8].add(3, 0, 2), leaves);
        placeBlock(positions[8].add(3, 0, 3), leaves);
        placeBlock(positions[8].add(3, 0, 4), leaves);
        placeBlock(positions[8].add(3, 0, 5), leaves);
        placeBlock(positions[8].add(3, 0, 6), leaves);
        //精修东方
        placeBlock(positions[7].add(-1, 0, 7), leaves);
        placeBlock(positions[7].add(1, 0, 7), leaves);
        placeBlock(positions[8].add(-2, 0, 7), leaves);
        placeBlock(positions[8].add(-1, 0, 7), leaves);
        placeBlock(positions[8].add(0, 0, 7), leaves);
        placeBlock(positions[8].add(1, 0, 7), leaves);
        placeBlock(positions[8].add(2, 0, 7), leaves);
        //精修北方
        placeBlock(positions[6].add(-3, 0, 3), leaves);
        placeBlock(positions[7].add(-3, 0, 3), leaves);
        placeBlock(positions[7].add(-3, 0, 4), leaves);
        placeBlock(positions[7].add(-3, 0, 6), leaves);
        placeBlock(positions[8].add(-3, 0, 2), leaves);
        placeBlock(positions[8].add(-3, 0, 3), leaves);
        placeBlock(positions[8].add(-3, 0, 4), leaves);
        placeBlock(positions[8].add(-3, 0, 5), leaves);
        placeBlock(positions[8].add(-3, 0, 6), leaves);
        //精修西方
        placeBlock(positions[7].add(-2, 0, 1), leaves);
        placeBlock(positions[7].add(0, 0, 1), leaves);
        placeBlock(positions[8].add(-2, 0, 1), leaves);
        placeBlock(positions[8].add(-1, 0, 1), leaves);
        placeBlock(positions[8].add(0, 0, 1), leaves);
        placeBlock(positions[8].add(1, 0, 1), leaves);
        //顶
        placeBlock(positions[10].add(-1, 0, 3), leaves);
        placeBlock(positions[10].add(-1, 0, 4), leaves);
        placeBlock(positions[10].add(-1, 0, 5), leaves);
        placeBlock(positions[10].add(0, 0, 3), leaves);
        placeBlock(positions[10].add(0, 0, 4), leaves);
        placeBlock(positions[10].add(0, 0, 5), leaves);
        placeBlock(positions[10].add(1, 0, 4), leaves);

        //矮树叶主体
        pos = positions[5].add(-2, 0, -5);
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[5].add(2, 0, -1), air);
        placeBlock(positions[5].add(-2, 0, -1), air);
        //下垂
        placeBlock(positions[4].add(2, 0, -2), leaves);
        placeBlock(positions[4].add(2, 0, -4), leaves);
        placeBlock(positions[4].add(1, 0, -5), leaves);
        placeBlock(positions[4].add(-2, 0, -3), leaves);
        placeBlock(positions[4].add(-1, 0, -1), leaves);
        placeBlock(positions[3].add(-1, 0, -1), leaves);
        //精修南方
        placeBlock(positions[5].add(3, 0, -2), leaves);
        placeBlock(positions[6].add(3, 0, -5), leaves);
        placeBlock(positions[6].add(3, 0, -4), leaves);
        placeBlock(positions[6].add(3, 0, -3), leaves);
        placeBlock(positions[6].add(3, 0, -2), leaves);
        placeBlock(positions[7].add(3, 0, -5), leaves);
        placeBlock(positions[7].add(3, 0, -4), leaves);
        placeBlock(positions[7].add(3, 0, -3), leaves);
        placeBlock(positions[7].add(3, 0, -2), leaves);
        //精修东方
        placeBlock(positions[5].add(1, 0, 0), leaves);
        placeBlock(positions[6].add(1, 0, 0), leaves);
        placeBlock(positions[6].add(0, 0, 0), leaves);
        placeBlock(positions[6].add(-1, 0, 0), leaves);
        placeBlock(positions[6].add(-2, 0, 0), leaves);
        placeBlock(positions[7].add(2, 0, 0), leaves);
        placeBlock(positions[7].add(1, 0, 0), leaves);
        placeBlock(positions[7].add(0, 0, 0), leaves);
        placeBlock(positions[7].add(-1, 0, 0), leaves);
        placeBlock(positions[7].add(-2, 0, 0), leaves);
        //精修北方
        placeBlock(positions[6].add(-3, 0, -2), leaves);
        placeBlock(positions[6].add(-3, 0, -4), leaves);
        placeBlock(positions[6].add(-3, 0, -5), leaves);
        placeBlock(positions[7].add(-3, 0, -1), leaves);
        placeBlock(positions[7].add(-3, 0, -2), leaves);
        placeBlock(positions[7].add(-3, 0, -3), leaves);
        placeBlock(positions[7].add(-3, 0, -4), leaves);
        //精修西方
        placeBlock(positions[6].add(-1, 0, -6), leaves);
        placeBlock(positions[6].add(0, 0, -6), leaves);
        placeBlock(positions[6].add(1, 0, -6), leaves);
        placeBlock(positions[7].add(-2, 0, -6), leaves);
        placeBlock(positions[7].add(-1, 0, -6), leaves);
        placeBlock(positions[7].add(0, 0, -6), leaves);
        placeBlock(positions[7].add(1, 0, -6), leaves);
        placeBlock(positions[7].add(2, 0, -6), leaves);
        //顶
        placeBlock(positions[9].add(-1, 0, -4), leaves);
        placeBlock(positions[9].add(-1, 0, -3), leaves);
        placeBlock(positions[9].add(-1, 0, -2), leaves);
        placeBlock(positions[9].add(0, 0, -4), leaves);
        placeBlock(positions[9].add(0, 0, -3), leaves);
        placeBlock(positions[9].add(0, 0, -2), leaves);
        placeBlock(positions[9].add(1, 0, -4), leaves);
        placeBlock(positions[9].add(1, 0, -3), leaves);

        //生成蜂巢
        placebeeNest(positions[4].add(0, 0, 1));
    }

//樱花树3负z
    private static void 樱花树3负z(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        placeBlock(positions[4], y);
        //矮支干
        placeBlock(positions[4].add(0, 0, 1), z);
        placeBlock(positions[4].add(0, 0, 2), z);
        placeBlock(positions[4].add(0, 0, 3), z);
        placeBlock(positions[5].add(0, 0, 3), y);
        placeBlock(positions[6].add(0, 0, 3), y);
        placeBlock(positions[7].add(0, 0, 3), y);
        //矮支干延伸
        placeBlock(positions[7].add(0, 0, 2), z);
        placeBlock(positions[7].add(0, 0, 1), z);
        placeBlock(positions[7].add(0, 0, 4), z);
        placeBlock(positions[7].add(0, 0, 5), z);
        placeBlock(positions[7].add(-1, 0, 3), x);
        placeBlock(positions[7].add(-2, 0, 3), x);
        placeBlock(positions[7].add(1, 0, 3), x);
        placeBlock(positions[7].add(2, 0, 3), x);
        //高支干
        placeBlock(positions[3].add(0, 0, -1), z);
        placeBlock(positions[3].add(0, 0, -2), z);
        placeBlock(positions[3].add(0, 0, -3), z);
        placeBlock(positions[4].add(0, 0, -3), y);
        placeBlock(positions[4].add(0, 0, -4), z);
        placeBlock(positions[5].add(0, 0, -4), y);
        placeBlock(positions[6].add(0, 0, -4), y);
        placeBlock(positions[7].add(0, 0, -4), y);
        placeBlock(positions[8].add(0, 0, -4), y);
        //高支干延伸
        placeBlock(positions[8].add(0, 0, -3), z);
        placeBlock(positions[8].add(0, 0, -2), z);
        placeBlock(positions[8].add(0, 0, -5), z);
        placeBlock(positions[8].add(0, 0, -6), z);
        placeBlock(positions[8].add(-1, 0, -4), x);
        placeBlock(positions[8].add(-2, 0, -4), x);
        placeBlock(positions[8].add(1, 0, -4), x);
        placeBlock(positions[8].add(2, 0, -4), x);

        //高树叶主体
        BlockPos pos = positions[6].add(-2, 0, -6);
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[9].add(-2, 0, -2), air);
        placeBlock(positions[9].add(-2, 0, -6), air);
        //下垂
        placeBlock(positions[5].add(2, 0, -4), leaves);
        placeBlock(positions[5].add(-2, 0, -5), leaves);
        placeBlock(positions[5].add(-1, 0, -6), leaves);
        placeBlock(positions[5].add(-1, 0, -3), leaves);
        placeBlock(positions[4].add(-1, 0, -3), leaves);
        placeBlock(positions[5].add(1, 0, -5), leaves);
        placeBlock(positions[4].add(1, 0, -5), leaves);
        //精修南方
        placeBlock(positions[7].add(-3, 0, -2), leaves);
        placeBlock(positions[7].add(-3, 0, -4), leaves);
        placeBlock(positions[7].add(-3, 0, -5), leaves);
        placeBlock(positions[8].add(-3, 0, -2), leaves);
        placeBlock(positions[8].add(-3, 0, -3), leaves);
        placeBlock(positions[8].add(-3, 0, -4), leaves);
        placeBlock(positions[8].add(-3, 0, -5), leaves);
        placeBlock(positions[8].add(-3, 0, -6), leaves);
        //精修东方
        placeBlock(positions[7].add(1, 0, -7), leaves);
        placeBlock(positions[7].add(-1, 0, -7), leaves);
        placeBlock(positions[8].add(2, 0, -7), leaves);
        placeBlock(positions[8].add(1, 0, -7), leaves);
        placeBlock(positions[8].add(0, 0, -7), leaves);
        placeBlock(positions[8].add(-1, 0, -7), leaves);
        placeBlock(positions[8].add(-2, 0, -7), leaves);
        //精修北方
        placeBlock(positions[6].add(3, 0, -3), leaves);
        placeBlock(positions[7].add(3, 0, -3), leaves);
        placeBlock(positions[7].add(3, 0, -4), leaves);
        placeBlock(positions[7].add(3, 0, -6), leaves);
        placeBlock(positions[8].add(3, 0, -2), leaves);
        placeBlock(positions[8].add(3, 0, -3), leaves);
        placeBlock(positions[8].add(3, 0, -4), leaves);
        placeBlock(positions[8].add(3, 0, -5), leaves);
        placeBlock(positions[8].add(3, 0, -6), leaves);
        //精修西方
        placeBlock(positions[7].add(2, 0, -1), leaves);
        placeBlock(positions[7].add(0, 0, -1), leaves);
        placeBlock(positions[8].add(2, 0, -1), leaves);
        placeBlock(positions[8].add(1, 0, -1), leaves);
        placeBlock(positions[8].add(0, 0, -1), leaves);
        placeBlock(positions[8].add(-1, 0, -1), leaves);
        //顶
        placeBlock(positions[10].add(1, 0, -3), leaves);
        placeBlock(positions[10].add(1, 0, -4), leaves);
        placeBlock(positions[10].add(1, 0, -5), leaves);
        placeBlock(positions[10].add(0, 0, -3), leaves);
        placeBlock(positions[10].add(0, 0, -4), leaves);
        placeBlock(positions[10].add(0, 0, -5), leaves);
        placeBlock(positions[10].add(-1, 0, -4), leaves);

        //矮树叶主体
        pos = positions[5].add(-2, 0, 1);
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[5].add(-2, 0, 1), air);
        placeBlock(positions[5].add(2, 0, 1), air);
        //下垂
        placeBlock(positions[4].add(-2, 0, 2), leaves);
        placeBlock(positions[4].add(-2, 0, 4), leaves);
        placeBlock(positions[4].add(-1, 0, 5), leaves);
        placeBlock(positions[4].add(2, 0, 3), leaves);
        placeBlock(positions[4].add(1, 0, 1), leaves);
        placeBlock(positions[3].add(1, 0, 1), leaves);
        //精修南方
        placeBlock(positions[5].add(-3, 0, 2), leaves);
        placeBlock(positions[6].add(-3, 0, 5), leaves);
        placeBlock(positions[6].add(-3, 0, 4), leaves);
        placeBlock(positions[6].add(-3, 0, 3), leaves);
        placeBlock(positions[6].add(-3, 0, 2), leaves);
        placeBlock(positions[7].add(-3, 0, 5), leaves);
        placeBlock(positions[7].add(-3, 0, 4), leaves);
        placeBlock(positions[7].add(-3, 0, 3), leaves);
        placeBlock(positions[7].add(-3, 0, 2), leaves);
        //精修东方
        placeBlock(positions[5].add(-1, 0, 0), leaves);
        placeBlock(positions[6].add(-1, 0, 0), leaves);
        placeBlock(positions[6].add(0, 0, 0), leaves);
        placeBlock(positions[6].add(1, 0, 0), leaves);
        placeBlock(positions[6].add(2, 0, 0), leaves);
        placeBlock(positions[7].add(-2, 0, 0), leaves);
        placeBlock(positions[7].add(-1, 0, 0), leaves);
        placeBlock(positions[7].add(0, 0, 0), leaves);
        placeBlock(positions[7].add(1, 0, 0), leaves);
        placeBlock(positions[7].add(2, 0, 0), leaves);
        //精修北方
        placeBlock(positions[6].add(3, 0, 2), leaves);
        placeBlock(positions[6].add(3, 0, 4), leaves);
        placeBlock(positions[6].add(3, 0, 5), leaves);
        placeBlock(positions[7].add(3, 0, 1), leaves);
        placeBlock(positions[7].add(3, 0, 2), leaves);
        placeBlock(positions[7].add(3, 0, 3), leaves);
        placeBlock(positions[7].add(3, 0, 4), leaves);
        //精修西方
        placeBlock(positions[6].add(1, 0, 6), leaves);
        placeBlock(positions[6].add(0, 0, 6), leaves);
        placeBlock(positions[6].add(-1, 0, 6), leaves);
        placeBlock(positions[7].add(2, 0, 6), leaves);
        placeBlock(positions[7].add(1, 0, 6), leaves);
        placeBlock(positions[7].add(0, 0, 6), leaves);
        placeBlock(positions[7].add(-1, 0, 6), leaves);
        placeBlock(positions[7].add(-2, 0, 6), leaves);
        //顶
        placeBlock(positions[9].add(1, 0, 4), leaves);
        placeBlock(positions[9].add(1, 0, 3), leaves);
        placeBlock(positions[9].add(1, 0, 2), leaves);
        placeBlock(positions[9].add(0, 0, 4), leaves);
        placeBlock(positions[9].add(0, 0, 3), leaves);
        placeBlock(positions[9].add(0, 0, 2), leaves);
        placeBlock(positions[9].add(-1, 0, 4), leaves);
        placeBlock(positions[9].add(-1, 0, 3), leaves);

        //生成蜂巢
        placebeeNest(positions[4].add(0, 0, -1));
    }

//樱花树4正x
    private static void 樱花树4正x(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        //高支干
        placeBlock(positions[3].add(1, 0, 0), x);
        placeBlock(positions[4].add(1, 0, 0), y);
        placeBlock(positions[5].add(1, 0, 0), y);
        placeBlock(positions[5].add(2, 0, 0), x);
        placeBlock(positions[6].add(2, 0, 0), y);
        placeBlock(positions[7].add(2, 0, 0), y);
        //矮支干
        placeBlock(positions[4].add(-1, 0, 0), x);
        placeBlock(positions[4].add(-2, 0, 0), x);
        placeBlock(positions[5].add(-2, 0, 0), y);
        placeBlock(positions[5].add(-3, 0, 0), x);
        placeBlock(positions[5].add(-4, 0, 0), x);
        placeBlock(positions[6].add(-4, 0, 0), y);

        //主体树叶
        BlockPos pos = positions[5].add(-5, 0, -1);
        for (int x = 0; x <= 9; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = 0; z <= 2; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[5].add(3, 0, -1), air);
        placeBlock(positions[5].add(4, 0, -1), air);
        placeBlock(positions[5].add(4, 0, 1), air);
        placeBlock(positions[5].add(4, 0, 0), air);

        //顶层主体树叶
        pos = positions[8].add(0, 0, -1);
        for (int x = 0; x <= 4; x++) {
            for (int z = 0; z <= 2; z++) {
                placeBlock(pos.add(x, 0, z), leaves);
            }
        }
        placeBlock(positions[8].add(-1, 0, 0), leaves);

        //下垂
        placeBlock(positions[4].add(1, 0, 1), leaves);
        placeBlock(positions[4].add(2, 0, 1), leaves);
        placeBlock(positions[3].add(2, 0, 1), leaves);
        placeBlock(positions[4].add(1, 0, -1), leaves);
        placeBlock(positions[4].add(-3, 0, -1), leaves);
        placeBlock(positions[4].add(-4, 0, -1), leaves);
        placeBlock(positions[3].add(-4, 0, -1), leaves);
        placeBlock(positions[4].add(-3, 0, 0), leaves);
        placeBlock(positions[4].add(-4, 0, 0), leaves);
        placeBlock(positions[4].add(-5, 0, 0), leaves);
        placeBlock(positions[4].add(-1, 0, 1), leaves);
        placeBlock(positions[4].add(-2, 0, 1), leaves);
        placeBlock(positions[4].add(-3, 0, 1), leaves);
        placeBlock(positions[3].add(-3, 0, 1), leaves);
        placeBlock(positions[4].add(-4, 0, 1), leaves);

        //精修南面
        placeBlock(positions[4].add(-1, 0, 2), leaves);
        placeBlock(positions[5].add(-3, 0, 2), leaves);
        placeBlock(positions[5].add(-2, 0, 2), leaves);
        placeBlock(positions[5].add(-1, 0, 2), leaves);
        placeBlock(positions[5].add(1, 0, 2), leaves);
        placeBlock(positions[6].add(-5, 0, 2), leaves);
        placeBlock(positions[6].add(-4, 0, 2), leaves);
        placeBlock(positions[6].add(-3, 0, 2), leaves);
        placeBlock(positions[6].add(-2, 0, 2), leaves);
        placeBlock(positions[6].add(-1, 0, 2), leaves);
        placeBlock(positions[6].add(0, 0, 2), leaves);
        placeBlock(positions[6].add(1, 0, 2), leaves);
        placeBlock(positions[6].add(2, 0, 2), leaves);
        placeBlock(positions[6].add(3, 0, 2), leaves);
        placeBlock(positions[7].add(-4, 0, 2), leaves);
        placeBlock(positions[7].add(-3, 0, 2), leaves);
        placeBlock(positions[7].add(-2, 0, 2), leaves);
        placeBlock(positions[7].add(1, 0, 2), leaves);
        placeBlock(positions[7].add(2, 0, 2), leaves);
        placeBlock(positions[7].add(3, 0, 2), leaves);
        placeBlock(positions[7].add(4, 0, 2), leaves);
        placeBlock(positions[8].add(2, 0, 2), leaves);
        placeBlock(positions[8].add(3, 0, 2), leaves);
        //精修东面
        placeBlock(positions[7].add(5, 0, -1), leaves);
        placeBlock(positions[7].add(5, 0, 0), leaves);
        placeBlock(positions[7].add(5, 0, 1), leaves);
        //精修北面
        placeBlock(positions[5].add(-4, 0, -2), leaves);
        placeBlock(positions[5].add(-1, 0, -2), leaves);
        placeBlock(positions[4].add(-1, 0, -2), leaves);
        placeBlock(positions[5].add(0, 0, -2), leaves);
        placeBlock(positions[6].add(-5, 0, -2), leaves);
        placeBlock(positions[6].add(-4, 0, -2), leaves);
        placeBlock(positions[6].add(-3, 0, -2), leaves);
        placeBlock(positions[6].add(-2, 0, -2), leaves);
        placeBlock(positions[6].add(-1, 0, -2), leaves);
        placeBlock(positions[6].add(0, 0, -2), leaves);
        placeBlock(positions[6].add(1, 0, -2), leaves);
        placeBlock(positions[6].add(3, 0, -2), leaves);
        placeBlock(positions[7].add(-4, 0, -2), leaves);
        placeBlock(positions[7].add(-3, 0, -2), leaves);
        placeBlock(positions[7].add(1, 0, -2), leaves);
        placeBlock(positions[7].add(2, 0, -2), leaves);
        placeBlock(positions[7].add(3, 0, -2), leaves);
        placeBlock(positions[7].add(4, 0, -2), leaves);
        placeBlock(positions[8].add(2, 0, -2), leaves);
        placeBlock(positions[8].add(3, 0, -2), leaves);
        //精修西面
        placeBlock(positions[6].add(-6, 0, -1), leaves);
        placeBlock(positions[6].add(-6, 0, 0), leaves);
        placeBlock(positions[6].add(-6, 0, 1), leaves);
        //顶
        placeBlock(positions[8].add(-4, 0, -1), leaves);
        placeBlock(positions[8].add(-4, 0, 0), leaves);
        placeBlock(positions[8].add(-3, 0, 0), leaves);
        placeBlock(positions[8].add(-3, 0, 1), leaves);
        placeBlock(positions[9].add(1, 0, 0), leaves);
        placeBlock(positions[9].add(2, 0, -1), leaves);
        placeBlock(positions[9].add(2, 0, 0), leaves);
        placeBlock(positions[9].add(2, 0, 1), leaves);
        placeBlock(positions[9].add(3, 0, -1), leaves);
        placeBlock(positions[9].add(3, 0, 0), leaves);
        placeBlock(positions[9].add(3, 0, 1), leaves);

        //生成蜂巢
        placebeeNest(positions[4].add(-1, 0, 0));
    }

//樱花树4负x
    private static void 樱花树4负x(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        //高支干
        placeBlock(positions[3].add(-1, 0, 0), x);
        placeBlock(positions[4].add(-1, 0, 0), y);
        placeBlock(positions[5].add(-1, 0, 0), y);
        placeBlock(positions[5].add(-2, 0, 0), x);
        placeBlock(positions[6].add(-2, 0, 0), y);
        placeBlock(positions[7].add(-2, 0, 0), y);
        //矮支干
        placeBlock(positions[4].add(1, 0, 0), x);
        placeBlock(positions[4].add(2, 0, 0), x);
        placeBlock(positions[5].add(2, 0, 0), y);
        placeBlock(positions[5].add(3, 0, 0), x);
        placeBlock(positions[5].add(4, 0, 0), x);
        placeBlock(positions[6].add(4, 0, 0), y);

        //主体树叶
        BlockPos pos = positions[5].add(-4, 0, -1);
        for (int x = 0; x <= 9; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = 0; z <= 2; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[5].add(-3, 0, 1), air);
        placeBlock(positions[5].add(-4, 0, 1), air);
        placeBlock(positions[5].add(-4, 0, -1), air);
        placeBlock(positions[5].add(-4, 0, 0), air);

        //顶层主体树叶
        pos = positions[8].add(-4, 0, -1);
        for (int x = 0; x <= 4; x++) {
            for (int z = 0; z <= 2; z++) {
                placeBlock(pos.add(x, 0, z), leaves);
            }
        }
        placeBlock(positions[8].add(1, 0, 0), leaves);

        //下垂
        placeBlock(positions[4].add(-1, 0, -1), leaves);
        placeBlock(positions[4].add(-2, 0, -1), leaves);
        placeBlock(positions[3].add(-2, 0, -1), leaves);
        placeBlock(positions[4].add(-1, 0, 1), leaves);
        placeBlock(positions[4].add(3, 0, 1), leaves);
        placeBlock(positions[4].add(4, 0, 1), leaves);
        placeBlock(positions[3].add(4, 0, 1), leaves);
        placeBlock(positions[4].add(3, 0, 0), leaves);
        placeBlock(positions[4].add(4, 0, 0), leaves);
        placeBlock(positions[4].add(5, 0, 0), leaves);
        placeBlock(positions[4].add(1, 0, -1), leaves);
        placeBlock(positions[4].add(2, 0, -1), leaves);
        placeBlock(positions[4].add(3, 0, -1), leaves);
        placeBlock(positions[3].add(3, 0, -1), leaves);
        placeBlock(positions[4].add(4, 0, -1), leaves);

        //精修南面
        placeBlock(positions[4].add(1, 0, -2), leaves);
        placeBlock(positions[5].add(3, 0, -2), leaves);
        placeBlock(positions[5].add(2, 0, -2), leaves);
        placeBlock(positions[5].add(1, 0, -2), leaves);
        placeBlock(positions[5].add(-1, 0, -2), leaves);
        placeBlock(positions[6].add(5, 0, -2), leaves);
        placeBlock(positions[6].add(4, 0, -2), leaves);
        placeBlock(positions[6].add(3, 0, -2), leaves);
        placeBlock(positions[6].add(2, 0, -2), leaves);
        placeBlock(positions[6].add(1, 0, -2), leaves);
        placeBlock(positions[6].add(0, 0, -2), leaves);
        placeBlock(positions[6].add(-1, 0, -2), leaves);
        placeBlock(positions[6].add(-2, 0, -2), leaves);
        placeBlock(positions[6].add(-3, 0, -2), leaves);
        placeBlock(positions[7].add(4, 0, -2), leaves);
        placeBlock(positions[7].add(3, 0, -2), leaves);
        placeBlock(positions[7].add(2, 0, -2), leaves);
        placeBlock(positions[7].add(-1, 0, -2), leaves);
        placeBlock(positions[7].add(-2, 0, -2), leaves);
        placeBlock(positions[7].add(-3, 0, -2), leaves);
        placeBlock(positions[7].add(-4, 0, -2), leaves);
        placeBlock(positions[8].add(-2, 0, -2), leaves);
        placeBlock(positions[8].add(-3, 0, -2), leaves);
        //精修东面
        placeBlock(positions[7].add(-5, 0, 1), leaves);
        placeBlock(positions[7].add(-5, 0, 0), leaves);
        placeBlock(positions[7].add(-5, 0, -1), leaves);
        //精修北面
        placeBlock(positions[5].add(4, 0, 2), leaves);
        placeBlock(positions[5].add(1, 0, 2), leaves);
        placeBlock(positions[4].add(1, 0, 2), leaves);
        placeBlock(positions[5].add(0, 0, 2), leaves);
        placeBlock(positions[6].add(5, 0, 2), leaves);
        placeBlock(positions[6].add(4, 0, 2), leaves);
        placeBlock(positions[6].add(3, 0, 2), leaves);
        placeBlock(positions[6].add(2, 0, 2), leaves);
        placeBlock(positions[6].add(1, 0, 2), leaves);
        placeBlock(positions[6].add(0, 0, 2), leaves);
        placeBlock(positions[6].add(-1, 0, 2), leaves);
        placeBlock(positions[6].add(-3, 0, 2), leaves);
        placeBlock(positions[7].add(4, 0, 2), leaves);
        placeBlock(positions[7].add(3, 0, 2), leaves);
        placeBlock(positions[7].add(-1, 0, 2), leaves);
        placeBlock(positions[7].add(-2, 0, 2), leaves);
        placeBlock(positions[7].add(-3, 0, 2), leaves);
        placeBlock(positions[7].add(-4, 0, 2), leaves);
        placeBlock(positions[8].add(-2, 0, 2), leaves);
        placeBlock(positions[8].add(-3, 0, 2), leaves);
        //精修西面
        placeBlock(positions[6].add(6, 0, 1), leaves);
        placeBlock(positions[6].add(6, 0, 0), leaves);
        placeBlock(positions[6].add(6, 0, -1), leaves);
        //顶
        placeBlock(positions[8].add(4, 0, 1), leaves);
        placeBlock(positions[8].add(4, 0, 0), leaves);
        placeBlock(positions[8].add(3, 0, 0), leaves);
        placeBlock(positions[8].add(3, 0, -1), leaves);
        placeBlock(positions[9].add(-1, 0, 0), leaves);
        placeBlock(positions[9].add(-2, 0, 1), leaves);
        placeBlock(positions[9].add(-2, 0, 0), leaves);
        placeBlock(positions[9].add(-2, 0, -1), leaves);
        placeBlock(positions[9].add(-3, 0, 1), leaves);
        placeBlock(positions[9].add(-3, 0, 0), leaves);
        placeBlock(positions[9].add(-3, 0, -1), leaves);

        //生成蜂巢
        placebeeNest(positions[4].add(1, 0, 0));
    }

//樱花树4正z
    private static void 樱花树4正z(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        //高支干
        placeBlock(positions[3].add(0, 0, 1), z);
        placeBlock(positions[4].add(0, 0, 1), y);
        placeBlock(positions[5].add(0, 0, 1), y);
        placeBlock(positions[5].add(0, 0, 2), z);
        placeBlock(positions[6].add(0, 0, 2), y);
        placeBlock(positions[7].add(0, 0, 2), y);
        //矮支干
        placeBlock(positions[4].add(0, 0, -1), z);
        placeBlock(positions[4].add(0, 0, -2), z);
        placeBlock(positions[5].add(0, 0, -2), y);
        placeBlock(positions[5].add(0, 0, -3), z);
        placeBlock(positions[5].add(0, 0, -4), z);
        placeBlock(positions[6].add(0, 0, -4), y);

        //主体树叶
        BlockPos pos = positions[5].add(-1, 0, -5);
        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = 0; z <= 9; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[5].add(-1, 0, 3), air);
        placeBlock(positions[5].add(-1, 0, 4), air);
        placeBlock(positions[5].add(1, 0, 4), air);
        placeBlock(positions[5].add(0, 0, 4), air);

        //顶层主体树叶
        pos = positions[8].add(-1, 0, 0);
        for (int x = 0; x <= 2; x++) {
            for (int z = 0; z <= 4; z++) {
                placeBlock(pos.add(x, 0, z), leaves);
            }
        }
        placeBlock(positions[8].add(0, 0, -1), leaves);

        //下垂
        placeBlock(positions[4].add(1, 0, 1), leaves);
        placeBlock(positions[4].add(1, 0, 2), leaves);
        placeBlock(positions[3].add(1, 0, 2), leaves);
        placeBlock(positions[4].add(-1, 0, 1), leaves);
        placeBlock(positions[4].add(-1, 0, -3), leaves);
        placeBlock(positions[4].add(-1, 0, -4), leaves);
        placeBlock(positions[3].add(-1, 0, -4), leaves);
        placeBlock(positions[4].add(0, 0, -3), leaves);
        placeBlock(positions[4].add(0, 0, -4), leaves);
        placeBlock(positions[4].add(0, 0, -5), leaves);
        placeBlock(positions[4].add(1, 0, -1), leaves);
        placeBlock(positions[4].add(1, 0, -2), leaves);
        placeBlock(positions[4].add(1, 0, -3), leaves);
        placeBlock(positions[3].add(1, 0, -3), leaves);
        placeBlock(positions[4].add(1, 0, -4), leaves);

        //精修南面
        placeBlock(positions[4].add(2, 0, -1), leaves);
        placeBlock(positions[5].add(2, 0, -3), leaves);
        placeBlock(positions[5].add(2, 0, -2), leaves);
        placeBlock(positions[5].add(2, 0, -1), leaves);
        placeBlock(positions[5].add(2, 0, 1), leaves);
        placeBlock(positions[6].add(2, 0, -5), leaves);
        placeBlock(positions[6].add(2, 0, -4), leaves);
        placeBlock(positions[6].add(2, 0, -3), leaves);
        placeBlock(positions[6].add(2, 0, -2), leaves);
        placeBlock(positions[6].add(2, 0, -1), leaves);
        placeBlock(positions[6].add(2, 0, 0), leaves);
        placeBlock(positions[6].add(2, 0, 1), leaves);
        placeBlock(positions[6].add(2, 0, 2), leaves);
        placeBlock(positions[6].add(2, 0, 3), leaves);
        placeBlock(positions[7].add(2, 0, -4), leaves);
        placeBlock(positions[7].add(2, 0, -3), leaves);
        placeBlock(positions[7].add(2, 0, -2), leaves);
        placeBlock(positions[7].add(2, 0, 1), leaves);
        placeBlock(positions[7].add(2, 0, 2), leaves);
        placeBlock(positions[7].add(2, 0, 3), leaves);
        placeBlock(positions[7].add(2, 0, 4), leaves);
        placeBlock(positions[8].add(2, 0, 2), leaves);
        placeBlock(positions[8].add(2, 0, 3), leaves);
        //精修东面
        placeBlock(positions[7].add(-1, 0, 5), leaves);
        placeBlock(positions[7].add(0, 0, 5), leaves);
        placeBlock(positions[7].add(1, 0, 5), leaves);
        //精修北面
        placeBlock(positions[5].add(-2, 0, -4), leaves);
        placeBlock(positions[5].add(-2, 0, -1), leaves);
        placeBlock(positions[4].add(-2, 0, -1), leaves);
        placeBlock(positions[5].add(-2, 0, 0), leaves);
        placeBlock(positions[6].add(-2, 0, -5), leaves);
        placeBlock(positions[6].add(-2, 0, -4), leaves);
        placeBlock(positions[6].add(-2, 0, -3), leaves);
        placeBlock(positions[6].add(-2, 0, -2), leaves);
        placeBlock(positions[6].add(-2, 0, -1), leaves);
        placeBlock(positions[6].add(-2, 0, 0), leaves);
        placeBlock(positions[6].add(-2, 0, 1), leaves);
        placeBlock(positions[6].add(-2, 0, 3), leaves);
        placeBlock(positions[7].add(-2, 0, -4), leaves);
        placeBlock(positions[7].add(-2, 0, -3), leaves);
        placeBlock(positions[7].add(-2, 0, 1), leaves);
        placeBlock(positions[7].add(-2, 0, 2), leaves);
        placeBlock(positions[7].add(-2, 0, 3), leaves);
        placeBlock(positions[7].add(-2, 0, 4), leaves);
        placeBlock(positions[8].add(-2, 0, 2), leaves);
        placeBlock(positions[8].add(-2, 0, 3), leaves);
        //精修西面
        placeBlock(positions[6].add(-1, 0, -6), leaves);
        placeBlock(positions[6].add(0, 0, -6), leaves);
        placeBlock(positions[6].add(1, 0, -6), leaves);
        //顶
        placeBlock(positions[8].add(-1, 0, -4), leaves);
        placeBlock(positions[8].add(0, 0, -4), leaves);
        placeBlock(positions[8].add(0, 0, -3), leaves);
        placeBlock(positions[8].add(1, 0, -3), leaves);
        placeBlock(positions[9].add(0, 0, 1), leaves);
        placeBlock(positions[9].add(-1, 0, 2), leaves);
        placeBlock(positions[9].add(0, 0, 2), leaves);
        placeBlock(positions[9].add(1, 0, 2), leaves);
        placeBlock(positions[9].add(-1, 0, 3), leaves);
        placeBlock(positions[9].add(0, 0, 3), leaves);
        placeBlock(positions[9].add(1, 0, 3), leaves);

        //生成蜂巢
        placebeeNest(positions[4].add(0, 0, -1));
    }

//樱花树4负z
    private static void 樱花树4负z(BlockPos[] positions) {
        //主干
        placeBlock(positions[1], y, true);
        placeBlock(positions[2], y);
        placeBlock(positions[3], y);
        //高支干
        placeBlock(positions[3].add(0, 0, -1), z);
        placeBlock(positions[4].add(0, 0, -1), y);
        placeBlock(positions[5].add(0, 0, -1), y);
        placeBlock(positions[5].add(0, 0, -2), z);
        placeBlock(positions[6].add(0, 0, -2), y);
        placeBlock(positions[7].add(0, 0, -2), y);
        //矮支干
        placeBlock(positions[4].add(0, 0, 1), z);
        placeBlock(positions[4].add(0, 0, 2), z);
        placeBlock(positions[5].add(0, 0, 2), y);
        placeBlock(positions[5].add(0, 0, 3), z);
        placeBlock(positions[5].add(0, 0, 4), z);
        placeBlock(positions[6].add(0, 0, 4), y);

        //主体树叶
        BlockPos pos = positions[5].add(-1, 0, -4);
        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = 0; z <= 9; z++) {
                    placeBlock(pos.add(x, y, z), leaves);
                }
            }
        }
        placeBlock(positions[5].add(1, 0, -3), air);
        placeBlock(positions[5].add(1, 0, -4), air);
        placeBlock(positions[5].add(-1, 0, -4), air);
        placeBlock(positions[5].add(-0, 0, -4), air);

        //顶层主体树叶
        pos = positions[8].add(-1, 0, -4);
        for (int x = 0; x <= 2; x++) {
            for (int z = 0; z <= 4; z++) {
                placeBlock(pos.add(x, 0, z), leaves);
            }
        }
        placeBlock(positions[8].add(0, 0, 1), leaves);

        //下垂
        placeBlock(positions[4].add(-1, 0, -1), leaves);
        placeBlock(positions[4].add(-1, 0, -2), leaves);
        placeBlock(positions[3].add(-1, 0, -2), leaves);
        placeBlock(positions[4].add(1, 0, -1), leaves);
        placeBlock(positions[4].add(1, 0, 3), leaves);
        placeBlock(positions[4].add(1, 0, 4), leaves);
        placeBlock(positions[3].add(1, 0, 4), leaves);
        placeBlock(positions[4].add(0, 0, 3), leaves);
        placeBlock(positions[4].add(0, 0, 4), leaves);
        placeBlock(positions[4].add(0, 0, 5), leaves);
        placeBlock(positions[4].add(-1, 0, 1), leaves);
        placeBlock(positions[4].add(-1, 0, 2), leaves);
        placeBlock(positions[4].add(-1, 0, 3), leaves);
        placeBlock(positions[3].add(-1, 0, 3), leaves);
        placeBlock(positions[4].add(-1, 0, 4), leaves);

        //精修南面
        placeBlock(positions[4].add(-2, 0, 1), leaves);
        placeBlock(positions[5].add(-2, 0, 3), leaves);
        placeBlock(positions[5].add(-2, 0, 2), leaves);
        placeBlock(positions[5].add(-2, 0, 1), leaves);
        placeBlock(positions[5].add(-2, 0, -1), leaves);
        placeBlock(positions[6].add(-2, 0, 5), leaves);
        placeBlock(positions[6].add(-2, 0, 4), leaves);
        placeBlock(positions[6].add(-2, 0, 3), leaves);
        placeBlock(positions[6].add(-2, 0, 2), leaves);
        placeBlock(positions[6].add(-2, 0, 1), leaves);
        placeBlock(positions[6].add(-2, 0, 0), leaves);
        placeBlock(positions[6].add(-2, 0, -1), leaves);
        placeBlock(positions[6].add(-2, 0, -2), leaves);
        placeBlock(positions[6].add(-2, 0, -3), leaves);
        placeBlock(positions[7].add(-2, 0, 4), leaves);
        placeBlock(positions[7].add(-2, 0, 3), leaves);
        placeBlock(positions[7].add(-2, 0, 2), leaves);
        placeBlock(positions[7].add(-2, 0, -1), leaves);
        placeBlock(positions[7].add(-2, 0, -2), leaves);
        placeBlock(positions[7].add(-2, 0, -3), leaves);
        placeBlock(positions[7].add(-2, 0, -4), leaves);
        placeBlock(positions[8].add(-2, 0, -2), leaves);
        placeBlock(positions[8].add(-2, 0, -3), leaves);
        //精修东面
        placeBlock(positions[7].add(1, 0, -5), leaves);
        placeBlock(positions[7].add(0, 0, -5), leaves);
        placeBlock(positions[7].add(-1, 0, -5), leaves);
        //精修北面
        placeBlock(positions[5].add(2, 0, 4), leaves);
        placeBlock(positions[5].add(2, 0, 1), leaves);
        placeBlock(positions[4].add(2, 0, 1), leaves);
        placeBlock(positions[5].add(2, 0, 0), leaves);
        placeBlock(positions[6].add(2, 0, 5), leaves);
        placeBlock(positions[6].add(2, 0, 4), leaves);
        placeBlock(positions[6].add(2, 0, 3), leaves);
        placeBlock(positions[6].add(2, 0, 2), leaves);
        placeBlock(positions[6].add(2, 0, 1), leaves);
        placeBlock(positions[6].add(2, 0, 0), leaves);
        placeBlock(positions[6].add(2, 0, -1), leaves);
        placeBlock(positions[6].add(2, 0, -3), leaves);
        placeBlock(positions[7].add(2, 0, 4), leaves);
        placeBlock(positions[7].add(2, 0, 3), leaves);
        placeBlock(positions[7].add(2, 0, -1), leaves);
        placeBlock(positions[7].add(2, 0, -2), leaves);
        placeBlock(positions[7].add(2, 0, -3), leaves);
        placeBlock(positions[7].add(2, 0, -4), leaves);
        placeBlock(positions[8].add(2, 0, -2), leaves);
        placeBlock(positions[8].add(2, 0, -3), leaves);
        //精修西面
        placeBlock(positions[6].add(1, 0, 6), leaves);
        placeBlock(positions[6].add(0, 0, 6), leaves);
        placeBlock(positions[6].add(-1, 0, 6), leaves);
        //顶
        placeBlock(positions[8].add(1, 0, 4), leaves);
        placeBlock(positions[8].add(0, 0, 4), leaves);
        placeBlock(positions[8].add(0, 0, 3), leaves);
        placeBlock(positions[8].add(-1, 0, 3), leaves);
        placeBlock(positions[9].add(0, 0, -1), leaves);
        placeBlock(positions[9].add(1, 0, -2), leaves);
        placeBlock(positions[9].add(0, 0, -2), leaves);
        placeBlock(positions[9].add(-1, 0, -2), leaves);
        placeBlock(positions[9].add(1, 0, -3), leaves);
        placeBlock(positions[9].add(0, 0, -3), leaves);
        placeBlock(positions[9].add(-1, 0, -3), leaves);

        //生成蜂巢
        placebeeNest(positions[4].add(0, 0, 1));
    }

//放置落英
    public static void 放置落英(BlockPos pos, int r) {
        放置落英(world, pos, r, 10);
    }
    public static void 放置落英(World world, BlockPos pos, int r, int maxH) {
        for (int x = -r; x <= r; x++) {// r 生成半径
            for (int z = -r; z <= r; z++) {
                if (x * x + z * z <= r * r) {// 判断是否在圆内
                    BlockPos currentPos = pos.add(x, 0, z);
                    currentPos = world.getHeight(currentPos);// 确保位置在地表上

                    int y = pos.getY();
                    int currentY = currentPos.getY();
                    if (currentY > (y + maxH) || currentY < (y - maxH)) {
                        continue;
                    }

                    Block 下方方块 = world.getBlockState(currentPos.down()).getBlock();

                    if (下方方块 != Blocks.GRASS) {
                        continue;
                    }

                    Block 旧方块 = world.getBlockState(currentPos).getBlock();

                    if (旧方块 == Blocks.AIR) {
                        /*是否生成落英*/int a = (int) (Math.random() * 5);
                        if (a == 0) {
                            int axis = (int) (Math.random() * 4) + 1;
                            int level = (int) (Math.random() * 4) + 1;

                            IBlockState state = BlockBase.PINK_PETALS.getDefaultState()
                                .withProperty(SBlockPetals.AXIS, axis)
                                .withProperty(SBlockPetals.LEVEL, level);

                            world.setBlockState(currentPos, state, 2);
                        }
                    }
                }
            }
        }
    }

//放置方块
    private static void placeBlock(BlockPos pos, IBlockState state) {
        placeBlock(pos, state, false);
    }
    private static void placeBlock(BlockPos pos, IBlockState state, boolean a) {
        Block oldBlock = world.getBlockState(pos).getBlock();

        if (
            oldBlock == Blocks.AIR ||
            oldBlock == BlockBase.CHERRY_LEAVES ||
            a
           )
        {
            world.setBlockState(pos, state, 6);

            if (state.getBlock() == BlockBase.CHERRY_LOG && a) {
                world.notifyNeighborsOfStateChange(pos, BlockBase.CHERRY_LOG, true);
            }
        }
    }

//生成蜂巢和蜜蜂
    private static void placebeeNest(BlockPos pos) {
        if (
            (isSapling && !(ConfigValue.saplingSpawnBee)) ||
            (!isSapling && !(ConfigValue.spawnTreeSpawnBee))
        ) {return;}

    //概率生成蜂巢
        if (Examine.FuturemcID) {
            if (isSapling) {

                if  (
                        //true ||
                        ((int) (Math.random() * 10)) == 0 // 1/10概率
                    )
                {
                    placebeeNest(pos, true);
                }

            } else {
                if ((random.nextInt(20)) == 0) {
                    placebeeNest(pos, true);
                }
            }
        }
    }
    public static void placebeeNest(BlockPos beeHivePos, boolean lixizhihun) {
    //生成蜂巢
        if (world.getBlockState(beeHivePos).getBlock() == Blocks.AIR){
            //通过注册名获取方块
            Block block = Block.getBlockFromItem(Item.getByNameOrId("futuremc:bee_nest"));

        //设置方块状态并放置
            /*获取方块的默认状态*/IBlockState state = block.getDefaultState();

            //调整方块方向
            EnumFacing facing;
            EnumFacing[] directions = {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
            int attempts = 0;
            do {
                //如果尝试次数达到上限，返回取消生成
                if (attempts >= 10) {
                    return; //取消生成
                }

                //随机选择一个朝向
                facing = directions[(int) (Math.random() * directions.length)];
                attempts++;
            } while (
                //只让蜂巢朝向空气
                world.getBlockState(beeHivePos.offset(facing)).getBlock() != Blocks.AIR
            );

            /*设置方块的朝向属性*/state = state.withProperty(BeeHiveBlock.FACING, facing);

            /*放置方块*/world.setBlockState(beeHivePos, state, 2);

        //获取蜜蜂的实体, 并生成
            BlockPos spawnPos = beeHivePos.offset(facing).add(1.5, -0.5, 1.5);

            //防止蜜蜂窒息
            if (world.getBlockState(spawnPos).getBlock() == Blocks.AIR) {
                //生成3只蜜蜂
                for (int i = 1; i <= 3; i++) {
                    suike.suikecherry.sentity.bee.Bee.spawnBee(world, beeHivePos, spawnPos);
                }
            }
        }
    }
}