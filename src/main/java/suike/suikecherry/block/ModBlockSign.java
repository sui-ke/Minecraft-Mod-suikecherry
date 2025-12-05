package suike.suikecherry.block;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.tileentity.HasBackSideSignTileEntity;
import suike.suikecherry.inter.IAxis;
import suike.suikecherry.inter.IBlockSign;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockFaceShape;
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
import net.minecraft.tileentity.TileEntity;

//告示牌类
public class ModBlockSign extends Block implements IBlockSign, IAxis {
    public ModBlockSign(String name) {
        super(Material.WOOD);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置硬度*/this.setHardness(1.0F);
        /*设置抗爆性*/this.setResistance(1.0F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(ModBlockPlanks.EnumType.getEnumType(name).getWoodSound());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*设置默认属性*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(AXIS, 0));
    }

    private Item item;
    public void setItem(Item item) {
        if (this.item == null) this.item = item;
    }

// 方块数据
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new HasBackSideSignTileEntity();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return BlockFaceShape.UNDEFINED;
    }

// 元数据
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

// 轮廓箱&碰撞箱
    protected static final AxisAlignedBB box = new AxisAlignedBB(0.25D, 0.0, 0.25D, 0.75D, 1.0, 0.75D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return box;
    }
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
        // 该方块没有碰撞箱
        addCollisionBoxToList(pos, entityBox, collidingBoxes, null);
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

// 方块更新
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        Block downBlock = world.getBlockState(pos.down()).getBlock();
        if (downBlock == net.minecraft.init.Blocks.AIR) {
            world.destroyBlock(pos, true);
        }
    }

// 模型透明
    @Override
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

// 方块交互
    @Override
    public boolean onBlockActivated(World world, BlockPos clickPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing clickFacing, float hitX, float hitY, float hitZ) {
        if (clickFacing == EnumFacing.UP || clickFacing == EnumFacing.DOWN || player.isSneaking()) return false;

        // 获取点击面
        int slot = this.getSlotFromFacings(state.getValue(AXIS), clickFacing, player);
        if (slot == -1) return false;

        HasBackSideSignTileEntity tile = (HasBackSideSignTileEntity) world.getTileEntity(clickPos);

        // 为蹲下
        if (player.isSneaking()) return false;

        // 获取手持物
        ItemStack heldItem = player.getHeldItem(hand);

        // 尝试打蜡
        if (tile.tryWaxed(slot, heldItem)) {
            // 减少物品栏物品
            if (!player.capabilities.isCreativeMode) heldItem.shrink(1);
            return true;
        }

        // 编辑文字
        return tile.editText(slot, player);
    }
}