package suike.suikecherry.sound;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

public class SoundTypeLeaves extends SoundType {
    public SoundTypeLeaves() {
        super(2.0F, 1.0F, 
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherryleaves.break")),//被破坏
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherryleaves.place")),// 脚步
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherryleaves.place")),//放置
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherryleaves.break")),//破坏过程
            new SoundEvent(new ResourceLocation("suikecherry", "block.cherryleaves.place"))
        );
    }
}