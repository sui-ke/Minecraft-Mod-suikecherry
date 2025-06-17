package suike.suikecherry.expand.futuremc;

import suike.suikecherry.sound.Sound;
import suike.suikecherry.expand.Examine;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.common.Optional;

public class Lantern {
    //方块交互-放置灯笼
    public static boolean lantern(World world, BlockPos pos, EntityPlayer player, EnumHand hand, int blockMeta) {
        if (Examine.FuturemcID) return placLantern(world, pos, player, hand, blockMeta);
        return false;
    }

    @Optional.Method(modid = "futuremc")
    private static boolean placLantern(World world, BlockPos pos, EntityPlayer player, EnumHand hand, int blockMeta) {
        if (world.isAirBlock(pos)) {
            ItemStack itemstack = player.getHeldItem(hand);

            if (itemstack.isEmpty() || itemstack.getItem().getRegistryName() == null) return false;

            String itemRegName = itemstack.getItem().getRegistryName().toString();

            //检查玩家手持物
            if ("futuremc:lantern".equals(itemRegName)) {
                /*通过注册名获取方块*/Block lantern = Block.getBlockFromItem(Item.getByNameOrId("futuremc:lantern"));
                /*通过元数据获取方块状态*/IBlockState lanternState = lantern.getStateFromMeta(blockMeta);

                // 二次验证（防止异步冲突）
                if (world.isAirBlock(pos)) {
                    // 尝试放置方块
                    if (world.setBlockState(pos, lanternState)) {
                        // 减少物品栏物品
                        if (!player.field_71075_bZ.isCreativeMode) player.getHeldItem(hand).shrink(1);
                        // 播放灯笼音效
                        Sound.playSound(world, pos, "futuremc", "lantern_place");

                        return true;
                    }
                }
            }
        }

        return false;
    }
}