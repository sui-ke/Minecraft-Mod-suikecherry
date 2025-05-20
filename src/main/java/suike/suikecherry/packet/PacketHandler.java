package suike.suikecherry.packet;

import suike.suikecherry.SuiKe;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SuiKe.MODID);

    public static void registerClientPackets() {
        // 服务器 → 客户端的包 // 客户端处理的包
        INSTANCE.registerMessage(ChestBoatUpdatePacket.ClientHandler.class, ChestBoatUpdatePacket.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(HangingSignUpdatePacket.ClientHandler.class, HangingSignUpdatePacket.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(RequestOpenChestBoatPacket.ClientHandler.class, RequestOpenChestBoatPacket.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(SignTextUpdatePacket.ClientHandler.class, SignTextUpdatePacket.class, 3, Side.CLIENT);
    }

    public static void registerServerPackets() {
        // 客户端 → 服务器的包 // 服务器处理的包
        INSTANCE.registerMessage(ChestBoatUpdatePacket.ServerHandler.class, ChestBoatUpdatePacket.class, 0, Side.SERVER);
        INSTANCE.registerMessage(HangingSignUpdatePacket.ServerHandler.class, HangingSignUpdatePacket.class, 1, Side.SERVER);
        INSTANCE.registerMessage(RequestOpenChestBoatPacket.ServerHandler.class, RequestOpenChestBoatPacket.class, 2, Side.SERVER);
        INSTANCE.registerMessage(SignTextUpdatePacket.ServerHandler.class, SignTextUpdatePacket.class, 3, Side.SERVER);
    }
}