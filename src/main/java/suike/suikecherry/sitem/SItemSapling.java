package suike.suikecherry.sitem;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sblock.BlockBase;

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

//物品属性
    private Block block; //树苗对应的方块
    public void setBlock(Block block) {
        this.block = block; //设置树苗的方块
    }

//放置树苗
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block == Blocks.FLOWER_POT) {
            //替换花盆为树苗盆栽
            world.setBlockState(pos, BlockBase.CHERRY_SAPLING_POT.getDefaultState(), 2);

            /*消耗一个物品*/player.getHeldItem(hand).shrink(1);

            return EnumActionResult.SUCCESS;
        } else if (block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.TALLGRASS) {
            //只能放在草方块上

            Block oldBlock = world.getBlockState(pos.up()).getBlock();
            if (oldBlock.getMaterial(null).isLiquid() || oldBlock != Blocks.AIR) {
                return EnumActionResult.FAIL;// 不能放液体里 & 防止吞方块
            }

            if (block != Blocks.TALLGRASS) {
                pos = pos.up();
            }

            /*放置树苗*/placeSapling(world, pos, this.block);
            /*消耗一个物品*/player.getHeldItem(hand).shrink(1);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL; 
    }

    public static void placeSapling(World world, BlockPos pos, Block block) {
        //放置树苗
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