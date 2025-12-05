package suike.suikecherry.tileentity;

import suike.suikecherry.sound.Sound;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.ModBlockHangingSign;
import suike.suikecherry.block.ModBlockHangingSignAttached;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.packets.HangingSignUpdatePacket;
import suike.suikecherry.client.render.tileentity.HangingSignTileEntityRender;
import suike.suikecherry.data.AxisPosition;

import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

// 悬挂告示牌 TileEntity 类
public class HangingSignTileEntity extends HasBackSideSignTileEntity {
    private int hasDisplayedSides = -1;
    private static boolean lastPickedSide = true;

    public int getDisplayedSides() {
        return this.hasDisplayedSides;
    }

    public static int getAndToggleLastPickedSide() {
        lastPickedSide = !lastPickedSide;
        return lastPickedSide ? 0 : 1;
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            HangingSignTileEntity.this.markDirty();
        }
    };

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ItemStack getDisplayItem(int slot) {
        if (slot == 0 || slot == 1) {
            return this.itemHandler.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }

    // 更新面
    public void upDisplayedSides() {
        boolean has0 = this.hasDisplayItems(0);
        boolean has1 = this.hasDisplayItems(1);

        if (has0 && has1) {
            this.hasDisplayedSides = 2; // 两面都有物品
        } else if (has0) {
            this.hasDisplayedSides = 0; // 仅正面（0号槽位）有物品
        } else if (has1) {
            this.hasDisplayedSides = 1; // 仅背面（1号槽位）有物品
        } else {
            this.hasDisplayedSides = -1; // 都没有
        }

        this.removeItemRenderPos(has0 ? -1 : 0);
        this.removeItemRenderPos(has1 ? -1 : 1);
    }

// 检查是否有占位
    public boolean hasDisplayItems(int slot) {
        if (slot == 0 || slot == 1) {
            return !this.itemHandler.getStackInSlot(slot).isEmpty();
        }
        return false;
    }

    @Override
    public boolean hasAny(int slot) {
        return super.hasAny(slot) || this.hasDisplayItems(slot);
    }

// 展示物品
    public boolean displayItems(World world, BlockPos clickPos, IBlockState state, EntityPlayer player, ItemStack heldItem, int slot, ResourceLocation placeSound) {
        if (slot == -1) return false;

        // 获取物品
        ItemStack copyItem = heldItem.copy();
        copyItem.setCount(1); // 展示一个
        // 修改物品
        this.modifyDisplayedItem(world, clickPos, slot, copyItem);

        // 减少物品栏物品
        if (!player.capabilities.isCreativeMode) {
            heldItem.shrink(1);
        }

        // 播放音效
        Sound.playSound(world, clickPos, placeSound);
        
        return true;
    }

// 移除展示的物品
    public boolean removeDisplayItems(World world, BlockPos clickPos, IBlockState state, EntityPlayer player, int slot) {
        if (slot == -1) return false;

        // 获取展示的物品
        ItemStack stackInSlot = this.itemHandler.getStackInSlot(slot);
        if (!stackInSlot.isEmpty()) {
            // 修改物品
            this.modifyDisplayedItem(world, clickPos, slot, ItemStack.EMPTY);

            // 给玩家物品
            ItemBase.givePlayerItem(player, stackInSlot.copy());

            return true;
        }
        return false;
    }

// 修改物品
    public void modifyDisplayedItem(World world, BlockPos clickPos, int slot, ItemStack newStack) {
        ItemStack copy = newStack.copy();
        this.itemHandler.setStackInSlot(slot, copy);
        this.markDirty();
        this.upDisplayedSides();
        if (!world.isRemote) {
            // 服务器同步物品
            PacketHandler.INSTANCE.sendToAllAround(
                new HangingSignUpdatePacket(clickPos, slot, copy),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), clickPos.getX(), clickPos.getY(), clickPos.getZ(), 64)
            );
        }
    }

// 存储 & 读取
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Items")) {
            itemHandler.deserializeNBT(compound.getCompoundTag("Items"));
        }
        this.upDisplayedSides();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setTag("Items", this.itemHandler.serializeNBT());
        return compound;
    }

// 存储渲染位置
    private AxisPosition itemRenderPos_0;
    private AxisPosition itemRenderPos_1;

    public AxisPosition getItemRenderPos(int slot) {
        if (slot == 0)
            return itemRenderPos_0;
        else if (slot == 1)
            return itemRenderPos_1;
        else
        return null;
    }

    public void removeItemRenderPos(int slot) {
        if (slot == 0)
            this.itemRenderPos_0 = null;
        else if (slot == 1)
            this.itemRenderPos_1 = null;
    }

    public void setItemRenderPos(int slot, AxisPosition renderPos) {
        if (slot == 0)
            this.itemRenderPos_0 = renderPos;
        else if (slot == 1)
            this.itemRenderPos_1 = renderPos;
    }

// 获取渲染位置
    public AxisPosition getItemRenderPosition(int slot) {
        AxisPosition renderPos = getItemRenderPos(slot);
        if (renderPos != null) return renderPos; // 有已存储的渲染位置直接返回, 避免重复获取

        IBlockState state = this.getWorld().getBlockState(this.getPos());
        Block block = state.getBlock();

        // 正方向悬挂告示牌
        if (block instanceof ModBlockHangingSign || ((ModBlockHangingSignAttached) block).isCardinalFacing(state, block.getMetaFromState(state))) {
            boolean isNorthSouth = (block instanceof ModBlockHangingSign) 
                ? ((ModBlockHangingSign) block).isNorthSouth(state.getValue(ModBlockHangingSign.FACING))
                : ((ModBlockHangingSignAttached) block).isNorthSouth(block.getMetaFromState(state));
    
            if (isNorthSouth) {
                if (slot == 0) {
                    this.setItemRenderPos(slot, HangingSignTileEntityRender.ROTATION_MAP.get(0)); // 北方
                } else {
                    this.setItemRenderPos(slot, HangingSignTileEntityRender.ROTATION_MAP.get(8)); // 南方
                }
            } else {
                if (slot == 0) {
                    this.setItemRenderPos(slot, HangingSignTileEntityRender.ROTATION_MAP.get(12)); // 西方
                } else {
                    this.setItemRenderPos(slot, HangingSignTileEntityRender.ROTATION_MAP.get(4)); // 东方 
                }
            }

            return this.getItemRenderPos(slot); // 返回刚存储的渲染位置
        }
        // 精确方向悬挂告示牌
        else if (block instanceof ModBlockHangingSignAttached) {
            int meta = block.getMetaFromState(state);
            if (slot == 1) {
                meta = (meta + 8) % 16; // 计算对称面meta值
            }
            this.setItemRenderPos(slot, HangingSignTileEntityRender.ROTATION_MAP.get(meta));
            return this.getItemRenderPos(slot);
        }

        // 默认返回: 北方 0 槽
        return HangingSignTileEntityRender.ROTATION_MAP.get(0);
    }

// 覆盖方法
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.itemHandler);
        }
        return super.getCapability(capability, facing);
    }
}