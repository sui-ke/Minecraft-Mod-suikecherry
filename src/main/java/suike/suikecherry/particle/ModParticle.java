package suike.suikecherry.particle;

import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.proxy.ClientProxy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;

public class ModParticle {

    public static final int CHERRY_PARTICLE_ID = 18432;

    public static void registerParticleTexture() {
        String base = "particle/cherry_";

        if (Examine.OPTID) {
            base = "particle-opt/cherry_";
        }

        CherryParticle.TEXTURES.clear();
        for (int i = 0; i <= 11; i++) {
            CherryParticle.TEXTURES.add(
                ClientProxy.addRegisterTextures(
                    new ResourceLocation(SuiKe.MODID, base + i)
                )
            );
        }
    }

    public static final Random rand = new Random();
    public static void spawnParticles(World world, double x, double y, double z, String particlesName) {
        spawnParticles(world, new BlockPos(x, y, z), particlesName);
    }
    public static void spawnParticles(World world, BlockPos pos, String particlesName) {
        if ("villager_happy".equals(particlesName)) {
            spawnParticlesVillagerHappy(world, pos);
        }
        else if (particlesName.startsWith("brushable_block_broken_")) {
            String blockType = particlesName.substring("brushable_block_broken_".length());
            spawnParticlesBrushableBlockBroken(world, pos, blockType);
        }
        else if ("decorated_pot_smoke".equals(particlesName)) {
            spawnParticlesDecoratedPotSmoke(world, pos);
        }
    }

    public static void spawnParticlesVillagerHappy(World world, BlockPos pos) {
        for (int i = 0; i < 15; ++i) {
            double x = (double) ((float) pos.getX() + rand.nextFloat());
            double y = (double) ((float) pos.getY() + rand.nextFloat());
            double z = (double) ((float) pos.getZ() + rand.nextFloat());

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

    private static void spawnParticlesBrushableBlockBroken(World world, BlockPos pos, String blockType) {
        EnumFacing facing = EnumFacing.UP;
        double baseX = pos.getX() + 0.5 + facing.getFrontOffsetX() * 0.51;
        double baseY = pos.getY() + 0.5 + facing.getFrontOffsetY() * 0.51;
        double baseZ = pos.getZ() + 0.5 + facing.getFrontOffsetZ() * 0.51;

        // 创建方块资源路径
        ResourceLocation blockId = new ResourceLocation(SuiKe.MODID, blockType);
        Block block = Block.REGISTRY.getObject(blockId);

        if (block == null) return;

        IBlockState state = block.getDefaultState();

        for (int i = 0; i < 8; ++i) {
            double offsetX = (facing.getAxis() != EnumFacing.Axis.X) ? world.rand.nextDouble() - 0.5 : 0;
            double offsetY = (facing.getAxis() != EnumFacing.Axis.Y) ? world.rand.nextDouble() - 0.5 : 0;
            double offsetZ = (facing.getAxis() != EnumFacing.Axis.Z) ? world.rand.nextDouble() - 0.5 : 0;

            // 应用面方向偏移
            offsetX *= 0.7 * Math.abs(facing.getFrontOffsetX() + 1);
            offsetY *= 0.7 * Math.abs(facing.getFrontOffsetY() + 1);
            offsetZ *= 0.7 * Math.abs(facing.getFrontOffsetZ() + 1);

            world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, 
                baseX + offsetX,
                baseY + offsetY,
                baseZ + offsetZ,
                0.0D, 0.0D, 0.0D,
                Block.getStateId(state)
            );
        }
    }

    public static void spawnParticlesDecoratedPotSmoke(World world, BlockPos pos) {
        for (int i = 0; i < 8; ++i) {
            double x = (double)pos.getX() + 0.45 + rand.nextFloat() * 0.1;
            double y = (double)pos.getY() + 1.15 + rand.nextFloat() * 0.1;
            double z = (double)pos.getZ() + 0.45 + rand.nextFloat() * 0.1;

            double motionX = (rand.nextDouble() - 0.5) * 0.04D; // 扩散
            double motionY = rand.nextDouble() * 0.006D + 0.004D; // 上升
            double motionZ = (rand.nextDouble() - 0.5) * 0.04D; // 扩散

            world.spawnParticle(
                EnumParticleTypes.SMOKE_NORMAL,
                x, y, z,
                motionX, motionY, motionZ
            );
        }
    }
}