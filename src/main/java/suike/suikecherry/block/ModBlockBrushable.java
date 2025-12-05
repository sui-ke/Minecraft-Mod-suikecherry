package suike.suikecherry.block;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.sound.ModSoundType;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.item.ModItemBrush;
import suike.suikecherry.tileentity.BrushableTileEntity;
import suike.suikecherry.data.TreasureData;
import suike.suikecherry.proxy.ClientProxy;
import suike.suikecherry.particle.ModParticle;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.packets.SpawnParticlesPacket;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
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

import net.minecraftforge.fml.common.network.NetworkRegistry;

// 可疑的方块
public class ModBlockBrushable extends BlockFalling {
    public ModBlockBrushable(String name, Block defaultBlock, String brushingSound, SoundType soundType) {
        super(Material.SAND);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        //*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.DECORATIONS);
        /*设置硬度*/this.setHardness(0.25F);
        /*设置抗爆性*/this.setResistance(0.25F);
        /*设置挖掘等级*/this.setHarvestLevel("shovel", 0);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(soundType);
        /*设置方块数据*/this.setBlockData(defaultBlock, brushingSound, name);

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        Item itemBlock = new ItemBlock(this).setRegistryName(this.getRegistryName());
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(itemBlock);
        /*添加到考古学物品栏列表*/ModItemBrush.ARCHAEOLOGY_ITEMS.add(itemBlock);
    }

// 方块数据
    private String brokenSound;
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
        this.brokenSound = "block." + name + ".break";
        this.brushingCompleteSound = brushingSound + ".complete";

        if (SuiKe.isServer) return;

        // 贴图资源
        this.suspiciousTextures1 = new ResourceLocation(SuiKe.MODID, "textures/blocks/suspicious/" + name + "_1.png");
        this.suspiciousTextures2 = new ResourceLocation(SuiKe.MODID, "textures/blocks/suspicious/" + name + "_2.png");
        this.suspiciousTextures3 = new ResourceLocation(SuiKe.MODID, "textures/blocks/suspicious/" + name + "_3.png");
    }
    public String getBrokenSound() {
        return this.brokenSound;
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
    public IBlockState getDefaultBlock() {
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
            if (placer instanceof EntityPlayer && ((EntityPlayer) placer).capabilities.isCreativeMode) {
                BrushableTileEntity tile = (BrushableTileEntity) world.getTileEntity(pos);
                if (tile != null) {
                    List<TreasureData.Structure> structures = TreasureData.getStructureList();
                    tile.initTreasure(structures.get(world.rand.nextInt(structures.size())));
                }
            }
        }
    }

// 不允许收获
    @Override
    public void onEndFalling(World world, BlockPos pos, IBlockState fallingState, IBlockState hitState) {
        if (world.isRemote) return;

        Sound.playSound(world, pos, this.getBrokenSound());
        if (world.getBlockState(pos).getBlock() instanceof ModBlockBrushable) {
            world.setBlockToAir(pos);
            this.spawnBrokenParticles(world, pos);
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) return;

        if (canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= 0) {
            if (world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
                ModBlockBrushableFallingEntity entity = new ModBlockBrushableFallingEntity(
                    world,
                    pos.getX() + 0.5D,
                    pos.getY(),
                    pos.getZ() + 0.5D,
                    world.getBlockState(pos)
                );
                this.onStartFalling(entity);
                world.spawnEntity(entity);
            }
            else {
                // 未加载区块, 直接移除方块
                world.setBlockToAir(pos);
            }
        }
    }

    public class ModBlockBrushableFallingEntity extends EntityFallingBlock {
        public ModBlockBrushableFallingEntity(World world) {
            super(world);
        }

        private ModBlockBrushableFallingEntity(World world, double x, double y, double z, IBlockState fallingBlockState) {
            super(world, x, y, z, fallingBlockState);
        }

        @Override
        public EntityItem entityDropItem(ItemStack item, float a) {
            ModBlockBrushable block = (ModBlockBrushable) this.getBlock().getBlock();
            Sound.playSound(world, this.getPos(), block.getBrokenSound());
            block.spawnBrokenParticles(world, this.getPos());

            return null;
        }

        public BlockPos getPos() {
            return new BlockPos(this.posX, this.posY, this.posZ);
        }
    }

    private void spawnBrokenParticles(World world, BlockPos pos) {
        float particleType = this == BlockBase.SUSPICIOUS_SAND ? 1.1F : 1.2F;
        PacketHandler.INSTANCE.sendToAllAround(
            new SpawnParticlesPacket(pos, particleType),
            new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64)
        );
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState state, int fortune) {}
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {}
}