package suike.suikecherry.expand;

import suike.suikecherry.SuiKe;

import net.minecraftforge.fml.common.Loader;

//检查是否拥有联动模组模组
public class Examine {
    public static boolean UNBID = false; // 下界
    public static boolean OPTID = false; // 高清修复
    public static boolean OEID = false; // 海洋拓展
    public static boolean sakuraID = false; // 樱
    public static boolean MobendsID = false; // 弯曲动作
    public static boolean FuturemcID = false; // 未来MC
    public static boolean BlockArmorID = false;
    public static boolean exnihilocreatioID = false; // 无中生有

    //检查是否拥有模组
    public static void examine() {
        /*海洋*/OEID = Loader.isModLoaded("oe");
        /*下界*/UNBID = Loader.isModLoaded("nb");
        /*樱*/sakuraID = Loader.isModLoaded("sakura");
        /*弯曲动作*/MobendsID = Loader.isModLoaded("mobends");
        /*未来MC*/FuturemcID = Loader.isModLoaded("futuremc");
        /*方块盔甲*/BlockArmorID = Loader.isModLoaded("blockarmor");
        /*无中生有*/exnihilocreatioID = Loader.isModLoaded("exnihilocreatio");

        try {
            Class.forName("optifine.Installer");
            OPTID = true;
        } catch (ClassNotFoundException e) {
            OPTID = false;
        }
    }

    public static boolean hasMixin() {
        return Loader.isModLoaded("mixinbooter");
    }
}