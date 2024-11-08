package suike.suikecherry.sblock;

import java.lang.reflect.Field;

import suike.suikecherry.SuiKe;

import thedarkcolour.core.block.FBlock.Properties;
import thedarkcolour.futuremc.block.buzzybees.BeeHiveBlock;

import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

//蜂巢类
public class SBeeHive {
    public static Block beeHive(String name) {
        /*创建方块实例*/
        Block block = new BeeHiveBlock(
            new Properties(Material.WOOD, name)
                .hardnessAndResistance(0.6f, 0.6f)
                .sound(SoundType.WOOD)
                //.group(CreativeTabs.DECORATIONS)
        );

        /*设置物品名key*/block.setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置不透明度*/block.setLightOpacity(0);

        return block;
    }
}