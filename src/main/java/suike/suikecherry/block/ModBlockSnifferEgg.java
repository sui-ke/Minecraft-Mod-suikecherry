package suike.suikecherry.block;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.item.ModItemBrush;
import suike.suikecherry.particle.ModParticle;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.packets.SpawnParticlesPacket;
import suike.suikecherry.entity.sniffer.SnifferEntity;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.NetworkRegistry;

// 嗅探兽蛋
public class ModBlockSnifferEgg extends Block {
    public ModBlockSnifferEgg(String name) {
        super(Material.DRAGON_EGG);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.MATERIALS);
        /*设置硬度*/this.setHardness(0.5F);
        /*设置抗爆性*/this.setResistance(0.5F);
        /*设置不透明度*/this.setLightOpacity(0);
        /*打开方块更新*/this.setTickRandomly(true);

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        Item itemBlock = new ItemBlock(this).setRegistryName(this.getRegistryName());
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(itemBlock);
        /*添加到考古学物品栏列表*/ModItemBrush.ARCHAEOLOGY_ITEMS.add(itemBlock);

        /*设置默认属性*/this.setDefaultState(this.blockState.getBaseState()
            .withProperty(HATCH, 0));
    }

// 方块数据
    public static final int FINAL_HATCH_STAGE = 2; // 最大孵化阶段值
    private static final int HATCHING_TIME = 24000; // 默认孵化时间（游戏刻）
    private static final int BOOSTED_HATCHING_TIME = 12000; // 加速后孵化时间
    private static final int MAX_RANDOM_CRACK_TIME_OFFSET = 300;

    public static final PropertyInteger HATCH = PropertyInteger.create("hatch", 0, 2);
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HATCH);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return BlockFaceShape.UNDEFINED;
    }

// 元数据
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(HATCH, meta);
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HATCH);
    }
    // 获取孵化阶段
    public int getHatchStage(IBlockState state) {
        return state.getValue(HATCH);
    }

// 轮廓箱&碰撞箱
    protected static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0625D, 0.0D, 0.125D, 0.9375D, 1.0D, 0.875D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, SHAPE);
    }

// 方块更新
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!this.isReadyToHatch(state)) {
            Sound.playSound(world, pos, "entity.sniffer.egg_crack");
            world.setBlockState(pos, state.withProperty(HATCH, this.getHatchStage(state) + 1), 3);
            spawnParticles(world, pos);
        } else {
            Sound.playSound(world, pos, "entity.sniffer.egg_hatch");
            world.setBlockToAir(pos);

            SnifferEntity sniffer = SnifferEntity.getChild(world);
            sniffer.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
            world.spawnEntity(sniffer);

            for (int i = 0; i < 3; i++) spawnParticles(world, pos);
        }
    }

    // 是否孵化完成
    private boolean isReadyToHatch(IBlockState state) {
        return this.getHatchStage(state) == FINAL_HATCH_STAGE;
    }

    private static void spawnParticles(World world, BlockPos pos) {
        if (world.isRemote) return;
        PacketHandler.INSTANCE.sendToAllAround(
            new SpawnParticlesPacket(pos, ModParticle.PARTICLE_VILLAGER_HAPPY),
            new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY() + 0.5D, pos.getZ(), 64)
        );
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (world.isAirBlock(pos.down())) {
            world.destroyBlock(pos, true);
        }
    }

// 方块放置
    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        if (!super.canPlaceBlockAt(world, pos)) {
            return false;
        }

        IBlockState downState = world.getBlockState(pos.down());
        Block downBlock = downState.getBlock();

        if (downBlock.isFullCube(downState)) {
            return true;
        }

        AxisAlignedBB downAABB = downState.getCollisionBoundingBox(world, pos.down());
        if (downAABB == null) return false;

        // 严格条件：maxY=1.0 且 X/Z全覆盖
        boolean isStableSurface = 
            downAABB.maxY == 1.0 &&
            (downAABB.maxX - downAABB.minX) == 1.0 &&
            (downAABB.maxZ - downAABB.minZ) == 1.0;

        return isStableSurface;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        boolean isBoosted = isAboveHatchBooster(world, pos);
        if (isBoosted) spawnParticles(world, pos);

        int baseTime = isBoosted ? BOOSTED_HATCHING_TIME : HATCHING_TIME;
        int delay = baseTime / 3 + world.rand.nextInt(MAX_RANDOM_CRACK_TIME_OFFSET);

        world.scheduleUpdate(pos, this, delay);
    }

    // 可加速方块: 苔藓, 树叶, 羊毛
    public static boolean isAboveHatchBooster(IBlockAccess world, BlockPos pos) {
        IBlockState downState = world.getBlockState(pos.down());
        Block downBlock = downState.getBlock();
        String blockName = downBlock.getRegistryName().toString();

        boolean isMoss = blockName.contains("moss") && downBlock.isFullCube(downState);

        return isMoss
                || downBlock instanceof BlockLeaves 
                || blockName.contains("wool");
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

// 物品提示
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tooltip.suikecherry.sniffer.egg"));
    }
}