package suike.suikecherry.packet.packets;

import suike.suikecherry.particle.ModParticle;

import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

import io.netty.buffer.ByteBuf;

public class SpawnParticlesPacket implements IMessage {
    private BlockPos pos = BlockPos.ORIGIN;
    private String particlesNaem;

    public SpawnParticlesPacket() {}

    public SpawnParticlesPacket(BlockPos pos, String particlesNaem) {
        this.pos = pos;
        this.particlesNaem = particlesNaem;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeUTF8String(buf, particlesNaem);
        // System.out.println("传递: " + "pos: " + pos + " particlesNaem: " + particlesNaem);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        particlesNaem = ByteBufUtils.readUTF8String(buf);
        // System.out.println("接收: " + "pos: " + pos + " particlesNaem: " + particlesNaem);
    }

    // 客户端处理类: 处理服务器发来的包
    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler<SpawnParticlesPacket, IMessage> {
        @Override
        public IMessage onMessage(SpawnParticlesPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                ModParticle.spawnParticles(world, message.pos, message.particlesNaem);
            });
            return null;
        }
    }

    // 服务端处理类: 处理客户端发来的包
    @SideOnly(Side.SERVER)
    public static class ServerHandler implements IMessageHandler<SpawnParticlesPacket, IMessage> {
        @Override
        public IMessage onMessage(SpawnParticlesPacket message, MessageContext ctx) {
            return null;
        }
    }
}