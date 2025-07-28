package suike.suikecherry.mixin;

import suike.suikecherry.client.render.armor.ArmorTrimRender;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;

import org.spongepowered.asm.mixin.Mixin;  
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LayerArmorBase.class)
public abstract class LayerArmorBaseMixin {
    @Inject(
        method = "renderArmorLayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void renderArmorTrim(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slot, CallbackInfo ci, ItemStack stack, ItemArmor itemArmor, ModelBase model, boolean flag) {
        ArmorTrimRender.renderArmorTrim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, slot, stack, model);
    }
}