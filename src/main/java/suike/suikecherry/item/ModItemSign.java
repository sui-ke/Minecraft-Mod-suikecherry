package suike.suikecherry.item;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.ModBlockSign;
import suike.suikecherry.block.ModBlockSignWall;
import suike.suikecherry.sound.Sound;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumActionResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.creativetab.CreativeTabs;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.network.play.server.SPacketTitle;

//告示牌物品
public class ModItemSign extends ItemSign {
    public ModItemSign(String name, ModBlockSign block, ModBlockSignWall blockInWall) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置堆叠数量*/this.setMaxStackSize(16);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.DECORATIONS);
        block.setItem(this);
        blockInWall.setItem(this);
        this.setBlock(block, blockInWall);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    private Block block;
    private Block blockInWall;
    private void setBlock(Block block, Block blockInWall) {
        this.block = block;
        this.blockInWall = blockInWall;
    }

//放置告示牌
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clickPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        /*获取坐标*/BlockPos placePos = clickPos.offset(facing);

        /*获取点击的方块状态*/IBlockState clickBlockState = world.getBlockState(clickPos);
        /*获取点击的方块*/Block clickBlock = clickBlockState.getBlock();

        // 吞方块
        boolean replaceBlock = clickBlock.isReplaceable(world, clickPos);
        if (replaceBlock) placePos = clickPos;

        // 不能放液体里
        if (world.isOutsideBuildHeight(placePos)) return EnumActionResult.FAIL;

        if (replaceBlock || (facing != EnumFacing.DOWN && world.isAirBlock(placePos))) {

            if (replaceBlock || facing == EnumFacing.UP) {
                int meta = ItemBase.getPlayerFacing(player.rotationYaw);
                IBlockState signState = this.block.getStateFromMeta(meta);

                placeBlock(player, world, placePos, hand, signState);
                return EnumActionResult.SUCCESS;
            } else {
                int meta = facing.getIndex();
                IBlockState signState = this.blockInWall.getStateFromMeta(meta);

                placeBlock(player, world, placePos, hand, signState);
                return EnumActionResult.SUCCESS;
            }
        }

        return EnumActionResult.FAIL;
    }

    public void placeBlock(EntityPlayer player, World world, BlockPos pos, EnumHand hand, IBlockState signState) {
        /*放置方块*/world.setBlockState(pos, signState);

        /*消耗一个物品*/player.getHeldItem(hand).shrink(1);
        /*播放原木音效*/Sound.playSound(world, pos, "block.cherry_wood.place");
    }
}