package suike.suikecherry.item;

import java.util.List;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.ModBlockHangingSign;
import suike.suikecherry.block.ModBlockHangingSignAttached;
import suike.suikecherry.sound.Sound;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;

//悬挂告示牌物品
public class ModItemHangingSign extends Item {
    public ModItemHangingSign(String name, ModBlockHangingSign block, ModBlockHangingSign blockInWall, ModBlockHangingSignAttached blockAttached) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置堆叠数量*/this.setMaxStackSize(16);
        //*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.DECORATIONS);
        block.setItem(this);
        blockInWall.setItem(this);
        blockAttached.setItem(this);
        this.setBlock(block, blockInWall, blockAttached);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
        /*添加到悬挂告示牌物品栏列表*/ModItemSign.SIGN_ITEMS.add(this);
    }

    private Block block;
    private Block blockInWall;
    private ModBlockHangingSignAttached blockAttached;
    private void setBlock(Block block, Block blockInWall, ModBlockHangingSignAttached blockAttached) {
        this.block = block;
        this.blockInWall = blockInWall;
        this.blockAttached = blockAttached;
    }

// 放置告示牌
    private static final List<Integer> validOrientations = Lists.newArrayList(0, 4, 8, 12);

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
            EnumFacing playerFacing = player.getHorizontalFacing();

            if (clickFacing == EnumFacing.DOWN) {
                BlockPos upBlockPos = placePos.up();
                IBlockState upBlockState = world.getBlockState(upBlockPos);
                Block upBlock = upBlockState.getBlock();
                String upBlockName = upBlock.getRegistryName().toString();

                // 通过 玩家精确朝向 获取元数据
                int meta = ItemBase.getPlayerFacing(player.rotationYaw);

                if (!player.isSneaking() && upBlockName.contains("hanging") && this.canPlaceHangingSign(playerFacing, upBlock, upBlockState, meta)) {
                    // 双锁链悬挂告示牌 放置

                    // 获取新的元数据放置
                    placeBlock(player, world, placePos, hand, this.block, playerFacing.getIndex());
                    return EnumActionResult.SUCCESS;
                }

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
                    // 锁链汇聚于一点 悬挂告示牌
                    placeBlock(player, world, placePos, hand, this.blockAttached, meta);
                    return EnumActionResult.SUCCESS;
                }

                // 上方方块完整
                if (upBlockState.isSideSolid(world, upBlockPos, EnumFacing.DOWN)) {
                    // 不在精确方向列表
                    if (!validOrientations.contains(meta)) {
                        // 非正东南西北, 放置 旋转的 锁链汇聚于一点 悬挂告示牌
                        placeBlock(player, world, placePos, hand, this.blockAttached, meta);
                        return EnumActionResult.SUCCESS;
                    }
                    // 双锁链悬挂 告示牌放置

                    // 获取新的元数据放置
                    placeBlock(player, world, placePos, hand, this.block, playerFacing.getIndex());
                    return EnumActionResult.SUCCESS;
                }
            }

            IBlockState signState = null;
            String clickBlockName = clickBlock.getRegistryName().toString();

            if (
                ( // 排除无连接功能的非完整方块
                    !clickBlock.isFullCube(clickBlockState) &&
                    !clickBlockName.contains("wall") &&
                    !clickBlockName.contains("fence") &&
                    !clickBlockName.contains("chain")
                ) ||
                (clickFacing == EnumFacing.UP || clickFacing == EnumFacing.DOWN)
            ) {
                // 通过玩家朝向获取方块元数据
                signState = this.blockInWall.getStateFromMeta(playerFacing.getIndex());
            }
            else {
                // 旋转 90度 放置
                signState = this.blockInWall.getDefaultState().withProperty(ModBlockHangingSign.FACING, clickFacing.rotateY());
            }

            // 墙上的悬挂告示牌
            placeBlock(player, world, placePos, hand, signState);
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

    // 是否能放置双锁链告示牌
    public boolean canPlaceHangingSign(EnumFacing playerFacing, Block upBlock, IBlockState upBlockState, int playerAccurateFacing) {
        if (upBlock instanceof ModBlockHangingSign) {
            if (validOrientations.contains(playerAccurateFacing)) {
                EnumFacing blockFacing = upBlockState.getValue(ModBlockHangingSign.FACING);
                return playerFacing.getAxis() == blockFacing.getAxis();
            }
        }
        else if (upBlock instanceof ModBlockHangingSignAttached) {
            int blockFacing = upBlock.getMetaFromState(upBlockState);

            if (blockFacing == 0 || blockFacing == 8) {
                return playerAccurateFacing == 0 || playerAccurateFacing == 8;
            }
            if (blockFacing == 4 || blockFacing == 12) {
                return playerAccurateFacing == 4 || playerAccurateFacing == 12;
            }
        }
        return false;
    }

// 放置方块
    public void placeBlock(EntityPlayer player, World world, BlockPos pos, EnumHand hand, Block block, int meta) {
        this.placeBlock(player, world, pos, hand, block.getStateFromMeta(meta));
    }
    public void placeBlock(EntityPlayer player, World world, BlockPos pos, EnumHand hand, IBlockState signState) {
        /*放置方块*/world.setBlockState(pos, signState);

        /*消耗一个物品*/player.getHeldItem(hand).shrink(1);

        //播放音效
        Sound.playSound(world, pos, this.blockAttached.getType().getPlaceSound());
    }

// 燃料功能
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 800;
    }

// 物品提示
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tooltip.suikecherry.item.sign"));
    }
}