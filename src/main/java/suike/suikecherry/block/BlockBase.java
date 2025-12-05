package suike.suikecherry.block;

import java.util.List;
import java.util.ArrayList;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.config.ConfigValue;
import suike.suikecherry.sound.ModSoundType;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//方块
@EventBusSubscriber
public class BlockBase {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Item> ITEMBLOCKS = new ArrayList<>();

    /*樱花树苗*/public static final ModBlockSapling CHERRY_SAPLING = new ModBlockSapling("cherry_sapling");
    /*樱花树苗盆栽*/public static final ModBlockSaplingPot CHERRY_SAPLING_POT = new ModBlockSaplingPot("cherry_sapling_pot");
    /*落英*/public static final ModBlockPetals PINK_PETALS = new ModBlockPetals("pink_petals", EnumDyeColor.PINK);
    /*樱花原木*/public static final ModBlockLog CHERRY_LOG = new ModBlockLog("cherry_log");
    /*樱花木头*/public static final ModBlockLog CHERRY_WOOD = new ModBlockLog("cherry_wood");
    /*樱花去皮原木*/public static final Block CHERRY_STRIPPED_LOG = new ModBlockLog("cherry_stripped_log", CHERRY_LOG);
    /*樱花去皮木头*/public static final Block CHERRY_STRIPPED_WOOD = new ModBlockLog("cherry_stripped_wood", CHERRY_WOOD);
    /*樱花树叶*/public static final ModBlockLeaves CHERRY_LEAVES = new ModBlockLeaves("cherry_leaves");
    /*樱花木板*/public static final ModBlockPlanks CHERRY_PLANKS = new ModBlockPlanks("cherry_planks");
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
    //*橡木告示牌*/public static final ModBlockSign OAK_SIGN = new ModBlockSign("oak_sign");
    //*橡木告示牌*/public static final ModBlockSignWall OAK_SIGN_WALL = new ModBlockSignWall("oak_sign_wall");
    /*云杉木告示牌*/public static final ModBlockSign SPRUCE_SIGN = new ModBlockSign("spruce_sign");
    /*云杉木告示牌*/public static final ModBlockSignWall SPRUCE_SIGN_WALL = new ModBlockSignWall("spruce_sign_wall");
    /*白桦木告示牌*/public static final ModBlockSign BIRCH_SIGN = new ModBlockSign("birch_sign");
    /*白桦木告示牌*/public static final ModBlockSignWall BIRCH_SIGN_WALL = new ModBlockSignWall("birch_sign_wall");
    /*丛林木告示牌*/public static final ModBlockSign JUNGLE_SIGN = new ModBlockSign("jungle_sign");
    /*丛林木告示牌*/public static final ModBlockSignWall JUNGLE_SIGN_WALL = new ModBlockSignWall("jungle_sign_wall");
    /*金合欢木告示牌*/public static final ModBlockSign ACACIA_SIGN = new ModBlockSign("acacia_sign");
    /*金合欢木告示牌*/public static final ModBlockSignWall ACACIA_SIGN_WALL = new ModBlockSignWall("acacia_sign_wall");
    /*深色橡木告示牌*/public static final ModBlockSign DARK_OAK_SIGN = new ModBlockSign("dark_oak_sign");
    /*深色橡木告示牌*/public static final ModBlockSignWall DARK_OAK_SIGN_WALL = new ModBlockSignWall("dark_oak_sign_wall");
    /*竹告示牌*/public static ModBlockSign BAMBOO_SIGN = null;
    /*竹告示牌*/public static ModBlockSignWall BAMBOO_SIGN_WALL = null;
    /*绯红木告示牌*/public static ModBlockSign CRIMSON_SIGN = null;
    /*绯红木告示牌*/public static ModBlockSignWall CRIMSON_SIGN_WALL = null;
    /*诡异木告示牌*/public static ModBlockSign WARPED_SIGN = null;
    /*诡异木告示牌*/public static ModBlockSignWall WARPED_SIGN_WALL = null;

    /*悬挂式樱花木告示牌*/public static final ModBlockHangingSign CHERRY_HANGING_SIGN = new ModBlockHangingSign("cherry_hanging_sign");
    /*悬挂式樱花木告示牌*/public static final ModBlockHangingSign CHERRY_HANGING_SIGN_WALL = new ModBlockHangingSign("cherry_hanging_sign_wall");
    /*悬挂式樱花木告示牌*/public static final ModBlockHangingSignAttached CHERRY_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("cherry_hanging_sign_attached");
    /*悬挂式橡木告示牌*/public static final ModBlockHangingSign OAK_HANGING_SIGN = new ModBlockHangingSign("oak_hanging_sign");
    /*悬挂式橡木告示牌*/public static final ModBlockHangingSign OAK_HANGING_SIGN_WALL = new ModBlockHangingSign("oak_hanging_sign_wall");
    /*悬挂式橡木告示牌*/public static final ModBlockHangingSignAttached OAK_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("oak_hanging_sign_attached");
    /*悬挂式云杉木告示牌*/public static final ModBlockHangingSign SPRUCE_HANGING_SIGN = new ModBlockHangingSign("spruce_hanging_sign");
    /*悬挂式云杉木告示牌*/public static final ModBlockHangingSign SPRUCE_HANGING_SIGN_WALL = new ModBlockHangingSign("spruce_hanging_sign_wall");
    /*悬挂式云杉木告示牌*/public static final ModBlockHangingSignAttached SPRUCE_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("spruce_hanging_sign_attached");
    /*悬挂式白桦木告示牌*/public static final ModBlockHangingSign BIRCH_HANGING_SIGN = new ModBlockHangingSign("birch_hanging_sign");
    /*悬挂式白桦木告示牌*/public static final ModBlockHangingSign BIRCH_HANGING_SIGN_WALL = new ModBlockHangingSign("birch_hanging_sign_wall");
    /*悬挂式白桦木告示牌*/public static final ModBlockHangingSignAttached BIRCH_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("birch_hanging_sign_attached");
    /*悬挂式丛林木告示牌*/public static final ModBlockHangingSign JUNGLE_HANGING_SIGN = new ModBlockHangingSign("jungle_hanging_sign");
    /*悬挂式丛林木告示牌*/public static final ModBlockHangingSign JUNGLE_HANGING_SIGN_WALL = new ModBlockHangingSign("jungle_hanging_sign_wall");
    /*悬挂式丛林木告示牌*/public static final ModBlockHangingSignAttached JUNGLE_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("jungle_hanging_sign_attached");
    /*悬挂式金合欢木告示牌*/public static final ModBlockHangingSign ACACIA_HANGING_SIGN = new ModBlockHangingSign("acacia_hanging_sign");
    /*悬挂式金合欢木告示牌*/public static final ModBlockHangingSign ACACIA_HANGING_SIGN_WALL = new ModBlockHangingSign("acacia_hanging_sign_wall");
    /*悬挂式金合欢木告示牌*/public static final ModBlockHangingSignAttached ACACIA_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("acacia_hanging_sign_attached");
    /*悬挂式深色橡木告示牌*/public static final ModBlockHangingSign DARK_OAK_HANGING_SIGN = new ModBlockHangingSign("dark_oak_hanging_sign");
    /*悬挂式深色橡木告示牌*/public static final ModBlockHangingSign DARK_OAK_HANGING_SIGN_WALL = new ModBlockHangingSign("dark_oak_hanging_sign_wall");
    /*悬挂式深色橡木告示牌*/public static final ModBlockHangingSignAttached DARK_OAK_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("dark_oak_hanging_sign_attached");
    /*悬挂式竹告示牌*/public static ModBlockHangingSign BAMBOO_HANGING_SIGN = null;
    /*悬挂式竹告示牌*/public static ModBlockHangingSign BAMBOO_HANGING_SIGN_WALL = null;
    /*悬挂式竹告示牌*/public static ModBlockHangingSignAttached BAMBOO_HANGING_SIGN_ATTACHED = null;
    /*悬挂式绯红木告示牌*/public static ModBlockHangingSign CRIMSON_HANGING_SIGN = null;
    /*悬挂式绯红木告示牌*/public static ModBlockHangingSign CRIMSON_HANGING_SIGN_WALL = null;
    /*悬挂式绯红木告示牌*/public static ModBlockHangingSignAttached CRIMSON_HANGING_SIGN_ATTACHED = null;
    /*悬挂式诡异木告示牌*/public static ModBlockHangingSign WARPED_HANGING_SIGN = null;
    /*悬挂式诡异木告示牌*/public static ModBlockHangingSign WARPED_HANGING_SIGN_WALL = null;
    /*悬挂式诡异木告示牌*/public static ModBlockHangingSignAttached WARPED_HANGING_SIGN_ATTACHED = null;

    /*可疑的沙子*/public static final ModBlockBrushable SUSPICIOUS_SAND = new ModBlockBrushable("suspicious_sand", Blocks.SAND, "item.brush.brushing.sand", ModSoundType.suspiciousSand);
    /*可疑的沙砾*/public static final ModBlockBrushable SUSPICIOUS_GRAVEL = new ModBlockBrushable("suspicious_gravel", Blocks.GRAVEL, "item.brush.brushing.gravel", ModSoundType.suspiciousGravel);
    /*嗅探兽蛋*/public static final ModBlockSnifferEgg SNIFFER_EGG = new ModBlockSnifferEgg("sniffer_egg");
    /*饰纹陶罐*/public static final ModBlockDecoratedPot DECORATED_POT = new ModBlockDecoratedPot("decorated_pot");

    /*雕纹书架*/public static final ModBlockChiseledBookShelf CHISELED_BOOKSHELF = new ModBlockChiseledBookShelf("chiseled_bookshelf");
    /*锻造台*/public static final ModBlockSmithingTable SMITHING_TABLE = new ModBlockSmithingTable("smithing_table");

    static {
        if (ConfigValue.hasBamboo) {
            BAMBOO_SIGN = new ModBlockSign("bamboo_sign");
            BAMBOO_SIGN_WALL = new ModBlockSignWall("bamboo_sign_wall");
            BAMBOO_HANGING_SIGN = new ModBlockHangingSign("bamboo_hanging_sign");
            BAMBOO_HANGING_SIGN_WALL = new ModBlockHangingSign("bamboo_hanging_sign_wall");
            BAMBOO_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("bamboo_hanging_sign_attached");
        }

        if (Examine.UNBID) {
            CRIMSON_SIGN = new ModBlockSign("crimson_sign");
            CRIMSON_SIGN_WALL = new ModBlockSignWall("crimson_sign_wall");
            WARPED_SIGN = new ModBlockSign("warped_sign");
            WARPED_SIGN_WALL = new ModBlockSignWall("warped_sign_wall");
            CRIMSON_HANGING_SIGN = new ModBlockHangingSign("crimson_hanging_sign");
            CRIMSON_HANGING_SIGN_WALL = new ModBlockHangingSign("crimson_hanging_sign_wall");
            CRIMSON_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("crimson_hanging_sign_attached");
            WARPED_HANGING_SIGN = new ModBlockHangingSign("warped_hanging_sign");
            WARPED_HANGING_SIGN_WALL = new ModBlockHangingSign("warped_hanging_sign_wall");
            WARPED_HANGING_SIGN_ATTACHED = new ModBlockHangingSignAttached("warped_hanging_sign_attached");
        }
    }

// 方块燃烧
    public static void setBlockFire() {
        if (ConfigValue.blockFire) blockCanFire();
    }
    // 方块燃烧
    private static void blockCanFire() {
        /*落英*/Blocks.FIRE.setFireInfo(PINK_PETALS, 60, 100);
        /*樱花原木*/Blocks.FIRE.setFireInfo(CHERRY_LOG, 5, 5);
        /*樱花木头*/Blocks.FIRE.setFireInfo(CHERRY_WOOD, 5, 5);
        /*樱花去皮原木*/Blocks.FIRE.setFireInfo(CHERRY_STRIPPED_LOG, 5, 5);
        /*樱花去皮木头*/Blocks.FIRE.setFireInfo(CHERRY_STRIPPED_WOOD, 5, 5);
        /*樱花树叶*/Blocks.FIRE.setFireInfo(CHERRY_LEAVES, 30, 60);
        /*樱花木板*/Blocks.FIRE.setFireInfo(CHERRY_PLANKS, 5, 20);
        /*樱花木楼梯*/Blocks.FIRE.setFireInfo(CHERRY_STAIRS, 5, 20);
        /*樱花木单层台阶*/Blocks.FIRE.setFireInfo(CHERRY_SLAB, 5, 20);
        /*樱花木双层台阶*/Blocks.FIRE.setFireInfo(CHERRY_SLAB_DOUBLE, 5, 20);
        /*樱花木栅栏*/Blocks.FIRE.setFireInfo(CHERRY_FENCE, 5, 20);
        /*樱花木栅栏门*/Blocks.FIRE.setFireInfo(CHERRY_FENCEGATE, 5, 20);
    }

// 注册方块
    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(BLOCKS.toArray(new Block[0]));
    }

// 注册模型
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Block block : BLOCKS) {
            registerModels(block);
        }
    }
    public static void registerModels(Block block) {
        SuiKe.proxy.registerItemRenderer(Item.getItemFromBlock(block));
    }
}