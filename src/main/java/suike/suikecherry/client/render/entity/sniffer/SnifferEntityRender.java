package suike.suikecherry.client.render.entity.sniffer;

import suike.suikecherry.SuiKe;
import suike.suikecherry.entity.sniffer.SnifferEntity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;

public class SnifferEntityRender extends RenderLiving<SnifferEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(SuiKe.MODID, "textures/entity/sniffer.png");

    public SnifferEntityRender(RenderManager manager) {
        super(manager, new SnifferEntityModel(), 1.1F);
    }

    @Override
    protected ResourceLocation getEntityTexture(SnifferEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(SnifferEntity entity, float partialTickTime) {
        // 幼年缩放
        if (entity.isChild()) {
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
        }

        // 挖掘时下沉效果
        if(entity.getState() == SnifferEntity.State.DIGGING) {
            float progress = entity.getDigProgress(partialTickTime);
            GlStateManager.translate(0, -progress * 0.2F, 0);
            this.spawnParticles(entity);
        }
    }

    public void spawnParticles(SnifferEntity entity) {
        // 渲染尘土粒子效果
        BlockPos pos = entity.getPos().down();
        IBlockState state = entity.world.getBlockState(pos);
        if(state.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            for(int i=0; i<8; i++) {
                entity.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, 
                entity.posX + (entity.getRandom().nextDouble()-0.5)*0.5,
                entity.posY + 0.1,
                entity.posZ + (entity.getRandom().nextDouble()-0.5)*0.5,
                0, 0, 0, 
                Block.getStateId(state));
            }
        }
    }
}