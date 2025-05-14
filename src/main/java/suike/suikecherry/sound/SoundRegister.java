package suike.suikecherry.sound;

import suike.suikecherry.SuiKe;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class SoundRegister {
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        // 去皮音效
        registerSound(event, "axe.strip");

        // 樱花树叶音效
        registerSound(event, "block.cherryleaves.break");
        registerSound(event, "block.cherryleaves.step");
        registerSound(event, "block.cherryleaves.hit");
        registerSound(event, "block.cherryleaves.place");
        registerSound(event, "block.cherryleaves.fall");

        // 樱花木音效
        registerSound(event, "block.cherrywood.break");
        registerSound(event, "block.cherrywood.step");
        registerSound(event, "block.cherrywood.hit");
        registerSound(event, "block.cherrywood.place");
        registerSound(event, "block.cherrywood.fall");

        // 樱花木按钮音效
        registerSound(event, "block.cherrybutton.click");
        registerSound(event, "block.cherrybutton.restore");

        // 生物群系背景音乐
        // registerSound(event, "music.overworld.cherry_grove");

        // 空的音效
        registerSound(event, "none");
    }

    private static void registerSound(RegistryEvent.Register<SoundEvent> event, String soundName) {
        ResourceLocation location = new ResourceLocation(SuiKe.MODID, soundName);
        SoundEvent soundEvent = new SoundEvent(location);

        soundEvent.setRegistryName(location);
        event.getRegistry().register(soundEvent);
    }

    // public static final SoundEvent cherryGroveSound = new SoundEvent(new ResourceLocation(SuiKe.MODID, "music.overworld.cherry_grove"));
}