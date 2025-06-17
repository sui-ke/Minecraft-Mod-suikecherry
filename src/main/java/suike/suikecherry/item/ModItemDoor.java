package suike.suikecherry.item;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.ModBlockDoor;
import suike.suikecherry.sound.Sound;

import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;

//门物品
public class ModItemDoor extends ItemDoor {
    public ModItemDoor(String name, ModBlockDoor block) {
        super(block);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        block.setItem(this);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

//燃料功能
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 200; //返回燃烧时间（单位：tick）
    }
}