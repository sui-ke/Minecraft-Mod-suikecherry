package suike.suikecherry.block;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.sound.SoundTypeWood;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;

//栅栏类
public class ModBlockFence extends BlockFence {
    public ModBlockFence(String name) {
        /*创建方块实例*/super(Material.WOOD, MapColor.WOOD);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.DECORATIONS);
        /*设置硬度*/this.setHardness(2.0F);
        /*设置抗爆性*/this.setResistance(3.0F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(new SoundTypeWood());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }
}