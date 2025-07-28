package suike.suikecherry.client.render.entity.sniffer;

import suike.suikecherry.SuiKe;
import suike.suikecherry.entity.sniffer.SnifferEntity;

import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SnifferEntityModel extends ModelBase {
    private SnifferEntity sniffer;
    private final ModelRenderer root;
    private final ModelRenderer bone;

    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer leftEar;
    private final ModelRenderer rightEar;
    private final ModelRenderer nose;
    // private final ModelRenderer noseCore;
    private final ModelRenderer lowerBeak;
    private final ModelRenderer[] legs = new ModelRenderer[6];

    public SnifferEntityModel() {
        textureWidth = 192;
        textureHeight = 192;

        // 根部件
        this.root = new ModelRenderer(this);
        this.root.setRotationPoint(0F, 5F, 0F);
        this.bone = new ModelRenderer(this);
        this.root.addChild(this.bone);

        // 身体部分
        this.body = new ModelRenderer(this);
        this.body.setTextureOffset(62, 68).addBox(-12.5F, -14F, -20F, 25, 29, 40);
        this.body.setTextureOffset(62, 0).addBox(-12.5F, -14F, -20F, 25, 24, 40, 0.5F);
        this.body.setTextureOffset(87, 68).addBox(-12.5F, 12F, -20F, 25, 0, 40);
        this.bone.addChild(this.body);

        // 头部
        this.head = new ModelRenderer(this);
        this.head.setTextureOffset(8, 15).addBox(-6.5F, -7.5F, -11.5F, 13, 18, 11);
        this.head.setTextureOffset(8, 4).addBox(-6.5F, 7.5F, -11.5F, 13, 0, 11);
        this.head.setRotationPoint(0F, 6.5F, -19.48F);
        this.body.addChild(this.head);

        // 左耳
        this.leftEar = new ModelRenderer(this);
        this.leftEar.setTextureOffset(2, 0).addBox(0F, 0F, -3F, 1, 19, 7);
        this.leftEar.setRotationPoint(6.51F, -7.5F, -4.51F);
        this.head.addChild(this.leftEar);

        // 右耳
        this.rightEar = new ModelRenderer(this);
        this.rightEar.setTextureOffset(48, 0).addBox(-1F, 0F, -3F, 1, 19, 7);
        this.rightEar.setRotationPoint(-6.51F, -7.5F, -4.51F);
        this.head.addChild(this.rightEar);

        // 鼻子主体
        this.nose = new ModelRenderer(this);
        this.nose.setTextureOffset(10, 45).addBox(-6.5F, -2F, -9F, 13, 2, 9);
        this.nose.setRotationPoint(0F, -4.5F, -11.5F);
        this.head.addChild(this.nose);

        // 鼻子子体
        // this.noseCore = new ModelRenderer(this);
        // this.noseCore.setTextureOffset(10, 45).addBox(-6.5F, -2F, -9F, 13, 2, 9);
        // this.noseCore.setRotationPoint(0F, 0F, 0F);
        // this.nose.addChild(noseCore);

        // 下喙
        this.lowerBeak = new ModelRenderer(this);
        this.lowerBeak.setTextureOffset(10, 57).addBox(-6.5F, -7F, -8F, 13, 12, 9);
        this.lowerBeak.setRotationPoint(0F, 2.5F, -12.5F);
        this.head.addChild(this.lowerBeak);

        // 腿部初始化
        initLegs();
    }

    private void initLegs() {
        this.legs[0] = createLeg(32, 87, -7.5F, 10F, -15F);  // 右前
        this.legs[1] = createLeg(0, 87, 7.5F, 10F, -15F);    // 左前
        this.legs[2] = createLeg(32, 105, -7.5F, 10F, 0F);   // 右中
        this.legs[3] = createLeg(0, 105, 7.5F, 10F, 0F);     // 左中
        this.legs[4] = createLeg(32, 123, -7.5F, 10F, 15F);  // 右后
        this.legs[5] = createLeg(0, 123, 7.5F, 10F, 15F);    // 左后

        for(ModelRenderer leg : this.legs) {
            this.bone.addChild(leg);
        }
    }

    private ModelRenderer createLeg(int texU, int texV, float x, float y, float z) {
        ModelRenderer leg = new ModelRenderer(this, texU, texV);
        leg.addBox(-3.5F, -1F, -4F, 7, 10, 8);
        leg.setRotationPoint(x, y, z);
        return leg;
    }

// 渲染
    // 音效触发标记
    private int lastScentSoundTime = 0;
    private int lastSniffSoundTime = 0;
    private static final int SCENT_CYCLE = 160; // 8秒*20ticks
    private static final int SNIFF_CYCLE = 100; // 5秒*20ticks

    /*/ 鼻子动画状态控制
    private boolean isScenting = false;
    private boolean isSniffing = false;
    private int animationStartTick = 0;
    private final int SCENT_DURATION = 160; // 8秒*20tick
    private final int SNIFF_DURATION = 40;  // 2秒*20tick

    // 移动范围限制
    private static final float MAX_UP_OFFSET = 0.9F;    // 最大上移量（不超过鼻子高度）

    private boolean isAnimating() {
        return isScenting || isSniffing;
    }

    private void startScenting(int currentTick) {
        isScenting = true;
        animationStartTick = currentTick;
    }

    private void startSniffing(int currentTick) {
        isSniffing = true;
        animationStartTick = currentTick;
    }*/

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        this.sniffer = (SnifferEntity) entity;

        /*if((int)ageInTicks % 40 == 0) {
            // 30%概率触发SCENTING，20%概率触发SNIFFING
            float rand = sniffer.getRNG().nextFloat();
            if(rand < 0.3F && !isAnimating()) {
                startScenting((int)ageInTicks);
            } 
            else if(rand < 0.5F && !isAnimating()) {
                startSniffing((int)ageInTicks);
            }
        }

        if(isScenting) {
            applyScentingAnimation((int) ageInTicks);
        } 
        else if(isSniffing) {
            applySniffingAnimation((int) ageInTicks);
        }*/

        // 头部基础旋转
        head.rotateAngleX = headPitch * 0.017453292F;
        head.rotateAngleY = netHeadYaw * 0.017453292F;

        // 耳朵动画更新
        this.updateEarAnimations(ageInTicks);

        // 腿部动画
        if(this.sniffer.getState() == SnifferEntity.State.DIGGING) {
            applyDiggingAnimations(ageInTicks);
        } else {
            this.applyWalkingAnimations(ageInTicks, limbSwing, limbSwingAmount);
        }
    }

    // 耳朵动画更新
    private void updateEarAnimations(float ageInTicks) {
        // 挖掘时抬起耳朵
        if (this.sniffer.getState() == SnifferEntity.State.DIGGING) {
            leftEar.rotateAngleZ = -0.6F;
            rightEar.rotateAngleZ = 0.6F;
            return;
        }

        leftEar.rotateAngleZ = -this.sniffer.getEarAngle();
        rightEar.rotateAngleZ = this.sniffer.getEarAngle();
    }

    // 挖掘动画
    private void applyDiggingAnimations(float ageInTicks) {
        // 身体抖动
        body.rotateAngleX = MathHelper.sin(ageInTicks * 0.8F) * 0.2F;
        body.offsetY = MathHelper.sin(ageInTicks * 1.2F) * 0.1F;

        // 头部摆动
        head.rotateAngleX += MathHelper.sin(ageInTicks * 0.6F) * 0.3F;

        // 腿部特殊姿势
        for(int i=0; i<legs.length; i++) {
            legs[i].rotateAngleZ = (i%2==0 ? 1.2F : -1.2F);
            legs[i].offsetY = -0.2F;
        }
    }

    // 行走动画
    private void applyWalkingAnimations(float ageInTicks, float limbSwing, float limbSwingAmount) {
        float walkSpeed = 1.6662F;

        // 前腿
        legs[0].rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * 1.6F * limbSwingAmount;
        legs[1].rotateAngleX = MathHelper.cos(limbSwing * walkSpeed + (float)Math.PI) * 1.6F * limbSwingAmount;

        // 中腿
        legs[2].rotateAngleX = MathHelper.cos(limbSwing * walkSpeed * 0.8F) * 0.96F * limbSwingAmount;
        legs[3].rotateAngleX = MathHelper.cos(limbSwing * walkSpeed * 0.8F + (float) Math.PI) * 0.96F * limbSwingAmount;

        // 后腿
        legs[4].rotateAngleX = MathHelper.cos(limbSwing * walkSpeed * 0.6F) * 0.96F * limbSwingAmount;
        legs[5].rotateAngleX = MathHelper.cos(limbSwing * walkSpeed * 0.6F + (float) Math.PI) * 0.96F * limbSwingAmount;

        // 身体摆动
        this.body.rotateAngleZ = MathHelper.sin(ageInTicks * 0.02F) * 0.1F;
        this.body.rotateAngleX = MathHelper.sin(ageInTicks * 0.1F) * 0.02F;

        // 头部摆动
        this.head.rotateAngleX += MathHelper.sin(ageInTicks * 0.08F) * 0.08F;
    }

    /*/ 长嗅探
    private static final SoundEvent scentingSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(SuiKe.MODID, "entity.sniffer.scenting"));
    private void applyScentingAnimation(float ageInTicks) {
        int currentTime = (int) ageInTicks;
        float animTime = ageInTicks * 0.125F; // 8秒周期
        float cycle = animTime % 2.0F;

        if(cycle > 0.5833F && cycle <= 0.6667F) {
            if(currentTime - lastScentSoundTime > SCENT_CYCLE) {
                if(this.sniffer != null) {
                    this.playSound(scentingSound); // 触发音效
                }
                lastScentSoundTime = currentTime;
            }
        }

        float elapsed = ageInTicks - animationStartTick;
        float progress = Math.min(elapsed / (float)SCENT_DURATION, 1.0F);

        // 三次贝塞尔曲线控制运动轨迹
        float offsetY = calculateBezierY(progress, 
            0.25F, 0.1F,   // 控制点1
            0.75F, 0.8F    // 控制点2
        );
        
        // 应用限制：0.0F ≤ offsetY ≤ MAX_UP_OFFSET
        noseCore.offsetY = MathHelper.clamp(offsetY * MAX_UP_OFFSET, 0.0F, MAX_UP_OFFSET);
        
        // 结束检测
        if(progress >= 1.0F) {
            isScenting = false;
            noseCore.offsetY = 0.0F;
        }
    }

    // 短促的嗅探
    private static final SoundEvent sniffingSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(SuiKe.MODID, "entity.sniffer.sniffing"));
    private void applySniffingAnimation(float ageInTicks) {
        int currentTime = (int) ageInTicks;
        float animTime = ageInTicks * 0.2F; // 5秒周期
        float cycle = animTime % 1.0F;

        if(cycle < 0.125F) {
            if(currentTime - lastSniffSoundTime > SNIFF_CYCLE) {
                if(this.sniffer != null) {
                    this.playSound(sniffingSound);
                }
                lastSniffSoundTime = currentTime;
            }
        }

        float elapsed = ageInTicks - animationStartTick;
        float progress = Math.min(elapsed / (float)SNIFF_DURATION, 1.0F);

        // 正弦曲线运动
        float offsetY = MathHelper.sin(progress * (float)Math.PI * 2) * 0.5F;

        // 应用限制: 0 ≤ offsetY ≤ MAX_UP_OFFSET
        offsetY = MathHelper.clamp(offsetY, 0, MAX_UP_OFFSET);
        noseCore.offsetY = offsetY;

        // 结束检测
        if(progress >= 1.0F) {
            isSniffing = false;
            noseCore.offsetY = 0.0F;
        }
    }

    // 三次贝塞尔曲线计算（用于SCENTING）
    private float calculateBezierY(float t, float cp1x, float cp1y, float cp2x, float cp2y) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;
        
        float y = uuu * 0 + 
                3 * uu * t * cp1y + 
                3 * u * tt * cp2y + 
                ttt * 1;
        
        return y;
    }

    public void playSound(SoundEvent soundEvent) {
        this.sniffer.world.playSound(null, this.sniffer.getPos(), soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }*/
}