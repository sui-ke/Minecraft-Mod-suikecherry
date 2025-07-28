package suike.suikecherry.block;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.creativetab.CreativeTabs;

//栅栏门类
public class ModBlockFenceGate extends BlockFenceGate {
    public ModBlockFenceGate(String name) {
        super(EnumType.OAK);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.REDSTONE);
        /*设置硬度*/this.setHardness(2.0F);
        /*设置抗爆性*/this.setResistance(3.0F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(ModBlockPlanks.EnumType.getEnumType(name).getWoodSound());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }
}