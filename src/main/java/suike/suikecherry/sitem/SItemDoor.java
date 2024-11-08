package suike.suikecherry.sitem;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sblock.BlockBase;

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
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;

//门物品
public class SItemDoor extends Item implements SItem {
    public SItemDoor(String name, CreativeTabs tabs) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/setCreativeTab(tabs);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    private Block block; //门对应的方块
    public void setBlock(Block block) {
        this.block = block; //设置门的方块
    }

//使用物品
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL; //仅允许在上方放置
        }

        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(world, pos)) {
            pos = pos.offset(facing); //调整放置位置
        }

        ItemStack itemstack = player.getHeldItem(hand);
        if (this.block.canPlaceBlockAt(world, pos)) {
            EnumFacing enumfacing = EnumFacing.fromAngle(player.rotationYaw);
            /*确定铰链位置*/boolean isRightHinge = determineHingePosition(pos, enumfacing, hitX, hitZ);
            /*放置门*/placeDoor(world, pos, enumfacing, this.block, isRightHinge);
            /*消耗一个物品*/itemstack.shrink(1);
            //*播放原木音效*/world.playSound(null, pos, new SoundEvent(new ResourceLocation(SuiKe.MODID, "block.cherrywood.place" + ((int) (Math.random() * 5) + 1))), SoundCategory.BLOCKS, 1.0F, 1.0F);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL; //放置失败
    }

//确定铰链位置
    private boolean determineHingePosition(BlockPos pos, EnumFacing facing, float hitX, float hitZ) {
        BlockPos blockpos = pos.offset(facing.rotateY());
        BlockPos blockpos1 = pos.offset(facing.rotateYCCW());
        
        //计算点击位置相对于方块中心的偏移
        boolean isRightHinge = hitX > 0.5;//面朝北方

        if (facing == EnumFacing.SOUTH) {//面朝南方
            isRightHinge = !isRightHinge;
        } else if (facing == EnumFacing.WEST) {//面朝西方
            isRightHinge = hitZ < 0.5;
        } else if (facing == EnumFacing.EAST) {//面朝东方
            isRightHinge = hitZ > 0.5;
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