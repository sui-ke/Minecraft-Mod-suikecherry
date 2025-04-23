package suike.suikecherry.sblock;

import suike.suikecherry.sound.SoundTypeWood;

import suike.suikecherry.suikecherry.Tags;
import thedarkcolour.futuremc.block.villagepillage.LanternBlock;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.entity.player.EntityPlayer;

//台阶类
public class SBlockSlab extends BlockSlab implements SBlock {
    public SBlockSlab(String name, boolean isDouble) {
        /*创建方块实例*/super(Material.WOOD);
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setTranslationKey(name + "_" + Tags.MOD_ID);
        /*设置硬度*/setHardness(2.0F);
        /*设置抗爆性*/setResistance(3.0F);
        /*设置挖掘等级*/setHarvestLevel("axe", 0);
        /*设置不透明度*/setLightOpacity(0);
        /*设置声音*/setSoundType(new SoundTypeWood());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);

        /*设置单双层状态*/this.isDouble = isDouble;
        /*设置单双层状态*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(VARIANT, Variant.DEFAULT));
    }

    private Item item;
    protected void setItem(Item item) {
        this.item = item;
    }

//方块属性
    public boolean isDouble; //是否是双层
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.<Variant>create("variant", Variant.class);
    public static final PropertyEnum<EnumBlockHalf> HALF = PropertyEnum.create("half", EnumBlockHalf.class);

    public static boolean notIsDouble(IBlockState state) {
        return state.getValue(SBlockSlab.HALF) == EnumBlockHalf.TOP ||
               state.getValue(SBlockSlab.HALF) == EnumBlockHalf.BOTTOM;
    }
    @Override
    public boolean isDouble() {
        return isDouble;
    }
    @Override
    protected BlockStateContainer createBlockState() {
        //创建方块状态容器，根据单双层状态设置不同的属性
        return this.isDouble() ? new BlockStateContainer(this, new IProperty[] {VARIANT}) 
                               : new BlockStateContainer(this, new IProperty[] {HALF, VARIANT});
    }
    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return stack.getMetadata() == 0 ? Variant.DEFAULT : null;
    }
    @Override
    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (state.getValue(HALF) == EnumBlockHalf.TOP) {
            meta |= 0x8;
        }
        return meta;
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState().withProperty(VARIANT, Variant.DEFAULT);
        if (!this.isDouble()) {
            state = state.withProperty(HALF, (meta & 0x8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
        }
        return state;
    }
    public static enum Variant implements IStringSerializable {
        DEFAULT;
        public String getName() {
            return "default";
        }
    }
    @Override
    public String getTranslationKey(int meta) {
        return super.getTranslationKey();
    }

//获取物品&掉落物
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        //返回台阶的物品
        return new ItemStack(this.item);
    }
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {
        int am = this.isDouble() ? 2 : 1;
        drops.add(new ItemStack(this.item, am));
    }

//方块交互
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!this.isDouble && state.getValue(SBlockSlab.HALF) == EnumBlockHalf.TOP) {
            if (facing == EnumFacing.UP)
                return BlockBase.lantern(world, pos.up(), player, hand, 0);
        }
        return false;
    }
}