package suike.suikecherry.item;

import java.util.List;
import java.util.ArrayList;

import suike.suikecherry.SuiKe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.creativetab.CreativeTabs;

import net.minecraftforge.fml.common.Loader;

// 锻造模板
public class ModItemSmithingTemplate extends Item {
    public ModItemSmithingTemplate(String name) {
        this(name, null, null);
    }
    public ModItemSmithingTemplate(String name, String lootModID, String targetTable) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.MISC);
        this.patternType = name.toString().replace("_armor_trim_smithing_template", "");
        ALL_ITEM.add(this);
        ALL_PATTERN_TYPE.add(this.patternType);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);

        if (lootModID != null && Loader.isModLoaded(lootModID)) {
            this.targetTable = new ResourceLocation(lootModID, targetTable);
        }
    }

    private final String patternType;
    public String getPatternType() {
        return this.patternType;
    }

    private static final List<String> ALL_PATTERN_TYPE = new ArrayList<>();
    public static List<String> getAllPatternType() {
        return new ArrayList<>(ALL_PATTERN_TYPE);
    }

    private static final List<ModItemSmithingTemplate> ALL_ITEM = new ArrayList<>();
    public static List<ItemStack> getAllTemplateItme() {
        List<ItemStack> stacks = new ArrayList<>();
        for (ModItemSmithingTemplate item : ALL_ITEM) {
            stacks.add(new ItemStack(item));
        }
        return stacks;
    }

    public boolean needAddLootTable = true;

    // 目标战利品列表
    public ResourceLocation targetTable;
    public ResourceLocation getTargetTable(String originalTargetTable) {
        return this.targetTable != null ? this.targetTable : new ResourceLocation(originalTargetTable);
    }
}