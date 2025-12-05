package suike.suikecherry.block;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.tileentity.HangingSignTileEntity;
import suike.suikecherry.tileentity.HasBackSideSignTileEntity;
import suike.suikecherry.inter.IAxis;
import suike.suikecherry.inter.IBlockHangingSign;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.gui.GuiScreen;

//悬挂告示牌类
public class ModBlockHangingSignAttached extends Block implements IBlockHangingSign, IAxis {
    public ModBlockHangingSignAttached(String name) {
        super(Material.WOOD);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置硬度*/this.setHardness(1.0F);
        /*设置抗爆性*/this.setResistance(1.0F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(0);

        this.type = ModBlockPlanks.EnumType.getEnumType(name);
        /*设置声音*/this.setSoundType(this.type.getWoodSound());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*设置默认属性*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(AXIS, 0));
    }

    private Item item;
    public void setItem(Item item) {
        if (this.item == null) this.item = item;
    }

    private ModBlockPlanks.EnumType type;
    public ModBlockPlanks.EnumType getType() {
        return this.type;
    }

// 方块数据
    /*方向*/public static final PropertyInteger AXIS = PropertyInteger.create("axis", 0, 15);
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new HangingSignTileEntity();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return BlockFaceShape.UNDEFINED;
    }

// 元数据
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

// 获取物品
    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        if (GuiScreen.isCtrlKeyDown()) return new ItemStack(this.item);

        HangingSignTileEntity tile = (HangingSignTileEntity) world.getTileEntity(pos);
        int hasItemSide = tile.getDisplayedSides();

        ItemStack itemStack = new ItemStack(this.item);

        // 只有一面有物品时返回此面
        if (hasItemSide == 0 || hasItemSide == 1) {
            itemStack = tile.getDisplayItem(hasItemSide);
        } 
        else if (hasItemSide == 2) { // 交替获取两面物品
            itemStack = tile.getDisplayItem(tile.getAndToggleLastPickedSide());
        }

        return itemStack;
    }

// 掉落物品
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        if (!player.capabilities.isCreativeMode) {
            Block.spawnAsEntity(world, pos, new ItemStack(this.item));
        }
        Block.spawnAsEntity(world, pos, ((HangingSignTileEntity) tile).getItemHandler().getStackInSlot(0));
        Block.spawnAsEntity(world, pos, ((HangingSignTileEntity) tile).getItemHandler().getStackInSlot(1));
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!world.isRemote && player.capabilities.isCreativeMode) {        
            this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItem(EnumHand.MAIN_HAND));
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(this.item));
        HangingSignTileEntity tile = (HangingSignTileEntity) world.getTileEntity(pos);
        drops.add(tile.getItemHandler().getStackInSlot(0));
        drops.add(tile.getItemHandler().getStackInSlot(1));
    }

// 方块更新
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

        this.spawnDrop(world, pos);
        world.destroyBlock(pos, true);
    }

    public void spawnDrop(World world, BlockPos pos) {
        HangingSignTileEntity tile = (HangingSignTileEntity) world.getTileEntity(pos);
        Block.spawnAsEntity(world, pos, tile.getItemHandler().getStackInSlot(0));
        Block.spawnAsEntity(world, pos, tile.getItemHandler().getStackInSlot(1));
    }

// 模型透明
    @Override
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
    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT_MIPPED;
    }

// 方块交互
    @Override
    public boolean onBlockActivated(World world, BlockPos clickPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing clickFacing, float hitX, float hitY, float hitZ) {
        // 点击 上/下方
        if (clickFacing == EnumFacing.UP || clickFacing == EnumFacing.DOWN) return false;

        // 为蹲下
        if (player.isSneaking()) return false;

        // 获取点击面
        int slot = this.getSlotFromFacings(state.getValue(AXIS), clickFacing, player);
        if (slot == -1) return false;

        // 获取存储物品的 HangingSignTileEntity
        HangingSignTileEntity tile = (HangingSignTileEntity) world.getTileEntity(clickPos);

        if (tile.hasDisplayItems(slot)) { // 如果这一面有物品
            // 移除展示的物品
            return tile.removeDisplayItems(world, clickPos, state, player, slot);
        }

        // 获取手持物
        ItemStack heldItem = player.getHeldItem(hand);

        // 空手点击
        if (heldItem.isEmpty()) {
            // 编辑文字
            return ((HasBackSideSignTileEntity) tile).editText(slot, player);
        }
        // 这一面什么都没有
        else if (!tile.hasAny(slot)) {
            // 展示物品
            return tile.displayItems(world, clickPos, state, player, heldItem, slot, this.type.getPlaceSound());
        }

        // 尝试打蜡
        if (tile.tryWaxed(slot, player.getHeldItem(hand))) {
            // 减少物品栏物品
            if (!player.capabilities.isCreativeMode) heldItem.shrink(1);
            return true;
        }

        return false;
    }
}