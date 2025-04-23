package suike.suikecherry.sound;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypePetals extends SoundType {
    public SoundTypePetals() {
        super(2.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherryleaves.break")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherryleaves.step")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherryleaves.place")),
            new SoundEvent(new ResourceLocation("suikecherry", "none")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherryleaves.fall"))
        );
    }
}