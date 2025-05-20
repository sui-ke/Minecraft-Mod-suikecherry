package suike.suikecherry.tileentity;

import java.util.Map;
import java.util.HashMap;

import suike.suikecherry.sound.Sound;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.ModBlockHangingSign;
import suike.suikecherry.block.ModBlockHangingSignAttached;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.HangingSignUpdatePacket;
import suike.suikecherry.tileentity.HasBackSideSignTileEntity.RenderPosition;

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

// 悬挂告示牌 TileEntity 类
public class HangingSignTileEntity extends HasBackSideSignTileEntity {
    private int hasDisplayedSides = -1;
    private static boolean lastPickedSide = true;
    public static final ItemStack isNull = ItemStack.EMPTY;

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
        return super.hasAny(slot) || this.hasDisplayItems(slot);
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

        this.removeItemRenderPos(has0 ? -1 : 0);
        this.removeItemRenderPos(has1 ? -1 : 1);
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
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), clickPos.getX(), clickPos.getY(), clickPos.getZ(), 64)
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
    private RenderPosition itemRenderPos_0;
    private RenderPosition itemRenderPos_1;

    private RenderPosition getItemRenderPos(int slot) {
        if (slot == 0)
            return itemRenderPos_0;
        else if (slot == 1)
            return itemRenderPos_1;
        else
        return null;
    }

    private void removeItemRenderPos(int slot) {
        if (slot == -1) return;
        if (slot == 0 || slot == 2)
            this.itemRenderPos_0 = null;
        else if (slot == 1 || slot == 2)
            this.itemRenderPos_1 = null;
    }

    private void setItemRenderPos(int slot, RenderPosition renderPos) {
        if (slot == 0)
            this.itemRenderPos_0 = renderPos;
        else if (slot == 1)
            this.itemRenderPos_1 = renderPos;
    }

// 获取渲染位置
    private static final double x = 0.5, z = 0.5781;
    private static final RenderPosition defaultItemRenderPos = new RenderPosition(0.5, 0.5781, 180f);;
    private static final Map<Integer, RenderPosition> ROTATION_MAP = new HashMap<>();

    static {
        ROTATION_MAP.put(1, new RenderPosition(0.4701, 0.5722, 157.5f));
        ROTATION_MAP.put(2, new RenderPosition(0.4448, 0.5552, 135f));
        ROTATION_MAP.put(3, new RenderPosition(0.4278, 0.5299, 112.5f));
        ROTATION_MAP.put(5, new RenderPosition(0.4278, 0.4701, 67.5f));
        ROTATION_MAP.put(6, new RenderPosition(0.4448, 0.4448, 45f));
        ROTATION_MAP.put(7, new RenderPosition(0.4701, 0.4278, 22.5f));
        ROTATION_MAP.put(9, new RenderPosition(0.5299, 0.4278, -22.5f));
        ROTATION_MAP.put(10, new RenderPosition(0.5552, 0.4448, -45f));
        ROTATION_MAP.put(11, new RenderPosition(0.5722, 0.4701, -67.5f));
        ROTATION_MAP.put(13, new RenderPosition(0.5722, 0.5299, -112.5f));
        ROTATION_MAP.put(14, new RenderPosition(0.5552, 0.5552, -135f));
        ROTATION_MAP.put(15, new RenderPosition(0.5299, 0.5722, -157.5f));
    }

    public RenderPosition getItemRenderPosition(int slot) {
        RenderPosition renderPos = getItemRenderPos(slot);

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
                    this.setItemRenderPos(slot, defaultItemRenderPos); // 北方
                } else {
                    this.setItemRenderPos(slot, new RenderPosition(x, 0.4218, 0f)); // 南方
                }
            } else {
                if (slot == 0) {
                    this.setItemRenderPos(slot, new RenderPosition(z, x, 270f)); // 西方 // 交换 x z
                } else {
                    this.setItemRenderPos(slot, new RenderPosition(0.4218, x, 90f)); // 东方 
                }
            }

            return this.getItemRenderPos(slot); // 返回刚存储的渲染位置
        } else if (block instanceof ModBlockHangingSignAttached) {
            int meta = block.getMetaFromState(state);
            if (slot == 1) {
                meta = (meta + 8) % 16; // 计算对称面meta值
            }
            this.setItemRenderPos(slot, ROTATION_MAP.get(meta));
            return this.getItemRenderPos(slot);
        }

        // 默认返回: 北方 0 槽
        return defaultItemRenderPos;
    }
}