package suike.suikecherry.tileentity;

import java.io.*;
import java.util.Random;

import suike.suikecherry.sound.Sound;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.item.ModItemPotterySherd;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.block.ModBlockBrushable;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.packets.BrushableBlockUpdatePacket;
import suike.suikecherry.data.EnchData;
import suike.suikecherry.data.TreasureData;
import suike.suikecherry.data.TreasureData.Structure;
import suike.suikecherry.data.AxisPosition;
import suike.suikecherry.achievement.ModAdvancements;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.ITickable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.enchantment.EnchantmentHelper;

import net.minecraftforge.fml.common.network.NetworkRegistry;

public class BrushableTileEntity extends TileEntity implements ITickable {
    private int dusted;
    private int recoveryTime;
    private int lastRecoveryTime;
    private EnumFacing brushFacing;
    private ItemStack treasure = ItemStack.EMPTY;

    public void reloadRecoveryTime() {
        this.recoveryTime = 40;
        this.lastRecoveryTime = 0;
    }

    public void reloadLastRecoveryTime() {
        this.lastRecoveryTime = 4;
    }

    public int getDusted() {
        return this.dusted;
    }

    public void setBrushFacing(EnumFacing brushFacing) {
        if (this.brushFacing == null) this.brushFacing = brushFacing;
    }

    public EnumFacing getBrushFacing() {
        return this.brushFacing;
    }

    public ModBlockBrushable getParentBlock() {
        return (ModBlockBrushable) this.getParentBlockState().getBlock();
    }
    public IBlockState getParentBlockState() {
        return this.world.getBlockState(this.pos);
    }

    // 初始化宝藏
    public void initTreasure(World world, BlockPos pos, Structure structure) {
        this.pos = pos;
        this.world = world;
        this.initTreasure(structure);
    }
    public void initTreasure(Structure structure) {
        Random rand = this.getRandom(structure);
        TreasureData treasureData = structure.getRandomTreasure(rand);
        Item item = Item.getByNameOrId(treasureData.getTreasure());
        ItemStack treasure = new ItemStack(item, 1, treasureData.getMeta());

        if (ItemBase.isValidItemStack(treasure)) {
            // 如果有耐久
            if (item.isDamageable()) {
                int maxDurability = treasure.getMaxDamage();
                int randomDamage = rand.nextInt(maxDurability); // 随机物品耐久
                treasure.setItemDamage(randomDamage);
            }
            // 附魔书
            else if (item == Items.ENCHANTED_BOOK) {
                EnchData.createRandomEnchantedBook(this.world.rand, treasure); // 随机附魔书
            }

            this.setTreasure(treasure);
            this.markDirty();
        }
    }
    public void setTreasure(ItemStack treasure) {
        this.treasure = treasure;
    }

    // 获取宝藏
    public ItemStack getTreasure() {
        return this.treasure;
    }

    // 复原延迟
    @Override
    public void update() {
        if (this.dusted > 0) {
            if (this.recoveryTime > 0) {
                this.recoveryTime--;
            } else {
                if (this.lastRecoveryTime > 0) {
                    this.lastRecoveryTime--;
                } else {
                    this.lastRecoveryTime = 4;
                    this.dusted--; // 减少刷扫等级
                }
            }

            if (this.dusted == 0) {
                this.brushFacing = null; // 重置刷扫方向
            }
        }
    }

    // 更新数据
    public void upData(ItemStack treasure, EnumFacing brushFacing, int dusted) {
        this.setBrushFacing(brushFacing);
        this.reloadRecoveryTime(); // 重置复原延迟

        if (!this.world.isRemote) {
            PacketHandler.INSTANCE.sendToAllAround(
                new BrushableBlockUpdatePacket(this.pos, treasure, brushFacing, this.dusted),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 64)
            );
        }
        else {
            this.setTreasure(treasure);
            this.dusted = dusted;
            this.markDirty();
        }
    }

// 刷扫方块
    public void upDusted(EntityPlayer player, ItemStack brushStack, EnumFacing brushFacing) {
        ModBlockBrushable block = (ModBlockBrushable) this.getParentBlockState().getBlock();

        this.dusted++;

        if (this.dusted >= 4) { // 刷扫完成
            this.spawnTreasureItem(player);
            this.world.setBlockState(this.pos, block.getDefaultBlock());
            Sound.playSound(this.world, this.pos, block.getBrushingCompleteSound());

            if (player != null && brushStack != null) {
                int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, brushStack);

                boolean damage = true;
                if (efficiencyLevel > 0) {
                    if (Math.random() * 10 < efficiencyLevel) {
                        damage = false;
                    }
                }

                if (damage) {
                    brushStack.damageItem(1, player);
                }
            }

            return;
        }

        this.upData(this.getTreasure(), brushFacing, this.dusted);
    }

// 生成掉落物
    private void spawnTreasureItem(EntityPlayer player) {
        BlockPos facingPos = this.pos.offset(this.brushFacing);

        this.world.spawnEntity(new EntityItem(
            this.world,
            facingPos.getX() + 0.5D,
            facingPos.getY() + 0.5D,
            facingPos.getZ() + 0.5D,
            this.getTreasure()
        ));

        Item item = this.getTreasure().getItem();
        if (item == Item.getItemFromBlock(BlockBase.SNIFFER_EGG)) {
            ModAdvancements.grant(player, ModAdvancements.SNIFFER_EGG);
        } else if (item instanceof ModItemPotterySherd) {
            ModAdvancements.grant(player, ModAdvancements.SALVAGE_SHERD);
        }
    }

// 存储 & 读取
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("Dusted")) {
            this.dusted = compound.getInteger("Dusted");
        }
        if(compound.hasKey("Treasure")) {
            this.setTreasure(new ItemStack(compound.getCompoundTag("Treasure")));
        }
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setInteger("Dusted", this.dusted);
        compound.setTag("Treasure", this.getTreasure().writeToNBT(new NBTTagCompound()));
        return compound;
    }

// 获取随机
    private static final long REGION_SEED_OFFSET = 1145141919810L;
    private Random getRandom(Structure structure) {
        long regionSeed = this.world.getSeed()
            + structure.ordinal() * 31L
            + (this.pos.getX() >> 4) * 1234567L
            + (this.pos.getZ() >> 4) * 9876543L
            + REGION_SEED_OFFSET;

        long preciseSeed = regionSeed 
            + this.pos.getX() * 127L 
            + this.pos.getY() * 317L 
            + this.pos.getZ() * 911L;

        return new Random(regionSeed + preciseSeed);
    }

// 渲染
    public boolean canRender() {
        return this.dusted != 0;
    }
}