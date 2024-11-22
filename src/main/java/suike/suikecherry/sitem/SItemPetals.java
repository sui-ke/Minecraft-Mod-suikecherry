package suike.suikecherry.sitem;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sblock.BlockBase;
import suike.suikecherry.sblock.SBlockPetals;
import suike.suikecherry.sound.Sound;

import net.minecraft.item.Item;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;

//落英物品
public class SItemPetals extends Item implements SItem {
    public SItemPetals(String name, CreativeTabs tabs) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/setCreativeTab(tabs);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    private Block block;
    public void setBlock(Block block) {
        this.block = block;
    }

//使用物品
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clickPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        /*点击方块的状态*/IBlockState clickBlockState = world.getBlockState(clickPos);
        /*点击的方块*/Block clickBlock = clickBlockState.getBlock();

        if (clickBlock == this.block) {
        //升级落英
            if (/*升级落英*/upPetals(world, clickPos, this.block)) {
                /*消耗一个物品*/player.getHeldItem(hand).shrink(1);
                return EnumActionResult.SUCCESS;
            }

        } else {
            //首次放置落英
            if (facing == EnumFacing.UP) {// 仅允许在上方放置
                if (clickBlock == Blocks.GRASS ||
                    clickBlock == Blocks.MYCELIUM ||
                    clickBlock == Blocks.DIRT ||
                    clickBlock == Blocks.FARMLAND ||
                    (
                        clickBlock.getRegistryName().toString().contains("moss") &&
                        clickBlock.isFullCube(clickBlockState)
                    )
                ) {
                    if (/*放置落英*/placePetals(player, world, clickPos.up(), this.block)) {
                        /*消耗一个物品*/player.getHeldItem(hand).shrink(1);
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
        }
        return EnumActionResult.FAIL;
    }

//放置落英
    public static boolean placePetals(EntityPlayer player, World world, BlockPos pos, Block block) {
        Block oldBlock = world.getBlockState(pos).getBlock();

        if (oldBlock != Blocks.AIR || oldBlock instanceof net.minecraft.block.BlockLiquid) {
            return false; // 防止吞方块 && 不能放液体里
        }

        int axis = 1;//面朝北方

        /*获取玩家朝向*/EnumFacing facing = player.getHorizontalFacing();

        if (facing == EnumFacing.EAST) {//面朝东方
            axis = 2;
        } else if (facing == EnumFacing.SOUTH) {//面朝南方
            axis = 3;
        } else if (facing == EnumFacing.WEST) {//面朝西方
            axis = 4;
        }

        IBlockState state = ((SBlockPetals) block).getDefaultState().withProperty(SBlockPetals.AXIS, axis);
        world.setBlockState(pos, state, 2);
        world.notifyNeighborsOfStateChange(pos, block, false);

        /*播放树叶音效*/Sound.playSound(world, pos, "block.cherryleaves.place");

        return true;
    }

//升级落英
    public static boolean upPetals(World world, BlockPos pos, Block block) {
        IBlockState state = world.getBlockState(pos);
        int level = state.getValue(SBlockPetals.LEVEL);

        if (level < 4 ) {

            switch (level) {
                case 1:
                    state = state.withProperty(SBlockPetals.LEVEL, 2);
                    break;
                case 2:
                    state = state.withProperty(SBlockPetals.LEVEL, 3);
                    break;
                case 3:
                    state = state.withProperty(SBlockPetals.LEVEL, 4);
                    break;
            }

            world.setBlockState(pos, state, 2);
            world.notifyNeighborsOfStateChange(pos, block, false);

            /*播放树叶音效*/Sound.playSound(world, pos, "block.cherryleaves.place");

            return true;
        }

        return false;
    }
}