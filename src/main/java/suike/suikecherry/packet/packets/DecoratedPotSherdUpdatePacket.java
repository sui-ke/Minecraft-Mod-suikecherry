package suike.suikecherry.packet.packets;

import suike.suikecherry.tileentity.DecoratedPotTileEntity.PotTileClient;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

import io.netty.buffer.ByteBuf;

public class DecoratedPotSherdUpdatePacket implements IMessage {
    private BlockPos pos = BlockPos.ORIGIN;
    private int successSign;

    public DecoratedPotSherdUpdatePacket() {}

    public DecoratedPotSherdUpdatePacket(BlockPos pos, boolean success) {
        this.pos = pos;
        this.successSign = success ? 1 : -1;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(successSign);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        successSign = buf.readInt();
    }

    // 客户端处理类: 处理服务器发来的包
    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler<DecoratedPotSherdUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(DecoratedPotSherdUpdatePacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                // 更新客户端 TileEntity 渲染
                World world = Minecraft.getMinecraft().world;
                TileEntity tile = world.getTileEntity(message.pos);
                if (tile instanceof PotTileClient) {
                    // 通知客户端更新渲染
                    ((PotTileClient) tile).triggerWobble(message.successSign);
                }
            });
            return null;
        }
    }

    public static class ServerHandler implements IMessageHandler<DecoratedPotSherdUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(DecoratedPotSherdUpdatePacket message, MessageContext ctx) {
            return null;
        }
    }
}