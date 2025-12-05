package suike.suikecherry.config;

import java.io.File;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.expand.exnihilocreatio.ExNihiloCreatioExpand;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.FMLCommonHandler;

public class Config {
    public static File config;
    public static File configFile;
    public static File configFileCopy;
    public static File exnihilocreatioConfigFile;
    //获取和读取配置文件
    public static void config() {
        //获取 Minecraft/config/suike 目录
        if (SuiKe.isServer) {
            config = FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory();
        } else {
            config = Minecraft.getMinecraft().mcDataDir;
        }

        configFile = new File(config, "config/sui_ke/cherry/Cherry.cfg");
        configFileCopy = new File(config, "config/sui_ke/cherry/CherryCopy.cfg");
        exnihilocreatioConfigFile = new File(config, "config/exnihilocreatio");

        /*无中生有*/if (Examine.exnihilocreatioID) {ExNihiloCreatioExpand.expand();}
    }

    public static void configRead() {
        //检查配置文件
        CreateConfigFile.config();
        //读取配置文件
        ConfigRead.config();

        if (CreateConfigFile.oldConfigVersion != -1 && CreateConfigFile.oldConfigVersion <= 11) {
            ConfigValue.cherryBiomeBaseHeight = 2.0f;
            CreateConfigFile.upConfigValue("B:cherryBiomeBaseHeight", "2.0");
        }
    }

    public static void configReadTreasure() {
        TreasureList.config();
        TreasureListRead.config();
    }
}