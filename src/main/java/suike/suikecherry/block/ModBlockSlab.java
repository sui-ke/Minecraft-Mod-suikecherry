package suike.suikecherry.block;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.expand.futuremc.Lantern;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;

//台阶类
public class ModBlockSlab extends BlockSlab {
    public ModBlockSlab(String name) {
        super(Material.WOOD);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        /*设置硬度*/this.setHardness(2.0F);
        /*设置抗爆性*/this.setResistance(3.0F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(0);
        this.type = ModBlockPlanks.EnumType.getEnumType(name);
        /*设置声音*/this.setSoundType(this.type.getWoodSound());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);

        /*设置单双层状态*/this.isDouble = name.endsWith("double");
        /*设置单双层状态*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(ModBlockPlanks.TYPE, type));
    }

    private Item item;
    public void setItem(Item item) {
        if (this.item == null) this.item = item;
    }

    private final ModBlockPlanks.EnumType type;

// 方块属性
    private final boolean isDouble;

    @Override
    public boolean isDouble() {
        return isDouble;
    }
    @Override
    public IProperty<?> getVariantProperty() {
        return ModBlockPlanks.TYPE;
    }
    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return this.type;
    }
    @Override
    protected BlockStateContainer createBlockState() {
        return this.isDouble() ? new BlockStateContainer(this, new IProperty[] {ModBlockPlanks.TYPE}) :
                                 new BlockStateContainer(this, new IProperty[] {HALF, ModBlockPlanks.TYPE});
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (!this.isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP) {
            meta |= 8;
        }
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState().withProperty(ModBlockPlanks.TYPE, this.type);
        if (!this.isDouble()) {
            state = state.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }
        return state;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public String getUnlocalizedName(int meta) {
        return super.getUnlocalizedName();
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        //返回台阶的物品
        return new ItemStack(this.item);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {
        int am = this.isDouble() ? 2 : 1;
        drops.add(new ItemStack(this.item, am));
    }


// 方块交互
    @Override
    public boolean onBlockActivated(World world, BlockPos clickPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing clickFacing, float hitX, float hitY, float hitZ) {
        if (!this.isDouble && state.getValue(ModBlockSlab.HALF) == EnumBlockHalf.TOP) {
            if (clickFacing == EnumFacing.UP) {
                return Lantern.lantern(world, clickPos.up(), player, hand, 0);
            }
        }
        return false;
    }
}