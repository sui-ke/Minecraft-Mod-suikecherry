package suike.suikecherry.config;

import java.io.File;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.expand.exnihilocreatio.ExNihiloCreatioExpand;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.FMLCommonHandler;

public class Config {
    public static File configFile;
    public static File configFileCopy;
    public static File 无中生有configFile;
    //获取和读取配置文件
    public static void config() {
        File config;
        //获取 Minecraft/config/suike 目录
        if (SuiKe.server) {
            config = FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory();
        } else {
            config = Minecraft.getMinecraft().mcDataDir;
        }

        configFile = new File(config, "config/sui_ke/cherry/Cherry.cfg");
        configFileCopy = new File(config, "config/sui_ke/cherry/CherryCopy.cfg");
        无中生有configFile = new File(config, "config/exnihilocreatio");

        /*无中生有*/if (Examine.exnihilocreatioID) {ExNihiloCreatioExpand.expand();}
    }

    public static void configRead() {
        //检查配置文件
        CreateConfigFile.config();
        //读取配置文件
        ConfigRead.config();
    }

    public static void setBiomeID() {
        //CreateConfigFile.setBiomeID();
    }
}