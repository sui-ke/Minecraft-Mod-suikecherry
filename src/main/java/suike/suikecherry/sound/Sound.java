package suike.suikecherry.sound;

import suike.suikecherry.SuiKe;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

//播放音效方法
public class Sound {
    public static void playSound(World world, BlockPos pos, String soundName) {
        playSound(world, pos, SuiKe.MODID, soundName, 1.0F, 1.0F);
    }
    public static void playSound(World world, BlockPos pos, String soundName, float volume, float pitch) {
        playSound(world, pos, SuiKe.MODID, soundName, volume, pitch);
    }
    public static void playSound(World world, BlockPos pos, String modId, String soundName) {
        playSound(world, pos, modId, soundName, 1.0F, 1.0F);
    }
    public static void playSound(World world, BlockPos pos, String modId, String soundName, float volume, float pitch) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(modId, soundName));
        playSound(world, pos, soundEvent, volume, pitch, getSoundCategory(soundName));
    }

    public static void playSound(World world, BlockPos pos, ResourceLocation soundRes) {
        playSound(world, pos, soundRes, 1.0F, 1.0F);
    }
    public static void playSound(World world, BlockPos pos, ResourceLocation soundRes, float volume, float pitch) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundRes);
        playSound(world, pos, soundEvent, volume, pitch);
    }

    public static void playSound(World world, BlockPos pos, SoundEvent soundEvent) {
        playSound(world, pos, soundEvent, 1.0F, 1.0F);
    }
    public static void playSound(World world, double posX, double posY, double posZ, SoundEvent soundEvent, float volume, float pitch) {
        playSound(world, new BlockPos(posX, posY, posZ), soundEvent, volume, pitch);
    }

    public static void playSound(World world, BlockPos pos, SoundEvent soundEvent, float volume, float pitch) {
        playSound(world, pos, soundEvent, volume, pitch, SoundCategory.BLOCKS);
    }

    public static void playSound(World world, BlockPos pos, SoundEvent soundEvent, float volume, float pitch, SoundCategory category) {
        //播放音效
        if (!world.isRemote && soundEvent != null) {
            world.playSound(null, pos, soundEvent, category, volume, pitch);
        }
    }

    public static SoundCategory getSoundCategory(String soundName) {
        if (soundName.startsWith("entity")) {
            return SoundCategory.NEUTRAL;
        }
        if (soundName.startsWith("item")) {
            return SoundCategory.PLAYERS;
        }
        return SoundCategory.BLOCKS;
    }
}

/*
1. volume（音量）
    作用：控制音效的音量大小。

    取值范围：
        通常为 0.0F 到 1.0F。
            0.0F：完全静音。
            1.0F：最大音量。

    可以超过 1.0F，但可能会导致音效失真。

    示例：
        volume = 0.5F：音量为最大音量的一半。
        volume = 1.0F：音量为最大音量。

2. pitch（音高）
    作用：控制音效的音高（频率）。

    取值范围：
        通常为 0.5F 到 2.0F。
            0.5F：音高降低一半（听起来更低沉）。
            1.0F：正常音高。
            2.0F：音高提高一倍（听起来更尖锐）。

    示例：
        pitch = 0.8F：音高略低于正常。
        pitch = 1.2F：音高略高于正常。
*/