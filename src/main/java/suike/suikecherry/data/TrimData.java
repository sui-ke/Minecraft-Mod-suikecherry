package suike.suikecherry.data;

import java.util.*;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.common.util.Constants;

import org.apache.commons.io.IOUtils;

public class TrimData {
    // 盔甲材质列表
    private static final Map<String, ResourceLocation> TRIM_TEXTURES = new HashMap<>();
    private static final Map<String, ResourceLocation> TRIM_LEG_TEXTURES = new HashMap<>();

    // 纹饰颜色列表
    private static final Map<String, float[]> COLOR_MAP = new HashMap<>();

    // 矿词到颜色材质映射
    private static final Map<String, String> ORE_PREFIX_TO_MATERIAL = createOreToMaterialList();
    private static final Map<String, String> createOreToMaterialList() {
        Map<String, String> map = new HashMap<>();
        map.put("gemDiamond", "diamond");       // 钻石
        map.put("dustDiamond", "diamond");      // 钻石
        map.put("gemEmerald", "emerald");       // 绿宝石
        map.put("ingotGold", "gold");           // 金
        map.put("dustGold", "gold");            // 金
        map.put("ingotIron", "iron");           // 铁
        map.put("dustIron", "iron");            // 铁
        map.put("gemQuartz", "quartz");         // 石英
        map.put("dustRedstone", "redstone");    // 红石
        map.put("gemLapis", "lapis");           // 青金石
        map.put("ingotCopper", "copper");       // 铜
        map.put("dustCopper", "copper");        // 铜
        map.put("gemAmethyst", "amethyst");     // 紫水晶
        map.put("ingotNetherite", "netherite"); // 下界合金
        map.put("itemResin", "resin");          // 树脂
        return map;
    }
    private static List<String> ALL_MATERIAL_TYPE = null;
    public static List<String> getAllOreToMaterial() {
        if (ALL_MATERIAL_TYPE != null) {
            return new ArrayList<>(ALL_MATERIAL_TYPE);
        }

        List<String> materials = new ArrayList<>();
        for (String material : ORE_PREFIX_TO_MATERIAL.keySet()) {
            materials.add(material);
        }

        ALL_MATERIAL_TYPE = materials;
        return new ArrayList<>(materials);
    }

// 添加材料物品
    public static void addItemMaterial(String material, String... oreDicts) {
        if (material.isEmpty()) return;
        for (String oreDict : oreDicts) {
            if (oreDict.isEmpty()) continue;
            ORE_PREFIX_TO_MATERIAL.put(oreDict, material);
        }
    }

// 获取材料的 Material
    public static String getMaterial(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        // 匹配矿词
        for (int id : OreDictionary.getOreIDs(stack)) {
            String material = ORE_PREFIX_TO_MATERIAL.get(OreDictionary.getOreName(id));
            if (material != null) {
                return material;
            }
        }

        return null;
    }

// 获取纹饰
    public static ResourceLocation getTrimTexture(ItemStack stack, boolean isLegSlot) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("Pattern")) return null;

        String patternType = stack.getTagCompound().getString("Pattern");
        if (patternType.isEmpty()) return null;

        return isLegSlot ? getTrimLegTexture(patternType) : getTrimTexture(patternType);
    }
    private static ResourceLocation getTrimTexture(String patternType) {
        return TRIM_TEXTURES.computeIfAbsent(patternType, 
            k -> new ResourceLocation(SuiKe.MODID, "textures/trims/humanoid/" + k + ".png"));
    }
    private static ResourceLocation getTrimLegTexture(String patternType) {
        return TRIM_LEG_TEXTURES.computeIfAbsent(patternType, 
            k -> new ResourceLocation(SuiKe.MODID, "textures/trims/humanoid_leggings/" + k + ".png"));
    }

// 获取纹饰颜色
    public static float[] getTrimColor(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("Material")) return null;

        String materialType = stack.getTagCompound().getString("Material");
        if (materialType.isEmpty()) return null;

        return getTrimColorRGB(materialType);
    }

    private static float[] getTrimColorRGB(String materialType) {
        return COLOR_MAP.computeIfAbsent(materialType, TrimData::readColorFromTexture);
    }
    private static float[] readColorFromTexture(String materialType) {
        ResourceLocation texture = getTrimColorTexture(materialType);
        InputStream in = null;
        try {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(texture);
            in = resource.getInputStream();
            BufferedImage image = ImageIO.read(in);

            // 取第一个像素 (最浅色)
            int rgb = image.getRGB(0, 0);
            return new float[] {
                ((rgb >> 16) & 0xFF) / 255.0f,
                ((rgb >> 8) & 0xFF) / 255.0f,
                (rgb & 0xFF) / 255.0f
            };
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    private static ResourceLocation getTrimColorTexture(String materialType) {
        return new ResourceLocation(SuiKe.MODID, "textures/trims/color_palettes/" + materialType + ".png");
    }

// 保存已制作的纹饰到玩家 NBT
    public static void addPatternTypeToPlayer(EntityPlayer player, String patternType) {
        NBTTagCompound playerData = player.getEntityData();
        NBTTagCompound persistentData = playerData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

        // 获取现有列表或创建新列表
        NBTTagList patternList;
        if (persistentData.hasKey("suikecherry.CraftArmorPatterns")) {
            patternList = persistentData.getTagList("suikecherry.CraftArmorPatterns", Constants.NBT.TAG_STRING);
        } else {
            patternList = new NBTTagList();
        }

        // 添加新图案 (避免重复)
        boolean alreadyExists = false;
        for (int i = 0; i < patternList.tagCount(); i++) {
            if (patternList.getStringTagAt(i).equals(patternType)) {
                alreadyExists = true;
                break;
            }
        }

        if (!alreadyExists) {
            patternList.appendTag(new NBTTagString(patternType));
            persistentData.setTag("suikecherry.CraftArmorPatterns", patternList);
            playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentData);
        }
    }

    public static Set<String> getPlayerPatternTypes(EntityPlayer player) {
        Set<String> patterns = new HashSet<>();
        NBTTagCompound playerData = player.getEntityData();
        NBTTagCompound persistentData = playerData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

        if (persistentData.hasKey("suikecherry.CraftArmorPatterns")) {
            NBTTagList patternList = persistentData.getTagList("suikecherry.CraftArmorPatterns", Constants.NBT.TAG_STRING);
            for (int i = 0; i < patternList.tagCount(); i++) {
                patterns.add(patternList.getStringTagAt(i));
            }
        }
        return patterns;
    }
}