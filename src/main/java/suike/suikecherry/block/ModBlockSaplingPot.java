package suike.suikecherry.block;

import java.util.List;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.item.ModItemSapling;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

// 树苗盆栽类
public class ModBlockSaplingPot extends Block {
    public ModBlockSaplingPot(String name) {
        super(Material.CIRCUITS);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置不透明度*/this.setLightOpacity(0);

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
    }

    private ModItemSapling item;
    public void setItem(ModItemSapling item) { // 仅允许树苗设置
        if (this.item == null && item.getRegistryName().toString().contains("sapling")) {
            this.item = item;
        }
    }

// 轮廓箱 & 碰撞箱
    protected static final AxisAlignedBB box = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return box;
    }
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
    }

// 获取物品 & 掉落物
    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        //返回树苗的物品
        return new ItemStack(this.item);
    }
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {
        //返回树苗盆栽的掉落物
        drops.add(new ItemStack(Item.getByNameOrId("minecraft:flower_pot")));
        drops.add(new ItemStack(this.item));
    }

// 交互
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // 替换当前方块为花盆
        world.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), 2);

        // 将植物给玩家
        ItemBase.givePlayerItem(player, new ItemStack(this.item));

        return true;
    }

// 方块更新
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!world.getBlockState(pos.down()).isOpaqueCube()) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

// 状态覆盖
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return BlockFaceShape.UNDEFINED;
    }
}