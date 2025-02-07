package suike.suikecherry.sblock;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.sound.SoundTypeWood;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

//悬挂告示牌类
public class SBlockHangingSignAttached extends Block implements SBlock {
    public SBlockHangingSignAttached(String name) {
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
            .withProperty(AXIS, 0));
    }

    private Item item;
    protected void setItem(Item item) {
        this.item = item;
    }

//元数据
    /*方向*/public static final PropertyInteger AXIS = PropertyInteger.create("axis", 0, 15);
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
            .withProperty(AXIS, meta);
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AXIS);
    }

//轮廓箱&碰撞箱
    protected static final AxisAlignedBB Box = new AxisAlignedBB(0.1875D, 0.0, 0.1875D, 0.8125D, 1.0, 0.8125D);
    protected static final AxisAlignedBB XBox = new AxisAlignedBB(0.0625D, 0.0, 0.4375D, 0.9375D, 0.625D, 0.5625D);
    protected static final AxisAlignedBB ZBox = new AxisAlignedBB(0.4375D, 0.0, 0.0625D, 0.5625D, 0.625D, 0.9375D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int axis = state.getValue(AXIS);
        if (axis == 0 || axis == 8)
            return XBox;
        if (axis == 4 || axis == 12)
            return ZBox;
        return Box;
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
        IBlockState upBlockState = world.getBlockState(pos.up());
        Block upBlock = upBlockState.getBlock();
        String upBlockName = upBlock.getRegistryName().toString();

        if (
            upBlock == Blocks.END_ROD ||
            upBlockName.contains("wall") ||
            upBlockName.contains("fence") ||
            upBlockName.contains("chain") ||
            upBlockName.contains("hanging") ||
            upBlock instanceof net.minecraft.block.BlockPane
           )
        {
            return;
        }

        if (upBlockState.isSideSolid(world, pos.up(), EnumFacing.DOWN)) {
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
        return false;
    }
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

//方块交互
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (facing == EnumFacing.DOWN)
            return BlockBase.lantern(world, pos.down(), player, hand, 1);
        return false;
    }
}