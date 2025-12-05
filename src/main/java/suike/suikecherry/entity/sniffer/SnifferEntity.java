package suike.suikecherry.entity.sniffer;

import java.util.Random;

import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.packet.PacketHandler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import net.minecraftforge.fml.common.network.NetworkRegistry;

public class SnifferEntity extends EntityAnimal {

    private int digAnimTime;
    private int prevDigAnimTime;
    private float earAngle = 0.1f;
    private boolean increasing = true;

    private static final double snifferMoveSpeed = 0.38D;
    private static final DataParameter<Byte> STATE = EntityDataManager.createKey(SnifferEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Boolean> RIDING = EntityDataManager.createKey(SnifferEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> DIG_TIME = EntityDataManager.createKey(SnifferEntity.class, DataSerializers.VARINT);

    public SnifferEntity(World world) {
        super(world);
        this.setHealth(14.0F);
        this.setSnifferSize();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(14.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getMoveSpeed());
    }

// 初始化
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(STATE, (byte) State.IDLING.ordinal());
        this.dataManager.register(RIDING, false); 
        this.dataManager.register(DIG_TIME, 0);
    }

    @Override
    protected void initEntityAI() {
        /*游泳*/this.tasks.addTask(0, new EntityAISwimming(this));
        /*繁殖*/this.tasks.addTask(0, new EntityAIMate(this, this.getMoveSpeed()));
        /*玩家吸引*/this.tasks.addTask(1, new EntityAITempt(this, this.getMoveSpeed(), ItemBase.PINK_PETALS, false));
        /*随机漫步*/this.tasks.addTask(2, new EntityAIWander(this, this.getMoveSpeed()));
        /*随机漫步*/this.tasks.addTask(3, new EntityAIWander(this, this.getMoveSpeed()));
        /*随机漫步*/this.tasks.addTask(4, new EntityAIWander(this, this.getMoveSpeed()));
        /*随机漫步*/this.tasks.addTask(5, new EntityAIWander(this, this.getMoveSpeed()));
        /*观察玩家*/this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 5.0F));
        /*空闲时环顾*/this.tasks.addTask(3, new EntityAILookIdle(this));
        /*空闲时环顾*/this.tasks.addTask(4, new EntityAILookIdle(this));
        /*空闲时环顾*/this.tasks.addTask(5, new EntityAILookIdle(this));
    }

    public void setSnifferSize() {
        if (this.isChild()) {
            this.setSize(1.4875f, 1.255f);
        } else {
            this.setSize(1.9f, 1.75f);
        }
    }

// 更新
    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote) {
            // 更新动画计时器
            this.prevDigAnimTime = this.digAnimTime;
            if(this.getState() == State.DIGGING) {
                if(digAnimTime < 20) digAnimTime++;
            } else {
                if(digAnimTime > 0) digAnimTime--;
            }

            // 更新耳朵摆动
            if (this.getState() != State.DIGGING) {
                if (increasing) {
                    this.earAngle += 0.001f;
                    if (this.earAngle >= 0.2f) increasing = false;
                } else {
                    this.earAngle -= 0.001f;
                    if (this.earAngle <= 0.1f) increasing = true;
                }

            }
        } else {
            if (this.sniffingCooldown > 0) this.sniffingCooldown--;
            if (this.oracleTime > 0) this.oracleTime--;
            if (this.lastSnoreTime > 0) this.lastSnoreTime--;

            if (this.getState() == State.SNIFFING) {
                // 检查是否达到最大嗅探时间
                if (this.sniffingTime++ > MAX_SNIFFING_TIME) {
                    this.setState(State.IDLING); // 嗅探超时, 设为空闲状态
                    this.sniffingTime = 0;
                }
            }

            if (this.getState() == State.IDLING && this.rand.nextFloat() < 0.008F) {
                float a = this.rand.nextFloat();
                if ((happyTime > 0 && a < 0.6F) || a < 0.1F) {
                    this.snore("entity.sniffer.happy");
                }
                this.snore("entity.sniffer.idle");
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // 每tick增长年龄
        if (!this.world.isRemote) {
            int age = this.getGrowingAge();
            if (age < 0) {
                this.setGrowingAge(age + 1);
            }
        }
    }

// 声音
    private int lastSnoreTime;
    public void snore(String snoreName) {
        if (this.lastSnoreTime != 0) return;
        float a = this.isChild() ? 0.6f : 1.0f;
        Sound.playSound(this.world, this.getPos(), snoreName, a, 1.0f);

        this.lastSnoreTime = 80;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.snore("entity.sniffer.death");
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!super.attackEntityFrom(source, amount)) return false;

        if (this.getHealth() > 0) {
            this.snore("entity.sniffer.hurt");
        }
        return true;
    }

    @Override
    public void playStepSound(BlockPos pos, Block block) {
        if (this.rand.nextFloat() < 0.15F) {
            this.snore("entity.sniffer.step");
        }
    }

// 交互
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (!this.isBreedingItem(player.getHeldItem(hand))) {
            if (!this.isBeingRidden() && !player.isRiding() && !this.isChild()) {
                if (!this.world.isRemote) player.startRiding(this);
            }
            return true;
        }

        return super.processInteract(player, hand);
    }

// 骑乘
    @Override
    public double getMountedYOffset() {
        return 1.88; 
    }

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

// 繁殖
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == ItemBase.PINK_PETALS;
    }

    @Override
    public boolean canMateWith(EntityAnimal other) {
        boolean a = other != this && other instanceof SnifferEntity
                    && this.isInLove() && other.isInLove()
                    && !this.isChild() && !other.isChild()
                    && super.canMateWith(other);

        if (a) this.snore("entity.sniffer.eat");
        
        return a;
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        this.dropItem(Item.getItemFromBlock(BlockBase.SNIFFER_EGG), 0);
        this.resetInLove();
        return null;
    }

// 幼年
    @Override
    public void setGrowingAge(int age) {
        super.setGrowingAge(age);
        if (age == 0) {
            this.setSnifferSize();
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getMoveSpeed());
        }
    }

    public static SnifferEntity getChild(World world) {
        SnifferEntity sniffer = new SnifferEntity(world);
        sniffer.setGrowingAge(-48000);
        sniffer.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(sniffer.getMoveSpeed());
        return sniffer;
    }

    @Override
    public boolean isChild() {
        return this.getGrowingAge() < 0;
    }

// 状态
    // 状态持续时间
    private int happyTime;
    private int oracleTime;
    private int sniffingTime;
    private static final int MAX_HAPPY_TIME = 2400; // 2 min
    private static final int MAX_SNIFFING_TIME = 4800; // 4 min
    private static final int MAX_ORACLE_TIME = 9600; // 8 min

    // 状态冷却
    private int sniffingCooldown;
    private static final int SNIFFING_COOLDOWN = 4800; // 4 min

    public enum State {
        IDLING, DIGGING, SNIFFING // 空闲, 挖掘, 嗅探
    }

    public State getState() {
        return State.values()[this.dataManager.get(STATE)];
    }

    public void setState(State state) {
        // 状态相同
        if (this.getState() == state || this.world.isRemote) return;

        if (!this.canSetState(state)) return;

        this.reloadCooldown();

        // 修改状态
        this.dataManager.set(STATE, (byte) state.ordinal());
    }

    // 检查修改目标是否处于冷却
    private boolean canSetState(State state) {
        switch(state) {
            case SNIFFING:
                if (this.sniffingCooldown > 0 || this.oracleTime > 0) return false; // 目标状态冷却未结束, 不允许修改
            case DIGGING:
                if (this.oracleTime > 0) return false; // 处于贤者状态, 不允许修改
            default:
                return true;
        }
    }

    // 重置冷却
    private void reloadCooldown() {
        switch(this.getState()) {
            case SNIFFING:
                this.sniffingCooldown = SNIFFING_COOLDOWN; // 嗅探状态进入冷却
            case DIGGING:
                this.oracleTime = MAX_ORACLE_TIME; // 挖掘结束, 进入贤者状态
            default:
                return;
        }
    }

// 获取
    public double getMoveSpeed() {
        if (this.isChild()) {
            return snifferMoveSpeed * 0.8;
        }
        return snifferMoveSpeed;
    }
    public BlockPos getPos() {
        return new BlockPos(this.posX, this.posY, this.posZ);
    }
    public Random getRandom() {
        return this.rand;
    }
    public float getEarAngle() {
        return this.earAngle;
    }
    public float getDigProgress(float partialTicks) {
        return MathHelper.clamp((prevDigAnimTime + (digAnimTime - prevDigAnimTime) * partialTicks) / 20F, 0F, 1F);
    }
}