package suike.suikecherry.sblock;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.sound.SoundTypeWood;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.creativetab.CreativeTabs;

//原木类
public class SLog extends BlockLog implements SBlock {
    public SLog(String name) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        /*设置硬度*/setHardness(2.0F);
        /*设置抗爆性*/setResistance(5.0F);
        /*设置挖掘等级*/setHarvestLevel("axe", 0);
        /*设置不透明度*/setLightOpacity(255);
        /*设置声音*/setSoundType(new SoundTypeWood());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/ItemBase.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));

        /*设置朝向*/this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumAxis.Y));
    }

//方块属性
    public static final IProperty<EnumAxis> AXIS = BlockLog.LOG_AXIS;

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState();
        switch (meta & 12) {
            case 0:
                state = state.withProperty(AXIS, EnumAxis.Y);
                break;
            case 4:
                state = state.withProperty(AXIS, EnumAxis.X);
                break;
            case 8:
                state = state.withProperty(AXIS, EnumAxis.Z);
                break;
            default:
                state = state.withProperty(AXIS, EnumAxis.NONE);
        }
        return state;
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        switch ((EnumAxis) state.getValue(AXIS)) {
            case X:
                meta |= 4;
                break;
            case Z:
                meta |= 8;
                break;
            case NONE:
                meta |= 12;
        }
        return meta;
    }
}