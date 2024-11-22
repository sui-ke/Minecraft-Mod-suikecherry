package suike.suikecherry.monitor;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.*;
import suike.suikecherry.sblock.*;
import suike.suikecherry.sentity.*;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.world.CherryTree;
import suike.suikecherry.world.CherryBiome;

import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.particle.Particle;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

@Mod.EventBusSubscriber(modid = SuiKe.MODID)
public class Monitor {
//方块放置触发器
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.getState().getBlock() != null) {
            Block block = event.getState().getBlock();
            //将树叶属性设置为不腐烂
            if (block instanceof SLeaves) {
                World world = event.getWorld();
                BlockPos pos = event.getPos();

                IBlockState state = event.getState().withProperty(SLeaves.DECAYABLE, false);
                world.setBlockState(pos, state, 1); //更新方块状态
            }
        }
    }

//方块右键触发器
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onClickBlock(PlayerInteractEvent.RightClickBlock event) {
        //检查是否是右键点击
        if (!event.getWorld().isRemote) {

            /*BlockPos pos = event.getPos();
            World world = event.getWorld();
            Block block = world.getBlockState(pos).getBlock();

            System.out.println("点击的方块为: " + block);
            System.out.println("点击的方块属于类: " + block.getClass().toString());*/

            //检查手持物品
            Item item = event.getItemStack().getItem();
            if (item != null) {
                if (item == Items.DYE && event.getItemStack().getMetadata() == 15) {
                    //骨粉
                    使用骨粉方法(event);
                }
            }
        }
    }

//使用骨粉方法
    public static void 使用骨粉方法(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        World world = event.getWorld();
        Block block = world.getBlockState(pos).getBlock();

        if (block == BlockBase.CHERRY_SAPLING) {
        //树苗逻辑
            event.setCanceled(true);
        } else if (block == Blocks.GRASS && world.getBiome(pos) instanceof CherryBiome) {
        //群系生成落英逻辑
            EnumHand hand = event.getHand();
            EntityPlayer player = event.getEntityPlayer();

            /*if (!player.capabilities.isCreativeMode)
                /*减少物品栏物品*/player.getHeldItem(hand).shrink(1);
            /*播放树叶音效*/Sound.playSound(world, pos, "block.cherryleaves.place");
            CherryTree.放置落英(world, pos, 5, 3);

        } else if (block instanceof SBlockPetals) {
        //落英升级逻辑
            EnumHand hand = event.getHand();
            EntityPlayer player = event.getEntityPlayer();

            if (world.getBlockState(pos).getValue(SBlockPetals.LEVEL) < 4) {
                /*执行落英升级方法*/SItemPetals.upPetals(world, pos, block);
            } else {
                /*生成一个掉落物*/Block.spawnAsEntity(world, pos, new ItemStack(ItemBase.PINK_PETALS));
            }

            /*if (!player.capabilities.isCreativeMode)
                /*减少物品栏物品*/player.getHeldItem(hand).shrink(1);
            /*播放树叶音效*/Sound.playSound(world, pos, "block.cherryleaves.place");
        }
    }

    public static void particle(World world, int a, int b, int c) {
        for (int i = 1; i <= 30; i++) {
            Particle particle = null;
            net.minecraft.client.Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }
}