package suike.suikecherry.sound.soundtype;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeChiseledBookShelf extends SoundType {
    public SoundTypeChiseledBookShelf() {
        super(1.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "block.chiseled_bookshelf.break")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.chiseled_bookshelf.step")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.chiseled_bookshelf.place")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.chiseled_bookshelf.hit")),
            new SoundEvent(new ResourceLocation("suikecherry", "block.chiseled_bookshelf.fall"))
        );
    }
}