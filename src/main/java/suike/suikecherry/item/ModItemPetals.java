package suike.suikecherry.item;

import java.util.List;
import java.util.ArrayList;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.block.ModBlockPetals;
import suike.suikecherry.sound.Sound;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;

//落英物品
public class ModItemPetals extends ItemBlock {
    public ModItemPetals(ModBlockPetals block) {
        super(block);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.DECORATIONS);
        block.setItem(this);
        itemsToRemove.add(this);
    }

// 使用物品
    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        return true;
    }
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clickPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // 点击方块的状态
        IBlockState clickBlockState = world.getBlockState(clickPos);
        // 点击的方块
        Block clickBlock = clickBlockState.getBlock();

        if (clickBlock == this.block) {
            if (this.upPetals(player, world, clickPos, hand, clickBlockState)) {
                return EnumActionResult.SUCCESS;
            }
        }

        // 放置坐标
        BlockPos placePos = clickPos.offset(facing);

        // 替换
        boolean replaceBlock = clickBlock.isReplaceable(world, clickPos);
        if (replaceBlock) placePos = clickPos;

        if (world.getBlockState(placePos).getBlock() == this.block) {
            // 点击其他方块升级落英
            if (this.upPetals(player, world, placePos, hand, world.getBlockState(placePos))) {
                return EnumActionResult.SUCCESS;
            }
            else return EnumActionResult.FAIL;
        }

        // 是否为可放置方块
        if (!ModBlockPetals.downBlockIsGrass(world, placePos)) return EnumActionResult.FAIL;

        // 不能放液体里
        if (world.isOutsideBuildHeight(placePos)) return EnumActionResult.FAIL;

        // 是空气, 或是可替换方块
        if (world.isAirBlock(placePos) || replaceBlock) {
            //获取玩家朝向
            EnumFacing playerFacing = player.getHorizontalFacing();

            int axis = 1;
            if (playerFacing == EnumFacing.EAST) axis = 2;
            else if (playerFacing == EnumFacing.SOUTH) axis = 3;
            else if (playerFacing == EnumFacing.WEST) axis = 4;

            IBlockState state = this.block.getDefaultState().withProperty(ModBlockPetals.AXIS, axis);

            /*放置方块*/this.placeBlock(player, world, placePos, hand, state);
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

    public boolean upPetals(EntityPlayer player, World world, BlockPos petalsPos, EnumHand hand, IBlockState clickBlockState) {
        // 升级落英
        int level = clickBlockState.getValue(ModBlockPetals.LEVEL);

        // 检查等级是否已是最大值
        if (level >= 4) return false;

        IBlockState state = clickBlockState.withProperty(ModBlockPetals.LEVEL, (level + 1));

        //放置方块
        this.placeBlock(player, world, petalsPos, hand, state);

        return true;
    }

// 放置落英
    public void placeBlock(EntityPlayer player, World world, BlockPos pos, EnumHand hand, IBlockState state) {
        world.setBlockState(pos, state, 2);
        player.getHeldItem(hand).shrink(1);
        //播放树叶音效
        Sound.playSound(world, pos, "block.cherry_leaves.place");
    }

// jei
    public static List<Item> itemsToRemove = new ArrayList<>(); // 物品移除列表

    @JEIPlugin
    public static class PetalsJEIPlugin implements IModPlugin {
        @Override
        public void register(IModRegistry registry) {
            // 获取JEI帮助器
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();

            if (jeiHelpers != null) {
                for (Item item : itemsToRemove) {
                    for (int meta = 1; meta < 16; meta++) {
                        // 将物品添加到JEI黑名单
                        jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(item, 1, meta));
                    }
                }
            }

            itemsToRemove = null;
        }
    }
}