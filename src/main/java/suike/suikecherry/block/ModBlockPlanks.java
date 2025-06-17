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
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;
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

// 方块类型
    public static final PropertyEnum<ModBlockPlanks.EnumType> TYPE = PropertyEnum.create("type", ModBlockPlanks.EnumType.class);

    public static enum EnumType implements IStringSerializable {
        NONE("none", null, SoundType.WOOD, SoundType.PLANT),
        Cherry("cherry", MapColor.getBlockColor(EnumDyeColor.PINK), ModSoundType.cherryWood, ModSoundType.cherryLeaves),
        Bamboo("bamboo", MapColor.getBlockColor(EnumDyeColor.GREEN), ModSoundType.bamboo, ModSoundType.bamboo),
        Crimson("crimson", null, ModSoundType.stem, ModSoundType.stem);

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

        private EnumType(String name, MapColor mapColor, SoundType woodSound, SoundType leavesSound) {
            this.name = name;
            this.mapColor = mapColor;
            this.woodSound = woodSound;
            this.leavesSound = leavesSound;
        }

        public String getName() {
            return this.name;
        }

        public SoundType getWoodSound() {
            return this.woodSound;
        }

        public SoundType getLeavesSound() {
            return this.leavesSound;
        }

        public MapColor getMapColor() {
            return this.mapColor;
        }

        public int getMetadata() {
            return 0;
        }

        public String getUnlocalizedName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public static EnumType getEnumType(String name) {
            return EnumType_Map.getOrDefault(name.replaceFirst("_.*", ""), EnumType.NONE);
        }
    }
}