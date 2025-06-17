package suike.suikecherry.tileentity;

import java.util.Map;
import java.util.Arrays;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.block.ModBlockDecoratedPot;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.packets.DecoratedPotSherdUpdatePacket;
import suike.suikecherry.data.AxisPosition;
import suike.suikecherry.particle.ModParticle;

import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.ITickable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.google.common.collect.ImmutableMap;

public class DecoratedPotTileEntity extends TileEntity implements ITickable {

    private static final String[] NULL_SHERD_IDS = new String[]{"brick", "brick", "brick", "brick"};

    public static String[] getNullSherdIDs() {
        return NULL_SHERD_IDS.clone();
    }

    // 存储四个面的陶片ID
    private String[] sherdIDs = NULL_SHERD_IDS.clone(); // 右 后 左 前

    // 晃动动画相关
    public static final int WOBBLE_DURATION = 10; // 动画持续时间 (ticks)
    public float wobbleProgress;  // 当前动画进度 (0.0-1.0)
    public float wobbleIntensity; // 动画强度
    public int wobbleDirection;   // 1 = 成功动画, -1 = 失败动画
    public long lastWobbleTime;   // 上次触发动画的时间

    // 存储物品相关
    private ItemStack storedItem = ItemStack.EMPTY;

    // 是否有存储物品
    public boolean hasStoredItem() {
        return this.storedItem != ItemStack.EMPTY;
    }
    // 获取存储物品
    public ItemStack getStoredItem() {
        return this.storedItem.copy();
    }

    // 获取父方块
    public ModBlockDecoratedPot getParentBlock() {
        return (ModBlockDecoratedPot) this.getParentBlockState().getBlock();
    }
    public IBlockState getParentBlockState() {
        return this.world.getBlockState(this.pos);
    }
    // 获取父方块方向
    private EnumFacing parentBlockFacing;
    public EnumFacing getParentBlockFacing() {
        if (this.parentBlockFacing == null) {
            this.parentBlockFacing = this.getParentBlockState().getValue(ModBlockDecoratedPot.FACING);
        }
        return this.parentBlockFacing;
    }

    // 设置陶罐 NBT
    public void setDecoratedPotFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) {
            nbt = nbt.getCompoundTag("BlockEntityTag");
        }

        if (nbt.hasKey("Sherds", Constants.NBT.TAG_LIST)) {
            NBTTagList list = nbt.getTagList("Sherds", Constants.NBT.TAG_STRING);
            if (list.tagCount() == 4) {
                for (int i = 0; i < 4; i++) {
                    this.sherdIDs[i] = list.getStringTagAt(i);
                }
            }
        }
        if (nbt.hasKey("StoredItem", Constants.NBT.TAG_COMPOUND)) {
            this.storedItem = new ItemStack(nbt.getCompoundTag("StoredItem"));
        }
        this.markDirty();
    }

    public void setDecoratedPotFromString(String[] sherdIDs) {
        if (sherdIDs == null) {
            this.sherdIDs = NULL_SHERD_IDS.clone(); // 恢复默认纹饰
        }
        // 无纹饰才能设置
        else if (Arrays.equals(this.sherdIDs, getNullSherdIDs())) {
            this.sherdIDs = sherdIDs;
        }
    }

    // 获取陶片类型
    public String[] getAllPotteryType() {
        return this.sherdIDs;
    }

// 存储物品
    public boolean addItem(EntityPlayer player, ItemStack heldItem) {
        // 仅服务端修改物品
        if (world.isRemote) return true;

        boolean success = false;

        // 确保主手有物品
        if (!heldItem.isEmpty()) {
            if (this.storedItem.isEmpty()) {
                this.storedItem = heldItem.copy();
                this.storedItem.setCount(1); // 一次只存储一个
                success = true;
            }
            else if (this.canAdd(heldItem)) {
                this.storedItem.grow(1); // 增加数量
                success = true;
            }
        }

        if (success) {
            // 修改成功
            if (!player.field_71075_bZ.isCreativeMode) heldItem.shrink(1); // 减少物品栏物品
            this.markDirty();
        }

        // 播放音效
        Sound.playSound(world, pos, "block.decorated_pot." + (success ? "insert" : "insert_fail"));

        // 同步修改状态到客户端
        PacketHandler.INSTANCE.sendToAllAround(
            new DecoratedPotSherdUpdatePacket(this.pos, success),
            new NetworkRegistry.TargetPoint(world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 64)
        );

        return true; // 永远返回 true 防止放置新方块
    }

    public boolean canAdd(ItemStack item) {
        return ItemBase.itemsEqual(this.storedItem, item)                      // 相同物品
            && this.storedItem.getCount() < this.storedItem.getMaxStackSize(); // 不超过最大堆叠数量
    }

// 触发动画 (客户端调用)
    public void triggerWobble(int successSign) {
        this.wobbleDirection = successSign;
        this.wobbleProgress = 1.0f;
        this.wobbleIntensity = 0.0f;
        this.lastWobbleTime = this.world.getTotalWorldTime();

        // 生成烟雾粒子
        if (successSign == 1) {
            ModParticle.spawnParticlesDecoratedPotSmoke(this.world, this.pos);
        }
    }

    @Override
    public void update() {
        if (world.isRemote && wobbleProgress > 0) {
            // 更新动画进度
            wobbleProgress -= 1.0f / WOBBLE_DURATION;

            if (wobbleProgress <= 0) {
                wobbleProgress = 0;
            }
        }
    }

// 存储 & 读取
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Sherds")) {
            NBTTagList list = compound.getTagList("Sherds", Constants.NBT.TAG_STRING);
            for (int i = 0; i < 4; i++) {
                this.sherdIDs[i] = list.getStringTagAt(i);
            }
        }
        if (compound.hasKey("StoredItem")) {
            this.storedItem = new ItemStack(compound.getCompoundTag("StoredItem"));
        }
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        NBTTagList list = new NBTTagList();
        for (String id : this.sherdIDs) {
            list.appendTag(new NBTTagString(id != null ? id : "brick"));
        }
        compound.setTag("Sherds", list);
        if (ItemBase.isValidItemStack(this.storedItem)) {
            compound.setTag("StoredItem", this.storedItem.writeToNBT(new NBTTagCompound()));
        }
        return compound;
    }

// 更新包
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
    }
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        NBTTagList sherdList = new NBTTagList();
        for (String id : this.sherdIDs) {
            sherdList.appendTag(new NBTTagString(id));
        }
        tag.setTag("Sherds", sherdList);
        return tag;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt.getNbtCompound();
        this.readFromNBT(tag);
    }
}