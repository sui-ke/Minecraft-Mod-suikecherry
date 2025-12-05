package suike.suikecherry.expand.jei;

import java.util.*;
import java.util.stream.Collectors;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.data.TreasureData;

import mezz.jei.api.*;
import mezz.jei.api.gui.*;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.ingredients.IIngredients;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

@JEIPlugin
public class TreasureDataJEIPlugin implements IModPlugin {
    private static final int xStart = 0; // 起始位置
    private static final int yStart = 5;
    private static final int xStep = 55; // 间距
    private static final int yStep = 18;
    private static final int pageX = 142; // 页码起始位置
    private static final int pageY = 116;

    private static final String DESERT_WELL = "suikecherry.desert_well";       // 沙漠水井
    private static final String DESERT_PYRAMID = "suikecherry.desert_pyramid"; // 沙漠神殿
    private static final String OCEAN_RUINS = "suikecherry.ocean_ruins";       // 海底废墟
    private static final String TRAIL_RUINS = "suikecherry.trail_ruins";       // 古迹废墟

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(getTreasureRecipes(TreasureData.Structure.desertWell), DESERT_WELL);
        registry.addRecipes(getTreasureRecipes(TreasureData.Structure.desertPyramid), DESERT_PYRAMID);
        registry.addRecipes(getTreasureRecipes(TreasureData.Structure.oceanRuins), OCEAN_RUINS);
        registry.addRecipes(getTreasureRecipes(TreasureData.Structure.trailRuins), TRAIL_RUINS);

        // 添加描述
        registry.addRecipeCatalyst(new ItemStack(ItemBase.BRUSH), DESERT_WELL);
        registry.addRecipeCatalyst(new ItemStack(ItemBase.BRUSH), DESERT_PYRAMID);
        registry.addRecipeCatalyst(new ItemStack(ItemBase.BRUSH), OCEAN_RUINS);
        registry.addRecipeCatalyst(new ItemStack(ItemBase.BRUSH), TRAIL_RUINS);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        registry.addRecipeCategories(
            new TreasureLootCategory(guiHelper, DESERT_WELL),
            new TreasureLootCategory(guiHelper, DESERT_PYRAMID),
            new TreasureLootCategory(guiHelper, OCEAN_RUINS),
            new TreasureLootCategory(guiHelper, TRAIL_RUINS)
        );
    }

    private static List<TreasureLootWrapper> getTreasureRecipes(TreasureData.Structure structure) {
        List<TreasureLootWrapper> wrappers = new ArrayList<>();
        List<TreasureData> validItems = structure.getTreasureList().stream()
            .filter(TreasureData::isValid)
            .collect(Collectors.toList());

        // 每页显示18个物品 (3x6)
        int itemsPerPage = 18;
        int pageCount = (int) Math.ceil((double) validItems.size() / itemsPerPage);

        for (int page = 0; page < pageCount; page++) {
            int start = page * itemsPerPage;
            int end = Math.min(start + itemsPerPage, validItems.size());

            List<ItemStack> items = new ArrayList<>();
            List<Float> chances = new ArrayList<>();

            for (int i = start; i < end; i++) {
                TreasureData data = validItems.get(i);
                items.add(new ItemStack(Item.getByNameOrId(data.getTreasure()), 1, data.getMeta()));
                chances.add((data.getChance() / structure.getTotalChance()) * 100);
            }

            wrappers.add(new TreasureLootWrapper(items, chances, page, pageCount));
        }

        return wrappers;
    }

    public static class TreasureLootCategory implements IRecipeCategory<TreasureLootWrapper> {
        private final String uid;
        private final IDrawable icon;
        private final IDrawable background;

        public TreasureLootCategory(IGuiHelper guiHelper, String uid) {
            this.uid = uid;
            this.background = guiHelper.createBlankDrawable(160, 125);
            this.icon = guiHelper.createDrawable(this.getIconRes(), 0, 0, 16, 16, 16, 16);
        }

        public ResourceLocation getIconRes() {
            return new ResourceLocation(
                SuiKe.MODID,
                "textures/gui/jei/" + this.uid.replace("suikecherry.", "") + ".png"
            );
        }

        @Override
        public String getUid() {
            return uid;
        }

        @Override
        public String getModName() {
            return "cherry";
        }

        @Override
        public String getTitle() {
            return I18n.format("jei." + uid);
        }

        @Override
        public IDrawable getBackground() {
            return background;
        }

        @Override
        public IDrawable getIcon() {
            return icon;
        }

        @Override
        public void setRecipe(IRecipeLayout recipeLayout, TreasureLootWrapper recipeWrapper, IIngredients ingredients) {
            IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

            // 初始化所有槽位
            for (int y = 0; y < 6; y++) {
                for (int x = 0; x < 3; x++) {
                    int index = y * 3 + x;
                    if (index < recipeWrapper.getItems().size()) {
                        int xPos = xStart + x * xStep;
                        int yPos = yStart + y * yStep;

                        guiItemStacks.init(index, false, xPos, yPos);
                        guiItemStacks.set(index, recipeWrapper.getItems().get(index));
                    }
                }
            }
        }
    }

    public static class TreasureLootWrapper implements IRecipeWrapper {
        private final List<ItemStack> items;
        private final List<Float> chances;
        private final int page;
        private final int pageCount;

        public TreasureLootWrapper(List<ItemStack> items, List<Float> chances, int page, int pageCount) {
            this.items = items;
            this.chances = chances;
            this.page = page;
            this.pageCount = pageCount;
        }

        public List<ItemStack> getItems() {
            return items;
        }

        public List<Float> getChances() {
            return chances;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setOutputs(ItemStack.class, items);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            // 绘制物品概率
            for (int y = 0; y < 6; y++) {
                for (int x = 0; x < 3; x++) {
                    int index = y * 3 + x;
                    if (index < items.size()) {
                        float chance = chances.get(index);
                        String chanceText = String.format("%.2f%%", chance);

                        int xPos = xStart + x * xStep + 20;
                        int yPos = yStart + y * yStep + 6;

                        minecraft.fontRenderer.drawString(chanceText, xPos, yPos, 0x404040);
                    }
                }
            }

            // 绘制页码
            if (this.pageCount > 1) {
                String pageText = String.format("%d/%d", this.page + 1, this.pageCount);
                minecraft.fontRenderer.drawString(pageText, pageX, pageY, 0x404040);
            }
        }
    }
}