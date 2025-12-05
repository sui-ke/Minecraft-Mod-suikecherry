package suike.suikecherry.item;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.block.ModBlockLeaves;
import suike.suikecherry.block.ModBlockSapling;
import suike.suikecherry.block.ModBlockSaplingPot;
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
public class ModItemSapling extends Item {
    public ModItemSapling(String name, ModBlockSapling sapling) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setBlock(sapling);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    public ModItemSapling(String name, ModBlockSapling sapling, ModBlockSaplingPot saplingPot, ModBlockLeaves leaves) {
        this(name, sapling);
        this.setBlockPot(saplingPot);

        sapling.setItem(this);
        saplingPot.setItem(this);
        leaves.setSapling(this);
    }

    private Block sapling; //树苗
    private void setBlock(ModBlockSapling sapling) {
        this.sapling = sapling;
    }
    private Block saplingPot; //树苗盆栽
    private void setBlockPot(ModBlockSaplingPot saplingPot) {
        this.saplingPot = saplingPot;
    }

//使用物品
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clickPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        /*点击方块的状态*/IBlockState clickBlockState = world.getBlockState(clickPos);
        /*点击的方块*/Block clickBlock = clickBlockState.getBlock();

        if (clickBlock == Blocks.FLOWER_POT && this.saplingPot != null) {
            // 替换花盆为树苗盆栽
            world.setBlockState(clickPos, this.saplingPot.getDefaultState(), 2);
            // 消耗一个物品
            player.getHeldItem(hand).shrink(1);
            return EnumActionResult.SUCCESS;
        }

        // 获取放置坐标
        BlockPos placePos = clickPos.offset(facing);
        boolean replaceBlock = clickBlock.isReplaceable(world, clickPos);
        if (replaceBlock) placePos = clickPos;

        // 不能放液体里
        if (world.isOutsideBuildHeight(placePos)) return EnumActionResult.FAIL;

        if (downBlockIsGrass(world, placePos)) {
            if (replaceBlock || world.isAirBlock(placePos)) {
                /*放置树苗*/placeSapling(world, placePos, this.sapling);
                /*播放音效*/Sound.playSound(world, placePos, "block.cherry_leaves.place");
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