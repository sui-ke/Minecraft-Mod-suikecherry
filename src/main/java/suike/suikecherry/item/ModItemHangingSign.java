package suike.suikecherry.item;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.ModBlockHangingSign;
import suike.suikecherry.block.ModBlockHangingSignAttached;
import suike.suikecherry.sound.Sound;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
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

//悬挂告示牌物品
public class ModItemHangingSign extends Item {
    public ModItemHangingSign(String name, ModBlockHangingSign block, ModBlockHangingSign blockInWall, ModBlockHangingSignAttached blockAttached) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置堆叠数量*/this.setMaxStackSize(16);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.DECORATIONS);
        block.setItem(this);
        blockInWall.setItem(this);
        blockAttached.setItem(this);
        this.setBlock(block, blockInWall, blockAttached);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    private Block block;
    private Block blockInWall;
    private Block blockAttached;
    private void setBlock(Block block, Block blockInWall, Block blockAttached) {
        this.block = block;
        this.blockInWall = blockInWall;
        this.blockAttached = blockAttached;
    }

//放置告示牌
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clickPos, EnumHand hand, EnumFacing clickFacing, float hitX, float hitY, float hitZ) {
        /*获取放置坐标*/BlockPos placePos = clickPos.offset(clickFacing);

        /*获取点击的方块状态*/IBlockState clickBlockState = world.getBlockState(clickPos);
        /*获取点击的方块*/Block clickBlock = clickBlockState.getBlock();

        // 吞方块
        boolean replaceBlock = clickBlock.isReplaceable(world, clickPos);
        if (replaceBlock) placePos = clickPos;

        // 不能放液体里
        if (world.isOutsideBuildHeight(placePos)) return EnumActionResult.FAIL;

        if (replaceBlock || world.isAirBlock(placePos)) {
            if (!player.isSneaking() && clickFacing == EnumFacing.DOWN) {
                BlockPos upBlockPos = placePos.up();
                IBlockState upBlockState = world.getBlockState(upBlockPos);
                Block upBlock = upBlockState.getBlock();
                String upBlockName = upBlock.getRegistryName().toString();

                //获取精确的元数据
                int meta = ItemBase.getPlayerFacing(player.rotationYaw);

                if (
                    upBlockName.contains("wall") ||
                    upBlockName.contains("fence") ||
                    upBlockName.contains("chain") ||
                    upBlockName.contains("hanging") ||
                    upBlock instanceof net.minecraft.block.BlockPane ||
                    upBlock == Blocks.END_ROD &&
                        (
                            upBlock.getMetaFromState(upBlockState) == 0 ||
                            upBlock.getMetaFromState(upBlockState) == 1
                        )
                   )
                {
                    // 锁链汇聚于一点的悬挂告示牌
                    placeBlock(player, world, placePos, hand, this.blockAttached, meta);
                    return EnumActionResult.SUCCESS;
                }

                //上方方块完整
                if (upBlockState.isSideSolid(world, upBlockPos, EnumFacing.DOWN)) {
                    if (meta != 0 && meta != 4 && meta != 8 && meta != 12) {
                        // 锁链汇聚于一点的悬挂告示牌
                        placeBlock(player, world, placePos, hand, this.blockAttached, meta);
                        return EnumActionResult.SUCCESS;
                    }

                    // 双锁链悬挂告示牌
                    /*获取新的元数据*/meta = player.getHorizontalFacing().getIndex();

                    placeBlock(player, world, placePos, hand, this.block, meta);
                    return EnumActionResult.SUCCESS;
                }
            }

            IBlockState signState = null;

            if (clickFacing == EnumFacing.UP || clickFacing == EnumFacing.DOWN) {
                //通过玩家朝向获取方块元数据
                int meta = player.getHorizontalFacing().getIndex();
                signState = this.blockInWall.getStateFromMeta(meta);
            } else {
                signState = this.blockInWall.getDefaultState().withProperty(ModBlockHangingSign.FACING, clickFacing.rotateY());
            }

            // 墙上的悬挂告示牌
            placeBlock(player, world, placePos, hand, signState);
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

    public void placeBlock(EntityPlayer player, World world, BlockPos pos, EnumHand hand, Block block, int meta) {
        this.placeBlock(player, world, pos, hand, block.getStateFromMeta(meta));
    }

    public void placeBlock(EntityPlayer player, World world, BlockPos pos, EnumHand hand, IBlockState signState) {
        /*放置方块*/world.setBlockState(pos, signState);

        /*消耗一个物品*/player.getHeldItem(hand).shrink(1);
        /*播放原木音效*/Sound.playSound(world, pos, "block.cherrywood.place");
    }
}