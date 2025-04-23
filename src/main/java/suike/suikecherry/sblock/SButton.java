package suike.suikecherry.sblock;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.sound.SoundTypeWood;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.BlockButton;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import suike.suikecherry.suikecherry.Tags;

//按钮类
public class SButton extends BlockButton implements SBlock {
    public SButton(String name) {
        /*创建方块实例*/super(false);
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setTranslationKey(name + "_" + Tags.MOD_ID);
        /*设置创造模式物品栏*/setCreativeTab(CreativeTabs.REDSTONE);
        /*设置硬度*/setHardness(0.5F);
        /*设置抗爆性*/setResistance(0.5F);
        /*设置挖掘等级*/setHarvestLevel("axe", 0);
        /*设置不透明度*/setLightOpacity(0);
        /*设置声音*/setSoundType(new SoundTypeWood());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/ItemBase.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

//按钮交互
    @Override
    public void playClickSound(EntityPlayer player, World world, BlockPos pos) {
        /*播放点击声音*/Sound.playSound(world, pos, "block.cherrybutton.click", 2.0F, 1.0F);
        /*持续30 ticks*/world.scheduleUpdate(pos, this, 30);
    }
    @Override
    public void playReleaseSound(World world, BlockPos pos) {
        /*播放点击声音*/Sound.playSound(world, pos, "block.cherrybutton.restore", 2.0F, 1.0F);
    }
}