package suike.suikecherry.data;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
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
    private static final Map<String, ResourceLocation> TRIM_TEXTURES = new HashMap<>();
    private static final Map<String, ResourceLocation> TRIM_LEG_TEXTURES = new HashMap<>();

    private static final Map<String, float[]> COLOR_MAP = new HashMap<>();

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
        map.put("itemResin", "resin");          // 树脂砖
        return map;
    }

    private static final Map<String, String> ITEM_TO_MATERIAL = createItemToMaterialList();
    private static final Map<String, String> createItemToMaterialList() {
        Map<String, String> map = new HashMap<>();
        map.put(createString(Items.SLIME_BALL), "resin"); // 粘液球
        return map;
    }

// 添加材料物品
    public static void addItemMaterial(String material, String... oreDicts) {
        if (material.isEmpty()) return;
        for (String oreDict : oreDicts) {
            if (oreDict.isEmpty()) continue;
            ORE_PREFIX_TO_MATERIAL.put(oreDict, material);
        }
    }
    public static void addItemMaterial(String material, ItemStack... stacks) {
        if (material.isEmpty()) return;
        for (ItemStack stack : stacks) {
            if (ItemBase.isValidItemStack(stack)) {
                ITEM_TO_MATERIAL.put(createString(stack), material);
            }
        }
    }

// 获取材料的 Material
    public static String getMaterial(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        // 优先匹配矿词
        for (int id : OreDictionary.getOreIDs(stack)) {
            String material = ORE_PREFIX_TO_MATERIAL.get(OreDictionary.getOreName(id));
            if (material != null) {
                return material;
            }
        }

        System.out.println("粘液球: " + createString(Items.SLIME_BALL));
        System.out.println("输入: " + createString(stack));
        return ITEM_TO_MATERIAL.get(createString(stack));
    }

// 精简 ItemStack
    private static String createString(Item stack) {
        return createString(new ItemStack(stack));
    }
    private static String createString(ItemStack stack) {
        return createKeyStack(stack).toString();
    }
    private static ItemStack createKeyStack(ItemStack stack) {
        ItemStack key = stack.copy();
        key.setCount(1);          // 忽略数量差异
        key.clearCustomName();    // 移除 自定义名称
        key.setTagCompound(null); // 移除 NBT
        return key;
    }

// 获取纹饰
    public static ResourceLocation getTrimTexture(ItemStack stack, boolean isLegSlot) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Pattern")) return null;

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
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Material")) return null;

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
            SuiKe.LOGGER.info("Failed to read color from texture: " + texture + e);
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