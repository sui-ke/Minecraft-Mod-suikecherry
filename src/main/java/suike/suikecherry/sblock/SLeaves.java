package suike.suikecherry.sblock;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.sound.SoundTypeLeaves;
import suike.suikecherry.particle.CherryParticle;

import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import suike.suikecherry.suikecherry.Tags;

//树叶类
public class SLeaves extends BlockLeaves implements SBlock {
    public SLeaves(String name) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setTranslationKey(name + "_" + Tags.MOD_ID);
        /*设置创造模式物品栏*/setCreativeTab(CreativeTabs.DECORATIONS);
        /*设置硬度*/setHardness(0.2F);
        /*设置抗爆性*/setResistance(0.2F);
        /*设置挖掘等级*/setHarvestLevel("shears", 0);
        /*设置不透明度*/setLightOpacity(0);
        /*设置声音*/setSoundType(new SoundTypeLeaves());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/ItemBase.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));

        /*添加腐烂效果*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(CHECK_DECAY, Boolean.valueOf(true))
            .withProperty(DECAYABLE, Boolean.valueOf(true)));//检查树干
    }

//方块属性
    //创建方块状态容器
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DECAYABLE, CHECK_DECAY); //包含腐烂、检查腐烂属性
    }
    //根据元数据获取方块状态
    public IBlockState getStateFromMeta(int meta) {
        /*return this.getDefaultState()
            .withProperty(CHECK_DECAY, Boolean.valueOf(true))
            .withProperty(DECAYABLE, Boolean.valueOf(true));*/
        return this.getDefaultState()
            .withProperty(DECAYABLE, (meta & 1) == 0)
            .withProperty(CHECK_DECAY, (meta & 2) != 0);
    }
    //根据方块状态获取元数据
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(DECAYABLE) ? 0 : 1) | 
               (state.getValue(CHECK_DECAY) ? 2 : 0);
    }
    //返回方块掉落的物品
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ItemBase.CHERRY_SAPLING; //返回当前方块本身
    }

//破坏部分
    //处理方块被收获的逻辑
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (!world.isRemote && stack.getItem() == Items.SHEARS) { //如果不是客户端并且使用剪刀
            spawnAsEntity(world, pos, new ItemStack(this)); //生成当前方块的掉落物
        } else {
            super.harvestBlock(world, player, pos, state, te, stack); //否则调用父类方法
        }
    }
    //当方块被剪切时返回掉落物品列表
    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return NonNullList.withSize(1, new ItemStack(this)); //返回当前方块的掉落物
    }

//粒子效果
    /*
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        super.randomDisplayTick(state, world, pos, rand);

        if (!SuiKe.server)
            CherryParticle.spawnParticle(world, pos);
    }
    */

//依附的木头
    @Override
    public EnumType getWoodType(int meta) {
        return EnumType.OAK;
    }

//模型透明
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED; // 设置为透明层
    }
    public boolean isOpaqueCube(IBlockState state) {
        return false; // 该方块是透明的
    }
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true; // 确保所有面都被渲染
    }
}