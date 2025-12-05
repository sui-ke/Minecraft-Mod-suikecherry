package suike.suikecherry.packet.packets;

import suike.suikecherry.tileentity.BrushableTileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

import io.netty.buffer.ByteBuf;

public class BrushableBlockUpdatePacket implements IMessage {
    private BlockPos pos = BlockPos.ORIGIN;
    private ItemStack stack = ItemStack.EMPTY;
    private EnumFacing facing = EnumFacing.UP;
    private int dusted;

    public BrushableBlockUpdatePacket() {}

    public BrushableBlockUpdatePacket(BlockPos pos, ItemStack stack, EnumFacing facing, int dusted) {
        this.pos = pos;
        this.stack = stack;
        this.facing = facing;
        this.dusted = dusted;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(facing.ordinal());
        buf.writeInt(dusted);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        stack = ByteBufUtils.readItemStack(buf);
        facing = EnumFacing.values()[buf.readInt()];
        dusted = buf.readInt();
    }

    // 客户端处理类: 处理服务器发来的包
    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler<BrushableBlockUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(BrushableBlockUpdatePacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                TileEntity tile = world.getTileEntity(message.pos);
                if (tile instanceof BrushableTileEntity) {
                    ((BrushableTileEntity) world.getTileEntity(message.pos)).upData(message.stack, message.facing, message.dusted);
                }
            });
            return null;
        }
    }

    // 服务端处理类: 处理客户端发来的包
    public static class ServerHandler implements IMessageHandler<BrushableBlockUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(BrushableBlockUpdatePacket message, MessageContext ctx) {
            return null;
        }
    }
}