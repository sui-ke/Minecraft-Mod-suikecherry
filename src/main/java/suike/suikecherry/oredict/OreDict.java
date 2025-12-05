package suike.suikecherry.oredict;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.BlockBase;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;

import net.minecraftforge.oredict.OreDictionary;

//添加块矿词
public class OreDict {
    public static void oreDict() {
        /*樱花树苗*/oreDictAdd("treeSapling", ItemBase.CHERRY_SAPLING);
        /*樱花树叶*/oreDictAdd("treeLeaves", BlockBase.CHERRY_LEAVES);
        /*樱花木板*/oreDictAdd("plankWood", BlockBase.CHERRY_PLANKS);
        /*樱花木楼梯*/oreDictAdd("stairWood", BlockBase.CHERRY_STAIRS);
        /*樱花木台阶*/oreDictAdd("slabWood", ItemBase.CHERRY_SLAB);
        /*樱花木门*/oreDictAdd("doorWood", ItemBase.CHERRY_DOOR);
        /*樱花木活板门*/oreDictAdd("trapDoorWood", BlockBase.CHERRY_TRAPDOOR);
        /*樱花木栅栏*/oreDictAdd("fenceWood", BlockBase.CHERRY_FENCE);
        /*樱花木栅栏门*/oreDictAdd("fenceGateWood", BlockBase.CHERRY_FENCEGATE);
        /*樱花木按钮*/oreDictAdd("buttonWood", BlockBase.CHERRY_BUTTON);
        /*樱花木压力板*/oreDictAdd("pressurePlateWood", BlockBase.CHERRY_PRESSURE_PLATE);
        /*樱花木船*/oreDictAdd("boat", ItemBase.CHERRY_BOAT);
        /*樱花原木*/oreDictAdd("logWood", BlockBase.CHERRY_LOG);
        /*樱花原木*/oreDictAdd("logCherry", BlockBase.CHERRY_LOG);
        /*樱花木头*/oreDictAdd("logWood", BlockBase.CHERRY_WOOD);
        /*樱花木头*/oreDictAdd("logCherry", BlockBase.CHERRY_WOOD);
        /*樱花去皮原木*/oreDictAdd("logWood", BlockBase.CHERRY_STRIPPED_LOG);
        /*樱花去皮原木*/oreDictAdd("logCherry", BlockBase.CHERRY_STRIPPED_LOG);
        /*樱花去皮原木*/oreDictAdd("logStrippedCherry", BlockBase.CHERRY_STRIPPED_LOG);
        /*樱花去皮木头*/oreDictAdd("logWood", BlockBase.CHERRY_STRIPPED_WOOD);
        /*樱花去皮木头*/oreDictAdd("logStrippedCherry", BlockBase.CHERRY_STRIPPED_WOOD);
        /*樱花去皮木头*/oreDictAdd("logCherry", BlockBase.CHERRY_STRIPPED_WOOD);

        oreDictAdd("itemResin", Items.SLIME_BALL);

        oreDictAdd("plankWoodBamboo", Item.getByNameOrId("futuremc:bamboo"));
        oreDictAdd("plankWoodBamboo", Item.getByNameOrId("biomesoplenty:bamboo_thatching"));
        oreDictAdd("plankWoodBamboo", Item.getByNameOrId("sakura:plank_bamboo"));

        oreDictAdd("sandstone", Item.getByNameOrId("futuremc:smooth_sandstone"));
    }

//添加矿词方法
    public static void oreDictAdd(String type, Item item) {
        if (item == null) return;
        oreDictAdd(type, new ItemStack(item));
    }
    public static void oreDictAdd(String type, Block block) {
        if (block == null) return;
        oreDictAdd(type, new ItemStack(block));
    }
    public static void oreDictAdd(String type, ItemStack itemStack) {
        if (ItemBase.isValidItemStack(itemStack)) {
            OreDictionary.registerOre(type, itemStack);
        }
    }
}