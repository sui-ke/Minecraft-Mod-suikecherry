package suike.suikecherry.sound;

import suike.suikecherry.SuiKe;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import suike.suikecherry.suikecherry.Tags;

@Mod.EventBusSubscriber(modid =  Tags.MOD_ID)
public class SoundRegister {
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        //去皮音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "axe.strip"))
            .setRegistryName("axe.strip"));

        //樱花树叶音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherryleaves.break"))
            .setRegistryName("block.cherryleaves.break"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherryleaves.step"))
            .setRegistryName("block.cherryleaves.step"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherryleaves.hit"))
            .setRegistryName("block.cherryleaves.hit"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherryleaves.place"))
            .setRegistryName("block.cherryleaves.place"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherryleaves.fall"))
            .setRegistryName("block.cherryleaves.fall"));

        //樱花木音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherrywood.break"))
            .setRegistryName("block.cherrywood.break"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherrywood.step"))
            .setRegistryName("block.cherrywood.step"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherrywood.hit"))
            .setRegistryName("block.cherrywood.hit"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherrywood.place"))
            .setRegistryName("block.cherrywood.place"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherrywood.fall"))
            .setRegistryName("block.cherrywood.fall"));

        //樱花木按钮音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherrybutton.click"))
            .setRegistryName("block.cherrybutton.click"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "block.cherrybutton.restore"))
            .setRegistryName("block.cherrybutton.restore"));

        //生物群系背景音乐
        //event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "music.overworld.cherry_grove"))
        //     .setRegistryName("music.overworld.cherry_grove"));

        //空的音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation( Tags.MOD_ID, "none"))
            .setRegistryName("none"));
    }

    //public static final SoundEvent cherryGroveSound = new SoundEvent(new ResourceLocation( Tags.MOD_ID, "music.overworld.cherry_grove"));
}