package suike.suikecherry.sblock;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.sound.*;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class BlockBase extends Block implements SBlock {
//方块
    public static final List<Block> BLOCKS = new ArrayList<>();

    /*樱花树苗*/public static final Block CHERRY_SAPLING = new SBlockSapling("cherry_sapling");
    /*樱花树苗盆栽*/public static final Block CHERRY_SAPLING_POT = new SBlockSaplingPot("cherry_sapling_pot");
    /*落英*/public static final Block PINK_PETALS = new SBlockPetals("pink_petals");
    /*樱花原木*/public static final Block CHERRY_LOG = new SLog("cherry_log");
    /*樱花木头*/public static final Block CHERRY_WOOD = new SLog("cherry_wood");
    /*樱花去皮原木*/public static final Block CHERRY_STRIPPED_LOG = new SLog("cherry_stripped_log");
    /*樱花去皮木头*/public static final Block CHERRY_STRIPPED_WOOD = new SLog("cherry_stripped_wood");
    /*樱花树叶*/public static final Block CHERRY_LEAVES = new SLeaves("cherry_leaves");
    /*樱花木板*/public static final Block CHERRY_PLANKS = new BlockBase("cherry_planks", CreativeTabs.BUILDING_BLOCKS);
    /*樱花木楼梯*/public static final Block CHERRY_STAIRS = new SStairs("cherry_stairs");
    /*樱花木单层台阶*/public static final Block CHERRY_SLAB = new SBlockSlab("cherry_slab", false);
    /*樱花木双层台阶*/public static final Block CHERRY_SLAB_DOUBLE = new SBlockSlab("cherry_slab_double", true);
    /*樱花木门*/public static final Block CHERRY_DOOR = new SBlockDoor("cherry_door");
    /*樱花木活板门*/public static final Block CHERRY_TRAPDOOR = new STrapDoor("cherry_trapdoor");
    /*樱花木栅栏*/public static final Block CHERRY_FENCE = new SFence("cherry_fence");
    /*樱花木栅栏门*/public static final Block CHERRY_FENCEGATE = new SFenceGate("cherry_fence_gate");
    /*樱花木按钮*/public static final Block CHERRY_BUTTON = new SButton("cherry_button");
    /*樱花木压力板*/public static final Block CHERRY_PRESSURE_PLATE = new SPressurePlate("cherry_pressure_plate");

    /*樱花木蜂巢*/public static boolean cherryBeehive;
    /*樱花木蜂巢*/public static Block CHERRY_BEEHIVE = null;
    public static void beeHive() {
        if (ConfigValue.addCherryBeehive && Examine.FuturemcID) {
            CHERRY_BEEHIVE = SBeeHive.beeHive("cherry_beehive");

            /*添加到BLOCKS列表*/BLOCKS.add(CHERRY_BEEHIVE);
            /*添加到ITEMS列表*/ItemBase.ITEMS.add(new ItemBlock(CHERRY_BEEHIVE).setRegistryName(CHERRY_BEEHIVE.getRegistryName()));
        }
    }

//构造函数
    public BlockBase(String name, CreativeTabs tabs) {
        /*创建方块实例*/super(Material.WOOD);
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/setCreativeTab(tabs); 
        /*设置硬度*/setHardness(2.0F);
        /*设置抗爆性*/setResistance(5.0F);
        /*设置挖掘等级*/setHarvestLevel("axe", 0);
        /*设置不透明度*/setLightOpacity(255);
        if (name.equals("cherry_planks")) {
            /*设置声音*/setSoundType(new SoundTypeWood());
        } else {
            /*清除声音*/setSoundType(new SoundTypeNone());
        }

        /*添加到BLOCKS列表*/BLOCKS.add(this);
        /*添加到ITEMS列表*/ItemBase.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

//注册方块
    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        /*try {
            樱花木蜂巢beeHive();

            cherryBeehive = true;
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        event.getRegistry().registerAll(BLOCKS.toArray(new Block[0]));
    }

//注册模型
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Block block : BLOCKS) {
            registerModels(block);
        }
    }
    public static void registerModels(Block block) {
        SuiKe.proxy.registerItemRenderer(Item.getItemFromBlock(block), 0, "inventory");
    }
}