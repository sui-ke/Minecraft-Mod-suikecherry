package suike.suikecherry.tileentity;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.ModBlockChiseledBookShelf;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class ChiseledBookShelfTileEntity extends TileEntity {

    // 比较器相关
    private int lastActivatedSlot = -1;
    public int getLastActivatedSlot() {
        return this.lastActivatedSlot;
    }
    private EntityPlayer lastActivatedPlayer;
    public EntityPlayer getLastActivatedPlayer() {
        EntityPlayer player = this.lastActivatedPlayer;
        this.lastActivatedPlayer = null;
        return player;
    }

    // 存储物品相关
    private final ItemStackHandler itemHandler = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            ChiseledBookShelfTileEntity.this.markDirty();
        }
    };

    // 是否有存储书本
    public boolean hasBook(int slot) {
        return !this.getBook(slot).isEmpty();
    }
    // 获取书本
    public ItemStack getBook(int slot) {
        if (slot >= 0 || slot <= 5) {
            return this.itemHandler.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }
    // 获取书本
    public List<ItemStack> getAllBook() {
        List<ItemStack> books = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            books.add(itemHandler.getStackInSlot(i).copy());
        }
        return books;
    }
    // 有书的槽位
    private final IntSet hasBookSlot = new IntOpenHashSet();
    public IntSet getAllHasBookSlot() {
        return this.hasBookSlot;
    }
    public boolean upHasBookSlot() {
        this.hasBookSlot.clear();
        boolean hasBook = false;
        for (int i = 0; i < 6; i++) {
            if (this.hasBook(i)) {
                hasBook = true;
                this.hasBookSlot.add(i);
            }
        }
        return hasBook;
    }

    // 获取父方块
    public ModBlockChiseledBookShelf getParentBlock() {
        return (ModBlockChiseledBookShelf) this.getParentBlockState().getBlock();
    }
    public IBlockState getParentBlockState() {
        return this.world.getBlockState(this.pos);
    }
    // 获取父方块方向
    private EnumFacing parentBlockFacing;
    public EnumFacing getParentBlockFacing() {
        if (this.parentBlockFacing == null) {
            this.parentBlockFacing = this.getParentBlockState().getValue(ModBlockChiseledBookShelf.FACING);
        }
        return this.parentBlockFacing;
    }

// 添加书本
    public boolean tryAddBook(EntityPlayer player, ItemStack heldItem, int slot) {
        if (slot < 0 || slot > 5) return false;

        boolean success = false;

        if (!this.world.isRemote) {
            // 确保主手有物品 && 槽位没有物品 && 是书本
            if (!heldItem.isEmpty() && !this.hasBook(slot) && this.isBook(heldItem)) {
                ItemStack book = heldItem.copy();
                book.setCount(1); // 只存储一个
                this.itemHandler.setStackInSlot(slot, book);
                success = true;
            }

            if (success) {
                // 修改成功
                if (!player.field_71075_bZ.isCreativeMode) heldItem.shrink(1); // 减少物品栏物品
                this.modifyEnd(player, slot, true);
            }
        }

        return true; // 永远返回 true 防止放置新方块
    }

    public boolean isBook(ItemStack heldItem) {
        if (!ItemBase.isValidItemStack(heldItem)) return false;

        Item item = heldItem.getItem();

        // 排除方块
        if (item instanceof ItemBlock) return false;

        // 注册名带 book 或者矿词带 book
        return item.getRegistryName().toString().contains("book")
            || ItemBase.getItemOreDict(heldItem, "book", true) != null;
    }

// 移除书本
    public boolean removeBook(EntityPlayer player, int slot) {
        if (slot < 0 || slot > 5) return false;

        // 获取展示的物品
        if (!this.world.isRemote) {
            ItemStack book = this.itemHandler.getStackInSlot(slot);
            if (!book.isEmpty()) {
                // 移除物品
                this.itemHandler.setStackInSlot(slot, ItemStack.EMPTY);

                // 给玩家物品
                if (!player.field_71075_bZ.isCreativeMode) {
                    player.func_191521_c(book.copy());
                } else {
                    ItemBase.giveCreativePlayerItem(player, book.copy());
                }

                this.modifyEnd(player, slot, false);
            }
        }
        return true;
    }

// 结束修改
    public void modifyEnd(EntityPlayer player, int slot, boolean insert) {
        this.lastActivatedSlot = slot;
        this.lastActivatedPlayer = player;
        this.markDirty();

        // 播放音效
        Sound.playSound(world, pos, "block.chiseled_bookshelf." + (insert ? "insert" : "pickup"));

        // 广播更新到所有客户端
        world.notifyBlockUpdate(pos, this.getBlockType().getDefaultState(), this.getBlockType().getDefaultState(), 3);
    }

// 存储 & 读取
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("lastActivatedSlot")) {
            this.lastActivatedSlot = compound.getInteger("lastActivatedSlot");
        }
        if (compound.hasKey("Items")) {
            itemHandler.deserializeNBT(compound.getCompoundTag("Items"));
        }
        this.remoteUp();
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setInteger("lastActivatedSlot", this.lastActivatedSlot);
        compound.setTag("Items", this.itemHandler.serializeNBT());
        return compound;
    }

// 更新包
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        nbt.setTag("Items", this.itemHandler.serializeNBT());
        return nbt;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound nbt = pkt.getNbtCompound();
        this.readFromNBT(nbt);
    }

// 渲染
    private boolean canRender;
    public boolean canRender() {
        return this.canRender;
    }

    private void remoteUp() {
        if (this.upHasBookSlot()) {
            this.canRender = true;
        } else {
            this.canRender = false;
        }
    }
}