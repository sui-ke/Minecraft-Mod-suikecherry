package suike.suikecherry.client.render.entity.boat;

import suike.suikecherry.entity.boat.ModEntityBoat;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class RaftModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer bottom;
    private final ModelRenderer leftPaddle;
    private final ModelRenderer rightPaddle;

    public RaftModel() {
        textureWidth = 128;
        textureHeight = 64;

        this.root = new ModelRenderer(this);

        // 船底部分
        this.bottom = new ModelRenderer(this);
        this.bottom.setRotationPoint(0.0F, -2.1F, 1.0F);
        this.bottom.rotateAngleX = (float)Math.PI / 2;
        this.root.addChild(this.bottom);
        // 船底
        ModelRenderer bottom1 = new ModelRenderer(this, 0, 0);
        bottom1.addBox(-14.0F, -11.0F, -4.0F, 28, 20, 4);
        this.bottom.addChild(bottom1);
        // 甲板
        ModelRenderer bottom2 = new ModelRenderer(this, 0, 0);
        bottom2.addBox(-14.0F, -9.0F, -8.0F, 28, 16, 4);
        this.bottom.addChild(bottom2);

        // 左船桨
        this.leftPaddle = new ModelRenderer(this);
        this.leftPaddle.setRotationPoint(3.0F, -4.0F, 9.0F);
        this.leftPaddle.rotateAngleZ = 0.19634955F;
        this.root.addChild(this.leftPaddle);

        ModelRenderer paddleLeft1 = new ModelRenderer(this, 0, 24);
        paddleLeft1.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18);
        this.leftPaddle.addChild(paddleLeft1);

        ModelRenderer paddleLeft2 = new ModelRenderer(this, 0, 24);
        paddleLeft2.addBox(-1.001F, -3.0F, 8.0F, 1, 6, 7);
        this.leftPaddle.addChild(paddleLeft2);

        // 右船桨
        this.rightPaddle = new ModelRenderer(this);
        this.rightPaddle.setRotationPoint(3.0F, -4.0F, -9.0F);
        this.rightPaddle.rotateAngleY = (float)Math.PI;
        this.rightPaddle.rotateAngleZ = 0.19634955F;
        this.root.addChild(this.rightPaddle);

        ModelRenderer paddleRight1 = new ModelRenderer(this, 40, 24);
        paddleRight1.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18);
        this.rightPaddle.addChild(paddleRight1);

        ModelRenderer paddleRight2 = new ModelRenderer(this, 40, 24);
        paddleRight2.addBox(0.001F, -3.0F, 8.0F, 1, 6, 7);
        this.rightPaddle.addChild(paddleRight2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        ModEntityBoat boat = (ModEntityBoat) entity;

        // 设置左船桨角度
        float leftPaddleTime = boat.getRowingTime(0, limbSwing);
        this.leftPaddle.rotateAngleX = (float)MathHelper.clampedLerp(
            -1.0471975803375244D, 
            -0.2617993950843811D, 
            (MathHelper.sin(-leftPaddleTime) + 1.0F) / 2.0F
        );
        this.leftPaddle.rotateAngleY = (float)MathHelper.clampedLerp(
            -(Math.PI / 4D), 
            (Math.PI / 4D), 
            (MathHelper.sin(-leftPaddleTime + 1.0F) + 1.0F) / 2.0F
        );

        // 设置右船桨角度（镜像对称）
        float rightPaddleTime = boat.getRowingTime(1, limbSwing);
        this.rightPaddle.rotateAngleX = (float)MathHelper.clampedLerp(
            -1.0471975803375244D, 
            -0.2617993950843811D, 
            (MathHelper.sin(-rightPaddleTime) + 1.0F) / 2.0F
        );
        float tempY = (float)MathHelper.clampedLerp(
            -(Math.PI / 4D), 
            (Math.PI / 4D), 
            (MathHelper.sin(-rightPaddleTime + 1.0F) + 1.0F) / 2.0F
        );
        this.rightPaddle.rotateAngleY = (float)Math.PI - tempY;
    }
}