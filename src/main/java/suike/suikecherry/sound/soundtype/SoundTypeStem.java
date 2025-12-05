package suike.suikecherry.sound.soundtype;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeStem extends SoundType {
    public SoundTypeStem() {
        super(1.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("nb", "block.stem.break")),
            new SoundEvent(new ResourceLocation("nb", "block.stem.step")),
            new SoundEvent(new ResourceLocation("nb", "block.stem.step")),
            new SoundEvent(new ResourceLocation("nb", "block.stem.step")),
            new SoundEvent(new ResourceLocation("nb", "block.stem.step"))
        );
    }
}