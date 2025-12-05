package suike.suikecherry.client.render.item;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.data.TrimData;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;

public class ArmorTrimItemRender {

    private static final ModelMask model = new ModelMask();
    private static final ResourceLocation HELMET = new ResourceLocation(SuiKe.MODID, "textures/trims/items/helmet_trim.png");
    private static final ResourceLocation CHEST  = new ResourceLocation(SuiKe.MODID, "textures/trims/items/chestplate_trim.png");
    private static final ResourceLocation LEG    = new ResourceLocation(SuiKe.MODID, "textures/trims/items/leggings_trim.png");
    private static final ResourceLocation BOOTS  = new ResourceLocation(SuiKe.MODID, "textures/trims/items/boots_trim.png");

    static void renderItem(ItemStack stack) {
        // 获取颜色纹理
        float[] colorRGB = TrimData.getTrimColor(stack);
        if (colorRGB == null) return; // 无纹饰

        // 获取纹理
        ResourceLocation trimTexture = getTrimTexture(((ItemArmor) stack.getItem()).armorType.getIndex());
        Minecraft.getMinecraft().getTextureManager().bindTexture(trimTexture);

        GlStateManager.pushMatrix();
        GlStateManager.color(colorRGB[0], colorRGB[1], colorRGB[2], 1.0f);
        try {
            model.root.render(0.0626F);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static ResourceLocation getTrimTexture(int armorType) {
        switch (armorType) {
            case 0: return BOOTS;
            case 1: return LEG;
            case 2: return CHEST;
            default: return HELMET;
        }
    }

    private static class ModelMask extends ModelBase {
        private final ModelRenderer root;
        private final ModelRenderer mask;

        private ModelMask() {
            textureWidth = 64;
            textureHeight = 32;

            // 根部件
            this.root = new ModelRenderer(this);

            this.mask = new ModelRenderer(this, 0, 0);
            this.mask.addBox(-8.0F, -0.5F, -8.0F, 16, 1, 16);
            this.root.addChild(this.mask);

            this.root.rotateAngleX = -(((float) Math.PI) / 2);
        }
    }
}