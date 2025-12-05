package suike.suikecherry.particle;

import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.proxy.ClientProxy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModParticle {

    @SideOnly(Side.CLIENT) public static final Random rand = new Random();
    @SideOnly(Side.CLIENT) public static final int CHERRY_PARTICLE_ID = 18432;

    @SideOnly(Side.CLIENT)
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

    public static final int PARTICLE_VILLAGER_HAPPY = 0;
    public static final int PARTICLE_BRUSHABLE_BROKEN = 1;
    public static final int PARTICLE_DECORATED_POT_SMOKE = 2;

    public static void spawnParticles(World world, double x, double y, double z, float particleType) {
        spawnParticles(world, new BlockPos(x, y, z), particleType);
    }
    public static void spawnParticles(World world, BlockPos pos, float particleType) {
        int mainType = (int) particleType;
        switch(mainType) {
            case PARTICLE_VILLAGER_HAPPY:
                // 村民高兴粒子
                spawnParticlesVillagerHappy(world, pos);
                break;
            case PARTICLE_BRUSHABLE_BROKEN:
                // 可疑方块碎裂粒子
                spawnParticlesBrushableBlockBroken(world, pos, particleType);
                break;
            case PARTICLE_DECORATED_POT_SMOKE:
                // 陶罐烟雾粒子
                spawnParticlesDecoratedPotSmoke(world, pos);
                break;
            default: return;
        }
    }

    public static void spawnParticlesVillagerHappy(World world, BlockPos pos) {
        for (int i = 0; i < 16; ++i) {
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

    private static void spawnParticlesBrushableBlockBroken(World world, BlockPos pos, float particleType) {
        // 创建方块资源路径
        Block block;

        if (particleType == 1.1F) block = BlockBase.SUSPICIOUS_SAND;
        else if (particleType == 1.2F) block = BlockBase.SUSPICIOUS_GRAVEL;
        else return;

        if (block == null) return;

        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (int i = 0; i < 16; ++i) {
            world.spawnParticle(
                EnumParticleTypes.BLOCK_CRACK, 
                centerX + (world.rand.nextDouble() - 0.5) * 0.2,
                centerY + (world.rand.nextDouble() - 0.5) * 0.2,
                centerZ + (world.rand.nextDouble() - 0.5) * 0.2,
                0.0D, 0.0D, 0.0D,
                Block.getStateId(block.getDefaultState())
            );
        }
    }

    public static void spawnParticlesDecoratedPotSmoke(World world, BlockPos pos) {
        for (int i = 0; i < 16; ++i) {
            DustPlumeParticle.spawnParticle(world, pos);
        }
    }
}