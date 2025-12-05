package suike.suikecherry.block;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.sound.Sound;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockButton;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;

// 按钮类
public class ModBlockButton extends BlockButton {
    public ModBlockButton(String name) {
        super(false);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.REDSTONE);
        /*设置硬度*/this.setHardness(0.5F);
        /*设置抗爆性*/this.setResistance(0.5F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(ModBlockPlanks.EnumType.getEnumType(name).getWoodSound());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

// 按钮交互
    @Override
    public void playClickSound(EntityPlayer player, World world, BlockPos pos) {
        /*播放点击声音*/Sound.playSound(world, pos, "block.cherry_button.click", 2.0F, 1.0F);
        /*持续30 ticks*/world.scheduleUpdate(pos, this, 30);
    }
    @Override
    public void playReleaseSound(World world, BlockPos pos) {
        /*播放点击声音*/Sound.playSound(world, pos, "block.cherry_button.restore", 2.0F, 1.0F);
    }
}