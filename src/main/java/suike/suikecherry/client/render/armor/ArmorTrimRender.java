package suike.suikecherry.client.render.armor;

import java.util.Map;
import java.util.HashMap;

import suike.suikecherry.data.TrimData;
import suike.suikecherry.expand.Examine;

import goblinbob.mobends.standard.client.model.armor.ArmorWrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelArmorStandArmor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.inventory.EntityEquipmentSlot;

import net.minecraftforge.fml.common.Loader;

import com.google.common.collect.ImmutableMap;

public class ArmorTrimRender {

    private static final Minecraft MC = Minecraft.getMinecraft();

    // 普通盔甲模型
    private static final Map<EntityEquipmentSlot, ModelBiped> MODEL_MAP = ImmutableMap.<EntityEquipmentSlot, ModelBiped>builder()
        .put(EntityEquipmentSlot.HEAD , getModelBiped(0))
        .put(EntityEquipmentSlot.CHEST, getModelBiped(1))
        .put(EntityEquipmentSlot.LEGS , getModelBiped(2))
        .put(EntityEquipmentSlot.FEET , getModelBiped(3))
        .build();

    public static final Map<Class<? extends ModelBase>, Map<EntityEquipmentSlot, ModelBase>> MODEL_MODEL_MAP = new HashMap<>();
    public static final Map<Class<? extends EntityLivingBase>, Map<EntityEquipmentSlot, ModelBase>> ENTITY_MODEL_MAP = new HashMap<>();
    static {
        // 盔甲架盔甲模型
        Map<EntityEquipmentSlot, ModelBase> armorStandBuilder = ImmutableMap.<EntityEquipmentSlot, ModelBase>builder()
            .put(EntityEquipmentSlot.HEAD , getArmorStandModel(0))
            .put(EntityEquipmentSlot.CHEST, getArmorStandModel(1))
            .put(EntityEquipmentSlot.LEGS , getArmorStandModel(2))
            .put(EntityEquipmentSlot.FEET , getArmorStandModel(3))
            .build();
        ENTITY_MODEL_MAP.put(EntityArmorStand.class, armorStandBuilder);

        if (Examine.MobendsID) {
            // 更多弯曲模组盔甲模型
            Map<EntityEquipmentSlot, ModelBase> armorWrapperBuilder = ImmutableMap.<EntityEquipmentSlot, ModelBase>builder()
                .put(EntityEquipmentSlot.HEAD , ArmorWrapper.createFor(MODEL_MAP.get(EntityEquipmentSlot.HEAD)))
                .put(EntityEquipmentSlot.CHEST, ArmorWrapper.createFor(MODEL_MAP.get(EntityEquipmentSlot.CHEST)))
                .put(EntityEquipmentSlot.LEGS , ArmorWrapper.createFor(MODEL_MAP.get(EntityEquipmentSlot.LEGS)))
                .put(EntityEquipmentSlot.FEET , ArmorWrapper.createFor(MODEL_MAP.get(EntityEquipmentSlot.FEET)))
                .build();
            MODEL_MODEL_MAP.put(ArmorWrapper.class, armorWrapperBuilder);
        }
    }

    public static void renderArmorTrim(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slot, ItemStack stack, ModelBase originalModel) {
        ResourceLocation trimTexture = TrimData.getTrimTexture(stack, isLegSlot(slot));
        float[] trimColor = TrimData.getTrimColor(stack);
        if (trimTexture == null || trimColor == null) return;

        ModelBase model = getModel(entity, slot, originalModel);

        model.setModelAttributes(originalModel);
        model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);

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
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-1.0F, -1.0F);

        MC.getTextureManager().bindTexture(trimTexture);
        GlStateManager.color(trimColor[0], trimColor[1], trimColor[2], 1.0F);

        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, getScale(stack, scale));

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private static ModelBase getModel(EntityLivingBase entity, EntityEquipmentSlot slot, ModelBase originalModel) {
        Map<EntityEquipmentSlot, ModelBase> modelMap = ENTITY_MODEL_MAP.get(entity.getClass());
        if (modelMap != null) {
            return modelMap.get(slot);
        }

        modelMap = MODEL_MODEL_MAP.get(originalModel.getClass());
        if (modelMap != null) {
            return modelMap.get(slot);
        }

        return MODEL_MAP.get(slot);
    }

    private static boolean isLegSlot(EntityEquipmentSlot slot) {
        return slot == EntityEquipmentSlot.LEGS;
    }

    private static float getScale(ItemStack stack, float scale) {
        if (!Examine.BlockArmorID) return scale;
        return stack.getItem().getRegistryName().toString().startsWith("blockarmor:") ? (scale + 0.001F) : scale;
    }

    private static ModelBiped getModelBiped(int slot) {
        return setupModelVisibility(new ModelBiped(slot == 2 ? 0.5F : 1.0F), slot);
    }

    private static ModelBiped getArmorStandModel(int slot) {
        return setupModelVisibility(new ModelArmorStandArmor(slot == 2 ? 0.5F : 1.0F), slot);
    }

    private static ModelBiped setupModelVisibility(ModelBiped model, int slot) {
        switch (slot) {
            case 0:
                model.bipedHead.showModel = true;
                model.bipedHeadwear.showModel = true;
                model.bipedBody.showModel = false;
                model.bipedRightArm.showModel = false;
                model.bipedLeftArm.showModel = false;
                model.bipedRightLeg.showModel = false;
                model.bipedLeftLeg.showModel = false;
                break;
            case 1:
                model.bipedBody.showModel = true;
                model.bipedRightArm.showModel = true;
                model.bipedLeftArm.showModel = true;
                model.bipedHead.showModel = false;
                model.bipedHeadwear.showModel = false;
                model.bipedRightLeg.showModel = false;
                model.bipedLeftLeg.showModel = false;
                break;
            case 2:
                model.bipedRightLeg.showModel = true;
                model.bipedLeftLeg.showModel = true;
                model.bipedHead.showModel = false;
                model.bipedHeadwear.showModel = false;
                model.bipedBody.showModel = false;
                model.bipedRightArm.showModel = false;
                model.bipedLeftArm.showModel = false;
                break;
            case 3:
                model.bipedRightLeg.showModel = true;
                model.bipedLeftLeg.showModel = true;
                model.bipedHead.showModel = false;
                model.bipedHeadwear.showModel = false;
                model.bipedBody.showModel = false;
                model.bipedRightArm.showModel = false;
                model.bipedLeftArm.showModel = false;
                break;
        }
        return model;
    }
}