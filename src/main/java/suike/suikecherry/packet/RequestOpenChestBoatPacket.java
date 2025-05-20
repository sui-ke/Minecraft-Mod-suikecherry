package suike.suikecherry.packet;

import suike.suikecherry.SuiKe;
import suike.suikecherry.client.gui.GuiID;
import suike.suikecherry.entity.boat.ModEntityChestBoat;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

import io.netty.buffer.ByteBuf;

public class RequestOpenChestBoatPacket implements IMessage {
    private int entityId;
    private boolean hasPlayerOpenChest;

    public RequestOpenChestBoatPacket() {}

    public RequestOpenChestBoatPacket(int entityId) {
        this.entityId = entityId;
    }
    public RequestOpenChestBoatPacket(int entityId, boolean hasPlayerOpenChest) {
        this.entityId = entityId;
        this.hasPlayerOpenChest = hasPlayerOpenChest;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(hasPlayerOpenChest);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        hasPlayerOpenChest = buf.readBoolean();
    }

    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler<RequestOpenChestBoatPacket, IMessage> {
        @Override
        public IMessage onMessage(RequestOpenChestBoatPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world == null) return;

                Entity entity = world.getEntityByID(message.entityId);

                if (entity instanceof ModEntityChestBoat) {
                    ((ModEntityChestBoat) entity).chestInventory.hasPlayerOpenChest = message.hasPlayerOpenChest;
                }
            });
            return null;
        }
    }

    // 服务端处理逻辑
    @SideOnly(Side.SERVER)
    public static class ServerHandler implements IMessageHandler<RequestOpenChestBoatPacket, IMessage> {
        @Override
        public IMessage onMessage(RequestOpenChestBoatPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            World world = player.world;

            // 在服务端主线程执行
            ((WorldServer) world).addScheduledTask(() -> {
            Entity entity = world.getEntityByID(message.entityId);
                if (entity instanceof ModEntityChestBoat) {
                    // 服务端打开容器
                    player.openGui(SuiKe.instance, GuiID.GUI_CHEST_BOAT, world, entity.getEntityId(), 0, 0);
                }
            });
            return null;
        }
    }
}