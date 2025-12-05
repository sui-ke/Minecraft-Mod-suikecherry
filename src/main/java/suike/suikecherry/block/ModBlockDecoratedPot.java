package suike.suikecherry.block;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.item.ModItmeDecoratedPot;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.sound.ModSoundType;
import suike.suikecherry.tileentity.DecoratedPotTileEntity;
import suike.suikecherry.tileentity.DecoratedPotTileEntity.PotTileClient;
import suike.suikecherry.world.gen.decoratedpot.DecoratedPotGen;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Lists;

// 陶罐方块类
public class ModBlockDecoratedPot extends Block {
    public ModBlockDecoratedPot(String name) {
        super(Material.CLAY);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(ModSoundType.decoratedPot);

        Item itemBlock = new ModItmeDecoratedPot(this);
        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(itemBlock);

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
        return world.isRemote ? new PotTileClient() : new DecoratedPotTileEntity();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        if (side == EnumFacing.UP) {
            return BlockFaceShape.SOLID;
        }
        return BlockFaceShape.UNDEFINED;
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

// 模型透明
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

// 轮廓箱 & 碰撞箱
    protected static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, SHAPE);
    }

// 方块放置
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing facing = EnumFacing.fromAngle(placer.rotationYaw);
        world.setBlockState(pos, state.withProperty(BlockHorizontal.FACING, facing));

        // 读取物品NBT并应用到方块实体
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof DecoratedPotTileEntity) {
            if (stack.hasTagCompound()) {
                ((DecoratedPotTileEntity) tile).setDecoratedPotFromNBT(stack.getTagCompound());
            }
        }
    }

// 获取物品
    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        if (GuiScreen.isCtrlKeyDown()) return new ItemStack(this);

        DecoratedPotTileEntity tile = (DecoratedPotTileEntity) world.getTileEntity(pos);
        String[] sherdIDs = ((DecoratedPotTileEntity) tile).getAllPotteryType();
        return getHasNbtItemStack(sherdIDs);
    }

// 收获
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        if (world.isRemote && !(tile instanceof DecoratedPotTileEntity)) return;

        DecoratedPotTileEntity potTile = (DecoratedPotTileEntity) tile;

        String[] sherdIDs = potTile.getAllPotteryType();

        // 使用剑, 掉落碎片
        if (stack.getItem() instanceof ItemSword) {
            // 掉落碎片
            this.breakPot(world, pos, sherdIDs);
            // 播放破碎音效
            Sound.playSound(world, pos, "block.decorated_pot.shatter");
        }
        // 否则掉落完整的罐子
        else {
            ItemStack itemStack = this.getHasNbtItemStack(sherdIDs);
            Block.spawnAsEntity(world, pos, itemStack);
        }

        this.dropStoredItem(world, pos, potTile);
    }

    public ItemStack getHasNbtItemStack(String[] sherdIDs) {
        ItemStack itemStack = new ItemStack(this);
        if (!Arrays.equals(sherdIDs, DecoratedPotTileEntity.getNullSherdIDs())) {
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList sherdsList = new NBTTagList();
            for (String sherd : sherdIDs) {
                sherdsList.appendTag(new NBTTagString(sherd));
            }
            nbt.setTag("Sherds", sherdsList);
            itemStack.setTagCompound(nbt);
        }
        return itemStack;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!world.isRemote && player.capabilities.isCreativeMode) {        
            this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItem(EnumHand.MAIN_HAND));
        }
    }

// 弹射物破坏
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote) return;
        // 确保是弹射物
        if (this.isProjectile(entity)) {
            // 播放破坏音效
            Sound.playSound(world, pos, "block.decorated_pot.break");

            DecoratedPotTileEntity potTile = (DecoratedPotTileEntity) world.getTileEntity(pos);
            // 打破陶罐并掉落内容物
            this.breakPot(world, pos, potTile.getAllPotteryType());
            this.dropStoredItem(world, pos, potTile);

            world.setBlockToAir(pos);
        }
    }

    // 检查弹射物
    public static final List<Class<? extends Entity>> EXTRA_PROJECTILE_CLASSES = Lists.newArrayList(
        EntityFireball.class, EntityShulkerBullet.class
    );

    private static boolean isProjectile(Entity entity) {
        if (entity == null) return false;

        if (entity instanceof IProjectile) return true;

        for (Class<? extends Entity> clazz : EXTRA_PROJECTILE_CLASSES) {
            if (clazz.isInstance(entity)) {
                return true;
            }
        }
        return false;
    }

    public static void tryBreakPot(Entity entity, RayTraceResult result) {
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockPos = result.getBlockPos();
            IBlockState blockState = entity.world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            if (block instanceof ModBlockDecoratedPot) {
                block.onEntityCollidedWithBlock(entity.world, blockPos, blockState, entity);
            }
        }
    }

// 掉落物品
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof DecoratedPotTileEntity) {
            DecoratedPotTileEntity potTile = (DecoratedPotTileEntity) tile;

            for (String potteryType : potTile.getAllPotteryType()) {
                Item item = this.getSherd(potteryType);
                drops.add(new ItemStack(item));
            }

            if (potTile.hasStoredItem()) {
                drops.add(potTile.getStoredItem().copy());
            }
        } else {
            drops.add(new ItemStack(this));
        }
    }

    // 掉落陶片
    private void breakPot(World world, BlockPos pos, String[] sherdIDs) {
        for (String potteryType : sherdIDs) {
            Item item = this.getSherd(potteryType);
            Block.spawnAsEntity(world, pos, new ItemStack(item));
        }
    }

    public Item getSherd(String potteryType) {
        return "brick".equals(potteryType)
            ? Items.BRICK
            : Item.getByNameOrId(SuiKe.MODID + ":" + potteryType.replace("pattern", "sherd"));
    }

    // 掉落内容物
    private void dropStoredItem(World world, BlockPos pos, DecoratedPotTileEntity potTile) {
        if (potTile.hasStoredItem()) {
            Block.spawnAsEntity(world, pos, potTile.getStoredItem());
        }
    }

// 方块交互
    @Override
    public boolean onBlockActivated(World world, BlockPos clickPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing clickFacing, float hitX, float hitY, float hitZ) {
        // 为蹲下
        if (player.isSneaking()) return false;

        // 存储物品
        DecoratedPotTileEntity tile = (DecoratedPotTileEntity) world.getTileEntity(clickPos);
        return tile.addItem(player, player.getHeldItem(hand));
    }

// 比较器检测功能
    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof DecoratedPotTileEntity) {
            ItemStack stack = ((DecoratedPotTileEntity) tile).getStoredItem();
            if (!stack.isEmpty()) {
                float fillRatio = (float) stack.getCount() / stack.getMaxStackSize();
                return MathHelper.floor(fillRatio * 14 + 1);
            }
        }
        return 0;
    }

// 活塞推动
    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.DESTROY; // 推动时破坏
    }

// 创建特殊标记陶罐
    public static ItemStack createRandomSherdPot() {
        ItemStack pot = new ItemStack(BlockBase.DECORATED_POT);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("RandomSherdDisplay", true); // 添加特殊标记
        pot.setTagCompound(tag);
        return pot;
    }

// 物品提示
    private static final String[] facings = new String[]{"right", "back", "left", "front"};
    private static final String[] displayOrder = new String[]{"front", "back", "left", "right"};

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) return;

        if (nbt.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) {
            nbt = nbt.getCompoundTag("BlockEntityTag");
        }

        if (!nbt.hasKey("Sherds")) return;

        NBTTagList sherdIDs = nbt.getTagList("Sherds", Constants.NBT.TAG_STRING);
        Map<String, String> sherdMap = new HashMap<>(); // 创建方向 → 图案的映射

        for (int i = 0; i < sherdIDs.tagCount(); i++) {
            String sherd = sherdIDs.getStringTagAt(i);
            if (!"brick".equals(sherd)) {
                sherdMap.put(facings[i], sherd);
            }
        }

        for (String directionKey : displayOrder) {
            if (sherdMap.containsKey(directionKey)) {
                String facing = I18n.format("tooltip.suikecherry." + directionKey);
                String sherd = I18n.format("tooltip.suikecherry." + sherdMap.get(directionKey));
                tooltip.add(facing + sherd);
            }
        }
    }
}