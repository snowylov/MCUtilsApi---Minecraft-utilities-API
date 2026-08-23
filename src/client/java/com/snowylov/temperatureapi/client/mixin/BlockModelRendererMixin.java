package com.snowylov.temperatureapi.client.mixin;

import com.snowylov.temperatureapi.api.TemperatureScale;
import com.snowylov.temperatureapi.client.ClientTemperatureCache;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockModelRenderer.class)
abstract class BlockModelRendererMixin {
    private static final ThreadLocal<BlockPos> TEMPERATURE_API_POS = new ThreadLocal<>();
    private static final ThreadLocal<BlockRenderView> TEMPERATURE_API_WORLD = new ThreadLocal<>();

    @Inject(method = "render", at = @At("HEAD"))
    private void temperatureApi$capturePosition(BlockRenderView world, List<BlockModelPart> parts,
                                                 BlockState state, BlockPos pos, MatrixStack matrices,
                                                 VertexConsumer consumer, boolean cull, int overlay,
                                                 CallbackInfo ci) {
        TEMPERATURE_API_POS.set(pos);
        TEMPERATURE_API_WORLD.set(world);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void temperatureApi$clearPosition(BlockRenderView world, List<BlockModelPart> parts,
                                               BlockState state, BlockPos pos, MatrixStack matrices,
                                               VertexConsumer consumer, boolean cull, int overlay,
                                               CallbackInfo ci) {
        TEMPERATURE_API_POS.remove();
        TEMPERATURE_API_WORLD.remove();
    }

    @ModifyArgs(
            method = "renderQuad",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/model/BakedQuad;[FFFF[II)V")
    )
    private void temperatureApi$applyTint(Args args) {
        BlockPos pos = TEMPERATURE_API_POS.get();
        if (pos == null) return;
        int temperature = ClientTemperatureCache.get(pos);
        BlockRenderView view = TEMPERATURE_API_WORLD.get();
        if (temperature == TemperatureScale.STANDARD && view != null) {
            int encoded = view.getColor(pos, (biome, x, z) -> {
                int value = Math.max(0, Math.min(255, Math.round(biome.getTemperature() * 127.5F)));
                return 0xFF000000 | (value << 16) | (value << 8) | value;
            });
            float blendedBiomeTemperature = ((encoded >> 16) & 0xFF) / 127.5F;
            temperature = TemperatureScale.biomeAirTemperature(blendedBiomeTemperature);
        }
        TemperatureScale.Tint tint = TemperatureScale.blockTint(temperature);
        if (tint.strength() <= 0.0F) return;
        float red = ((tint.rgb() >> 16) & 0xFF) / 255.0F;
        float green = ((tint.rgb() >> 8) & 0xFF) / 255.0F;
        float blue = (tint.rgb() & 0xFF) / 255.0F;
        float amount = tint.strength();
        args.set(3, blend((float) args.get(3), red, amount));
        args.set(4, blend((float) args.get(4), green, amount));
        args.set(5, blend((float) args.get(5), blue, amount));
    }

    private static float blend(float base, float target, float amount) {
        return base + (target - base) * amount;
    }
}
