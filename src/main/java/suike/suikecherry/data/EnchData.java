package suike.suikecherry.data;

import java.util.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;

public class EnchData {
    private static final List<EnchantmentData> ALL_POSSIBLE_ENCHANTMENTS = new ArrayList<>();

    public static void initEnchantment() {
        for (Enchantment enchantment : Enchantment.REGISTRY) {
            // 只选择可以出现在书上的附魔
            if (enchantment == null || !enchantment.isAllowedOnBooks()) continue;

            // 添加所有可能的等级 (从最小到最大-1)
            for (int level = enchantment.getMinLevel(); level <= Math.max(1, enchantment.getMaxLevel() - 1); level++) {
                ALL_POSSIBLE_ENCHANTMENTS.add(new EnchantmentData(enchantment, level));
            }
        }
    }

    public static ItemStack createRandomEnchantedBook(Random rand, ItemStack book) {
        if (book.getItem() != Items.ENCHANTED_BOOK) return book;

        // 随机决定附魔数量 (1~3个)
        int enchantCount = 1 + rand.nextInt(3);

        for (int i = 0; i < enchantCount; i++) {
            EnchantmentData enchantment = getRandomEnchantment(rand);
            if (enchantment != null) {
                // 添加随机附魔到书中
                ItemEnchantedBook.addEnchantment(book, enchantment);
            }
        }

        return book;
    }

    private static EnchantmentData getRandomEnchantment(Random rand) {
        if (ALL_POSSIBLE_ENCHANTMENTS.isEmpty()) {
            return null;
        }
        return ALL_POSSIBLE_ENCHANTMENTS.get(rand.nextInt(ALL_POSSIBLE_ENCHANTMENTS.size()));
    }
}