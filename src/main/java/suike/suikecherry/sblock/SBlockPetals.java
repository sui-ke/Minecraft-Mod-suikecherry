package suike.suikecherry.sblock;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.sound.SoundTypeLeaves;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.entity.Entity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;

//落英类
public class SBlockPetals extends BlockCarpet {
    public SBlockPetals(String name) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置硬度*/setHardness(0.0F);
        /*设置抗爆性*/setResistance(0.0F);
        /*设置不透明度*/setLightOpacity(0);
        /*设置声音*/setSoundType(new SoundTypeLeaves());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);

        /*初始化元数据列表*/initMetaMap();
        /*设置默认属性*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(AXIS, 1)
            .withProperty(LEVEL, 1)
            .withProperty(COLOR, EnumDyeColor.PINK));
    }

//落英属性
    /*方向*/public static final PropertyInteger AXIS = PropertyInteger.create("axis", 1, 4);
    /*等级*/public static final PropertyInteger LEVEL = PropertyInteger.create("level", 1, 4);
    /*颜色*/public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    //元数据列表
    /*元数据列表1: 状态 > 元数据*/public Map<List<Integer>, Integer> metaMap = new HashMap<>();
    /*元数据列表2: 元数据 > 状态*/public Map<Integer, List<Integer>> metaReverseMap = new HashMap<>();
    //初始化元数据列表
    public void initMetaMap() {
        int meta = 0;
        for (int axis = 1; axis <= 4; axis++) {
            for (int level = 1; level <= 4; level++) {
                //元数据列表1: 状态 > 元数据
                metaMap.put(Arrays.asList(axis, level), meta);

                //元数据列表2: 元数据 > 状态
                metaReverseMap.put(meta, Arrays.asList(axis, level));

                meta++;
            }
        }
    }

    //设置方块属性
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {AXIS, LEVEL, COLOR});
    }
    //获取方块状态
    @Override
    public IBlockState getStateFromMeta(int meta) {
        int axis = metaReverseMap.get(meta).get(0);
        int level = metaReverseMap.get(meta).get(1);

        return this.getDefaultState()
                .withProperty(AXIS, axis)
                .withProperty(LEVEL, level)
                .withProperty(COLOR, EnumDyeColor.PINK);
    }
    //获取方块元数据
    @Override
    public int getMetaFromState(IBlockState state) {
        int axis = state.getValue(AXIS);
        int level = state.getValue(LEVEL);

        return metaMap.get(Arrays.asList(axis, level));
    }

//碰撞箱
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
        //添加自定义的碰撞箱
        AxisAlignedBB box = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
    }
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        //自定义交互范围
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.2, 1.0);
    }

//获取物品&掉落物
    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        //返回落英的物品
        return new ItemStack(ItemBase.PINK_PETALS);
    }
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        //返回落英的掉落物
        drops.add(new ItemStack(ItemBase.PINK_PETALS, state.getValue(LEVEL)));
    }

//模型透明
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED; //设置为透明层
    }
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false; //该方块是透明的
    }
}