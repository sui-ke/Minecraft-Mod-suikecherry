package suike.suikecherry.world.gen.structure;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public class LootTable {
    public static ResourceLocation underwater_ruin = registerLootTable("chests/underwater_ruin");
    public static ResourceLocation big_underwater_ruin = registerLootTable("chests/big_underwater_ruin");

    private static ResourceLocation registerLootTable(String path) {
        return registerLootTable(SuiKe.MODID, path);
    }
    private static ResourceLocation registerLootTable(String modid, String path) {
        ResourceLocation res = new ResourceLocation(modid, path);
        LootTableList.register(res);
        return res;
    }

    static {
        registerLootTable("nb", "pig_towers");
        registerLootTable("nb", "bastion_hold");
        registerLootTable("nb", "bastion_treasure");
        if (Examine.FuturemcID) {
            registerLootTable("nb", "bastion_hold_futuremc");
            registerLootTable("nb", "bastion_treasure_futuremc");
        }
    }
}