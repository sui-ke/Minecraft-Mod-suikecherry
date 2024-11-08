package suike.suikecherry.sound;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeNone extends SoundType {
    public SoundTypeNone() {
        super(0.0F, 0.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "none")),
            new SoundEvent(new ResourceLocation("suikecherry", "none")),
            new SoundEvent(new ResourceLocation("suikecherry", "none")),
            new SoundEvent(new ResourceLocation("suikecherry", "none")),
            new SoundEvent(new ResourceLocation("suikecherry", "none"))
        );
    }
}