package suike.suikecherry.particle;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CherryParticle extends Particle {

    public static final Minecraft MC = Minecraft.getMinecraft();

    private static final int maxAge = (20 * 15); // 粒子生命周期 / 15s
    private static final float GRAVITY = 7.5E-4F; // 粒子重力

    private boolean isStopped;
    private final float phase;

    public static final List<ResourceLocation> TEXTURES = new ArrayList<>();

    private CherryParticle(World world, double x, double y, double z) {
        super(world, x, y, z);

        // 初始化粒子属性
        this.setSize(0.1F, 0.1F);
        this.canCollide = false; // 粒子不可以被碰撞
        this.particleMaxAge = maxAge; // 设置粒子生命周期
        this.particleAlpha = 1.0f;
        this.phase = this.rand.nextFloat();
        this.particleScale = 0.55f + this.rand.nextFloat() * 0.05f; // 粒子尺寸

        // 粒子运动
        this.motionX = this.motionY = this.motionZ = 0;
        this.particleGravity = GRAVITY;

        // 加载随机贴图
        TextureAtlasSprite sprite = MC.getTextureMapBlocks().getAtlasSprite(
            TEXTURES.get(random.nextInt(TEXTURES.size())).toString()
        );

        // 设置自定义贴图
        this.setParticleTexture(sprite);
    }

// 粒子更新逻辑
    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        IBlockState posBlockState = world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ));
        Block posBlock = posBlockState.getBlock();

        // 进入非透明方块立即消失
        if (posBlockState.isOpaqueCube()) {
            this.setExpired();
            return;
        }

        // 生命周期检查
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        // 下方支撑检测
        BlockPos supportPos = new BlockPos(
            MathHelper.floor(this.posX),
            MathHelper.floor(this.posY - 0.1),
            MathHelper.floor(this.posZ)
        );
        IBlockState supportState = world.getBlockState(supportPos);
        Block supportBlock = supportState.getBlock();

        if (this.isStopped) {
            BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
            if (!world.getBlockState(pos.down()).isFullCube()) {
                this.isStopped = false;
                this.motionX = 0;
                this.motionZ = 0;
                this.motionY = -0.05; // 缓慢垂直下落
            } else {
                // 下方仍有支撑方块，保持停止状态
                return;
            }
        }

        if (!(posBlock instanceof BlockLeaves)) {
            if (supportBlock instanceof BlockGlass || supportState.isFullCube()) {
                AxisAlignedBB box = supportState.getBoundingBox(world, supportPos);
                // 精确定位到方块表面
                if (box != null && box.maxY > 0) {
                    // 直接强制设置位置
                    this.motionX = this.motionY = this.motionZ = 0;
                    this.setPosition(this.posX, this.posY, this.posZ);
                    this.isStopped = true;
                    return;
                }
            }
        }

        // 核心运动逻辑
        float lifeProgress = (float) this.particleAge / maxAge;
        lifeProgress = Math.min(lifeProgress, 1.0F);

        // 计算水平偏移量
        double offsetX = Math.cos(Math.toRadians(phase * 60.0F)) * 2.0 * Math.pow(lifeProgress, 1.25);
        double offsetZ = Math.sin(Math.toRadians(phase * 60.0F)) * 2.0 * Math.pow(lifeProgress, 1.25);

        // 应用水平速度变化
        this.motionX += offsetX * 0.0025;
        this.motionZ += offsetZ * 0.0025;

        // 应用重力
        this.motionY -= this.particleGravity;

        // 移动粒子
        this.move(this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public int getFXLayer() {
        return 1; // 使用自定义贴图层
    }

// 粒子工厂
    @SideOnly(Side.CLIENT)
    public static class CherryParticleFactory implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
            return new CherryParticle(world, x, y, z);
        }
    }

// 生成粒子效果方法
    private static final Random random = new Random();
    public static void spawnParticle(World world, BlockPos pos) {
        // 生成位置随机散布
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5);
        double y = pos.getY() - 0.01;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5);

        MC.effectRenderer.addEffect(
            new CherryParticle(world, x, y, z)
        );
    }
}