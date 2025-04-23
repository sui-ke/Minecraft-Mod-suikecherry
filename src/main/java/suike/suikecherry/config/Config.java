package suike.suikecherry.config;

import java.io.File;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.expand.exnihilocreatio.ExNihiloCreatioExpand;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

public class Config {
    public static File configFile;
    public static File configFileCopy;
    public static File 无中生有configFile;

    // 获取和读取配置文件
    public static void config() {
        File configDir;

        // 根据运行环境（服务端/客户端）选择正确的配置目录
        if (SuiKe.server) {
            // 服务端：使用服务器数据目录
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            configDir = server.getDataDirectory(); // 返回服务端根目录（如 ./world/）
        } else {
            // 客户端：使用 Minecraft 游戏目录
            configDir = Minecraft.getMinecraft().gameDir; // 返回 .minecraft/ 目录
        }

        // 初始化配置文件路径
        configFile = new File(configDir, "config/sui_ke/cherry/Cherry.cfg");
        configFileCopy = new File(configDir, "config/sui_ke/cherry/CherryCopy.cfg");
        无中生有configFile = new File(configDir, "config/exnihilocreatio");

        // 检查并加载无中生有（Ex Nihilo Creatio）扩展
        if (Examine.exnihilocreatioID) {
            ExNihiloCreatioExpand.expand();
        }
    }

    public static void configRead() {
        CreateConfigFile.config(); // 检查或创建配置文件
        ConfigRead.config();       // 读取配置文件
    }

    public static void setBiomeID() {
        // 预留方法（如需设置生物群系ID）
    }
}