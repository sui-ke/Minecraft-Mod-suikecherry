package suike.suikecherry.entity.boat;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.client.gui.GuiID;
import suike.suikecherry.client.render.entity.boat.ModBoatRender;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.packets.OpenChestBoatPacket;
import suike.suikecherry.packet.packets.ChestBoatUpdatePacket;

import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.IInteractionObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.inventory.IInventory;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;

import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public abstract class ModEntityChestBoat extends ModEntityBoat implements IEntityMultiPart, IInteractionObject {
    public final BoatChestPart chestPart; // 箱子子实体
    public final ChestInventory chestInventory = new ChestInventory(); // 库存

    public ModEntityChestBoat(World world) {
        super(world);
        if (world.isRemote) ModBoatRender.setChestTexture();
        this.chestPart = new BoatChestPart(this);
    }

    // 蹲下打开容器
    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && player.isSneaking() && this.canFitPassenger(player)) {
            if (!this.chestInventory.hasPlayerOpenChest && this.getPassengers().isEmpty()) {
                player.openGui(SuiKe.instance, GuiID.GUI_CHEST_BOAT, world, getEntityId(), 0, 0);
            }
            return true;
        }
        return super.processInitialInteract(player, hand);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.chestPart.ChestUpdateEffect();
    }

    @Override
    public void setDead() {
        chestPart.setDead(); // 清除箱子实体
        super.setDead();
        if (!this.world.isRemote && this.isDead) {
            chestInventory.dropAllChestInventory(); // 掉落库存
        }
    }

    // 驾驶员位置偏移
    private static final float offset = 0.08F; // 固定向前偏移量
    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float yOffset = (float) (this.getMountedYOffset() + passenger.getYOffset());

            // 计算偏移后的位置
            Vec3d vec3d = (new Vec3d((double) offset, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
            passenger.setPosition(this.posX + vec3d.x, this.posY + (double) yOffset, this.posZ + vec3d.z);

            this.applyYawToEntity(passenger);
        }
    }

// 存储 & 读取
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        NBTTagCompound chestTag = new NBTTagCompound();
        chestInventory.writeToNBT(chestTag);
        compound.setTag("ChestItems", chestTag);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("ChestItems", Constants.NBT.TAG_COMPOUND)) {
            // 将物品添加到箱子里
            chestInventory.readFromNBT(compound.getCompoundTag("ChestItems"));
        }
        if (!world.isRemote) {
            // 同步到附近玩家
            this.chestInventory.syncChestContents();
        }
    }

// 服务器同步容器使用状态
    public void updateHasPlayerOpenChest(boolean hasPlayerOpenChest) {
        this.chestInventory.hasPlayerOpenChest = hasPlayerOpenChest;
        if (!this.world.isRemote) {
            PacketHandler.INSTANCE.sendToAllTracking(
                new OpenChestBoatPacket(this.getEntityId(), hasPlayerOpenChest),
                this
            );
        }
    }

    // 通知服务端创建容器
    public void openServerContainer(EntityPlayer player) {
        player.openGui(SuiKe.instance, GuiID.GUI_CHEST_BOAT, this.world, this.getEntityId(), 0, 0);
    }

// 箱子子实体
    public class BoatChestPart extends MultiPartEntityPart {
        public float lidAngle;     // 当前箱盖角度 (0=关闭, 1=完全打开)
        public float prevLidAngle; // 上一帧的箱盖角度

        public BoatChestPart(ModEntityChestBoat parentBoat) {
            super(parentBoat, "chest", 0, 0);
        }

        public void ChestUpdateEffect() {
            if (!ModEntityChestBoat.this.getPassengers().isEmpty() && this.lidAngle == 0) return;

            this.prevLidAngle = this.lidAngle;

            if (ModEntityChestBoat.this.chestInventory.hasPlayerOpenChest) {
                if (this.lidAngle == 0) {
                    ModEntityChestBoat.this.playOpenSound();
                }
                if (this.lidAngle < 1.0F) {
                    this.lidAngle += 0.1F;
                } else {
                    this.lidAngle = 1.0F;
                }
            }
            else if (this.lidAngle > 0.001f) {
                this.lidAngle -= 0.1F;
                if (this.lidAngle < 0.5F && this.prevLidAngle >= 0.5F) {
                    ModEntityChestBoat.this.playCloseSound();
                }
            }
            else {
                this.lidAngle = 0.0f;
            }
        }

        // 获取箱子打开角度
        public float getLidAngle(float partialTicks) {
            return this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * partialTicks;
        }

        public Vec3d getChestOffset() {
            return new Vec3d(-0.375, ModEntityChestBoat.this.getMountedYOffset() + 0.28, 0)
                .rotateYaw(-rotationYaw * 0.0175f - (float) Math.PI / 2);
        }

        @Override public void onUpdate() {}
    }

// 箱子容器及物品
    public static class ContainerChestBoat extends ContainerChest {
        private final ModEntityChestBoat chestBoat;

        public ContainerChestBoat(IInventory playerInventory, ModEntityChestBoat chestBoat, EntityPlayer player) {
            super(playerInventory, chestBoat.chestInventory, player);
            this.chestBoat = chestBoat;
            this.chestBoat.updateHasPlayerOpenChest(true);
        }

        @Override
        public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
            return super.transferStackInSlot(playerIn, index);
        }

        @Override
        public boolean canInteractWith(EntityPlayer player) {
            return true;
        }

        @Override
        public void onContainerClosed(EntityPlayer player) {
            super.onContainerClosed(player);
            this.chestBoat.updateHasPlayerOpenChest(false);
            // 关闭时同步数据
            this.chestBoat.chestInventory.chestMarkDirty();
        }
    }

    public ContainerChestBoat getContainerChestBoat(EntityPlayer player) {
        return new ContainerChestBoat(player.inventory, this, player);
    }

// 库存
    private static final int containerSlots = 27; // 箱子槽数量
    public class ChestInventory extends InventoryBasic {
        public boolean hasPlayerOpenChest;

        public ChestInventory() {
            super(
                SuiKe.isServer ? "chest.boat.inventory" : I18n.format("suikecherry.chest.boat.inventory"),
                false,
                containerSlots
            );
        }

        public void writeToNBT(NBTTagCompound compound) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < this.getSizeInventory(); ++i) {
                ItemStack stack = this.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setByte("Slot", (byte)i);
                    stack.writeToNBT(itemTag);
                    list.appendTag(itemTag);
                }
            }
            compound.setTag("Items", list);
        }

        public void readFromNBT(NBTTagCompound compound) {
            NBTTagList list = compound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound itemTag = list.getCompoundTagAt(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot < this.getSizeInventory()) {
                    this.setInventorySlotContents(slot, new ItemStack(itemTag));
                }
            }
        }

        public void chestMarkDirty() {
            if (!world.isRemote) {
                syncChestContents();
            } else {
                requestServerSync();
            }
        }

        // 服务端同步方法
        private void syncChestContents() {
            NBTTagCompound nbt = new NBTTagCompound();
            chestInventory.writeToNBT(nbt);
            ItemStack syncStack = new ItemStack(Blocks.CHEST);
            syncStack.setTagCompound(nbt);

            // 发送给所有可见玩家
            PacketHandler.INSTANCE.sendToAllTracking(
                new ChestBoatUpdatePacket(getEntityId(), syncStack),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 64)
            );
        }

        // 客户端请求方法
        private void requestServerSync() {
            NBTTagCompound nbt = new NBTTagCompound();
            chestInventory.writeToNBT(nbt);
            ItemStack requestStack = new ItemStack(Blocks.CHEST);
            requestStack.setTagCompound(nbt);

            PacketHandler.INSTANCE.sendToServer(
                new ChestBoatUpdatePacket(getEntityId(), requestStack)
            );
        }

        // 包更新库存方法
        public void updateChestVisuals(ItemStack stack) {
            if (world.isRemote) {
                chestInventory.readFromNBT(stack.getTagCompound());
            }
        }

        // 服务端更新库存方法
        public void validateAndUpdateContents(ItemStack stack) {
            if (!world.isRemote) {
                chestInventory.readFromNBT(stack.getTagCompound());
                syncChestContents();
            }
        }

        // 掉落库存物品
        private void dropAllChestInventory() {
            // 遍历所有库存槽位
            for (int i = 0; i < chestInventory.getSizeInventory(); ++i) {
                ItemStack stack = chestInventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    // 生成掉落物实体
                    world.spawnEntity(new EntityItem(world, posX, posY, posZ, stack));
                }
            }
        }
    }

// 音效
    private final void playOpenSound() {
        Sound.playSound(this.world, posX, posY, posZ, SoundEvents.BLOCK_CHEST_OPEN, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
    }

    private final void playCloseSound() {
        Sound.playSound(this.world, posX, posY, posZ, SoundEvents.BLOCK_CHEST_CLOSE, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
    }

// 覆盖
    @Override public World getWorld() { return this.world; }
    @Override public Entity[] getParts() { return new Entity[]{chestPart}; }
    @Override public String getGuiID() { return "suikecherry:chest_boat_gui"; }
    @Override public boolean hasCustomName() { return chestInventory.hasCustomName(); }
    @Override public boolean canFitPassenger(Entity passenger) { return this.getPassengers().size() == 0; }
    @Override public String getName() { return I18n.format("entity." + this.getBoatName() + ".name"); }
    @Override public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) { return this.attackEntityFrom(source, damage); }
    @Override public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player) { return new ContainerChestBoat(playerInventory, this, player);}
}