package suike.suikecherry.sitem;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sblock.BlockBase;
import suike.suikecherry.sblock.SBlockSlab;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumActionResult;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;

//台阶物品类
public class SItemSlab extends Item implements SItem {
    public SItemSlab(String name, CreativeTabs tabs) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/setCreativeTab(tabs);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    private Block block; //台阶对应的方块
    public void setBlock(Block block) {
        this.block = block; //设置台阶的方块
    }

//放置&升级台阶
    public static boolean 是否放置成功 = false;
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);

        /*重置放置*/是否放置成功 = false;

        if ((state.getBlock() == BlockBase.CHERRY_SLAB && facing == EnumFacing.UP && state.getValue(SBlockSlab.HALF) == EnumBlockHalf.BOTTOM) ||
            (state.getBlock() == BlockBase.CHERRY_SLAB && facing == EnumFacing.DOWN && state.getValue(SBlockSlab.HALF) == EnumBlockHalf.TOP))
        {
        //升级台阶
            点击台阶升级台阶(world, pos, state, facing);

        } else if (邻近方块是否为台阶(world, pos, facing)) {
        //邻近
            点击邻近方块升级台阶(world, pos, facing);

        } else {
        //放置新的台阶
            放置新的台阶(world, pos, facing, hitY);
        }

        if (是否放置成功) {
            放置结束(world, pos, player, hand);
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

    public static void 放置新的台阶(World world, BlockPos pos, EnumFacing facing, float hitY) {
        if (facing == EnumFacing.UP) {
            /*玩家点击上方放置下半砖*/是否放置成功 = 放置方块(world, pos.up(), BlockBase.CHERRY_SLAB.getDefaultState().withProperty(SBlockSlab.HALF, EnumBlockHalf.BOTTOM));
        } else if(facing == EnumFacing.DOWN) {
            /*玩家点击下方放置上半砖*/是否放置成功 = 放置方块(world, pos.add(0, -1, 0), BlockBase.CHERRY_SLAB.getDefaultState().withProperty(SBlockSlab.HALF, EnumBlockHalf.TOP));
        } else {
            //侧面放置半砖
            BlockPos adjacentPos = pos.offset(facing);
            if (hitY < 0.5) {
                /*玩家点击上方放置下半砖*/是否放置成功 = 放置方块(world, adjacentPos, BlockBase.CHERRY_SLAB.getDefaultState().withProperty(SBlockSlab.HALF, EnumBlockHalf.BOTTOM));
            } else if(hitY > 0.5) {
                /*玩家点击下方放置上半砖*/是否放置成功 = 放置方块(world, adjacentPos, BlockBase.CHERRY_SLAB.getDefaultState().withProperty(SBlockSlab.HALF, EnumBlockHalf.TOP));
            }
        }
    }

    public static void 点击台阶升级台阶(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
        if (/*玩家点击上方升级下半砖*/(facing == EnumFacing.UP && state.getValue(SBlockSlab.HALF) == EnumBlockHalf.BOTTOM) ||
           (/*玩家点击下方升级上半砖*/facing == EnumFacing.DOWN && state.getValue(SBlockSlab.HALF) == EnumBlockHalf.TOP)) {

            /*替换为完整方块*/是否放置成功 = 放置方块(world, pos, BlockBase.CHERRY_SLAB_DOUBLE.getDefaultState(), true);
        }
    }

    public static boolean 邻近方块是否为台阶(World world, BlockPos pos, EnumFacing facing) {
        /*获取邻近坐标*/BlockPos adjacentPos = pos.offset(facing);
        /*获取邻近方块*/Block block = world.getBlockState(adjacentPos).getBlock();

        return block == BlockBase.CHERRY_SLAB;
    }

    public static void 点击邻近方块升级台阶(World world, BlockPos pos, EnumFacing facing) {
        /*获取坐标*/BlockPos adjacentPos = pos.offset(facing);
        /*获取邻近方块的状态*/IBlockState adjacentState = world.getBlockState(adjacentPos);

        是否放置成功 = 放置方块(world, adjacentPos, BlockBase.CHERRY_SLAB_DOUBLE.getDefaultState(), true);
    }

    public static void 放置结束(World world, BlockPos pos, EntityPlayer player, EnumHand hand) {
        /*减少物品栏物品*/player.getHeldItem(hand).shrink(1);

        /*生成1到5之间的随机数*/int a = (int) (Math.random() * 5) + 1;
        /*播放原木音效*/world.playSound(null, pos, new SoundEvent(new ResourceLocation(SuiKe.MODID, "block.cherrywood.place" + a)), SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public static boolean 放置方块(World world, BlockPos pos, IBlockState state) {return 放置方块(world, pos, state, false);}
    public static boolean 放置方块(World world, BlockPos pos, IBlockState state, boolean a) {
        Block oldBlock = world.getBlockState(pos).getBlock();
        if (oldBlock == Blocks.AIR || oldBlock.getMaterial(null).isLiquid() || a) {
            world.setBlockState(pos, state);

            return true;
        }

        return false;
    }

//燃料功能
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 150; //返回燃烧时间（单位：tick）
    }
}