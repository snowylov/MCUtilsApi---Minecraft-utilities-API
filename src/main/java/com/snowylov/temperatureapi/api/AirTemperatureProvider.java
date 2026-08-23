package com.snowylov.temperatureapi.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface AirTemperatureProvider {
    /** Return a replacement temperature, or {@code null} to let later providers/defaults decide. */
    Integer getTemperature(World world, BlockPos pos, int currentTemperature);
}
