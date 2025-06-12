package suike.suikecherry.sound.soundtype;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeSuspiciousGravel extends SoundType {
    public SoundTypeSuspiciousGravel() {
        super(1.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "block.suspicious_gravel.break")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.suspicious_gravel.step")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.suspicious_gravel.place")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.suspicious_gravel.hit")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.suspicious_gravel.fall"))
        );
    }
}