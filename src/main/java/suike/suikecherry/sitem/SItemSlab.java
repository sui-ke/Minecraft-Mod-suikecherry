package suike.suikecherry.sitem;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
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
import net.minecraft.util.math.BlockPos;
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

    private Block block;
    private Block blockDouble;
    public void setBlock(Block block, Block blockDouble) {
        this.block = block;
        this.blockDouble = blockDouble;
    }

//放置&升级台阶
    public static boolean 是否放置成功 = false;
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState clickState = world.getBlockState(pos);
        Block clickBlock = clickState.getBlock();

        /*重置放置*/是否放置成功 = false;

        if (
            clickBlock == Blocks.TALLGRASS || // 草丛
            clickBlock == Blocks.DEADBUSH || // 枯萎灌木
            (
                clickBlock == Blocks.DOUBLE_PLANT && // 高草丛
                (
                    clickBlock.getMetaFromState(clickState) == 2 ||
                    world.getBlockState(pos.down()).getBlock().getMetaFromState(world.getBlockState(pos.down())) == 2
                )
            )
           )
        {
            //覆盖草从
            是否放置成功 = 放置方块(world, pos, this.block.getDefaultState().withProperty(SBlockSlab.HALF, EnumBlockHalf.BOTTOM), true);
        } else if (
            (
                clickBlock == this.block &&
                facing == EnumFacing.UP && // 点击 物品对应方块 的上方 升级下半砖
                clickState.getValue(SBlockSlab.HALF) == EnumBlockHalf.BOTTOM
            ) ||
            (
                clickBlock == this.block &&
                facing == EnumFacing.DOWN && // 点击 物品对应方块 的下方 升级上半砖
                clickState.getValue(SBlockSlab.HALF) == EnumBlockHalf.TOP
            )
           )
        {
            //升级台阶
            点击台阶升级台阶(world, pos, clickState, facing);

        } else if (邻近方块是否为台阶(world, pos, facing)) {
            //邻近升级
            点击邻近方块升级台阶(world, pos, facing);

        } else {
            //放置新的台阶
            放置新的台阶(world, pos, facing, hitY);
        }

        if (是否放置成功) {
            /*减少物品栏物品*/player.getHeldItem(hand).shrink(1);
            /*播放原木音效*/Sound.playSound(world, pos, "block.cherrywood.place");

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

//放置新的台阶
    public void 放置新的台阶(World world, BlockPos pos, EnumFacing facing, float hitY) {
        //向点击的方向移动一格
        BlockPos adjacentPos = pos.offset(facing);

        if (facing == EnumFacing.UP) {
            //玩家点击上方放置下半砖
            是否放置成功 = 放置方块(world, adjacentPos, this.block.getDefaultState().withProperty(SBlockSlab.HALF, EnumBlockHalf.BOTTOM));
        } else if(facing == EnumFacing.DOWN) {
            //玩家点击下方放置上半砖
            是否放置成功 = 放置方块(world, adjacentPos, this.block.getDefaultState().withProperty(SBlockSlab.HALF, EnumBlockHalf.TOP));
        } else {
            //侧面放置半砖
            if (hitY < 0.5) {
                //点击下半部分放置下半砖
                是否放置成功 = 放置方块(world, adjacentPos, this.block.getDefaultState().withProperty(SBlockSlab.HALF, EnumBlockHalf.BOTTOM));
            } else if(hitY > 0.5) {
                //点击上半部分放置上半砖
                是否放置成功 = 放置方块(world, adjacentPos, this.block.getDefaultState().withProperty(SBlockSlab.HALF, EnumBlockHalf.TOP));
            }
        }
    }

//升级台阶
    public void 点击台阶升级台阶(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
        if (/*玩家点击上方升级下半砖*/(facing == EnumFacing.UP && state.getValue(SBlockSlab.HALF) == EnumBlockHalf.BOTTOM) ||
           (/*玩家点击下方升级上半砖*/facing == EnumFacing.DOWN && state.getValue(SBlockSlab.HALF) == EnumBlockHalf.TOP)) {

            /*替换为完整方块*/是否放置成功 = 放置方块(world, pos, this.blockDouble.getDefaultState(), true);
        }
    }

//邻近升级
    public boolean 邻近方块是否为台阶(World world, BlockPos pos, EnumFacing facing) {
        /*获取邻近坐标*/BlockPos adjacentPos = pos.offset(facing);
        /*获取邻近方块*/Block block = world.getBlockState(adjacentPos).getBlock();

        return block == this.block;
    }
    public void 点击邻近方块升级台阶(World world, BlockPos pos, EnumFacing facing) {
        /*获取坐标*/BlockPos adjacentPos = pos.offset(facing);
        /*获取邻近方块的状态*/IBlockState adjacentState = world.getBlockState(adjacentPos);

        是否放置成功 = 放置方块(world, adjacentPos, this.blockDouble.getDefaultState(), true);
    }

//放置方块
    public boolean 放置方块(World world, BlockPos pos, IBlockState state) {
        return 放置方块(world, pos, state, false);
    }
    public boolean 放置方块(World world, BlockPos pos, IBlockState state, boolean a) {
        Block oldBlock = world.getBlockState(pos).getBlock();

        if (
            oldBlock == Blocks.AIR ||
            oldBlock == Blocks.TALLGRASS ||
            oldBlock instanceof net.minecraft.block.BlockLiquid ||
            a
           )
        {
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