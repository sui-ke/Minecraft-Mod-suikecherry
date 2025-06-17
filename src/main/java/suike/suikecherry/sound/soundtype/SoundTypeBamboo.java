package suike.suikecherry.sound.soundtype;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeBamboo extends SoundType {
    public SoundTypeBamboo() {
        super(1.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("futuremc", "bamboo_place")),
            new SoundEvent(new ResourceLocation("futuremc", "bamboo_step")),
            new SoundEvent(new ResourceLocation("futuremc", "bamboo_place")),
            new SoundEvent(new ResourceLocation("futuremc", "bamboo_place")),
            new SoundEvent(new ResourceLocation("futuremc", "bamboo_step"))
        );
    }
}