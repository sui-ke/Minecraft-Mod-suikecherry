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
    protected void setBlock(Block block) {
        this.block = block;
    }

//使用物品
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clickPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        /*点击方块的状态*/IBlockState clickBlockState = world.getBlockState(clickPos);
        /*点击的方块*/Block clickBlock = clickBlockState.getBlock();
        /*放置坐标*/BlockPos placeBlockPos = clickPos.offset(facing);

        //升级落英
        if (clickBlock == this.block) {
            /*修改放置位置*/placeBlockPos = clickPos;
            int level = clickBlockState.getValue(SBlockPetals.LEVEL);

            if (level >= 4 )
                return EnumActionResult.FAIL;

            IBlockState state = clickBlockState.withProperty(SBlockPetals.LEVEL, (level + 1));

            /*放置方块*/placeBlock(world, placeBlockPos, state);
            /*消耗一个物品*/player.getHeldItem(hand).shrink(1);
            return EnumActionResult.SUCCESS;
        }

        //首次放置落英
        //吞方块
        if (clickBlock == Blocks.TALLGRASS || (clickBlock == Blocks.SNOW_LAYER && clickBlock.getMetaFromState(clickBlockState) == 0))
            placeBlockPos = clickPos;

        if (downBlockIsGrass(world, placeBlockPos)) {
            IBlockState oldBlockState = world.getBlockState(placeBlockPos);
            Block oldBlock = oldBlockState.getBlock();

            // 不能放液体里
            if (oldBlock instanceof net.minecraft.block.BlockLiquid)
                return EnumActionResult.FAIL;

            // 防止吞方块 ; 可以吞草丛和一层的雪
            if (oldBlock == Blocks.AIR || oldBlock == Blocks.TALLGRASS || (oldBlock == Blocks.SNOW_LAYER && oldBlock.getMetaFromState(oldBlockState) == 0)) {
                /*获取玩家朝向*/facing = player.getHorizontalFacing();

                int axis = 1;//面朝北方
                if (facing == EnumFacing.EAST) {//面朝东方
                    axis = 2;
                } else if (facing == EnumFacing.SOUTH) {//面朝南方
                    axis = 3;
                } else if (facing == EnumFacing.WEST) {//面朝西方
                    axis = 4;
                }

                IBlockState state = this.block.getDefaultState().withProperty(SBlockPetals.AXIS, axis);

                /*放置方块*/placeBlock(world, placeBlockPos, state);
                /*消耗一个物品*/player.getHeldItem(hand).shrink(1);
                return EnumActionResult.SUCCESS;
            }
        }

        return EnumActionResult.FAIL;
    }

//检查下方方块
    public static boolean downBlockIsGrass(World world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos.down());
        Block block = blockState.getBlock();

        return  block == Blocks.DIRT ||
                block == Blocks.GRASS ||
                block == Blocks.MYCELIUM ||
                block == Blocks.FARMLAND ||
                (
                    block.getRegistryName().toString().contains("moss") &&
                    block.isFullCube(blockState)
                );
    }

//放置落英
    public void placeBlock(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, 2);
        /*播放树叶音效*/Sound.playSound(world, pos, "block.cherryleaves.place");
    }
}