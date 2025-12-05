package suike.suikecherry.sound.soundtype;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeLeaves extends SoundType {
    public SoundTypeLeaves() {
        super(2.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherry_leaves.break")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherry_leaves.step")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherry_leaves.place")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherry_leaves.hit")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherry_leaves.fall"))
        );
    }
}