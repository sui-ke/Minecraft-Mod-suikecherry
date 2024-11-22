package suike.suikecherry.sound;

import suike.suikecherry.SuiKe;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = SuiKe.MODID)
public class SoundRegister {
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        //樱花树叶音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "block.cherryleaves.place"))
            .setRegistryName("block.cherryleaves.place"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "block.cherryleaves.break"))
            .setRegistryName("block.cherryleaves.break"));

        //樱花木音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "block.cherrywood.place"))
            .setRegistryName("block.cherrywood.place"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "block.cherrywood.break"))
            .setRegistryName("block.cherrywood.break"));

        //去皮音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "axe.strip"))
            .setRegistryName("axe.strip"));

        //樱花木按钮音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "cherry_button/cherry_button_click"))
            .setRegistryName("block.cherrybutton.click"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "cherry_button/cherry_button_restore"))
            .setRegistryName("block.cherrybutton.restore"));

        //生物群系背景音乐
        // event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "music.overworld.cherry_grove"))
        //     .setRegistryName("music.overworld.cherry_grove"));

        //空的音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "none"))
            .setRegistryName("none"));
    }
}