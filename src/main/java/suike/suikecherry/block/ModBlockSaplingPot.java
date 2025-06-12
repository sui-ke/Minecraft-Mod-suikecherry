package suike.suikecherry.block;

import java.util.List;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.player.EntityPlayer;

//树苗盆栽类
public class ModBlockSaplingPot extends BlockFlowerPot {
    public ModBlockSaplingPot(String name) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置不透明度*/this.setLightOpacity(0);

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
    }

    private Item item;
    public void setItem(Item item) {
        if (this.item == null) this.item = item;
    }

//获取物品&掉落物
    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        //返回树苗的物品
        return new ItemStack(this.item);
    }
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {
        //返回树苗盆栽的掉落物
        drops.add(new ItemStack(Item.getByNameOrId("minecraft:flower_pot")));
        drops.add(new ItemStack(this.item));
    }

//交互
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        //检查玩家是否是右键
        if (hand == EnumHand.MAIN_HAND) {
            //替换当前方块为花盆
            world.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), 2);

            //给玩家一个樱花树苗
            if (!player.field_71075_bZ.isCreativeMode) {
                player.func_191521_c(new ItemStack(this.item));
            } else {
                ItemBase.giveCreativePlayerItem(player, new ItemStack(this.item));
            }

            return true;
        }
        return false;
    }
}