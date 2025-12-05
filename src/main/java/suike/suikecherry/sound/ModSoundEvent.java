package suike.suikecherry.sound;

import suike.suikecherry.SuiKe;
import suike.suikecherry.config.ConfigValue;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.RegistryEvent;

@Mod.EventBusSubscriber
public class ModSoundEvent {

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        // 物品音效
        registerSound(event, "item.axe.strip");                      // 去皮音效
        registerSound(event, "item.honeycomb.wax_on");               // 涂蜡
        registerSound(event, "item.brush.brushing.generic");         // 刷子: 刷扫中
        registerSound(event, "item.brush.brushing.sand");            // 刷子: 刷扫沙子中
        registerSound(event, "item.brush.brushing.gravel");          // 刷子: 刷扫沙砾中
        registerSound(event, "item.brush.brushing.sand.complete");   // 刷子: 刷扫沙子完毕
        registerSound(event, "item.brush.brushing.gravel.complete"); // 刷子: 刷扫沙砾完毕

        // 樱花树叶音效
        registerSound(event, "block.cherry_leaves.break");
        registerSound(event, "block.cherry_leaves.step");
        registerSound(event, "block.cherry_leaves.hit");
        registerSound(event, "block.cherry_leaves.place");
        registerSound(event, "block.cherry_leaves.fall");

        // 樱花木音效
        registerSound(event, "block.cherry_wood.break");
        registerSound(event, "block.cherry_wood.step");
        registerSound(event, "block.cherry_wood.hit");
        registerSound(event, "block.cherry_wood.place");
        registerSound(event, "block.cherry_wood.fall");

        // 樱花木按钮音效
        registerSound(event, "block.cherry_button.click");
        registerSound(event, "block.cherry_button.restore");

        // 可疑的沙子音效
        registerSound(event, "block.suspicious_sand.break");
        registerSound(event, "block.suspicious_sand.step");
        registerSound(event, "block.suspicious_sand.hit");
        registerSound(event, "block.suspicious_sand.place");
        registerSound(event, "block.suspicious_sand.fall");

        // 可疑的沙砾音效
        registerSound(event, "block.suspicious_gravel.break");
        registerSound(event, "block.suspicious_gravel.step");
        registerSound(event, "block.suspicious_gravel.hit");
        registerSound(event, "block.suspicious_gravel.place");
        registerSound(event, "block.suspicious_gravel.fall");

        // 陶罐
        registerSound(event, "block.decorated_pot.break");
        registerSound(event, "block.decorated_pot.step");
        registerSound(event, "block.decorated_pot.hit");
        registerSound(event, "block.decorated_pot.place");
        registerSound(event, "block.decorated_pot.fall");
        registerSound(event, "block.decorated_pot.shatter");     // 被剑破坏
        registerSound(event, "block.decorated_pot.insert");      // 饰纹陶罐: 装入
        registerSound(event, "block.decorated_pot.insert_fail"); // 饰纹陶罐: 晃动

        // 雕纹书架
        registerSound(event, "block.chiseled_bookshelf.break");
        registerSound(event, "block.chiseled_bookshelf.step");
        registerSound(event, "block.chiseled_bookshelf.hit");
        registerSound(event, "block.chiseled_bookshelf.place");
        registerSound(event, "block.chiseled_bookshelf.fall");
        registerSound(event, "block.chiseled_bookshelf.insert"); // 雕纹书架: 装入
        registerSound(event, "block.chiseled_bookshelf.pickup"); // 雕纹书架: 取出

        // 嗅探兽音效 
        registerSound(event, "entity.sniffer.egg_hatch");
        registerSound(event, "entity.sniffer.egg_crack");
        registerSound(event, "entity.sniffer.idle");         // 嗅探兽: 呼噜
        registerSound(event, "entity.sniffer.death");        // 嗅探兽: 死亡
        registerSound(event, "entity.sniffer.hurt");         // 嗅探兽: 受伤
        registerSound(event, "entity.sniffer.happy");        // 嗅探兽: 愉悦
        registerSound(event, "entity.sniffer.eat");          // 嗅探兽: 进食
        registerSound(event, "entity.sniffer.scenting");     // 嗅探兽: 嗅闻
        registerSound(event, "entity.sniffer.sniffing");     // 嗅探兽: 嗅探
        registerSound(event, "entity.sniffer.searching");    // 嗅探兽: 搜寻
        registerSound(event, "entity.sniffer.digging");      // 嗅探兽: 挖掘
        registerSound(event, "entity.sniffer.digging_stop"); // 嗅探兽: 站起
        registerSound(event, "entity.sniffer.drop_seed");    // 嗅探兽: 丢下种子
        registerSound(event, "entity.sniffer.step");         // 嗅探兽: 脚步声

        // 音乐唱片
        registerSound(event, "music_disc.relic");

        // 锻造
        registerSound(event, "block.smithing_table");

        // 竹子
        if (ConfigValue.hasBamboo) {
            registerSound(event, "block.bamboo.break");
            registerSound(event, "block.bamboo.step");
            registerSound(event, "block.bamboo.hit");
            registerSound(event, "block.bamboo.place");
            registerSound(event, "block.bamboo.fall");
        }

        // 生物群系背景音乐
        // registerSound(event, "music.overworld.cherry_grove");

        registerSound(event, "none");
    }

    private static void registerSound(RegistryEvent.Register<SoundEvent> event, String soundName) {
        ResourceLocation location = new ResourceLocation(SuiKe.MODID, soundName);
        SoundEvent soundEvent = new SoundEvent(location);

        soundEvent.setRegistryName(location);
        event.getRegistry().register(soundEvent);
    }
}

// private static final SoundEvent cherryGroveSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(SuiKe.MODID, "music.overworld.cherry_grove"));