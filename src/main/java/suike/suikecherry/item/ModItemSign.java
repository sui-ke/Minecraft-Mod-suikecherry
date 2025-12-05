package suike.suikecherry.item;

import java.util.List;
import java.util.ArrayList;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.ModBlockSign;
import suike.suikecherry.block.ModBlockSignWall;
import suike.suikecherry.sound.Sound;

import net.minecraft.init.Items;
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

//告示牌物品
public class ModItemSign extends Item {
    public ModItemSign(String name, ModBlockSign block, ModBlockSignWall blockInWall) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置堆叠数量*/this.setMaxStackSize(16);
        //*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.DECORATIONS);
        block.setItem(this);
        blockInWall.setItem(this);
        this.setBlock(block, blockInWall);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
        /*添加到悬挂告示牌物品栏列表*/ModItemSign.SIGN_ITEMS.add(this);
    }

    private Block block;
    private ModBlockSignWall blockInWall;
    private void setBlock(Block block, ModBlockSignWall blockInWall) {
        this.block = block;
        this.blockInWall = blockInWall;
    }

// 放置告示牌
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

// 放置方块
    public void placeBlock(EntityPlayer player, World world, BlockPos pos, EnumHand hand, IBlockState signState) {
        /*放置方块*/world.setBlockState(pos, signState);

        /*消耗一个物品*/player.getHeldItem(hand).shrink(1);

        //播放音效
        Sound.playSound(world, pos, this.blockInWall.getType().getPlaceSound());
    }

// 燃料功能
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 200;
    }

// 物品提示
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tooltip.suikecherry.item.sign"));
    }

// 告示牌物品栏
    public static final List<Item> SIGN_ITEMS = new ArrayList<>();
    public static final SignTabs SIGN_TABS = new SignTabs("suikecherry.signTabs");

    public static void InventoryTabs() {
        SIGN_TABS.displayAllRelevantItems(NonNullList.create());
    }

    public static class SignTabs extends CreativeTabs {

        private SignTabs(String label) {
            super(label);
        }

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ItemBase.CHERRY_HANGING_SIGN);
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> items) {
            super.displayAllRelevantItems(items);

            items.add(new ItemStack(Items.SIGN));
            for (Item item : SIGN_ITEMS) {
                items.add(new ItemStack(item));
            }
        }
    }
}