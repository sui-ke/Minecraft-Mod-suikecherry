package suike.suikecherry.sitem;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sblock.BlockBase;
import suike.suikecherry.sound.Sound;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import suike.suikecherry.suikecherry.Tags;

//树苗物品
public class SItemSapling extends Item implements SItem {
    public SItemSapling(String name, CreativeTabs tabs, boolean a) {
        if (a) {
            /*设置物品名*/setRegistryName(name);
            /*设置物品名key*/setTranslationKey(name + "_" + Tags.MOD_ID);
            /*设置创造模式物品栏*/setCreativeTab(tabs);

            /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
        }
    }

    private Block block; //树苗
    private Block blockPot; //树苗盆栽
    protected void setBlock(Block block, Block blockPot) {
        this.block = block;
        this.blockPot = blockPot;
    }

//使用物品
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clickPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        /*点击方块的状态*/IBlockState clickBlockState = world.getBlockState(clickPos);
        /*点击的方块*/Block clickBlock = clickBlockState.getBlock();

        if (clickBlock == Blocks.FLOWER_POT && this.blockPot != null) {
            //替换花盆为树苗盆栽
            world.setBlockState(clickPos, this.blockPot.getDefaultState(), 2);

            /*消耗一个物品*/player.getHeldItem(hand).shrink(1);

            return EnumActionResult.SUCCESS;
        }

        /*放置坐标*/BlockPos placeBlockPos = clickPos.offset(facing);
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
                /*放置树苗*/placeSapling(world, placeBlockPos, this.block);
                /*播放音效*/Sound.playSound(world, placeBlockPos, "block.cherryleaves.place");
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

//放置树苗
    public static void placeSapling(World world, BlockPos pos, Block block) {
        world.setBlockState(pos, block.getDefaultState(), 2);
        world.notifyNeighborsOfStateChange(pos, block, false);
    }

//燃料功能
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        if (ItemStack.areItemsEqual(itemStack, new ItemStack(ItemBase.CHERRY_SAPLING))) {
            return 100; //返回燃烧时间（单位：tick）
        }
        return 0;
    }
}