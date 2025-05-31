package suike.suikecherry.world.gen.structure;

import suike.suikecherry.SuiKe;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public class LootTable {
    public static ResourceLocation underwater_ruin = LootTableList.register(new ResourceLocation(SuiKe.MODID, "chests/underwater_ruin"));
    public static ResourceLocation big_underwater_ruin = LootTableList.register(new ResourceLocation(SuiKe.MODID, "chests/big_underwater_ruin"));
}