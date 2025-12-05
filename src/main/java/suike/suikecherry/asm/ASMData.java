package suike.suikecherry.asm;

import java.util.Map;
import java.util.HashMap;
import java.util.function.*;

import suike.suikecherry.asm.advice.*;
import suike.suikecherry.inter.IMethod;
import suike.suikecherry.expand.Examine;

import org.objectweb.asm.*;

public class ASMData {

    private static class Holder {
        static final Map<String, ASMData> ASM_DATA = initMap();
    }

    public final int classWriterType;
    public final int classReaderType;
    public final Function<AdviceAdapterData, IMethod> createMethod;

    public ASMData(int classWriterType, int classReaderType, Function<AdviceAdapterData, IMethod> createMethod) {
        this.classWriterType = classWriterType;
        this.classReaderType = classReaderType;
        this.createMethod = createMethod;
    }

    public static ASMData getDataByName(String transformedName) {
        if (Holder.ASM_DATA != null) {
            return Holder.ASM_DATA.get(normalizeClassName(transformedName));
        }
        return null;
    }
    public static String normalizeClassName(String transformedName) {
        // 从末尾反向查找 '@' 或 '.'
        for (int i = transformedName.length() - 1; i >= 0; i--) {
            char c = transformedName.charAt(i);
            if (c == '@') {
                return transformedName.substring(0, i); // 去除内存地址
            }
            if (c == '.') {
                return transformedName; // 无内存地址
            }
        }
        return transformedName;
    }

    private static void addMap(String transformed, ASMData data, boolean shouldAdd, Map<String, ASMData> map) {
        if (shouldAdd) {
            map.put(transformed, data);
        }
    }

    private static Map<String, ASMData> initMap() {
        Map<String, ASMData> map = new HashMap<>();
        boolean isClient = isClient();//hasClass("net.minecraft.client.main.Main");

        injectOriginal(map, isClient);
        injectProjectile(map, isClient);
        injectMod(map, isClient);

        if (!map.isEmpty()) {
            return map;
        }
        return null;
    }

    // 原版注入
    private static void injectOriginal(Map<String, ASMData> map, boolean isClient) {
        addMap(
            "net.minecraft.client.renderer.RenderItem",
            new ASMData(
                ClassWriter.COMPUTE_MAXS, 0,
                data -> new RenderItemMethod(data)
            ),
            isClient, map
        );
        addMap(
            "net.minecraft.client.renderer.entity.layers.LayerArmorBase",
            new ASMData(
                ClassWriter.COMPUTE_MAXS, 0,
                data -> new RenderArmorLayerMethod(data)
            ),
            isClient, map
        );
    }

    // 弹射物注入
    private static void injectProjectile(Map<String, ASMData> map, boolean isClient) {
        // 弹射物破坏陶罐注入
        ASMData projectileData = new ASMData(
            ClassWriter.COMPUTE_FRAMES, ClassReader.EXPAND_FRAMES,
            data -> new ProjectileMethod(data)
        );
        addMap(
            "net.minecraft.entity.projectile.EntityEgg", // 鸡蛋
            projectileData, true, map
        );
        addMap(
            "net.minecraft.entity.projectile.EntityPotion", // 药水
            projectileData, true, map
        );
        addMap(
            "net.minecraft.entity.projectile.EntitySnowball", // 雪球
            projectileData, true, map
        );
        addMap(
            "net.minecraft.entity.item.EntityExpBottle", // 经验瓶
            projectileData, true, map
        );
        addMap(
            "net.minecraft.entity.item.EntityEnderPearl", // 末影珍珠
            projectileData, true, map
        );
    }

    // 其他模组注入
    private static void injectMod(Map<String, ASMData> map, boolean isClient) {
        addMap(
            "thedarkcolour.futuremc.block.villagepillage.LanternBlock",
            new ASMData(
                ClassWriter.COMPUTE_FRAMES, ClassReader.EXPAND_FRAMES,
                data -> new LanternBlockMethod(data)
            ),
            hasClass("thedarkcolour.futuremc.FutureMC"), map
        );
    }

    private static boolean hasClass(String className) {
        try {
            ClassLoader launchClassLoader = ASMData.class.getClassLoader();
            launchClassLoader.loadClass(className);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private static boolean isClient() {
        try {
            return Thread.currentThread().getContextClassLoader()
                .getResource("net/minecraft/client/main/Main.class") != null;
        } catch (Throwable e) {
            return true;
        }
    }
}