package suike.suikecherry.expand.futuremc;

import java.lang.reflect.Constructor;

import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.BlockBase;


import thedarkcolour.futuremc.entity.bee.EntityBee;
import thedarkcolour.futuremc.block.buzzybees.BeeHiveBlock;
import thedarkcolour.futuremc.block.villagepillage.ComposterBlock;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

//未来的MC联动类
public class FuturemcExpand {
    public static void expand() {
        /*樱花树苗*/addComposter(ItemBase.CHERRY_SAPLING, 30);
        /*落英*/addComposter(ItemBase.PINK_PETALS, 30);
        /*樱花树叶*/addComposter(BlockBase.CHERRY_LEAVES, 30);

        /*落英*/addFlowersBlock("suikecherry:pink_petals", 15);
        /*樱花树叶*/addFlowersBlock("suikecherry:cherry_leaves", 3);
    }

//添加堆肥桶肥料原料
    public static void addComposter(Item item, int chance) {addComposter(new ItemStack(item), chance);}
    public static void addComposter(Block block, int chance) {addComposter(new ItemStack(block), chance);}
    public static void addComposter(ItemStack itemStack, int chance) {
        try {
            Constructor<ComposterBlock.ItemsForComposter> constructor = ComposterBlock.ItemsForComposter.class.getDeclaredConstructor();
            constructor.setAccessible(true); // 设置可访问性为true
            ComposterBlock.ItemsForComposter instance = constructor.newInstance();

            instance.add(itemStack, (byte) chance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//添加蜜蜂采蜜方块
    public static void addFlowersBlock(String blockName, int maxMeta) {
        for (int meta = 0; meta <= maxMeta; meta++) {
            IBlockState state = Block.getBlockFromName(blockName).getStateFromMeta(meta);
            EntityBee.FLOWERS.add(state);
        }
    }
}