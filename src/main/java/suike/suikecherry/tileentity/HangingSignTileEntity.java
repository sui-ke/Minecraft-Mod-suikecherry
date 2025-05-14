package suike.suikecherry.tileentity;

import java.util.Map;
import java.util.HashMap;

import suike.suikecherry.sound.Sound;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.ModBlockHangingSign;
import suike.suikecherry.block.ModBlockHangingSignAttached;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.HangingSignUpdatePacket;

import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.commons.lang3.tuple.Pair;

// 悬挂告示牌 TileEntity 类
public class HangingSignTileEntity extends HasBackSideSignTileEntity {
    private int hasDisplayedSides = -1;
    private boolean lastPickedSide = true;
    public static final ItemStack isNull = ItemStack.EMPTY;

    public int getAndToggleLastPickedSide() {
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
        compound.setTag("Items", this.itemHandler.serializeNBT());
        return super.writeToNBT(compound);
    }

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

    public ItemStack getDisplayItem(int slot) {
        if (slot == 0 || slot == 1) {
            return this.itemHandler.getStackInSlot(slot);
        }
        return isNull;
    }

    public boolean hasDisplayItems(int slot) {
        if (slot == 0 || slot == 1) {
            return !this.itemHandler.getStackInSlot(slot).isEmpty();
        }
        return false;
    }

    @Override
    public boolean hasAny(int slot) {
        return super.hasAny(slot) && hasDisplayItems(slot);
    }

    public int getDisplayedSides() {
        return this.hasDisplayedSides;
    }

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

        this.removeRnderPos(has0 ? -1 : 0);
        this.removeRnderPos(has1 ? -1 : 1);
    }

    public void modifyDisplayedItem(World world, BlockPos clickPos, int slot, ItemStack newStack) {
        ItemStack copy = newStack.copy();
        this.itemHandler.setStackInSlot(slot, copy);
        this.markDirty();
        this.upDisplayedSides();
        if (!world.isRemote) {
            // 服务器同步物品
            PacketHandler.INSTANCE.sendToAllAround(
                new HangingSignUpdatePacket(clickPos, slot, copy),
                new NetworkRegistry.TargetPoint(
                    world.provider.getDimension(),
                    clickPos.getX(), clickPos.getY(), clickPos.getZ(),
                    64
                )
            );
        }
    }

// 展示物品
    public boolean displayItems(World world, BlockPos clickPos, IBlockState state, EntityPlayer player, ItemStack heldItem, int slot) {
        if (slot == -1) return false;

        // 获取物品
        ItemStack copy = heldItem.copy();
        copy.setCount(1);
        // 修改物品
        this.modifyDisplayedItem(world, clickPos, slot, copy);

        // 减少物品栏物品
        if (!player.field_71075_bZ.isCreativeMode) heldItem.shrink(1); // 使用混淆名检查是否是创造

        // 播放音效
        Sound.playSound(world, clickPos, "block.cherrywood.place");
        
        return true;
    }

// 移除展示的物品
    public boolean removeDisplayItems(World world, BlockPos clickPos, IBlockState state, EntityPlayer player, int slot) {
        if (slot == -1) return false;

        // 获取展示的物品
        ItemStack stackInSlot = this.itemHandler.getStackInSlot(slot);
        if (!stackInSlot.isEmpty()) {
            // 修改物品
            this.modifyDisplayedItem(world, clickPos, slot, isNull);

            // 给玩家物品
            if (!player.field_71075_bZ.isCreativeMode) { // 使用混淆名检查游戏模式
                player.func_191521_c(stackInSlot.copy()); // 使用混淆名添加物品
            } else {
                ItemBase.giveCreativePlayerItem(player, stackInSlot.copy());
            }
            return true;
        }
        return false;
    }

// 存储渲染位置
    private RenderPosition renderPos_0;
    private RenderPosition renderPos_1;

    private RenderPosition getRnderPos(int slot) {
        if (slot == 0)
            return renderPos_0;
        else if (slot == 1)
            return renderPos_1;
        else
        return null;
    }

    private void removeRnderPos(int slot) {
        if (slot == -1) return;
        if (slot == 0 || slot == 2)
            this.renderPos_0 = null;
        if (slot == 1 || slot == 2)
            this.renderPos_1 = null;
    }

    private void setRnderPos(int slot, RenderPosition renderPos) {
        if (slot == 0)
            this.renderPos_0 = renderPos;
        if (slot == 1)
            this.renderPos_1 = renderPos;
    }

// 获取渲染位置
    private static final double x = 0.5, z = 0.5781;
    private static final RenderPosition defaultRender = new RenderPosition(0.5, 0.5781, 180f);;
    // private static final Map<Integer, RenderPosition> ROTATION_MAP = new HashMap<>();

    // public static void initRenderPosAndRotation() {
    //    ROTATION_MAP.put(0, new RenderPosition(x, z, 180f));
    // }

    public RenderPosition getItemRenderPosition(int slot) {
        RenderPosition renderPos = getRnderPos(slot);

        if (renderPos != null) return renderPos; // 有已存储的渲染位置直接返回, 避免重复获取

        IBlockState state = this.getWorld().getBlockState(this.getPos());
        Block block = state.getBlock();

        // 获取方块方向
        boolean isNorthSouth = (block instanceof ModBlockHangingSign) 
            ? ((ModBlockHangingSign) block).getBlockFacing(state.getValue(ModBlockHangingSign.FACING))
            : ((ModBlockHangingSignAttached) block).isNorthSouth(block.getMetaFromState(state));

        if (block instanceof ModBlockHangingSign || ((ModBlockHangingSignAttached) block).isCardinalFacing(state)) {

            if (isNorthSouth) {
                if (slot == 0) {
                    this.setRnderPos(slot, new RenderPosition(x, z, 180f)); // 北方
                } else {
                    this.setRnderPos(slot, new RenderPosition(x, 0.4218, 0f)); // 南方
                }
            } else {
                if (slot == 0) {
                    this.setRnderPos(slot, new RenderPosition(z, x, 270f)); // 西方 // 交换 x z
                } else {
                    this.setRnderPos(slot, new RenderPosition(0.4218, x, 90f)); // 东方 
                }
            }

            return getRnderPos(slot); // 返回刚存储的渲染位置
        }

        // 默认返回: 北方
        return defaultRender;
    }

    public static class RenderPosition{
        private final double x, z;
        private final float rotation;

        public RenderPosition(double x, double z, float rotation) {
            this.x = x;
            this.z = z;
            this.rotation = rotation;
        }

        public double getX() {
            return this.x;
        }

        public double getZ() {
            return this.z;
        }

        public float getRotation() {
            return this.rotation;
        }
    }
}