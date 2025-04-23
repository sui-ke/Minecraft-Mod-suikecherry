package suike.suikecherry.sitem;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sblock.BlockBase;
import suike.suikecherry.sound.Sound;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import suike.suikecherry.suikecherry.Tags;

//门物品
public class SItemDoor extends Item implements SItem {
    public SItemDoor(String name, CreativeTabs tabs) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setTranslationKey(name + "_" + Tags.MOD_ID);
        /*设置创造模式物品栏*/setCreativeTab(tabs);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    private Block block;
    protected void setBlock(Block block) {
        this.block = block;
    }

//使用物品
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL; //仅允许在上方放置
        }

        Block clickBlock = world.getBlockState(pos).getBlock();

        if (!clickBlock.isReplaceable(world, pos)) {
            pos = pos.offset(facing);
        }

        if (this.block.canPlaceBlockAt(world, pos)) {
            /*获取玩家方向*/EnumFacing enumfacing = EnumFacing.fromAngle(player.rotationYaw);
            /*确定铰链位置*/boolean isRightHinge = determineHingePosition(enumfacing, hitX, hitZ);
            /*放置门*/placeDoor(world, pos, enumfacing, this.block, isRightHinge);

            /*消耗一个物品*/player.getHeldItem(hand).shrink(1);
            /*播放原木音效*/Sound.playSound(world, pos, "block.cherrywood.place");

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL; //放置失败
    }

//确定铰链位置
    private boolean determineHingePosition(EnumFacing facing, float hitX, float hitZ) {
        boolean isRightHinge;

        if (facing == EnumFacing.SOUTH) {//面朝南方
            isRightHinge = hitX < 0.5;
        } else if (facing == EnumFacing.WEST) {//面朝西方
            isRightHinge = hitZ < 0.5;
        } else if (facing == EnumFacing.EAST) {//面朝东方
            isRightHinge = hitZ > 0.5;
        } else {
            isRightHinge = hitX > 0.5;//面朝北方
        }

        //根据点击位置返回铰链位置
        return isRightHinge;
    }

//放置门
    private static void placeDoor(World world, BlockPos pos, EnumFacing facing, Block door, boolean isRightHinge) {
        BlockPos blockpos2 = pos.up();
        boolean flag2 = world.isBlockPowered(pos) || world.isBlockPowered(blockpos2);
        IBlockState iblockstate = door.getDefaultState()
                .withProperty(BlockDoor.FACING, facing)
                .withProperty(BlockDoor.HINGE, isRightHinge ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT)
                .withProperty(BlockDoor.POWERED, flag2)
                .withProperty(BlockDoor.OPEN, flag2);

        world.setBlockState(pos, iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 2);
        world.setBlockState(blockpos2, iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2);
        world.notifyNeighborsOfStateChange(pos, door, false);
        world.notifyNeighborsOfStateChange(blockpos2, door, false);
    }

//燃料功能
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 200; //返回燃烧时间（单位：tick）
    }
}