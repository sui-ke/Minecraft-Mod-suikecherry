package suike.suikecherry.sound;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeWood extends SoundType {
    public SoundTypeWood() {
        super(1.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherrywood.break")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherrywood.step")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherrywood.place")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherrywood.hit")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherrywood.fall"))
        );
    }
}