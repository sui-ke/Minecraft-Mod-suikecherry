package suike.suikecherry.mixin;

import suike.suikecherry.inter.IBlockHangingSign;

import thedarkcolour.futuremc.block.villagepillage.LanternBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LanternBlock.class)
public abstract class LanternBlockMixin {
    @Inject(
        method = "isValidPos(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void onIsValidPos(World world, BlockPos pos, EnumFacing facing, CallbackInfoReturnable<Boolean> cir) {
        IBlockState state = world.getBlockState(pos.offset(facing));
        Block block = state.getBlock();

        // 修复末地传送门框架崩溃
        if (block instanceof BlockEndPortalFrame) {
            cir.setReturnValue(true); // 直接允许放置, 避免检查造成崩溃
            return;
        }

        // 修复半砖逻辑：只允许放在上半砖上
        if (block instanceof BlockSlab && facing == EnumFacing.DOWN) {
            if (state.getProperties().containsKey(BlockSlab.HALF)) {
                if (state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM) {
                    cir.setReturnValue(false);
                    return;
                }
            }
            cir.setReturnValue(true);
            return;
        }

        // 防止灯笼从悬挂告示牌掉落
        if (facing == EnumFacing.UP) {
            if (block instanceof IBlockHangingSign) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}