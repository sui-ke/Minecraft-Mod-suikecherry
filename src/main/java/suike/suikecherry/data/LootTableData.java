package suike.suikecherry.data;

import suike.suikecherry.SuiKe;
import suike.suikecherry.expand.Examine;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

import net.minecraftforge.fml.common.Loader;

public class LootTableData {
    public static final ResourceLocation underwater_ruin = registerLootTable("chests/underwater_ruin");
    public static final ResourceLocation big_underwater_ruin = registerLootTable("chests/big_underwater_ruin");

    private static ResourceLocation registerLootTable(String path) {
        return registerLootTable(SuiKe.MODID, path);
    }
    private static ResourceLocation registerLootTable(String modid, String path) {
        return registerLootTable(new ResourceLocation(modid, path));
    }
    private static ResourceLocation registerLootTable(ResourceLocation res) {
        if (!LootTableList.getAll().contains(res))  {
            return LootTableList.register(res);
        }
        return res;
    }

    public static void registerLootTable() {
        registerLootTableUNB();
    }

    private static void registerLootTableUNB() {
        if (Examine.UNBID) {
            registerLootTable("nb", "pig_towers");
            if (Examine.FuturemcID && !Loader.isModLoaded("patternbanners")) {
                registerLootTable("nb", "bastion_hold_futuremc");
                registerLootTable("nb", "bastion_treasure_futuremc");
            }
            else {
                registerLootTable("nb", "bastion_hold");
                registerLootTable("nb", "bastion_treasure");
            }
        }
    }
}