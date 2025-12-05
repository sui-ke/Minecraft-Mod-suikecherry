package suike.suikecherry.block;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.world.gen.cherry.PetalsGen;
import suike.suikecherry.world.gen.cherry.CherryTreeGen;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;

//树苗
public class ModBlockSapling extends BlockBush implements IGrowable {
    public ModBlockSapling(String name) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(ModBlockPlanks.EnumType.getEnumType(name).getLeavesSound());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);

        /*设置默认属性*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(STAGE, 0));
    }

    private Item item;
    public void setItem(Item item) {
        if (this.item == null) this.item = item;
    }

// 元数据
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STAGE);
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
            .withProperty(STAGE, Integer.valueOf((meta & 8) >> 3));
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        return 0 | ((Integer) state.getValue(STAGE)).intValue() << 3;
    }

// 轮廓箱&碰撞箱
    protected static final AxisAlignedBB box = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return box;
    }

// 获取物品&掉落物
    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(this.item);
    }
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return this.item;
    }
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

// 生长方法
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            super.updateTick(world, pos, state, rand);

            if (rand.nextInt(7) == 0 && world.getLightFromNeighbors(pos.up()) >= 9 ) {
                this.grow(world, pos, state, rand);
            }
        }
    }

    public void grow(World world, BlockPos pos, IBlockState state, Random rand) {
        int stage = state.getValue(STAGE);
        if (stage == 0) {
            world.setBlockState(pos, state.withProperty(STAGE, 1), 4);
        } else {
            this.generateTree(world, pos, state, rand);
        }
    }

    public void generateTree(World world, BlockPos pos, IBlockState state, Random random) {
        if (growthSpace(world, pos)) {
            if (!world.isRemote) {
                //播放音效
                Sound.playSound(world, pos, "block.cherry_leaves.place", 3.0F, 1.0F);

                // 生成樱花树
                CherryTreeGen.getCherryTree().generateCherryTree(world, pos, random, true);

                // 生成落英
                if (ConfigValue.cherrySaplingAndPetals) PetalsGen.placePetals(world, pos, 5);
            }
        }
    }

// 检查是否拥有空间生长
    public static boolean growthSpace(World world, BlockPos pos) {
        Block block = world.getBlockState(pos.up()).getBlock();
        //检查正上方一格
        if (block != Blocks.AIR) { //不为空气
            if (block != BlockBase.CHERRY_LOG) { //不为树干
                return false; //不允许生长
            }
        }

        //检查上方5x*5z*6y
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 2; y <= 5; y++) {
                    BlockPos rotatePos = pos.add(x, y, z);
                    block = world.getBlockState(rotatePos).getBlock();
                    if (block != BlockBase.CHERRY_LEAVES && block != BlockBase.CHERRY_LOG && !world.isAirBlock(rotatePos)) {
                        return false; //如果有一个方块不是树叶, 树干, 空气, 则不允许生长
                    }
                }
            }
        }

        return true; //所有方块都是树叶, 树干, 空气, 则允许生长
    }

// 骨粉 IGrowable 接口实现
    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return rand.nextFloat() < 0.45D;
    }
    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return !isClient;
    }
    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        this.grow(world, pos, state, rand);
    }
}