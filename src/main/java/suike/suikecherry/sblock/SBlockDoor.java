package suike.suikecherry.sblock;

import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.sound.SoundTypeWood;

import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import suike.suikecherry.suikecherry.Tags;

//门类
public class SBlockDoor extends BlockDoor implements SBlock {
    public SBlockDoor(String name) {
        /*创建方块实例*/super(Material.WOOD);
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setTranslationKey(name + "_" + Tags.MOD_ID);
        /*设置硬度*/setHardness(3.0F);
        /*设置抗爆性*/setResistance(3.0F);
        /*设置挖掘等级*/setHarvestLevel("axe", 0);
        /*设置不透明度*/setLightOpacity(0);
        /*设置声音*/setSoundType(new SoundTypeWood());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
    }

    private Item item;
    protected void setItem(Item item) {
        this.item = item;
    }

//获取物品&掉落物
    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        //返回门的物品
        return new ItemStack(this.item);
    }
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        //返回门的掉落物
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : this.item;
    }
}