package suike.suikecherry.sblock;

import java.lang.reflect.Field;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.sound.SoundTypeWood;

import thedarkcolour.futuremc.block.villagepillage.LanternBlock;

import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.IStringSerializable;
import net.minecraft.entity.player.EntityPlayer;

//台阶类
public class SBlockSlab extends BlockSlab implements SBlock {
    public SBlockSlab(String name, boolean isDouble) {
        /*创建方块实例*/super(Material.WOOD);
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置硬度*/setHardness(2.0F);
        /*设置抗爆性*/setResistance(5.0F);
        /*设置挖掘等级*/setHarvestLevel("axe", 0);
        /*设置不透明度*/setLightOpacity(0);
        /*设置声音*/setSoundType(new SoundTypeWood());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);

        /*设置单双层状态*/this.isDouble = isDouble;
        /*设置单双层状态*/this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, Variant.DEFAULT));
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
    public String getUnlocalizedName(int meta) {
        return super.getUnlocalizedName();
    }

//获取物品&掉落物
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        //返回台阶的物品
        return new ItemStack(ItemBase.CHERRY_SLAB);
    }
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (this.isDouble()) {
            //如果是双层台阶，掉落两个单层台阶
            drops.add(new ItemStack(ItemBase.CHERRY_SLAB, 2));
        } else {
            //如果是单层台阶，掉落一个单层台阶
            drops.add(new ItemStack(ItemBase.CHERRY_SLAB, 1));
        }
    }

//方块交互
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state.getValue(SBlockSlab.HALF) == EnumBlockHalf.TOP) {
            ItemStack itemstack = player.getHeldItem(hand);
            //检查玩家手持物
            if (itemstack.getItem().getRegistryName().toString().equals("futuremc:lantern")) {
                if (facing == EnumFacing.UP) {
                    Block block = Block.getBlockFromName("futuremc:lantern");
                    IBlockState lantern = block.getDefaultState();

                    try {
                        Field hangingField = LanternBlock.class.getDeclaredField("HANGING");
                        hangingField.setAccessible(true);
                        PropertyBool hangingProperty = (PropertyBool) hangingField.get(null);
                        
                        lantern = block.getDefaultState().withProperty(hangingProperty, false);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    world.setBlockState(pos.up(), lantern);
                    /*播放灯笼音效*/world.playSound(null, pos.up(), new SoundEvent(new ResourceLocation("futuremc", "lantern_place")), SoundCategory.BLOCKS, 1.0F, 1.0F);
                    player.getHeldItem(hand).shrink(1);

                    return true;
                }
            }
        }

        return false;
    }
}