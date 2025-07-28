package suike.suikecherry.event;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.block.ModBlockPetals;
import suike.suikecherry.world.gen.cherry.PetalsGen;
import suike.suikecherry.world.biome.CherryBiome;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.packets.SpawnParticlesPacket;

import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Mod.EventBusSubscriber
public class BlockOnEvent {
/*// 方块放置触发器
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {

    }

// 方块右键触发器*/
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getWorld().isRemote) {
            // 骨粉方法
            tryUseBoneMeal(event);
            /*
            BlockPos pos = event.getPos();
            World world = event.getWorld();
            Block block = world.getBlockState(pos).getBlock();

            SuiKe.LOGGER.info("点击的方块为: " + block);
            SuiKe.LOGGER.info("点击的方块位置: " + pos);
            SuiKe.LOGGER.info("点击的方块属于类: " + block.getClass().toString());//*/
        }
    }

// 使用骨粉方法
    public static void tryUseBoneMeal(PlayerInteractEvent.RightClickBlock event) {
        //检查手持物品
        Item item = event.getItemStack().getItem();
        if (item != null) {
            if (item == Items.DYE && event.getItemStack().getMetadata() == 15) {
                BlockPos pos = event.getPos();
                World world = event.getWorld();

                if (world.getBlockState(pos).getBlock() == Blocks.GRASS &&
                    world.getBiome(pos) instanceof CherryBiome) {

                    // 群系生成落英逻辑
                    PetalsGen.placePetals(world, pos, 5, 3);
                }
            }
        }
    }
}