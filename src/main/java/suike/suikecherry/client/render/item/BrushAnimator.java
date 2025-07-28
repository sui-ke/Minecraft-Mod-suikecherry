package suike.suikecherry.client.render.item;

import suike.suikecherry.item.ModItemBrush;

import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.model.ModelRenderer;

public class BrushAnimator {
    public static void animateBrushing(EntityLivingBase entity, ModelRenderer rightArm, ModelRenderer leftArm) {
        boolean isRight = entity.getActiveHand() == EnumHand.MAIN_HAND ^ entity.getPrimaryHand() == EnumHandSide.LEFT;
        ModelRenderer activeArm = isRight ? rightArm : leftArm;

        // 基础角度设置
        activeArm.rotateAngleX = -0.62831855F;
        activeArm.rotateAngleY = 0.0F;

        // 添加刷动动画
        float progress = (float) entity.getItemInUseCount() / ModItemBrush.MAX_USE_DURATION;
        float animationFactor = MathHelper.sin(progress * (float)Math.PI * 8) * 0.1F;

        activeArm.rotateAngleX += animationFactor;
        activeArm.rotateAngleZ = animationFactor * 0.5F;
    }

    public static void animateBrushHold(ModelRenderer rightArm, ModelRenderer leftArm, ModelRenderer head, boolean isRight) {
        ModelRenderer activeArm = isRight ? rightArm : leftArm;
        activeArm.rotateAngleX = -0.62831855F + head.rotateAngleX * 0.5F;
        activeArm.rotateAngleY = head.rotateAngleY * 0.5F;
    }

    private static float lerp(float progress, float start, float end) {
        return start + progress * (end - start);
    }
}