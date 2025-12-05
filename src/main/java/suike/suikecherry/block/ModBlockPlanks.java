package suike.suikecherry.block;

import java.util.Map;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.ModSoundType;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.creativetab.CreativeTabs;

import com.google.common.collect.ImmutableMap;

// 木板类
public class ModBlockPlanks extends Block {
    public ModBlockPlanks(String name) {
        super(Material.WOOD);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        /*设置硬度*/this.setHardness(2.0F);
        /*设置抗爆性*/this.setResistance(5.0F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(255);
        /*设置声音*/this.setSoundType(ModBlockPlanks.EnumType.getEnumType(name).getWoodSound());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

// 方块类型数据
    public static enum EnumType {
        NONE("none", null, SoundType.WOOD, SoundType.PLANT),
        Cherry("cherry", MapColor.getBlockColor(EnumDyeColor.PINK), ModSoundType.cherryWood, ModSoundType.cherryLeaves, new ResourceLocation("suikecherry", "block.cherry_wood.place")),
        Bamboo("bamboo", null, ModSoundType.bamboo, null, new ResourceLocation("suikecherry", "block.bamboo.place")),
        Crimson("crimson", null, ModSoundType.stem, null, new ResourceLocation("nb", "block.stem.step"));

        private static final Map<String, EnumType> EnumType_Map = ImmutableMap.<String, EnumType>builder()
            .put(Cherry.name, Cherry)
            .put(Bamboo.name, Bamboo)
            .put(Crimson.name, Crimson)
            .put("warped", Crimson)
            .build();

        private final String name;
        private final MapColor mapColor;
        private final SoundType woodSound;
        private final SoundType leavesSound;
        private final ResourceLocation placeSoundRes;

        private EnumType(String name, MapColor mapColor, SoundType woodSound, SoundType leavesSound) {
            this(name, mapColor, woodSound, leavesSound, null);
        }
        private EnumType(String name, MapColor mapColor, SoundType woodSound, SoundType leavesSound, ResourceLocation placeSoundRes) {
            this.name = name;
            this.mapColor = mapColor;
            this.woodSound = woodSound;
            this.leavesSound = leavesSound;
            this.placeSoundRes = placeSoundRes != null
                ? placeSoundRes
                : new ResourceLocation("block.wood.place");
        }

        public MapColor getMapColor() {
            return this.mapColor;
        }

        public SoundType getWoodSound() {
            return this.woodSound;
        }

        public SoundType getLeavesSound() {
            return this.leavesSound;
        }

        public ResourceLocation getPlaceSound() {
            return this.placeSoundRes;
        }

        public static EnumType getEnumType(String name) {
            return EnumType_Map.getOrDefault(name.replaceFirst("_.*", ""), EnumType.NONE);
        }
    }
}