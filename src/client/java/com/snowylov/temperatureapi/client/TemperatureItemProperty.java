package com.snowylov.temperatureapi.client;

import com.mojang.serialization.MapCodec;
import com.snowylov.temperatureapi.TemperatureApi;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import org.jspecify.annotations.Nullable;

public final class TemperatureItemProperty implements NumericProperty {
    public static final TemperatureItemProperty INSTANCE = new TemperatureItemProperty();
    public static final MapCodec<TemperatureItemProperty> CODEC = MapCodec.unit(INSTANCE);

    private TemperatureItemProperty() {
    }

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world,
                          @Nullable HeldItemContext context, int seed) {
        return TemperatureApi.getItemTemperature(stack);
    }

    @Override
    public MapCodec<? extends NumericProperty> getCodec() {
        return CODEC;
    }
}
