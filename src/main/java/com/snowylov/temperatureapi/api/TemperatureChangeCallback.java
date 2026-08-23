package com.snowylov.temperatureapi.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface TemperatureChangeCallback {
    Event<TemperatureChangeCallback> EVENT = EventFactory.createArrayBacked(
            TemperatureChangeCallback.class,
            listeners -> (world, pos, oldTemperature, newTemperature) -> {
                for (TemperatureChangeCallback listener : listeners) {
                    listener.onChanged(world, pos, oldTemperature, newTemperature);
                }
            });

    void onChanged(World world, BlockPos pos, int oldTemperature, int newTemperature);
}
