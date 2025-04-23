package suike.suikecherry.sblock;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.sound.SoundTypeWood;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.BlockStairs;
import net.minecraft.creativetab.CreativeTabs;
import suike.suikecherry.suikecherry.Tags;

//楼梯类
public class SStairs extends BlockStairs implements SBlock {
    public SStairs(String name) {
        /*创建方块实例*/super(BlockBase.CHERRY_PLANKS.getDefaultState());
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setTranslationKey(name + "_" + Tags.MOD_ID);
        /*设置创造模式物品栏*/setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        /*设置硬度*/setHardness(2.0F);
        /*设置抗爆性*/setResistance(5.0F);
        /*设置挖掘等级*/setHarvestLevel("axe", 0);
        /*设置不透明度*/setLightOpacity(0);
        /*设置声音*/setSoundType(new SoundTypeWood());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/ItemBase.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }
}