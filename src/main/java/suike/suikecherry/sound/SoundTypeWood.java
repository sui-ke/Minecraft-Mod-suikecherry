package suike.suikecherry.sound;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeWood extends SoundType {
    public SoundTypeWood() {
        super(1.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherrywood.break")),//被破坏
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherrywood.place")),// 脚步
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherrywood.place")),//放置
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherrywood.break")),//破坏过程
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherrywood.place"))
        );
    }
}