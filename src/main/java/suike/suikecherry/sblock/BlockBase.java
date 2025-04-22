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

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class BlockBase extends Block implements SBlock {
//方块
    public static final List<Block> BLOCKS = new ArrayList<>();

    /*樱花树苗*/public static final SBlockSapling CHERRY_SAPLING = new SBlockSapling("cherry_sapling");
    /*樱花树苗盆栽*/public static final SBlockSaplingPot CHERRY_SAPLING_POT = new SBlockSaplingPot("cherry_sapling_pot");
    /*落英*/public static final SBlockPetals PINK_PETALS = new SBlockPetals("pink_petals");
    /*樱花原木*/public static final Block CHERRY_LOG = new SLog("cherry_log");
    /*樱花木头*/public static final Block CHERRY_WOOD = new SLog("cherry_wood");
    /*樱花去皮原木*/public static final Block CHERRY_STRIPPED_LOG = new SLog("cherry_stripped_log");
    /*樱花去皮木头*/public static final Block CHERRY_STRIPPED_WOOD = new SLog("cherry_stripped_wood");
    /*樱花树叶*/public static final Block CHERRY_LEAVES = new SLeaves("cherry_leaves");
    /*樱花木板*/public static final Block CHERRY_PLANKS = new BlockBase("cherry_planks", CreativeTabs.BUILDING_BLOCKS);
    /*樱花木楼梯*/public static final Block CHERRY_STAIRS = new SStairs("cherry_stairs");
    /*樱花木单层台阶*/public static final SBlockSlab CHERRY_SLAB = new SBlockSlab("cherry_slab", false);
    /*樱花木双层台阶*/public static final SBlockSlab CHERRY_SLAB_DOUBLE = new SBlockSlab("cherry_slab_double", true);
    /*樱花木门*/public static final SBlockDoor CHERRY_DOOR = new SBlockDoor("cherry_door");
    /*樱花木活板门*/public static final Block CHERRY_TRAPDOOR = new STrapDoor("cherry_trapdoor");
    /*樱花木栅栏*/public static final Block CHERRY_FENCE = new SFence("cherry_fence");
    /*樱花木栅栏门*/public static final Block CHERRY_FENCEGATE = new SFenceGate("cherry_fence_gate");
    /*樱花木按钮*/public static final Block CHERRY_BUTTON = new SButton("cherry_button");
    /*樱花木压力板*/public static final Block CHERRY_PRESSURE_PLATE = new SPressurePlate("cherry_pressure_plate");
    /*樱花木告示牌*/public static final SBlockSign CHERRY_SIGN = new SBlockSign("cherry_sign");
    /*樱花木告示牌*/public static final SBlockSignWall CHERRY_SIGN_WALL = new SBlockSignWall("cherry_sign_wall");
    /*樱花木悬挂式告示牌*/public static final SBlockHangingSign CHERRY_HANGING_SIGN = new SBlockHangingSign("cherry_hanging_sign", false);
    /*樱花木悬挂式告示牌*/public static final SBlockHangingSign CHERRY_HANGING_SIGN_WALL = new SBlockHangingSign("cherry_hanging_sign_wall", true);
    /*樱花木悬挂式告示牌*/public static final SBlockHangingSignAttached CHERRY_HANGING_SIGN_ATTACHED = new SBlockHangingSignAttached("cherry_hanging_sign_attached");

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
        /*设置声音*/setSoundType(new SoundTypeWood());

        /*添加到BLOCKS列表*/BLOCKS.add(this);
        /*添加到ITEMS列表*/ItemBase.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

//设置方块对应的物品 & 方块燃烧
    public static void setBlockItem() {
        /*落英*/BlockBase.PINK_PETALS.setItem(ItemBase.PINK_PETALS);
        /*樱花木门*/BlockBase.CHERRY_DOOR.setItem(ItemBase.CHERRY_DOOR);
        /*樱花木台阶*/BlockBase.CHERRY_SLAB.setItem(ItemBase.CHERRY_SLAB);
        /*樱花木台阶*/BlockBase.CHERRY_SLAB_DOUBLE.setItem(ItemBase.CHERRY_SLAB);
        /*樱花树苗*/BlockBase.CHERRY_SAPLING.setItem(ItemBase.CHERRY_SAPLING);
        /*樱花树苗盆栽*/BlockBase.CHERRY_SAPLING_POT.setItem(ItemBase.CHERRY_SAPLING);
        /*樱花木告示牌*/BlockBase.CHERRY_SIGN.setItem(ItemBase.CHERRY_SIGN);
        /*樱花木告示牌*/BlockBase.CHERRY_SIGN_WALL.setItem(ItemBase.CHERRY_SIGN);
        /*樱花木悬挂式告示牌*/BlockBase.CHERRY_HANGING_SIGN.setItem(ItemBase.CHERRY_HANGING_SIGN);
        /*樱花木悬挂式告示牌*/BlockBase.CHERRY_HANGING_SIGN_WALL.setItem(ItemBase.CHERRY_HANGING_SIGN);
        /*樱花木悬挂式告示牌*/BlockBase.CHERRY_HANGING_SIGN_ATTACHED.setItem(ItemBase.CHERRY_HANGING_SIGN);

        if (ConfigValue.blockFire)
            setBlockFire();
    }

    //方块燃烧
    public static void setBlockFire() {
        /*落英*/Blocks.FIRE.setFireInfo(BlockBase.PINK_PETALS, 60, 100);
        /*樱花原木*/Blocks.FIRE.setFireInfo(CHERRY_LOG, 5, 5);
        /*樱花木头*/Blocks.FIRE.setFireInfo(CHERRY_WOOD, 5, 5);
        /*樱花去皮原木*/Blocks.FIRE.setFireInfo(CHERRY_STRIPPED_LOG, 5, 5);
        /*樱花去皮木头*/Blocks.FIRE.setFireInfo(CHERRY_STRIPPED_WOOD, 5, 5);
        /*樱花树叶*/Blocks.FIRE.setFireInfo(CHERRY_LEAVES, 30, 60);
        /*樱花木板*/Blocks.FIRE.setFireInfo(BlockBase.CHERRY_PLANKS, 5, 20);
        /*樱花木楼梯*/Blocks.FIRE.setFireInfo(CHERRY_STAIRS, 5, 20);
        /*樱花木单层台阶*/Blocks.FIRE.setFireInfo(CHERRY_SLAB, 5, 20);
        /*樱花木双层台阶*/Blocks.FIRE.setFireInfo(CHERRY_SLAB_DOUBLE, 5, 20);
        /*樱花木栅栏*/Blocks.FIRE.setFireInfo(CHERRY_FENCE, 5, 20);
        /*樱花木栅栏门*/Blocks.FIRE.setFireInfo(CHERRY_FENCEGATE, 5, 20);
    }

//注册方块
    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
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

//方块交互-放置灯笼
    public static boolean lantern(World world, BlockPos pos, EntityPlayer player, EnumHand hand, int blockMeta) {
        Block oldBlock = world.getBlockState(pos).getBlock();

        if (oldBlock == Blocks.AIR) {
            ItemStack itemstack = player.getHeldItem(hand);
            //检查玩家手持物
            if (itemstack.getItem().getRegistryName().toString().equals("futuremc:lantern")) {
                /*通过注册名获取方块*/Block lantern = Block.getBlockFromItem(Item.getByNameOrId("futuremc:lantern"));
                /*获取方块的默认状态*/IBlockState lanternState = lantern.getStateFromMeta(blockMeta);

                world.setBlockState(pos, lanternState);
                /*播放灯笼音效*/Sound.playSound(world, pos, "futuremc", "lantern_place");
                player.getHeldItem(hand).shrink(1);

                return true;
            }
        }

        return false;
    }
}