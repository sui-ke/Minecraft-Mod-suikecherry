package suike.suikecherry.monitor;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.*;
import suike.suikecherry.sblock.*;
import suike.suikecherry.sentity.*;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.player.EntityPlayer;

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
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            Block block = event.getState().getBlock();
            //播放自定义放置音效
            if (block instanceof SLeaves || block instanceof SBlockSapling) {

                /*播放树叶音效*/playSound(world, pos, "block.cherryleaves.place", 5);

                //将树叶属性设置为不腐烂
                if (block instanceof SLeaves) {
                    IBlockState state = event.getState().withProperty(SLeaves.DECAYABLE, false);
                    world.setBlockState(pos, state, 1); //更新方块状态
                }
            } else if (block instanceof SBlock) {
                /*播放原木音效*/playSound(world, pos, "block.cherrywood.place", 5);
            }
        }
    }

//方块破坏触发器
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockDestroyed(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() != null) {
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            Block block = event.getState().getBlock();

            //播放自定义破坏音效
            if (block instanceof SLeaves || block instanceof SBlockSapling || block instanceof SBlockPetals) {
                /*播放树叶音效*/playSound(world, pos, "block.cherryleaves.break", 5);
            } else if (block instanceof SBlock) {
                /*播放原木音效*/playSound(world, pos, "block.cherrywood.break", 5);
            }
        }
    }

//方块右键触发器
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onClickBlock(PlayerInteractEvent.RightClickBlock event) {
        //检查是否是右键点击
        if (/*event.getEntityPlayer().isSneaking() && */!event.getWorld().isRemote) {
            //检查手持物品
            Item item = event.getItemStack().getItem();
            if (item != null) {
                if (item instanceof ItemAxe) {
                    //斧子
                    树干去皮方法(event);
                } else if (item == Items.DYE && event.getItemStack().getMetadata() == 15) {
                    //骨粉
                    使用骨粉方法(event);
                }
            }
        }
    }

//树干去皮方法
    public static void 树干去皮方法(PlayerInteractEvent.RightClickBlock event) {
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        //检查是否是正确的方块
        if (block == BlockBase.CHERRY_LOG || block == BlockBase.CHERRY_WOOD) {
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            /*选择替换方块*/Block newBlock = (block == BlockBase.CHERRY_LOG) ? BlockBase.CHERRY_STRIPPED_LOG : BlockBase.CHERRY_STRIPPED_WOOD;
            /*获取原方块状态*/IBlockState state = event.getWorld().getBlockState(pos);

            /*获取替换方块*/IBlockState newState = newBlock.getDefaultState();
            /*修改替换方块的朝向*/newState = newState.withProperty(SLog.AXIS, state.getValue(SLog.AXIS));
            /*替换方块*/world.setBlockState(pos, newState);

            /*播放去皮音效*/playSound(world, pos, "axe.strip", 4);

            /*给斧子减去一点耐久*/event.getItemStack().damageItem(1, event.getEntityPlayer());

            event.setCanceled(true);
        }
    }

//使用骨粉方法
    public static void 使用骨粉方法(PlayerInteractEvent.RightClickBlock event) {
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();

        if (block == BlockBase.CHERRY_SAPLING) {
        //树苗逻辑
            event.setCanceled(true);

        } else if (block == BlockBase.PINK_PETALS) {
        //落英逻辑
            BlockPos pos = event.getPos();
            EntityPlayer player = event.getEntityPlayer();
            EnumHand hand = event.getHand();
            World world = event.getWorld();

            if (world.getBlockState(pos).getValue(SBlockPetals.LEVEL) < 4) {
                /*执行落英升级方法*/SItemPetals.upPetals(world, pos, block);
            } else {
                /*生成一个掉落物*/Block.spawnAsEntity(world, pos, new ItemStack(ItemBase.PINK_PETALS));
            }

            /*减少物品栏物品*/player.getHeldItem(hand).shrink(1);
            /*播放树叶音效*/playSound(world, pos, "block.cherryleaves.place", 5);

            //生成粒子效果
            /*for (int i = 1; i <= 10; i++) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, 
                                    pos.getX() + 0.5, 
                                    pos.getY() + 0.2, 
                                    pos.getZ() + 0.5, 
                                    0.0, 0.0, 0.0);
                world.spawnParticle(EnumParticleTypes.FLAME, 
                                    pos.getX() + 0.5, 
                                    pos.getY() + 0.2, 
                                    pos.getZ() + 0.5, 
                                    0.0, 0.0, 0.0);
            }*/
        }
    }

//播放音效方法
    public static void playSound(World world, BlockPos pos, String soundName, int amount) {
        /*生成随机数*/int a = (int) (Math.random() * amount) + 1;
        /*播放音效*/world.playSound(null, pos, new SoundEvent(new ResourceLocation(SuiKe.MODID, soundName + a)), SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}