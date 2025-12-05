package suike.suikecherry.expand.futuremc;

import suike.suikecherry.inter.IBlockHangingSign;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class Lantern {
    public static Boolean canPlaceLantern(World world, BlockPos pos, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos.offset(facing));
        Block block = state.getBlock();

        // 修复末地传送门框架崩溃
        if (block instanceof BlockEndPortalFrame) {
            return true;
        }

        // 修复半砖逻辑：只允许放在上半砖上
        if (facing == EnumFacing.DOWN && block instanceof BlockSlab) {
            if (state.getProperties().containsKey(BlockSlab.HALF)) {
                if (state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM) {
                    return false;
                }
            }
            return true;
        }

        // 防止灯笼从悬挂告示牌掉落
        if (facing == EnumFacing.UP) {
            if (block instanceof IBlockHangingSign) {
                return true;
            }
        }

        return null;
    }
}