package suike.suikecherry.sound.soundtype;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeNone extends SoundType {
    public SoundTypeNone() {
        super(0.0F, 0.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "none")), // 破坏
            new SoundEvent(new ResourceLocation("suikecherry", "none")), // 脚步
            new SoundEvent(new ResourceLocation("suikecherry", "none")), // 放置
            new SoundEvent(new ResourceLocation("suikecherry", "none")), // 损坏中
            new SoundEvent(new ResourceLocation("suikecherry", "none"))  // 着地
        );
    }
}