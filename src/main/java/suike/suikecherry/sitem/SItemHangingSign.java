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
    public void setBlock(Block block, Block blockInWall, Block blockAttached) {
        this.block = block;
        this.blockInWall = blockInWall;
        this.blockAttached = blockAttached;
    }

//放置告示牌
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clickPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        /*获取坐标*/BlockPos adjacentPos = clickPos.offset(facing);
        Block oldBlock = world.getBlockState(adjacentPos).getBlock();

        //通过玩家朝向获取方块元数据
        int meta = player.getHorizontalFacing().getIndex();

        if (facing == EnumFacing.DOWN &&
            (
                oldBlock == Blocks.AIR ||
                oldBlock instanceof net.minecraft.block.BlockLiquid
            )
           )
        {
            BlockPos upBlockPos = adjacentPos.up();
            IBlockState upBlockState = world.getBlockState(upBlockPos);
            Block upBlock = upBlockState.getBlock();
            String upBlockName = upBlock.getRegistryName().toString();

            if (upBlockState.isSideSolid(world, upBlockPos, EnumFacing.DOWN)) {
                放置方块(player, world, adjacentPos, hand, this.block, meta);
                return EnumActionResult.SUCCESS;

            } else if (
                // 锁链汇聚于一点的告示牌
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
                /*获取新的元数据*/meta = ItemBase.getPlayerFacing(player.rotationYaw);

                放置方块(player, world, adjacentPos, hand, this.blockAttached, meta);
                return EnumActionResult.SUCCESS;
            }
        }

        if (oldBlock == Blocks.AIR || oldBlock instanceof net.minecraft.block.BlockLiquid) {
            放置方块(player, world, adjacentPos, hand, this.blockInWall, meta);
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