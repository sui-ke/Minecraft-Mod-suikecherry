//*
package suike.suikecherry.expand.jei;

import java.util.*;
import java.util.stream.Collectors;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.item.ModItemSmithingTemplate;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.block.ModBlockSmithingTable;
import suike.suikecherry.data.TrimData;
import suike.suikecherry.client.render.entity.RenderPlayerModel;

import mezz.jei.api.*;
import mezz.jei.api.gui.*;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;

@JEIPlugin
public class SmithingTableJEIPlugin implements IModPlugin {

    private static final String SMITHING_TABLE = "suikecherry.smithing_table";

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(getRecipes(), SMITHING_TABLE);
        registry.addRecipeCatalyst(new ItemStack(BlockBase.SMITHING_TABLE), SMITHING_TABLE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(
            new RecipeCategory(guiHelper, SMITHING_TABLE)
        );
    }

    private static List<RecipeWrapper> getRecipes() {
        List<RecipeWrapper> wrappers = new ArrayList<>();
        List<String> materialOres = TrimData.getAllOreToMaterial();
        List<ItemStack> templates = ModItemSmithingTemplate.getAllTemplateItme();
        
        List<ItemStack> armors = Lists.newArrayList(
            new ItemStack(Items.DIAMOND_HELMET),
            new ItemStack(Items.DIAMOND_CHESTPLATE),
            new ItemStack(Items.DIAMOND_LEGGINGS),
            new ItemStack(Items.DIAMOND_BOOTS)
        );

        for (String materialOre : materialOres) {
            if (!ItemBase.oreStack(materialOre).isEmpty()) {
                wrappers.add(new RecipeWrapper(templates, armors, materialOre));
            }
        }

        return wrappers;
    }

    public static class RecipeCategory implements IRecipeCategory<RecipeWrapper> {
        private final String uid;
        private final IDrawable icon;
        private final IDrawable background;
        private final ResourceLocation slot;

        private final int yStep = 30; // 高度间距

        public RecipeCategory(IGuiHelper guiHelper, String uid) {
            this.uid = uid;
            this.background = guiHelper.createBlankDrawable(160, 125);
            this.icon = guiHelper.createDrawableIngredient(new ItemStack(BlockBase.SMITHING_TABLE));
            this.slot = new ResourceLocation("suikecherry", "textures/gui/jei/smithing.png");
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
        public void drawExtras(Minecraft minecraft) {
            for (int i = 0; i < 4; i++) {
                minecraft.getTextureManager().bindTexture(slot);
                Gui.drawModalRectWithCustomSizedTexture(16, 8 + i * yStep, 0, 0, 128, 18, 128, 18);
            }
        }

        @Override
        public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapper recipeWrapper, IIngredients ingredients) {
            IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

            for (int i = 0; i < 4; i++) {
                guiItemStacks.init(i * 4 + 0, true , 26 , 8 + i * yStep); // 模板
                guiItemStacks.init(i * 4 + 1, true , 44 , 8 + i * yStep); // 护甲
                guiItemStacks.init(i * 4 + 2, true , 62 , 8 + i * yStep); // 材料
                guiItemStacks.init(i * 4 + 3, false, 116, 8 + i * yStep); // 输出
            }

            List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
            for (int i = 0; i < 4; i++) {
                ItemStack armor = inputs.get(1).get(i);
                guiItemStacks.set(i * 4 + 0, inputs.get(0)); // 模板
                guiItemStacks.set(i * 4 + 1, armor);         // 护甲
                guiItemStacks.set(i * 4 + 2, inputs.get(2)); // 材料
                guiItemStacks.set(i * 4 + 3, recipeWrapper.getOutput(armor)); // 输出
            }
        }
    }

    public static class RecipeWrapper implements IRecipeWrapper {
        private final List<ItemStack> templates;
        private final List<ItemStack> armors;
        private final String materialOre;
 
        public RecipeWrapper(List<ItemStack> templates, List<ItemStack> armors, String materialOre) {
            this.templates = templates;
            this.armors = armors;
            this.materialOre = materialOre;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputs = new ArrayList<>();
            inputs.add(this.templates);
            inputs.add(this.armors);
            inputs.add(OreDictionary.getOres(this.materialOre));
            ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        }

        private ItemStack getOutput(ItemStack armor) {
            return SmithingTableJEIPlugin.getOutput(armor, ItemBase.oreStack(this.materialOre));
        }
    }

    private static ItemStack getOutput(ItemStack armor, ItemStack materialItme) {
        String material = TrimData.getMaterial(materialItme);
        if (material == null) {
            return armor;
        }

        ItemStack result = armor.copy();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Pattern", "none");
        tag.setString("Material", material);
        result.setTagCompound(tag);

        return result;
    }
}//*/