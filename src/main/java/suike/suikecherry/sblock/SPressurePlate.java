package suike.suikecherry.sblock;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.sound.SoundTypeWood;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

//压力板类
public class SPressurePlate extends BlockPressurePlate implements SBlock {
    public SPressurePlate(String name) {
        /*创建方块实例*/super(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING);
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/setCreativeTab(CreativeTabs.REDSTONE);
        /*设置硬度*/setHardness(0.5F);
        /*设置抗爆性*/setResistance(0.5F);
        /*设置挖掘等级*/setHarvestLevel("axe", 0);
        /*设置不透明度*/setLightOpacity(0);
        /*设置声音*/setSoundType(new SoundTypeWood());

        /*打开更新*/this.setTickRandomly(true);
        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/ItemBase.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }
}