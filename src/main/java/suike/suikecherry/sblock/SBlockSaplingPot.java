package suike.suikecherry.sblock;

import java.util.List;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;

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
import suike.suikecherry.suikecherry.Tags;

//树苗盆栽类
public class SBlockSaplingPot extends BlockFlowerPot {
    public SBlockSaplingPot(String name) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setTranslationKey(name + "_" +  Tags.MOD_ID);
        /*设置不透明度*/setLightOpacity(0);

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
    }

    private Item item;
    protected void setItem(Item item) {
        this.item = item;
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
            Block.spawnAsEntity(world, player.getPosition().add(0, 1, 0), new ItemStack(ItemBase.CHERRY_SAPLING));

            return true;
        }
        return false;
    }
}