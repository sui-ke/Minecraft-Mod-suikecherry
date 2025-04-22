package suike.suikecherry.sound;

import suike.suikecherry.SuiKe;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;

public class Sound {
    //播放音效方法
    public static void playSound(World world, BlockPos pos, String soundName) {
        playSound(world, pos, SuiKe.MODID, soundName, 1.0F, 1.0F);
    }
    public static void playSound(World world, BlockPos pos, String soundName, float a, float b) {
        playSound(world, pos, SuiKe.MODID, soundName, a, b);
    }
    public static void playSound(World world, BlockPos pos, String modId, String soundName) {
        playSound(world, pos, modId, soundName, 1.0F, 1.0F);
    }

    public static void playSound(World world, BlockPos pos, String modId, String soundName, float a, float b) {
        //播放音效
        world.playSound(null, pos, new SoundEvent(new ResourceLocation(modId, soundName)), SoundCategory.BLOCKS, a, b);
    }
}