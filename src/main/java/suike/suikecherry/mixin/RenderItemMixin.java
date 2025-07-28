package suike.suikecherry.mixin;

import suike.suikecherry.data.TrimData;
import suike.suikecherry.client.render.item.*;
import suike.suikecherry.block.ModBlockDecoratedPot;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin {
    @Inject(
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onRenderItemHEAD(ItemStack stack, IBakedModel model, CallbackInfo ci) {
        if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof ModBlockDecoratedPot) {
            DecoratedPotItemRender.renderDecoratedPot(stack);
            ci.cancel();
        }
    }

    @Inject(
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V",
        at = @At("RETURN")
    )
    private void onRenderItemRETURN(ItemStack stack, IBakedModel model, CallbackInfo ci) {
        if (stack.getItem() instanceof ItemArmor) {
            ArmorTrimItemRender.renderArmorTrim(stack);
        }
    }
}