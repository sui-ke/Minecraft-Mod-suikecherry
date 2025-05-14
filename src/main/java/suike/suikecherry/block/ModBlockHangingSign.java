package suike.suikecherry.block;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.sound.SoundTypeWood;
import suike.suikecherry.tileentity.HangingSignTileEntity;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiEditSign;

//悬挂告示牌类
public class ModBlockHangingSign extends Block {
    public ModBlockHangingSign(String name) {
        super(Material.WOOD);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置硬度*/this.setHardness(1.0F);
        /*设置抗爆性*/this.setResistance(1.0F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(new SoundTypeWood());
        /*设置是否是墙上的*/this.inWall = name.endsWith("wall");

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*设置默认属性*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH));
    }

    private Item item;
    public void setItem(Item item) {
        if (this.item == null) this.item = item;
    }

// 方块数据
    /*墙上的悬挂式告示牌*/private boolean inWall;
    /*方向*/public static final PropertyDirection FACING = BlockHorizontal.FACING;

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
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
        if (!this.inWall) return BlockFaceShape.UNDEFINED;

        EnumFacing facing = state.getValue(FACING);
        if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
            // 南北方向时，东西面可连接
            if (side == EnumFacing.EAST || side == EnumFacing.WEST) {
                return BlockFaceShape.SOLID;
            }
        } else if (facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
            // 东西方向时，南北面可连接
            if (side == EnumFacing.NORTH || side == EnumFacing.SOUTH) {
                return BlockFaceShape.SOLID;
            }
        }

        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        if (this.inWall) return true;
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (!this.inWall) return false;

        EnumFacing facing = state.getValue(FACING);
        if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
            // 南北方向时，南北面可攀爬
            if (side == EnumFacing.NORTH || side == EnumFacing.SOUTH) {
                return true;
            }
        } else if (facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
            // 东西方向时，东西面可攀爬
            if (side == EnumFacing.EAST || side == EnumFacing.WEST) {
                return true;
            }
        }

        return false;
    }

// 元数据
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
            .withProperty(FACING, EnumFacing.getFront(meta & 7));
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

// 轮廓箱&碰撞箱
    // 1/16 = 0.0625 // .union()
    protected static final AxisAlignedBB XInWallBox = new AxisAlignedBB(0.0, 0.875D, 0.375D, 1.0, 1.0, 0.625D);
    protected static final AxisAlignedBB ZInWallBox = new AxisAlignedBB(0.375D, 0.875D, 0.0, 0.625D, 1.0, 1.0);
    protected static final AxisAlignedBB XBox = new AxisAlignedBB(0.0625D, 0.0, 0.4375D, 0.9375D, 0.625D, 0.5625D);
    protected static final AxisAlignedBB ZBox = new AxisAlignedBB(0.4375D, 0.0, 0.0625D, 0.5625D, 0.625D, 0.9375D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (this.inWall) {
            switch ((EnumFacing)state.getValue(FACING)) {
                case NORTH:
                default:
                    return XInWallBox.union(XBox);
                case SOUTH:
                    return XInWallBox.union(XBox);
                case WEST:
                    return ZInWallBox.union(ZBox);
                case EAST:
                    return ZInWallBox.union(ZBox);
            }
        } else {
            switch ((EnumFacing)state.getValue(FACING)) {
                case NORTH:
                default:
                    return XBox;
                case SOUTH:
                    return XBox;
                case WEST:
                    return ZBox;
                case EAST:
                    return ZBox;
            }
        }
    }
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
        AxisAlignedBB box = null;
        if (this.inWall) {
            switch ((EnumFacing)state.getValue(FACING)) {
                case NORTH:
                default:
                    box = XInWallBox;
                    break;
                case SOUTH:
                    box = XInWallBox;
                    break;
                case WEST:
                    box = ZInWallBox;
                    break;
                case EAST:
                    box = ZInWallBox;
                    break;
            }
        }
        addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
    }

// 获取物品&掉落物
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
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return this.item;
    }
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);

        this.spawnAsEntity(world, pos, ((HangingSignTileEntity) tile).getItemHandler().getStackInSlot(0));
        this.spawnAsEntity(world, pos, ((HangingSignTileEntity) tile).getItemHandler().getStackInSlot(1));
    }

// 方块更新
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!this.inWall) {
            IBlockState upBlockState = world.getBlockState(pos.up());
            if (!upBlockState.isSideSolid(world, pos.up(), EnumFacing.DOWN)) {
                world.destroyBlock(pos, true);
            }
        }
    }

// 模型透明
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

// 渲染展示的物品
    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT_MIPPED;
    }

// 方块交互
    @Override
    public boolean onBlockActivated(World world, BlockPos clickPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing clickFacing, float hitX, float hitY, float hitZ) {
        // 点击上方
        if (clickFacing == EnumFacing.UP) return false;

        // 点击下方放置灯笼逻辑
        if (clickFacing == EnumFacing.DOWN) return BlockBase.lantern(world, clickPos.down(), player, hand, 1);

        // 获取存储物品的 HangingSignTileEntity
        HangingSignTileEntity tile = (HangingSignTileEntity) world.getTileEntity(clickPos);

        ItemStack heldItem = player.getHeldItem(hand);

        // 获取点击面
        int slot = this.getSlotFromFacings(state.getValue(FACING), clickFacing);

        if (tile.hasDisplayItems(slot)) { // 如果这一面有物品
            // 移除展示的物品
            return tile.removeDisplayItems(world, clickPos, state, player, slot);
        }

        // 为蹲下
        if (player.isSneaking()) return false; 

        // 空手点击
        if (heldItem.isEmpty()) {
            // 编辑文字
            return editText(world, clickPos, tile, player, slot);
        } // 这一面什么都没有
        else if (!tile.hasAny(slot)) {
            // 展示物品
            return tile.displayItems(world, clickPos, state, player, heldItem, slot);
        }

        return false; 
    }

    // 编辑文本
    private boolean editText(World world, BlockPos clickPos, HangingSignTileEntity tile, EntityPlayer player, int slot) {
        if (slot == -1) return false;

        tile.editingSide = slot;
        tile.setPlayer(player);

        // player.openEditSign(tile); // openEditSign 方法不可用
        player.func_175141_a(tile); // 使用混淆名

        return true;
    }

    // 根据方块朝向和点击面获取编辑的面
    private int getSlotFromFacings(EnumFacing blockFacing, EnumFacing clickFacing) {
        if (getBlockFacing(blockFacing)) {
            // 方块南北朝向，南北面编辑
            if (clickFacing == EnumFacing.SOUTH) return 0;
            if (clickFacing == EnumFacing.NORTH) return 1;
        } else {
            // 方块东西朝向，东西面编辑
            if (clickFacing == EnumFacing.EAST) return 0;
            if (clickFacing == EnumFacing.WEST) return 1;
        }

        return -1;
    }

    // 获取展示方向
    public boolean getBlockFacing(EnumFacing blockFacing) {
        switch (blockFacing) {
            case WEST:
            case EAST:
                return false; // 东西方向
            case NORTH:
            case SOUTH:
            default:
                return true; // 南北方向
        }
    }
}