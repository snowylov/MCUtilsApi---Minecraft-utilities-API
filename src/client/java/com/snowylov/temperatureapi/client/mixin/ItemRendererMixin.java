package com.snowylov.temperatureapi.client.mixin;

import com.snowylov.temperatureapi.api.TemperatureScale;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(ItemRenderer.class)
abstract class ItemRendererMixin {
    private static final ThreadLocal<Float> TEMPERATURE_API_COLD_STRENGTH =
            ThreadLocal.withInitial(() -> 0.0F);

    @Inject(method = "renderBakedItemQuads", at = @At("HEAD"))
    private static void temperatureApi$captureColdTint(MatrixStack matrices, VertexConsumer consumer,
                                                       List<BakedQuad> quads, int[] tints,
                                                       int light, int overlay, CallbackInfo ci) {
        float strength = 0.0F;
        if (tints.length >= 2 && tints[tints.length - 2] == ItemModelManagerMixin.COLD_TINT_SENTINEL) {
            strength = Math.min(TemperatureScale.MAX_COLD_TINT,
                    Math.max(0.0F, Float.intBitsToFloat(tints[tints.length - 1])));
        }
        TEMPERATURE_API_COLD_STRENGTH.set(strength);
    }

    @Inject(method = "renderBakedItemQuads", at = @At("RETURN"))
    private static void temperatureApi$clearColdTint(MatrixStack matrices, VertexConsumer consumer,
                                                     List<BakedQuad> quads, int[] tints,
                                                     int light, int overlay, CallbackInfo ci) {
        TEMPERATURE_API_COLD_STRENGTH.remove();
    }

    @ModifyArgs(
            method = "renderBakedItemQuads",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/model/BakedQuad;FFFFII)V")
    )
    private static void temperatureApi$applyColdTint(Args args) {
        float amount = TEMPERATURE_API_COLD_STRENGTH.get();
        if (amount <= 0.0F) return;
        float red = ((TemperatureScale.COLD_TINT_RGB >> 16) & 0xFF) / 255.0F;
        float green = ((TemperatureScale.COLD_TINT_RGB >> 8) & 0xFF) / 255.0F;
        float blue = (TemperatureScale.COLD_TINT_RGB & 0xFF) / 255.0F;
        args.set(2, blend((float) args.get(2), red, amount));
        args.set(3, blend((float) args.get(3), green, amount));
        args.set(4, blend((float) args.get(4), blue, amount));
    }

    private static float blend(float base, float target, float amount) {
        return base + (target - base) * amount;
    }
}
