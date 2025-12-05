package suike.suikecherry.client.render.item;

import suike.suikecherry.item.ModItmeDecoratedPot;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemRender {
    public static void render(ItemStack stack) {
        if (stack == null) return;

        Item item = stack.getItem();
        if (item instanceof ItemArmor) {
            ArmorTrimItemRender.renderItem(stack);
        }
        else if (item instanceof ModItmeDecoratedPot) {
            DecoratedPotItemRender.renderItem(stack);
        }
    }
}