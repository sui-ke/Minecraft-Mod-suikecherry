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

//树苗物品
public class SItemSapling extends Item implements SItem {
    public SItemSapling(String name, CreativeTabs tabs, boolean a) {
        if (a) {
            /*设置物品名*/setRegistryName(name);
            /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
            /*设置创造模式物品栏*/setCreativeTab(tabs);

            /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
        }
    }

    private Block block; //树苗
    private Block blockPot; //树苗盆栽
    public void setBlock(Block block, Block blockPot) {
        this.block = block;
        this.blockPot = blockPot;
    }

//使用物品
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        /*点击的方块*/Block clickBlock = world.getBlockState(pos).getBlock();

        if (clickBlock == Blocks.FLOWER_POT && this.blockPot != null) {
            //替换花盆为树苗盆栽
            world.setBlockState(pos, this.blockPot.getDefaultState(), 2);

            /*消耗一个物品*/player.getHeldItem(hand).shrink(1);

            return EnumActionResult.SUCCESS;
        } else if (clickBlock == Blocks.GRASS || clickBlock == Blocks.DIRT || clickBlock == Blocks.TALLGRASS) {
            //只能放在草方块上

            Block oldBlock = world.getBlockState(pos.up()).getBlock();
            if (oldBlock != Blocks.AIR || oldBlock instanceof net.minecraft.block.BlockLiquid) {
                return EnumActionResult.FAIL; // 防止吞方块 && 不能放液体里
            }

            if (clickBlock != Blocks.TALLGRASS) {
                pos = pos.up();// 没点击草丛时提高一格
            }

            /*放置树苗*/placeSapling(world, pos, this.block);
            /*播放音效*/Sound.playSound(world, pos, "block.cherryleaves.place");
            /*消耗一个物品*/player.getHeldItem(hand).shrink(1);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL; 
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