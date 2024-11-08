package suike.suikecherry.sound;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypePetals extends SoundType {
    public SoundTypePetals() {
        super(2.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "none")),//被破坏
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherryleaves.place2")),// 脚步
            new SoundEvent(new ResourceLocation("suikecherry", "none")),//放置
            new SoundEvent(new ResourceLocation("suikecherry", "none")),//破坏过程
            new SoundEvent(new ResourceLocation("suikecherry", "none"))
        );
    }
}