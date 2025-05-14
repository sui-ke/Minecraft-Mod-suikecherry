package suike.suikecherry.packet;

import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import io.netty.buffer.ByteBuf;

public class PlaySoundPacket implements IMessage {
    private static final Logger LOGGER = LogManager.getLogger("PlaySoundPacket");

    private BlockPos pos;
    private String modId;
    private String soundName;
    private float volume;
    private float pitch;

    public PlaySoundPacket() {}

    public PlaySoundPacket(BlockPos pos, String modId, String soundName, float volume, float pitch) {
        this.pos = pos;
        this.modId = modId;
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        modId = ByteBufUtils.readUTF8String(buf);
        soundName = ByteBufUtils.readUTF8String(buf);
        volume = buf.readFloat();
        pitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeUTF8String(buf, modId);
        ByteBufUtils.writeUTF8String(buf, soundName);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
    }

    public static class Handler implements IMessageHandler<PlaySoundPacket, IMessage> {
        @Override
        public IMessage onMessage(PlaySoundPacket message, MessageContext ctx) {
            // 打印调试信息
            LOGGER.info("尝试播放音效: modId={}, soundName={}, pos={}, volume={}, pitch={}",
            message.modId, message.soundName, message.pos, message.volume, message.pitch);

            ResourceLocation soundLoc = new ResourceLocation(message.modId, message.soundName);
            SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundLoc);

            if (soundEvent == null) {
                LOGGER.error("音效未找到: {}", soundLoc);
            } else {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    World world = Minecraft.getMinecraft().world;
                    if (world != null) {
                        world.playSound(
                            null,
                            message.pos,
                            soundEvent,
                            SoundCategory.BLOCKS,
                            message.volume,
                            message.pitch
                        );
                        LOGGER.info("音效已播放: {}", soundLoc);
                    } else {
                        LOGGER.error("世界未加载，无法播放音效");
                    }
                });
            }
            return null;
        }
    }
}