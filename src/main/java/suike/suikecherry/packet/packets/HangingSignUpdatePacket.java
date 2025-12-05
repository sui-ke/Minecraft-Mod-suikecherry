package suike.suikecherry.packet.packets;

import suike.suikecherry.tileentity.HangingSignTileEntity;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

import io.netty.buffer.ByteBuf;

public class HangingSignUpdatePacket implements IMessage {
    private BlockPos pos = BlockPos.ORIGIN;
    private int slot;
    private ItemStack stack = ItemStack.EMPTY;

    public HangingSignUpdatePacket() {}

    public HangingSignUpdatePacket(BlockPos pos, int slot, ItemStack stack) {
        this.pos = pos;
        this.slot = slot;
        this.stack = stack;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(slot);
        ByteBufUtils.writeTag(buf, stack.serializeNBT());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        slot = buf.readInt();
        stack = new ItemStack(ByteBufUtils.readTag(buf));
    }

    // 客户端处理类: 处理服务器发来的包
    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler<HangingSignUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(HangingSignUpdatePacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                // 更新客户端 TileEntity 渲染
                World world = Minecraft.getMinecraft().world;
                TileEntity tile = world.getTileEntity(message.pos);
                if (tile instanceof HangingSignTileEntity) {
                    // 通知客户端更新渲染
                    ((HangingSignTileEntity) tile).modifyDisplayedItem(world, message.pos, message.slot, message.stack);
                }
            });
            return null;
        }
    }

    // 服务端处理类: 处理客户端发来的包
    public static class ServerHandler implements IMessageHandler<HangingSignUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(HangingSignUpdatePacket message, MessageContext ctx) {
            // 在服务器线程处理逻辑
            EntityPlayerMP player = ctx.getServerHandler().player;
            World world = player.world;
            TileEntity tile = world.getTileEntity(message.pos);
            if (tile instanceof HangingSignTileEntity) {
                // 触发服务端的 modifyDisplayedItem
                ((HangingSignTileEntity) tile).modifyDisplayedItem(world, message.pos, message.slot, message.stack);
            }
            return null;
        }
    }
}