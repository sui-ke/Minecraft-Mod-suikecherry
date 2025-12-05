package suike.suikecherry.sound.soundtype;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeDecoratedPot extends SoundType {
    public SoundTypeDecoratedPot() {
        super(1.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "block.decorated_pot.break")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.decorated_pot.step")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.decorated_pot.place")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.decorated_pot.hit")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.decorated_pot.fall"))
        );
    }
}