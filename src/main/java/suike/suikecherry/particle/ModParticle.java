package suike.suikecherry.particle;

import java.util.Random;

import suike.suikecherry.SuiKe;

import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.TextureStitchEvent;

import net.minecraftforge.fml.common.Loader;

@Mod.EventBusSubscriber
public class ModParticle {

    public static final int CHERRY_PARTICLE_ID = 18432;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerParticleTexture(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        String base = "particle/cherry_";

        boolean isOptiFineInstalled;
        try {
            Class.forName("optifine.Installer");
            isOptiFineInstalled = true;
        } catch (ClassNotFoundException e) {
            isOptiFineInstalled = false;
        }
        if (isOptiFineInstalled) {
            base = "particle-opt/cherry_";
        }

        CherryParticle.TEXTURES.clear();
        for (int i = 0; i <= 11; i++) {
            ResourceLocation res = new ResourceLocation(SuiKe.MODID, base + i);
            map.registerSprite(res);
            CherryParticle.TEXTURES.add(res);
        }
    }

    public static final Random rand = new Random();
    public static void spawnParticles(World world, BlockPos pos, String particlesNaem) {
        if ("villager_happy".equals(particlesNaem))
            spawnParticlesVillagerHappy(world, pos);
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
}