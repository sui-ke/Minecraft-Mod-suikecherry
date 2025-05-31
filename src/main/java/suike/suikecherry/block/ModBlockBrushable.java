package suike.suikecherry.block;

import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.sound.ModSoundType;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.tileentity.BrushableTileEntity;
import suike.suikecherry.data.TreasureData;
import suike.suikecherry.proxy.ClientProxy;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityFallingBlock;

// 可疑的方块
public class ModBlockBrushable extends BlockFalling {
    public ModBlockBrushable(String name, Block defaultBlock, String brushingSound, SoundType soundType) {
        super(Material.SAND);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.DECORATIONS);
        /*设置硬度*/this.setHardness(0.25F);
        /*设置抗爆性*/this.setResistance(0.25F);
        /*设置挖掘等级*/this.setHarvestLevel("shovel", 0);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(soundType);
        /*设置方块数据*/this.setBlockData(defaultBlock, brushingSound, name);

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

// 方块数据
    private String brushingSound;
    private String brushingCompleteSound;
    private ResourceLocation suspiciousTextures1;
    private ResourceLocation suspiciousTextures2;
    private ResourceLocation suspiciousTextures3;
    private IBlockState defaultBlock;

    private void setBlockData(Block defaultBlock, String brushingSound, String name) {
        // 设置默认方块
        this.defaultBlock = defaultBlock.getDefaultState();

        // 设置音效
        this.brushingSound = brushingSound;
        this.brushingCompleteSound = brushingSound + ".complete";

        if (SuiKe.server) return;

        // 贴图资源
        this.suspiciousTextures1 = ClientProxy.addRegisterTextures(new ResourceLocation(SuiKe.MODID, "blocks/suspicious/" + name + "_1"));
        this.suspiciousTextures2 = ClientProxy.addRegisterTextures(new ResourceLocation(SuiKe.MODID, "blocks/suspicious/" + name + "_2"));
        this.suspiciousTextures3 = ClientProxy.addRegisterTextures(new ResourceLocation(SuiKe.MODID, "blocks/suspicious/" + name + "_3"));
    }
    public String getBrushingSound() {
        return this.brushingSound;
    }
    public String getBrushingCompleteSound() {
        return this.brushingCompleteSound;
    }
    public ResourceLocation getSuspiciousTextures(int dusted) {
        if (dusted == 1) return this.suspiciousTextures1;
        if (dusted == 2) return this.suspiciousTextures2;
        return this.suspiciousTextures3;
    }
    public IBlockState getParentBlock() {
        return this.defaultBlock;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new BrushableTileEntity();
    }

// 方块放置
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote) {
            if (placer instanceof EntityPlayer && ((EntityPlayer) placer).field_71075_bZ.isCreativeMode) {
                BrushableTileEntity tile = (BrushableTileEntity) world.getTileEntity(pos);
                if (tile != null) {
                    tile.initTreasure(TreasureData.structures[world.rand.nextInt(TreasureData.structures.length)]);
                }
            }
        }
    }

// 不允许收获
    @Override
    public void onEndFalling(World world, BlockPos pos, IBlockState fallingState, IBlockState hitState) {
        Sound.playSound(world, pos, this.getBrushingSound());
        if (world.getBlockState(pos).getBlock() instanceof ModBlockBrushable) {
            world.setBlockToAir(pos);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {}
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {}

    @Override
    protected void onStartFalling(EntityFallingBlock fallingEntity) {
        fallingEntity.shouldDropItem = false; // 禁止实体掉落物品
    }
}