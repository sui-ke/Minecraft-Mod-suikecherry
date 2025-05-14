package suike.suikecherry.entity.boat;

import java.util.Calendar;

import suike.suikecherry.entity.boat.ModEntityBoat.ModBoatRender;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.SoundEvents;

import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import net.minecraftforge.common.util.Constants;

public abstract class ModEntityChestBoat extends ModEntityBoat implements IEntityMultiPart {
    private final boolean isChristmas;
    public final BoatChestPart chestPart;

    private static final DataParameter<ItemStack> CHEST_STACK = EntityDataManager.createKey(ModEntityChestBoat.class, DataSerializers.ITEM_STACK);

    public ModEntityChestBoat(World world) {
        super(world);
        this.chestPart = new BoatChestPart(this, this.getBoatName());

        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && 
                            calendar.get(Calendar.DATE) >= 24 && 
                            calendar.get(Calendar.DATE) <= 26;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(CHEST_STACK, ItemStack.EMPTY);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!this.getPassengers().isEmpty()) {
            return false;
        }
        return super.processInitialInteract(player, hand);
    }

    public void openInventory(EntityPlayer player) {
        if (!player.func_175149_v()) { // isSpectator
            this.chestPart.numPlayersUsing++;
            // this.world.addBlockEvent(this.getPosition(), null, this.chestPart.numPlayersUsing);
        }
    }

    public void closeInventory(EntityPlayer player) {
        if (!player.func_175149_v()) {
            this.chestPart.numPlayersUsing--;
            // this.world.addBlockEvent(this.getPosition(), null, this.chestPart.numPlayersUsing);
        }
    }

    @Override
    public Entity[] getParts() {
        return new Entity[]{chestPart};
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        Vec3d offset = chestPart.getChestOffset(true);
        chestPart.setPosition(
            posX + offset.x,
            posY + offset.y,
            posZ + offset.z
        );
        
        // 确保旋转同步
        this.chestPart.rotationYaw = this.rotationYaw;
    }

    @Override
    public void setDead() {
        super.setDead();
        chestPart.setDead();
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < 1;
    }

    @Override
    public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
        return attackEntityFrom(source, damage);
    }

    public ItemStack getChestStack() {
        return dataManager.get(CHEST_STACK);
    }

    public void setChestStack(ItemStack stack) {
        dataManager.set(CHEST_STACK, stack);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setTag("ChestStack", getChestStack().serializeNBT());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("ChestStack", Constants.NBT.TAG_COMPOUND)) {
            setChestStack(new ItemStack(compound.getCompoundTag("ChestStack")));
        }
    }

    private static final float offset = 0.2F; // 固定向前偏移量
    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float yOffset = (float) ((this.isDead ? 0.009999999776482582D : this.getMountedYOffset()) + passenger.getYOffset());

            // 计算偏移后的位置
            Vec3d vec3d = (new Vec3d((double)offset, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
            passenger.setPosition(this.posX + vec3d.x, this.posY + (double) yOffset, this.posZ + vec3d.z);

            this.applyYawToEntity(passenger);
        }
    }

    public static class ModChestBoatRender extends ModBoatRender {
        private final boolean isChristmas;

        public ModChestBoatRender(RenderManager renderManager) {
            super(renderManager);
            Calendar calendar = Calendar.getInstance();
            isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && 
                            calendar.get(Calendar.DATE) >= 24 && 
                            calendar.get(Calendar.DATE) <= 26;
        }

        @Override
        public void doRender(EntityBoat entity, double x, double y, double z, float entityYaw, float partialTicks) {
            if (entity instanceof ModEntityChestBoat) {
                this.renderChest((ModEntityChestBoat) entity, x, y, z, entityYaw, partialTicks);
            }
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }

        private void renderChest(ModEntityChestBoat chestBoat, double x, double y, double z, float entityYaw, float partialTicks) {
            GlStateManager.pushMatrix();
            setupTranslation(x, y, z);
            setupRotation(chestBoat, entityYaw, partialTicks);

            final Vec3d offset = chestBoat.chestPart.getChestOffset(false);
            chestBoat.chestPart.getChestOffset(false);
            GlStateManager.translate(offset.x, -(offset.y + chestBoat.chestPart.getEyeHeight()), offset.z);
            chestBoat.chestPart.renderChest(x, y, z, entityYaw, partialTicks, isChristmas);

            GlStateManager.popMatrix();
        }
    }

    public static class BoatChestPart extends MultiPartEntityPart {
        private float lidAngle;
        private int numPlayersUsing;

        public final ModEntityChestBoat parentBoat;
        public static ModelChest CHEST_MODEL;

        public BoatChestPart(ModEntityChestBoat parentBoat, String partName) {
            super(parentBoat, partName, 0.775f, 0.775f);
            this.parentBoat = parentBoat;
        }

        private void playOpenSound() {
            this.parentBoat.playSound(SoundEvents.BLOCK_CHEST_OPEN, 0.5F, this.parentBoat.world.rand.nextFloat() * 0.1F + 0.9F);
        }

        private void playCloseSound() {
            this.parentBoat.playSound(SoundEvents.BLOCK_CHEST_CLOSE, 0.5F, this.parentBoat.world.rand.nextFloat() * 0.1F + 0.9F);
        }

        public void renderChest(double x, double y, double z, float entityYaw, float partialTicks, boolean isChristmas) {
            Minecraft.getMinecraft().renderEngine.bindTexture(getChestTexture(isChristmas));
            GlStateManager.translate(-0.0625, 0.15375, -0.0625);
            GlStateManager.scale(0.875, 0.875, 0.875);

            float animatedLidAngle = 1 - (this.lidAngle * partialTicks);
            animatedLidAngle = 1 - animatedLidAngle * animatedLidAngle * animatedLidAngle;

            if(CHEST_MODEL == null) CHEST_MODEL = new ModelChest();
            CHEST_MODEL.chestLid.rotateAngleX = -(animatedLidAngle * (float) Math.PI / 2);
            CHEST_MODEL.renderAll();
        }

        public Vec3d getChestOffset(boolean applyBoatRotation) {
            Vec3d offset = new Vec3d(-0.375, parentBoat.getMountedYOffset() + 0.28, 0);
            return applyBoatRotation ? offset.rotateYaw(-parentBoat.rotationYaw * 0.0175f - (float)Math.PI / 2) : offset;
        }

        @Override
        public void onUpdate() {
            super.onUpdate();

            this.rotationYaw = this.parentBoat.rotationYaw;

            // 添加位置同步
            Vec3d offset = getChestOffset(true);
            this.setPosition(
                parentBoat.posX + offset.x,
                parentBoat.posY + offset.y,
                parentBoat.posZ + offset.z
            );
        }

        @Override
        public boolean canBeCollidedWith() {
            return parentBoat.canBeCollidedWith();
        }

        @Override
        public AxisAlignedBB getEntityBoundingBox() {
            return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        }
    }

    // 返回箱子纹理
    private static final ResourceLocation getChestTexture(boolean isChristmas) {
        return isChristmas ? 
            new ResourceLocation("textures/entity/chest/christmas.png") :
            new ResourceLocation("textures/entity/chest/normal.png");
    }
}