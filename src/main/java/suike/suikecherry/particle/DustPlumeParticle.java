package suike.suikecherry.particle;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.particle.ParticleSmokeNormal;

public class DustPlumeParticle extends ParticleSmokeNormal {
    private static final float scale = 1.0F;
    private static final float gravityStrength = 0.88F;
    private static final float velocityMultiplier = 0.92F;
    private static final int COLOR = 12235202;
    //private static final double MAX_HEIGHT_ABOVE_SPAWN = 3.0D; // 最大上升高度

    private final double spawnY; // 记录初始生成高度
    private boolean isFalling = false; // 是否开始回落

    private DustPlumeParticle(World world, double x, double y, double z) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D, 1.0F);
        this.spawnY = y; // 记录初始Y坐标

        // 设置移动
        this.motionX = (rand.nextDouble() - 0.5) * 0.09D; // 扩散
        this.motionY = rand.nextDouble() * 0.06D + 0.15D; // 上升速度
        this.motionZ = (rand.nextDouble() - 0.5) * 0.09D; // 扩散

        // 设置颜色
        float f = (float) Math.random() * 0.2F;
        this.particleRed = (float) getRed(COLOR) / 255.0F - f;
        this.particleGreen = (float) getGreen(COLOR) / 255.0F - f;
        this.particleBlue = (float) getBlue(COLOR) / 255.0F - f;

        // 设置粒子寿命
        this.particleMaxAge = (int) (5.0F / (this.rand.nextFloat() * 0.8F + 0.3F));

        // 设置粒子大小
        this.particleScale = 1.0F + this.rand.nextFloat() * 0.4F - 0.2F;
    }

// 粒子更新
    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);

        this.motionY += 0.004D;
        this.move(this.motionX, this.motionY, this.motionZ);

        // 应用重力衰减和速度乘数
        this.motionY *= gravityStrength;
        this.motionX *= velocityMultiplier;
        this.motionZ *= velocityMultiplier;
    }

// 粒子颜色
    public static int getRed(int argb) {
        return argb >> 16 & 255;
    }

    public static int getGreen(int argb) {
        return argb >> 8 & 255;
    }

    public static int getBlue(int argb) {
        return argb & 255;
    }

// 生成粒子效果方法
    public static void spawnParticle(World world, BlockPos pos) {
        CherryParticle.MC.effectRenderer.addEffect(
            new DustPlumeParticle(world, pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D)
        );
    }
}