package suike.suikecherry.event;

import java.util.Random;

import suike.suikecherry.item.*;
import suike.suikecherry.block.*;
import suike.suikecherry.entity.*;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.world.gen.PetalsGen;
import suike.suikecherry.world.biome.CherryBiome;

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
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Mod.EventBusSubscriber
public class Event {
/*// 方块放置触发器
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {

    }

//方块右键触发器*/
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onClickBlock(PlayerInteractEvent.RightClickBlock event) {
        //检查是否是右键点击
        if (!event.getWorld().isRemote) {
            /*
            BlockPos pos = event.getPos();
            World world = event.getWorld();
            Block block = world.getBlockState(pos).getBlock();

            System.out.println("点击的方块为: " + block);
            System.out.println("点击的方块属于类: " + block.getClass().toString());
            */

            //检查手持物品
            Item item = event.getItemStack().getItem();
            if (item != null) {
                if (item == Items.DYE && event.getItemStack().getMetadata() == 15) {
                    /*骨粉方法*/useBoneMeal(event);
                }
            }
        }
    }

//使用骨粉方法
    public static void useBoneMeal(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        World world = event.getWorld();
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (block == Blocks.GRASS && world.getBiome(pos) instanceof CherryBiome) {
            //群系生成落英逻辑
            PetalsGen.placePetals(world, pos, 5, 3);
            return;
        }

        if (block instanceof ModBlockPetals) {
            //落英升级逻辑
            EnumHand hand = event.getHand();
            EntityPlayer player = event.getEntityPlayer();
            int level = blockState.getValue(ModBlockPetals.LEVEL);

            if (level < 4) {
                IBlockState state = blockState.withProperty(ModBlockPetals.LEVEL, (level + 1));
                world.setBlockState(pos, state, 2);
            } else {
                /*生成一个掉落物*/Block.spawnAsEntity(world, pos, blockState.getBlock().getItem(world, pos, blockState));
            }

            // 减少物品栏物品
            if (!player.field_71075_bZ.isCreativeMode) player.getHeldItem(hand).shrink(1);
            //播放树叶音效
            Sound.playSound(world, pos, "block.cherryleaves.place");
        }
    }

    public static void particle(World world, BlockPos pos) {
        /*
        for (int i = 1; i <= 30; i++) {
            Particle particle = null;
            net.minecraft.client.Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
        */

        net.minecraft.item.ItemDye.spawnBonemealParticles(world, pos, 15);

        Random rand = new Random();

        for (int i = 0; i < 15; ++i) {
            double x = (double) ((float)pos.getX() + rand.nextFloat());
            double y = (double) ((float)pos.getY() + rand.nextFloat());
            double z = (double) ((float)pos.getZ() + rand.nextFloat());

            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;

            world.spawnParticle(
                EnumParticleTypes.VILLAGER_HAPPY,
                x, y, z,
                d0, d1, d2
            );
        }
    }
}