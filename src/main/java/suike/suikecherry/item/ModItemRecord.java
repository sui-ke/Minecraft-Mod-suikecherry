package suike.suikecherry.item;

import suike.suikecherry.SuiKe;

import net.minecraft.item.ItemRecord;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

// 唱片
public class ModItemRecord extends ItemRecord {
    public ModItemRecord(String name, String soundName) {
        super(name, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(SuiKe.MODID, soundName)));
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        this.soundName = soundName;

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    private final String soundName;

    @Override
    public SoundEvent getSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(SuiKe.MODID, soundName));
    }
}