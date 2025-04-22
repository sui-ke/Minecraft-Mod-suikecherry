package suike.suikecherry.sitem;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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

//悬挂告示牌物品
public class SItemHangingSign extends Item implements SItem {
    public SItemHangingSign(String name, CreativeTabs tabs) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置堆叠数量*/setMaxStackSize(16);
        /*设置创造模式物品栏*/setCreativeTab(tabs);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    private Block block;
    private Block blockInWall;
    private Block blockAttached;
    protected void setBlock(Block block, Block blockInWall, Block blockAttached) {
        this.block = block;
        this.blockInWall = blockInWall;
        this.blockAttached = blockAttached;
    }

//放置告示牌
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clickPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        /*获取放置坐标*/BlockPos placeBlockPos = clickPos.offset(facing);

        /*获取点击的方块状态*/IBlockState clickBlockState = world.getBlockState(clickPos);
        /*获取点击的方块*/Block clickBlock = clickBlockState.getBlock();

        //防吞方块
        boolean replaceBlock = false;
        if (clickBlock == Blocks.TALLGRASS || (clickBlock == Blocks.SNOW_LAYER && clickBlock.getMetaFromState(clickBlockState) == 0)) {
            replaceBlock = true;
            placeBlockPos = clickPos;
        }

        Block oldBlock = world.getBlockState(placeBlockPos).getBlock();

        if (replaceBlock || oldBlock == Blocks.AIR || oldBlock instanceof net.minecraft.block.BlockLiquid) {
            if (!player.isSneaking() && facing == EnumFacing.DOWN) {
                BlockPos upBlockPos = placeBlockPos.up();
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
                    放置方块(player, world, placeBlockPos, hand, this.blockAttached, meta);
                    return EnumActionResult.SUCCESS;
                }

                //上方方块完整
                if (upBlockState.isSideSolid(world, upBlockPos, EnumFacing.DOWN)) {
                    if (meta != 0 && meta != 4 && meta != 8 && meta != 12) {
                        // 锁链汇聚于一点的悬挂告示牌
                        放置方块(player, world, placeBlockPos, hand, this.blockAttached, meta);
                        return EnumActionResult.SUCCESS;
                    }

                    // 双锁链悬挂告示牌
                    /*获取新的元数据*/meta = player.getHorizontalFacing().getIndex();

                    放置方块(player, world, placeBlockPos, hand, this.block, meta);
                    return EnumActionResult.SUCCESS;
                }
            }

            //普通放置
            //通过玩家朝向获取方块元数据
            int meta = player.getHorizontalFacing().getIndex();

            // 墙上的悬挂告示牌
            放置方块(player, world, placeBlockPos, hand, this.blockInWall, meta);
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

    public void 放置方块(EntityPlayer player, World world, BlockPos pos, EnumHand hand, Block block, int meta) {
        /*获取方块状态*/IBlockState signState = block.getStateFromMeta(meta);
        /*放置方块*/world.setBlockState(pos, signState);

        /*消耗一个物品*/player.getHeldItem(hand).shrink(1);
        /*播放原木音效*/Sound.playSound(world, pos, "block.cherrywood.place");
    }
}