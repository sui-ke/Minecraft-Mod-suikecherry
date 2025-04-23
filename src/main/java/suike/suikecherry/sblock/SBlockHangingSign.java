package suike.suikecherry.sblock;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.sound.SoundTypeWood;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import suike.suikecherry.suikecherry.Tags;

//悬挂告示牌类
public class SBlockHangingSign extends Block implements SBlock {
    public SBlockHangingSign(String name, boolean inWall) {
        super(Material.WOOD);
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setTranslationKey(name + "_" + Tags.MOD_ID);
        /*设置硬度*/setHardness(1.0F);
        /*设置抗爆性*/setResistance(1.0F);
        /*设置挖掘等级*/setHarvestLevel("axe", 0);
        /*设置不透明度*/setLightOpacity(0);
        /*设置声音*/setSoundType(new SoundTypeWood());
        /*设置是否是墙上的*/this.inWall = inWall;

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*设置默认属性*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH));
    }

    private Item item;
    protected void setItem(Item item) {
        this.item = item;
    }

//元数据
    /*墙上的悬挂式告示牌*/private boolean inWall;
    /*方向*/public static final PropertyDirection FACING = BlockHorizontal.FACING;

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
            .withProperty(FACING, EnumFacing.getFront(meta & 7));
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

//轮廓箱&碰撞箱
    // 1/16 = 0.0625 // .union()
    protected static final AxisAlignedBB XUPBox = new AxisAlignedBB(0.0, 0.875D, 0.375D, 1.0, 1.0, 0.625D);
    protected static final AxisAlignedBB ZUPBox = new AxisAlignedBB(0.375D, 0.875D, 0.0, 0.625D, 1.0, 1.0);
    protected static final AxisAlignedBB XBox = new AxisAlignedBB(0.0625D, 0.0, 0.4375D, 0.9375D, 0.625D, 0.5625D);
    protected static final AxisAlignedBB ZBox = new AxisAlignedBB(0.4375D, 0.0, 0.0625D, 0.5625D, 0.625D, 0.9375D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (this.inWall) {
            switch ((EnumFacing)state.getValue(FACING)) {
                case NORTH:
                default:
                    return XUPBox.union(XBox);
                case SOUTH:
                    return XUPBox.union(XBox);
                case WEST:
                    return ZUPBox.union(ZBox);
                case EAST:
                    return ZUPBox.union(ZBox);
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
                    box = XUPBox;
                    break;
                case SOUTH:
                    box = XUPBox;
                    break;
                case WEST:
                    box = ZUPBox;
                    break;
                case EAST:
                    box = ZUPBox;
                    break;
            }
        }
        addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
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
        if (!this.inWall) {
            IBlockState upBlockState = world.getBlockState(pos.up());
            if (!upBlockState.isSideSolid(world, pos.up(), EnumFacing.DOWN)) {
                world.destroyBlock(pos, true);
            }
        }
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