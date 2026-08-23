package com.snowylov.temperatureapi.client.mixin;

import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderState.LayerRenderState.class)
public interface ItemLayerRenderStateAccessor {
    @Accessor("tints")
    int[] temperatureApi$getTints();

    @Accessor("tints")
    void temperatureApi$setTints(int[] tints);
}
