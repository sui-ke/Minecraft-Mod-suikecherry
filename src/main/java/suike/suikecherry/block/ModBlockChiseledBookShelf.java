package suike.suikecherry.block;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.ModSoundType;
import suike.suikecherry.tileentity.ChiseledBookShelfTileEntity;
import suike.suikecherry.inter.ICardinal;
import suike.suikecherry.achievement.ModAdvancements;
import suike.suikecherry.config.ConfigValue;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Enchantments;
import net.minecraft.enchantment.EnchantmentHelper;

import net.minecraftforge.fml.common.network.NetworkRegistry;

// 雕纹书架
public class ModBlockChiseledBookShelf extends Block implements ICardinal {
    public ModBlockChiseledBookShelf(String name) {
        super(Material.WOOD);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.REDSTONE);
        /*设置硬度*/this.setHardness(1.5F);
        /*设置抗爆性*/this.setResistance(1.5F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(ModSoundType.chiseledBookShelf);

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(
            new ItemBlock(this) {
                @Override
                public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
                    if (tab == CreativeTabs.DECORATIONS || tab == CreativeTabs.REDSTONE) {
                        items.add(new ItemStack(this));
                    }
                }
            }.setRegistryName(this.getRegistryName())
        );

        /*设置默认属性*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH));
    }

// 方块数据
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new ChiseledBookShelfTileEntity();
    }

// 元数据
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
            .withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

// 方块放置
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing facing = EnumFacing.fromAngle(placer.rotationYaw);
        world.setBlockState(pos, state.withProperty(BlockHorizontal.FACING, facing));
    }

// 收获
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (((World) world).isRemote) return;

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ChiseledBookShelfTileEntity) {
            drops.addAll(((ChiseledBookShelfTileEntity) tile).getAllBook());
        }
        // 不需要精准采集时可以掉落自身
        if (!ConfigValue.chiseledBookShelfNeedSilkTouch) {
            drops.add(new ItemStack(this));
        }
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        if (world.isRemote) return;

        if (tile instanceof ChiseledBookShelfTileEntity) {
            this.dropAllBook(world, pos, ((ChiseledBookShelfTileEntity) tile));
        }
        // 如果需要精准采集则检查是否有精准采集, 否则掉落自身
        if (ConfigValue.chiseledBookShelfNeedSilkTouch) {
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
                Block.spawnAsEntity(world, pos, new ItemStack(this));
            }
        } else {
            Block.spawnAsEntity(world, pos, new ItemStack(this));
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!world.isRemote && player.capabilities.isCreativeMode) {        
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof ChiseledBookShelfTileEntity) {
                this.dropAllBook(world, pos, ((ChiseledBookShelfTileEntity) tile));
            }
        }
    }

    private void dropAllBook(World world, BlockPos pos, ChiseledBookShelfTileEntity tile) {
        for (ItemStack itme : tile.getAllBook()) {
            Block.spawnAsEntity(world, pos, itme);
        }
    }

// 比较器检测功能
    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof ChiseledBookShelfTileEntity) {
            ChiseledBookShelfTileEntity bookShelf = ((ChiseledBookShelfTileEntity) tile);
            int a = bookShelf.getLastActivatedSlot() + 1;
            if (a > 0) {
                ModAdvancements.grant(bookShelf.getLastActivatedPlayer(), ModAdvancements.CHISELED_BOOKSHELF);
                return a;
            }
        }
        return 0;
    }

// 方块交互
    @Override
    public boolean onBlockActivated(World world, BlockPos clickPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing clickFacing, float hitX, float hitY, float hitZ) {
        // 为蹲下
        if (player.isSneaking()) return false;

        // 检查是否点击槽位面
        EnumFacing blockFacing = state.getValue(FACING);
        if (!blockFacing.getOpposite().equals(clickFacing)) return false;

        // 获取点击槽位
        float slotHitX = this.isNorthSouth(blockFacing) ? hitX : hitZ;
        int slot = getClickSlot(slotHitX, hitY, this.isSouthWest(blockFacing));

        if (slot == -1) return false;

        ChiseledBookShelfTileEntity tile = (ChiseledBookShelfTileEntity) world.getTileEntity(clickPos);

        // 移除书本
        if (tile.hasBook(slot)) return tile.removeBook(player, slot);

        // 尝试添加书本
        return tile.tryAddBook(player, player.getHeldItem(hand), slot);
    }

    private int getClickSlot(float hitX, float hitY, boolean isSouthWest) {
        int slot = -1;

        for (int i = 0; i < SLOT_BOUNDS.length; i++) {
            float[] bounds = SLOT_BOUNDS[i];
            float minX = bounds[0];
            float maxX = bounds[1];
            float minY = bounds[2];
            float maxY = bounds[3];

            // 检查点击坐标是否在当前槽位范围内
            if (hitX >= minX && hitX <= maxX && 
                hitY >= minY && hitY <= maxY) {
                slot = i; // 返回槽位编号 (0 ~ 5)
                break;
            }
        }

        if (slot != -1 && isSouthWest) {
            if (slot == 0) return 2;
            if (slot == 2) return 0;
            if (slot == 3) return 5;
            if (slot == 5) return 3;
        }

        return slot;
    }

    private static final float[][] SLOT_BOUNDS = {
        // 槽位1: (2,10) ~ (5,15)
        {1f/16f, 5f/16f, 9f/16f, 15f/16f},
        // 槽位2: (7,10) ~ (10,15)
        {6f/16f, 10f/16f, 9f/16f, 15f/16f},
        // 槽位3: (12,10) ~ (15,15)
        {11f/16f, 15f/16f, 9f/16f, 15f/16f},
        // 槽位4: (2,2) ~ (5,7)
        {1f/16f, 5f/16f, 1f/16f, 7f/16f},
        // 槽位5: (7,2) ~ (10,7)
        {6f/16f, 10f/16f, 1f/16f, 7f/16f},
        // 槽位6: (12,2) ~ (15,7)
        {11f/16f, 15f/16f, 1f/16f, 7f/16f}
    };
}