package suike.suikecherry.sblock;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.sound.SoundTypeWood;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;

//墙上的告示牌类
public class SBlockSignWall extends Block implements SBlock {
    public SBlockSignWall(String name) {
        super(Material.WOOD);
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置硬度*/setHardness(1.0F);
        /*设置抗爆性*/setResistance(1.0F);
        /*设置挖掘等级*/setHarvestLevel("axe", 0);
        /*设置不透明度*/setLightOpacity(0);
        /*设置声音*/setSoundType(new SoundTypeWood());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*设置默认属性*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH));
    }

    private Item item;
    protected void setItem(Item item) {
        this.item = item;
    }

//元数据
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

//轮廓箱&碰撞箱
    protected static final AxisAlignedBB EastBox = new AxisAlignedBB(0.0D, 0.28125D, 0.0D, 0.125D, 0.78125D, 1.0D);
    protected static final AxisAlignedBB WestBox = new AxisAlignedBB(0.875D, 0.28125D, 0.0D, 1.0D, 0.78125D, 1.0D);
    protected static final AxisAlignedBB NorthBox = new AxisAlignedBB(0.0D, 0.28125D, 0.875D, 1.0D, 0.78125D, 1.0D);
    protected static final AxisAlignedBB SouthBox = new AxisAlignedBB(0.0D, 0.28125D, 0.0D, 1.0D, 0.78125D, 0.125D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch ((EnumFacing)state.getValue(FACING)) {
            case NORTH:
            default:
                return NorthBox;
            case SOUTH:
                return SouthBox;
            case EAST:
                return EastBox;
            case WEST:
                return WestBox;
        }
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
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return this.item;
    }

//方块更新
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        EnumFacing facing = state.getValue(FACING);
        BlockPos backPos = pos.offset(facing.getOpposite());
        Block backBlock = world.getBlockState(backPos).getBlock();
        if (backBlock == net.minecraft.init.Blocks.AIR) {
            world.destroyBlock(pos, true);
        }
    }

//模型透明
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}