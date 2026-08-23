package com.snowylov.temperatureapi;

import com.snowylov.temperatureapi.api.TemperatureScale;
import net.minecraft.util.math.BlockPos;

import java.util.function.ToIntFunction;

/** Side-safe bridge installed by the physical client entrypoint. */
public final class ClientTemperatureAccess {
    private static ToIntFunction<BlockPos> provider = pos -> TemperatureScale.STANDARD;

    private ClientTemperatureAccess() {
    }

    public static int get(BlockPos pos) {
        return provider.applyAsInt(pos);
    }

    public static void install(ToIntFunction<BlockPos> clientProvider) {
        provider = clientProvider;
    }
}
