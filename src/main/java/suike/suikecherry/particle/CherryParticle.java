package suike.suikecherry.particle;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sblock.BlockBase;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

//@Mod.EventBusSubscriber
public class CherryParticle extends Particle {

    public static Random random = new Random();
    public static final double[] axis = new double[]{0.03D, 0.04D, 0.05D, 0.06D, 0.07D};

    public CherryParticle(World world, double x, double y, double z) {
        super(world, x, y, z);

        // 获取粒子贴图
        ResourceLocation textureName = getTEXTURE();
        Texture texture = new Texture(textureName);

        // 设置粒子贴图
        this.TEXTURE = textureName;
        this.particleTexture = texture;
        this.particleTextureIndexX = 3;
        this.particleTextureIndexY = 3;

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

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.TEXTURE);
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
    private ResourceLocation TEXTURE;
    public static List<ResourceLocation> TEXTURES = new ArrayList<>();
    public static ResourceLocation getTEXTURE() {
        return TEXTURES.get(random.nextInt(TEXTURES.size()));
    }

    @SubscribeEvent
    public static void onTextureStitchPre(TextureStitchEvent.Pre event) {
        String textureName = "suikecherry:particle/cherry_";

        for (int i = 0; i <= 11; i++) {
            ResourceLocation texture = new ResourceLocation(textureName + i);
            TEXTURES.add(texture);
            event.getMap().registerSprite(texture);
        }
    }

    class Texture extends TextureAtlasSprite {
        public Texture(ResourceLocation texture) {
            super(texture.toString());

            this.width = 3;
            this.height = 3;
            this.initSprite(2, 2, 5, 5, true);
        }
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
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }
}