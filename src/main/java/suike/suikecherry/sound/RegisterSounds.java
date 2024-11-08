package suike.suikecherry.sound;

import suike.suikecherry.SuiKe;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = SuiKe.MODID)
public class RegisterSounds {
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        for (int i = 1; i <= 5; i++) {
            //注册去皮音效
            event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "axe/strip" + i))
                .setRegistryName("axe.strip" + i));

            //注册樱花树叶音效
            event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "cherry_leaves/place" + i))
                .setRegistryName("block.cherryleaves.place" + i));
            event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "cherry_leaves/break" + i))
                .setRegistryName("block.cherryleaves.break" + i));

            //注册樱花木音效
            event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "cherry_wood/place" + i))
                .setRegistryName("block.cherrywood.place" + i));
            event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "cherry_wood/break" + i))
                .setRegistryName("block.cherrywood.break" + i));
        }

        //注册樱花木按钮音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "cherry_button/cherry_button_click"))
            .setRegistryName("block.cherrybutton.click"));
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "cherry_button/cherry_button_restore"))
            .setRegistryName("block.cherrybutton.restore"));

        //注册空的音效
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SuiKe.MODID, "none"))
            .setRegistryName("none"));
    }
}