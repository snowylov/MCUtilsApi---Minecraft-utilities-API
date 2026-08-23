package com.snowylov.temperatureapi.client.mixin;

import com.snowylov.temperatureapi.TemperatureApi;
import com.snowylov.temperatureapi.api.TemperatureScale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ItemModelManager.class)
abstract class ItemModelManagerMixin {
    static final int COLD_TINT_SENTINEL = 0x54454D50; // "TEMP"

    @Inject(method = "clearAndUpdate", at = @At("RETURN"))
    private void temperatureApi$attachColdTint(ItemRenderState renderState, ItemStack stack,
                                                ItemDisplayContext displayContext, @Nullable World world,
                                                @Nullable HeldItemContext heldItemContext, int seed,
                                                CallbackInfo ci) {
        if (stack.isEmpty()) return;
        int temperature = TemperatureApi.getItemTemperature(stack);
        MinecraftClient client = MinecraftClient.getInstance();
        if (temperature == TemperatureScale.STANDARD && world != null && client.player != null) {
            temperature = TemperatureApi.getAirTemperature(world, client.player.getBlockPos());
        }
        TemperatureScale.Tint tint = TemperatureScale.coldTint(temperature);
        if (tint.strength() <= 0.0F) return;

        ItemRenderStateAccessor stateAccessor = (ItemRenderStateAccessor) renderState;
        ItemRenderState.LayerRenderState[] layers = stateAccessor.temperatureApi$getLayers();
        for (int index = 0; index < stateAccessor.temperatureApi$getLayerCount(); index++) {
            ItemLayerRenderStateAccessor layer = (ItemLayerRenderStateAccessor) layers[index];
            int[] oldTints = layer.temperatureApi$getTints();
            int[] tagged = Arrays.copyOf(oldTints, oldTints.length + 2);
            tagged[tagged.length - 2] = COLD_TINT_SENTINEL;
            tagged[tagged.length - 1] = Float.floatToIntBits(tint.strength());
            layer.temperatureApi$setTints(tagged);
        }
    }
}
