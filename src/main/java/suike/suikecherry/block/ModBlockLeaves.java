package suike.suikecherry.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.config.ConfigValue;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;

//树叶类
public class ModBlockLeaves extends BlockLeaves {
    public ModBlockLeaves(String name) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.DECORATIONS);
        /*设置硬度*/this.setHardness(0.2F);
        /*设置抗爆性*/this.setResistance(0.2F);
        /*设置挖掘等级*/this.setHarvestLevel("shears", 0);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(ModBlockPlanks.EnumType.getEnumType(name).getLeavesSound());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));

        /*添加腐烂效果*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(DECAYABLE, Boolean.valueOf(true))
            .withProperty(CHECK_DECAY, Boolean.valueOf(true)));//检查树干
    }

    private Item sapling;
    public void setSapling(Item sapling) {
        if (this.sapling == null) this.sapling = sapling;
    }

// 方块属性
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DECAYABLE, CHECK_DECAY);
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
            .withProperty(DECAYABLE, (meta & 1) == 0)
            .withProperty(CHECK_DECAY, (meta & 2) != 0);
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(DECAYABLE) ? 0 : 1) | 
               (state.getValue(CHECK_DECAY) ? 2 : 0);
    }

// 破坏部分
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (!world.isRemote && stack.getItem() == Items.SHEARS) { // 使用剪刀
            Block.spawnAsEntity(world, pos, new ItemStack(this));
        } else {
            super.harvestBlock(world, player, pos, state, te, stack);
        }
    }
    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return NonNullList.withSize(1, new ItemStack(this));
    }
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return rand.nextFloat() < 6 ? this.sapling : Item.getByNameOrId("minecraft:stick");
    }

// 方块放置
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state
                .withProperty(DECAYABLE, false)
                .withProperty(CHECK_DECAY, false));
    }

// 粒子效果
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        super.randomDisplayTick(state, world, pos, rand);

        int baseChance = ConfigValue.cherryParticleChance;
        if (baseChance == 0 || !world.isRemote) return;

        IBlockState downBlockState = world.getBlockState(pos.down());

        if (!downBlockState.getBlock().isFullCube(downBlockState)) {

            // 如果是可腐烂的方块，概率提高 3 倍 (更容易出现粒子)
            if (state.getValue(DECAYABLE)) {
                baseChance = (int) (baseChance / 3);
            }

            if (rand.nextInt(baseChance) == 0) {
                CherryParticle.spawnParticle(world, pos);
            }
        }
    }

// 依附的木头
    @Override
    public EnumType getWoodType(int meta) {
        return EnumType.OAK;
    }

// 模型透明
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED; // 设置为透明层
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false; // 该方块是透明的
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true; // 确保所有面都被渲染
    }
}