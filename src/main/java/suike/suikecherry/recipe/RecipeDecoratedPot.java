package suike.suikecherry.recipe;

import java.util.List;
import java.util.ArrayList;
import javax.annotation.Nullable;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.item.ModItemPotterySherd;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.achievement.ModAdvancements;

import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;

@EventBusSubscriber
public class RecipeDecoratedPot extends ShapedRecipes {

    private static final int[] hasItem = new int[]{5, 7, 3, 1};
    private static final int[] nullItem = new int[]{0, 2, 4, 6, 8};
    private static Ingredient BRICK_OR_SHERD;

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        BRICK_OR_SHERD = getBrickOrSherdIngredient();

        IRecipe recipe = new RecipeDecoratedPot().setRegistryName(
            new ResourceLocation(SuiKe.MODID, "decorated_pot")
        );

        event.getRegistry().register(recipe);
    }

    public RecipeDecoratedPot() {
        super("decorated_pot", 3, 3, 
            makeIngredientsList(),
            new ItemStack(BlockBase.DECORATED_POT)
        );
    }

    private static NonNullList<Ingredient> makeIngredientsList() {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);

        // 设置配方布局
        ingredients.set(1, BRICK_OR_SHERD); // 顶部位置
        ingredients.set(3, BRICK_OR_SHERD); // 左侧位置
        ingredients.set(5, BRICK_OR_SHERD); // 右侧位置
        ingredients.set(7, BRICK_OR_SHERD); // 底部位置

        // 其他位置为空
        return ingredients;
    }

    private static Ingredient getBrickOrSherdIngredient() {
        // 合并红砖和所有陶片
        List<ItemStack> validItems = new ArrayList<>();
        validItems.add(new ItemStack(Items.BRICK)); // 添加红砖
        validItems.addAll(ModItemPotterySherd.getAllSherdItme()); // 添加所有陶片

        return Ingredient.fromStacks(validItems.toArray(new ItemStack[0]));
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        // 获取输出物品 (陶罐)
        ItemStack output = super.getCraftingResult(inv);
        if (output.isEmpty()) return output;

        // 创建NBT标签存储陶片信息
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList sherdsList = new NBTTagList();

        // 从合成表位置对应的面获取 NBT
        for (int slot : hasItem) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack != null && !stack.isEmpty()) {
                if (stack.getItem() instanceof ModItemPotterySherd) {
                    // 如果是陶片，获取其类型
                    sherdsList.appendTag(new NBTTagString(
                        ((ModItemPotterySherd) stack.getItem()).getPotteryType()
                    ));
                } else if (stack.getItem() == Items.BRICK) {
                    // 如果是红砖，标记为"brick"
                    sherdsList.appendTag(new NBTTagString("brick"));
                }
            }
        }

        // 将陶片列表写入NBT
        nbt.setTag("Sherds", sherdsList);
        output.setTagCompound(nbt);
        return output;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        // 检查四个关键位置是否有有效物品
        for (int slot : hasItem) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty() || !BRICK_OR_SHERD.apply(stack)) {
                return false;
            }
        }

        // 检查其他位置是否为空
        for (int slot : nullItem) {
            if (!inv.getStackInSlot(slot).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @SubscribeEvent
    public static void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        ItemStack stack = event.crafting;

        // 检查是否是饰纹陶罐
        if (stack.getItem() == Item.getItemFromBlock(BlockBase.DECORATED_POT)) {
            if (stack.hasTagCompound() && isMaxSherdsPot(stack.getTagCompound())) {
                ModAdvancements.grant(event.player, ModAdvancements.CRAFT_BUILDING_POT);
            }
        }
    }

    // 是否满纹饰
    private static boolean isMaxSherdsPot(NBTTagCompound nbt) {
        if (nbt.hasKey("Sherds", Constants.NBT.TAG_LIST)) {
            NBTTagList list = nbt.getTagList("Sherds", Constants.NBT.TAG_STRING);
            if (list.tagCount() != 4) return false;

            for (int i = 0; i < 4; i++) {
                if ("brick".equals(list.getStringTagAt(i))) return false;
            }
        }
        return true; // 全非红砖
    }
}