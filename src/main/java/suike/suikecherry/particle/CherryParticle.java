package suike.suikecherry.particle;

import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sblock.BlockBase;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;

public class CherryParticle extends Particle {

    private static Random random = new Random();
    private static final double[] axis = new double[]{0.03D, 0.04D, 0.05D, 0.06D, 0.07D};

    public CherryParticle(World world, double x, double y, double z) {
        super(world, x, y, z);

        // 获取粒子贴图
        Texture texture = new Texture();

        // 设置粒子贴图
        this.particleTexture = texture;
        this.particleTextureIndexX = 1;
        this.particleTextureIndexY = 1;

        // 设置粒子生命周期
        this.isExpired = false;
        this.particleAge = 0;
        this.particleMaxAge = (20 * 10); // 粒子生命周期 / 10s

        // 粒子运动
        this.motionX = axis[random.nextInt(axis.length)];
        this.motionY = 0;
        this.motionZ = axis[random.nextInt(axis.length)];

        // 粒子不可以被碰撞
        this.canCollide = false;
    }

//生成粒子效果方法
    public static void spawnParticle(World world, BlockPos pos) {
        IBlockState downBlockState = world.getBlockState(pos.down());
        Block downBlock = downBlockState.getBlock();

        if (!downBlock.isFullCube(downBlockState) && random.nextInt(8) == 0) {
            double x = pos.getX() + (Math.random() * 1);
            double y = pos.getY() - 0.01;
            double z = pos.getZ() + (Math.random() * 1);

            CherryParticle particle = new CherryParticle(world, x, y, z);
            net.minecraft.client.Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }

//粒子更新逻辑
    private double fall = 0.09;
    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.motionY = -fall;
        if (this.particleAge % 5 == 0 && fall >= 0.01)
            /*更新下落速度*/fall -= 0.002;

        //移动
        this.move(this.motionX, this.motionY, this.motionZ);

        expiredParticle();
    }

//检查移除
    public void expiredParticle() {
        BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
        IBlockState posBlockState = this.world.getBlockState(pos);
        Block posBlock = posBlockState.getBlock();

        // 粒子生命周期结束 || (当前位置方块是完整方块 && 当前位置方块不是樱花树叶)
        if (this.particleAge++ >= this.particleMaxAge || (posBlock.isFullCube(posBlockState) && posBlock != BlockBase.CHERRY_LEAVES))
            /*移除粒子*/this.setExpired();
    }

//材质
    class Texture extends TextureAtlasSprite {
        public Texture() {
            super("suikecherry:particle/cherry_particle");

            this.width = 2;
            this.height = 2;
            this.initSprite(2, 2, 5, 5, false);
        }
    }
}