package suike.suikecherry.sound.soundtype;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeWood extends SoundType {
    public SoundTypeWood() {
        super(1.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherry_wood.break")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherry_wood.step")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherry_wood.place")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherry_wood.hit")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherry_wood.fall"))
        );
    }
}