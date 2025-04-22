package suike.suikecherry.sblock;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.sitem.SItemPetals;
import suike.suikecherry.sound.SoundTypeLeaves;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.entity.Entity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.BlockRenderLayer;
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

        if (getMetaFromState_Map.isEmpty()) {
            /*初始化元数据列表*/initMetaMap();
        }

        /*设置默认属性*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(AXIS, 1)
            .withProperty(LEVEL, 1)
            .withProperty(COLOR, EnumDyeColor.PINK));
    }

    private Item item;
    protected void setItem(Item item) {
        this.item = item;
    }

//落英属性
    /*方向*/public static final PropertyInteger AXIS = PropertyInteger.create("axis", 1, 4);
    /*等级*/public static final PropertyInteger LEVEL = PropertyInteger.create("level", 1, 4);
    /*颜色*/public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    //元数据列表
    /*元数据列表1: 获取元数据-通过状态*/private Map<List<Integer>, Integer> getMetaFromState_Map = new HashMap<>();
    /*元数据列表2: 获取状态-通过元数据*/private Map<Integer, List<Integer>> getStateFromMeta_Map = new HashMap<>();
    //初始化元数据列表
    private void initMetaMap() {
        int meta = 0;
        for (int axis = 1; axis <= 4; axis++) {
            for (int level = 1; level <= 4; level++) {
                //元数据列表1: 状态 > 元数据
                getMetaFromState_Map.put(Arrays.asList(axis, level), meta);

                //元数据列表2: 元数据 > 状态
                getStateFromMeta_Map.put(meta, Arrays.asList(axis, level));

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
        int axis = getStateFromMeta_Map.get(meta).get(0);
        int level = getStateFromMeta_Map.get(meta).get(1);

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

        return getMetaFromState_Map.get(Arrays.asList(axis, level));
    }

//轮廓箱&碰撞箱
    protected static final AxisAlignedBB box = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.2, 1.0);
    protected static final AxisAlignedBB box_1_1 = new AxisAlignedBB(0.5, 0.0, 0.5, 1.0, 0.2, 1.0);
    protected static final AxisAlignedBB box_1_2 = new AxisAlignedBB(0.5, 0.0, 0.0, 1.0, 0.2, 1.0);
    protected static final AxisAlignedBB box_2_1 = new AxisAlignedBB(0.0, 0.0, 0.5, 0.5, 0.2, 1.0);
    protected static final AxisAlignedBB box_2_2 = new AxisAlignedBB(0.0, 0.0, 0.5, 1.0, 0.2, 1.0);
    protected static final AxisAlignedBB box_3_1 = new AxisAlignedBB(0.0, 0.0, 0.0, 0.5, 0.2, 0.5);
    protected static final AxisAlignedBB box_3_2 = new AxisAlignedBB(0.0, 0.0, 0.0, 0.5, 0.2, 1.0);
    protected static final AxisAlignedBB box_4_1 = new AxisAlignedBB(0.5, 0.0, 0.0, 1.0, 0.2, 0.5);
    protected static final AxisAlignedBB box_4_2 = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.2, 0.5);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int level = state.getValue(LEVEL);

        if (state.getValue(LEVEL) == 1) {
            int axis = state.getValue(AXIS);
            switch (axis) {
                case 1:
                    return box_1_1;

                case 2:
                    return box_2_1;

                case 3:
                    return box_3_1;

                case 4:
                    return box_4_1;
            }

        } else if (state.getValue(LEVEL) == 2) {
            int axis = state.getValue(AXIS);
            switch (axis) {
                case 1:
                    return box_1_2;

                case 2:
                    return box_2_2;

                case 3:
                    return box_3_2;

                case 4:
                    return box_4_2;
            }
        }

        return box;
    }
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
        // 该方块没有碰撞箱
        addCollisionBoxToList(pos, entityBox, collidingBoxes, null);
    }

//获取物品&掉落物
    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(this.item);
    }
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(this.item, state.getValue(LEVEL)));
    }

//方块更新
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (SItemPetals.downBlockIsGrass(world, pos)) {
            return;
        }

        world.destroyBlock(pos, true);
    }

//模型透明
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED; // 设置为透明层
    }
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false; // 该方块是透明的
    }
}