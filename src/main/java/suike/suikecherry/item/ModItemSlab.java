package suike.suikecherry.item;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.block.ModBlockSlab;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumActionResult;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;

//台阶物品类
public class ModItemSlab extends ItemSlab {
    public ModItemSlab(String name, ModBlockSlab block, ModBlockSlab blockDouble) {
        super(block, block, blockDouble);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        block.setItem(this);
        blockDouble.setItem(this);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

// 放置台阶
    private final IBlockState slabState = this.block.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM);
    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        return true;
    }
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clickPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumActionResult tryPlace = super.onItemUse(player, world, clickPos, hand, facing, hitX, hitY, hitZ);
        if (tryPlace == EnumActionResult.SUCCESS) {
            if (world.getBlockState(clickPos).getBlock().isReplaceable(world, clickPos)) {
                if (world.setBlockState(clickPos, slabState)) {
                    player.getHeldItem(hand).shrink(1);
                    Sound.playSound(world, clickPos, "block.cherry_wood.place");
                    return EnumActionResult.SUCCESS;
                }
            }
            return tryPlace;
        }

        // 获取放置位置
        BlockPos placePos = getPlacementPosition(world, clickPos, facing);

        // 只做脚下方块放置下半砖判断, 其他情况交给 super.onItemUse
        double belowBlockTop = placePos.getY();
        // 验证玩家是否处在目标位置上方
        if (player.posY >= belowBlockTop + 0.5) {
            float pitch = player.rotationPitch;
            if (pitch < 0) {
                return EnumActionResult.FAIL;
            }
        }
        else return EnumActionResult.FAIL; // 玩家是不在目标位置上方

        // 计算坐标
        double playerFeetY = player.posY; // 玩家脚部实际Y坐标（无需减0.5）
        BlockPos belowPos = placePos.down();
        belowBlockTop = belowPos.getY() + 1.0; // 下方方块顶部Y坐标

        // 条件判断
        boolean canPlace = 
            playerFeetY >= belowBlockTop + 0.5 && // 玩家高于下方方块顶部0.5格
            (world.isAirBlock(placePos) ||        // 目标位置是空气
            world.getBlockState(placePos).getBlock().isReplaceable(world, placePos)); // 或可替换

        if (canPlace) {
            // 执行放置
            if (world.setBlockState(placePos, slabState)) {
                player.getHeldItem(hand).shrink(1);
                Sound.playSound(world, placePos, "block.cherry_wood.place");
                return EnumActionResult.SUCCESS;
            }
        }

        return EnumActionResult.FAIL;
    }

    // 获取放置位置
    private BlockPos getPlacementPosition(World world, BlockPos clickPos, EnumFacing facing) {
        IBlockState clickedState = world.getBlockState(clickPos);
        return clickedState.getBlock().isReplaceable(world, clickPos) ? 
            clickPos : 
            clickPos.offset(facing);
    }

//燃料功能
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 150; //返回燃烧时间（单位：tick）
    }
}