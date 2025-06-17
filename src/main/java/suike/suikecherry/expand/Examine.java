package suike.suikecherry.expand;

import suike.suikecherry.SuiKe;

import net.minecraftforge.fml.common.Loader;

//检查是否拥有联动模组模组
public class Examine {
    public static boolean UNBID = false; // 下界
    public static boolean OPTID = false; // 高清修复
    public static boolean OEID = false; // 海洋拓展
    public static boolean FuturemcID = false; // 未来MC
    public static boolean exnihilocreatioID = false; // 无中生有

    //检查是否拥有模组
    public static void examine() {
        /*下界*/if (Loader.isModLoaded("nb")) {UNBID = true;}
        /*海洋拓展*/if (Loader.isModLoaded("oe")) {OEID = true;}
        /*未来MC*/if (Loader.isModLoaded("futuremc")) {FuturemcID = true;}
        /*无中生有*/if (Loader.isModLoaded("exnihilocreatio")) {exnihilocreatioID = true;}

        try {
            Class.forName("optifine.Installer");
            OPTID = true;
        } catch (ClassNotFoundException e) {
            OPTID = false;
        }
    }
}