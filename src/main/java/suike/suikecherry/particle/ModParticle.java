package suike.suikecherry.particle;

import suike.suikecherry.SuiKe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.TextureStitchEvent;

import net.minecraftforge.fml.common.Loader;

@Mod.EventBusSubscriber
public class ModParticle {

    public static final int CHERRY_PARTICLE_ID = 0;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
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
}