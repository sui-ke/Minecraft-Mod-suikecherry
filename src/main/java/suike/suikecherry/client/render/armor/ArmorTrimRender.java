package suike.suikecherry.client.render.armor;

import suike.suikecherry.data.TrimData;

import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ArmorTrimRender {
    public static void renderArmorTrim(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slot, ItemStack stack, ModelBase model) {
        ResourceLocation trimTexture = TrimData.getTrimTexture(stack, isLegSlot(slot));
        float[] trimColor = TrimData.getTrimColor(stack);
        if (trimTexture == null || trimColor == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        );

        Minecraft.getMinecraft().getTextureManager().bindTexture(trimTexture);
        GlStateManager.color(trimColor[0], trimColor[1], trimColor[2], 1.0F);

        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private static boolean isLegSlot(EntityEquipmentSlot slot) {
        return slot == EntityEquipmentSlot.LEGS;
    }
}