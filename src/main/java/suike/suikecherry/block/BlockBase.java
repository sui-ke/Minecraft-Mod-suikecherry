package suike.suikecherry.block;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.item.ItemBase;
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

//方块
@EventBusSubscriber
public class BlockBase extends Block {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Item> ITEMBLOCKS = new ArrayList<>();

    /*樱花树苗*/public static final ModBlockSapling CHERRY_SAPLING = new ModBlockSapling("cherry_sapling");
    /*樱花树苗盆栽*/public static final ModBlockSaplingPot CHERRY_SAPLING_POT = new ModBlockSaplingPot("cherry_sapling_pot");
    /*落英*/public static final ModBlockPetals PINK_PETALS = new ModBlockPetals("pink_petals");
    /*樱花原木*/public static final Block CHERRY_LOG = new ModBlockLog("cherry_log");
    /*樱花木头*/public static final Block CHERRY_WOOD = new ModBlockLog("cherry_wood");
    /*樱花去皮原木*/public static final Block CHERRY_STRIPPED_LOG = new ModBlockLog("cherry_stripped_log");
    /*樱花去皮木头*/public static final Block CHERRY_STRIPPED_WOOD = new ModBlockLog("cherry_stripped_wood");
    /*樱花树叶*/public static final Block CHERRY_LEAVES = new ModBlockLeaves("cherry_leaves");
    /*樱花木板*/public static final Block CHERRY_PLANKS = new BlockBase("cherry_planks", CreativeTabs.BUILDING_BLOCKS);
    /*樱花木楼梯*/public static final Block CHERRY_STAIRS = new ModBlockStairs("cherry_stairs");
    /*樱花木单层台阶*/public static final ModBlockSlab CHERRY_SLAB = new ModBlockSlab("cherry_slab");
    /*樱花木双层台阶*/public static final ModBlockSlab CHERRY_SLAB_DOUBLE = new ModBlockSlab("cherry_slab_double");
    /*樱花木门*/public static final ModBlockDoor CHERRY_DOOR = new ModBlockDoor("cherry_door");
    /*樱花木活板门*/public static final Block CHERRY_TRAPDOOR = new ModBlockTrapDoor("cherry_trapdoor");
    /*樱花木栅栏*/public static final Block CHERRY_FENCE = new ModBlockFence("cherry_fence");
    /*樱花木栅栏门*/public static final Block CHERRY_FENCEGATE = new ModBlockFenceGate("cherry_fence_gate");
    /*樱花木按钮*/public static final Block CHERRY_BUTTON = new ModBlockButton("cherry_button");
    /*樱花木压力板*/public static final Block CHERRY_PRESSURE_PLATE = new ModBlockPressurePlate("cherry_pressure_plate");
    /*樱花木告示牌*/public static final ModBlockSign CHERRY_SIGN = new ModBlockSign("cherry_sign");
    /*樱花木告示牌*/public static final ModBlockSignWall CHERRY_SIGN_WALL = new ModBlockSignWall("cherry_sign_wall");
    /*樱花木悬挂式告示牌*/public static final ModBlockHangingSign CHERRY_HANGING_SIGN = new ModBlockHangingSign("cherry_hanging_sign");
    /*樱花木悬挂式告示牌*/public static final ModBlockHangingSign CHERRY_HANGING_SIGN_WALL = new ModBlockHangingSign("cherry_hanging_sign_wall");
    /*樱花木悬挂式告示牌*/public static final ModBlockHangingSignAttached CHERRY_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("cherry_hanging_sign_attached");

    /*橡木悬挂式告示牌*/public static final ModBlockHangingSign OAK_HANGING_SIGN = new ModBlockHangingSign("oak_hanging_sign");
    /*橡木悬挂式告示牌*/public static final ModBlockHangingSign OAK_HANGING_SIGN_WALL = new ModBlockHangingSign("oak_hanging_sign_wall");
    /*橡木悬挂式告示牌*/public static final ModBlockHangingSignAttached OAK_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("oak_hanging_sign_attached");
    /*云杉木悬挂式告示牌*/public static final ModBlockHangingSign SPRUCE_HANGING_SIGN = new ModBlockHangingSign("spruce_hanging_sign");
    /*云杉木悬挂式告示牌*/public static final ModBlockHangingSign SPRUCE_HANGING_SIGN_WALL = new ModBlockHangingSign("spruce_hanging_sign_wall");
    /*云杉木悬挂式告示牌*/public static final ModBlockHangingSignAttached SPRUCE_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("spruce_hanging_sign_attached");
    /*白桦木悬挂式告示牌*/public static final ModBlockHangingSign BIRCH_HANGING_SIGN = new ModBlockHangingSign("birch_hanging_sign");
    /*白桦木悬挂式告示牌*/public static final ModBlockHangingSign BIRCH_HANGING_SIGN_WALL = new ModBlockHangingSign("birch_hanging_sign_wall");
    /*白桦木悬挂式告示牌*/public static final ModBlockHangingSignAttached BIRCH_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("birch_hanging_sign_attached");
    /*丛林木悬挂式告示牌*/public static final ModBlockHangingSign JUNGLE_HANGING_SIGN = new ModBlockHangingSign("jungle_hanging_sign");
    /*丛林木悬挂式告示牌*/public static final ModBlockHangingSign JUNGLE_HANGING_SIGN_WALL = new ModBlockHangingSign("jungle_hanging_sign_wall");
    /*丛林木悬挂式告示牌*/public static final ModBlockHangingSignAttached JUNGLE_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("jungle_hanging_sign_attached");
    /*金合欢木悬挂式告示牌*/public static final ModBlockHangingSign ACACIA_HANGING_SIGN = new ModBlockHangingSign("acacia_hanging_sign");
    /*金合欢木悬挂式告示牌*/public static final ModBlockHangingSign ACACIA_HANGING_SIGN_WALL = new ModBlockHangingSign("acacia_hanging_sign_wall");
    /*金合欢木悬挂式告示牌*/public static final ModBlockHangingSignAttached ACACIA_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("acacia_hanging_sign_attached");
    /*深色橡木悬挂式告示牌*/public static final ModBlockHangingSign DARK_OAK_HANGING_SIGN = new ModBlockHangingSign("dark_oak_hanging_sign");
    /*深色橡木悬挂式告示牌*/public static final ModBlockHangingSign DARK_OAK_HANGING_SIGN_WALL = new ModBlockHangingSign("dark_oak_hanging_sign_wall");
    /*深色橡木悬挂式告示牌*/public static final ModBlockHangingSignAttached DARK_OAK_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("dark_oak_hanging_sign_attached");
    /*竹悬挂式告示牌*/public static ModBlockHangingSign BAMBOO_HANGING_SIGN = null;
    /*竹悬挂式告示牌*/public static ModBlockHangingSign BAMBOO_HANGING_SIGN_WALL = null;
    /*竹悬挂式告示牌*/public static ModBlockHangingSignAttached BAMBOO_HANGING_SIGN_ATTACHED = null;
    /*绯红木悬挂式告示牌*/public static ModBlockHangingSign CRIMSON_HANGING_SIGN = null;
    /*绯红木悬挂式告示牌*/public static ModBlockHangingSign CRIMSON_HANGING_SIGN_WALL = null;
    /*绯红木悬挂式告示牌*/public static ModBlockHangingSignAttached CRIMSON_HANGING_SIGN_ATTACHED = null;
    /*诡异木悬挂式告示牌*/public static ModBlockHangingSign WARPED_HANGING_SIGN = null;
    /*诡异木悬挂式告示牌*/public static ModBlockHangingSign WARPED_HANGING_SIGN_WALL = null;
    /*诡异木悬挂式告示牌*/public static ModBlockHangingSignAttached WARPED_HANGING_SIGN_ATTACHED = null;

//构造函数
    public BlockBase(String name, CreativeTabs tabs) {
        /*创建方块实例*/super(Material.WOOD);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/this.setCreativeTab(tabs);
        /*设置硬度*/this.setHardness(2.0F);
        /*设置抗爆性*/this.setResistance(5.0F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(255);
        /*设置声音*/this.setSoundType(new SoundTypeWood());

        /*添加到BLOCKS列表*/BLOCKS.add(this);
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    static {
        if (Examine.FuturemcID) {
            BAMBOO_HANGING_SIGN = new ModBlockHangingSign("bamboo_hanging_sign");
            BAMBOO_HANGING_SIGN_WALL = new ModBlockHangingSign("bamboo_hanging_sign_wall");
            BAMBOO_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("bamboo_hanging_sign_attached");
        }

        if (Examine.UNBID) {
            CRIMSON_HANGING_SIGN = new ModBlockHangingSign("crimson_hanging_sign");
            CRIMSON_HANGING_SIGN_WALL = new ModBlockHangingSign("crimson_hanging_sign_wall");
            CRIMSON_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("crimson_hanging_sign_attached");
            WARPED_HANGING_SIGN = new ModBlockHangingSign("warped_hanging_sign");
            WARPED_HANGING_SIGN_WALL = new ModBlockHangingSign("warped_hanging_sign_wall");
            WARPED_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("warped_hanging_sign_attached");
        }
    }

//方块燃烧
    public static void setBlockFire() {
        if (ConfigValue.blockFire) blockCanFire();
    }

    // 方块燃烧
    private static void blockCanFire() {
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
        if (world.isAirBlock(pos)) {
            ItemStack itemstack = player.getHeldItem(hand);
            //检查玩家手持物
            if (itemstack.getItem().getRegistryName().toString().equals("futuremc:lantern")) {
                /*通过注册名获取方块*/Block lantern = Block.getBlockFromItem(Item.getByNameOrId("futuremc:lantern"));
                /*获取方块的默认状态*/IBlockState lanternState = lantern.getStateFromMeta(blockMeta);

                world.setBlockState(pos, lanternState);
                // 减少物品栏物品
                if (!player.field_71075_bZ.isCreativeMode) player.getHeldItem(hand).shrink(1);
                // 播放灯笼音效
                Sound.playSound(world, pos, "futuremc", "lantern_place");

                return true;
            }
        }

        return false;
    }
}