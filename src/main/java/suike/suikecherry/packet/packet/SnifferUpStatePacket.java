package suike.suikecherry.packet.packets;

import suike.suikecherry.entity.sniffer.EntitySniffer;
import suike.suikecherry.entity.sniffer.EntitySniffer.State;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

import io.netty.buffer.ByteBuf;

public class SnifferUpStatePacket implements IMessage {
    private int entityID;
    private State state;

    public SnifferUpStatePacket() {}

    public SnifferUpStatePacket(int entityID, State state) {
        this.entityID = entityID;
        this.state = state;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeInt(state.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        state = State.values()[buf.readInt()];
    }

    // 客户端处理类: 处理服务器发来的包
    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler<SnifferUpStatePacket, IMessage> {
        @Override
        public IMessage onMessage(SnifferUpStatePacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                Entity entity = world.getEntityByID(message.entityID);
                if (entity instanceof EntitySniffer) {
                    ((EntitySniffer) entity).upState(message.state);
                }
            });
            return null;
        }
    }

    // 服务端处理类: 处理客户端发来的包
    @SideOnly(Side.SERVER)
    public static class ServerHandler implements IMessageHandler<SnifferUpStatePacket, IMessage> {
        @Override
        public IMessage onMessage(SnifferUpStatePacket message, MessageContext ctx) {
            return null;
        }
    }
}