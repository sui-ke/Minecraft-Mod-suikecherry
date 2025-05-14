package suike.suikecherry.packet;

import suike.suikecherry.SuiKe;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PacketHandler {
    public static int packetId = 0;
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SuiKe.MODID);

    public static void registerClientPackets() {
        // 服务器 → 客户端的包
        INSTANCE.registerMessage(
            HangingSignUpdatePacket.ClientHandler.class, // 客户端处理类
            HangingSignUpdatePacket.class,
            packetId++,
            Side.CLIENT // 服务器发往客户端
        );
    }

    public static void registerServerPackets() {
        // 客户端 → 服务器的包（如果需要）
        INSTANCE.registerMessage(
            HangingSignUpdatePacket.ServerHandler.class, // 服务端处理类
            HangingSignUpdatePacket.class,
            packetId++,
            Side.SERVER // 客户端发往服务器
        );
    }
}


// INSTANCE.registerMessage(PlaySoundPacket.Handler.class, PlaySoundPacket.class, packetId++, Side.CLIENT);