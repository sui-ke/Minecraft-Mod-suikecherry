package suike.suikecherry.packet.packets;

import suike.suikecherry.particle.ModParticle;
import suike.suikecherry.tileentity.HasBackSideSignTileEntity;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.tileentity.TileEntity;
import suike.suikecherry.particle.ModParticle;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

import io.netty.buffer.ByteBuf;

public class SignTextUpdatePacket implements IMessage {
    private BlockPos pos = BlockPos.ORIGIN;
    private int slot;
    private ITextComponent[] text;
    private boolean isLocking;

    public SignTextUpdatePacket() {}

    public SignTextUpdatePacket(BlockPos pos, int slot, ITextComponent[] text, boolean isLocking) {
        this.pos = pos;
        this.slot = slot;
        this.text = text;
        this.isLocking = isLocking;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(slot);
        
        boolean hasText = (text != null);
        buf.writeBoolean(hasText);
        if (hasText) {
            for (ITextComponent component : text) {
                ByteBufUtils.writeUTF8String(buf, ITextComponent.Serializer.componentToJson(component));
            }
        }

        buf.writeBoolean(isLocking);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        slot = buf.readInt();

        boolean hasText = buf.readBoolean();
        if (hasText) {
            text = new ITextComponent[4];
            for (int i = 0; i < text.length; i++) {
                text[i] = ITextComponent.Serializer.jsonToComponent(ByteBufUtils.readUTF8String(buf));
            }
        } else {
            text = null;
        }

        isLocking = buf.readBoolean();
    }

    // 客户端处理类: 处理服务器发来的包
    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler<SignTextUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(SignTextUpdatePacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                // 更新客户端 TileEntity
                World world = Minecraft.getMinecraft().world;
                TileEntity tile = world.getTileEntity(message.pos);
                if (tile instanceof HasBackSideSignTileEntity) {
                    if (message.text == null) {
                        ModParticle.spawnParticles(world, message.pos, ModParticle.PARTICLE_VILLAGER_HAPPY);
                        ((HasBackSideSignTileEntity) tile).setWaxed(message.slot);
                    }

                    // 通知客户端更新文字
                    ((HasBackSideSignTileEntity) tile).updateText(message.slot, message.text, message.isLocking);
                }
            });
            return null;
        }
    }

    // 服务端处理类: 处理客户端发来的包
    public static class ServerHandler implements IMessageHandler<SignTextUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(SignTextUpdatePacket message, MessageContext ctx) {
            World world = ctx.getServerHandler().player.world;
            ((WorldServer) world).addScheduledTask(() -> {
                TileEntity tile = world.getTileEntity(message.pos);
                if (tile instanceof HasBackSideSignTileEntity) {
                    // 服务端更新文字
                    ((HasBackSideSignTileEntity) tile).updateText(message.slot, message.text, message.isLocking);
                    world.markBlockRangeForRenderUpdate(message.pos, message.pos);
                }
            });
            return null;
        }
    }
}