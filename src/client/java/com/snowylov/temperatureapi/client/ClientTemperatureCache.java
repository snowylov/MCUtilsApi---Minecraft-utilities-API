package com.snowylov.temperatureapi.client;

import com.snowylov.temperatureapi.api.TemperatureScale;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.util.math.BlockPos;

public final class ClientTemperatureCache {
    private static final Long2IntOpenHashMap VALUES = new Long2IntOpenHashMap();

    static {
        VALUES.defaultReturnValue(TemperatureScale.STANDARD);
    }

    private ClientTemperatureCache() {
    }

    public static int get(BlockPos pos) {
        return VALUES.get(pos.asLong());
    }

    static void set(BlockPos pos, int temperature) {
        if (temperature == TemperatureScale.STANDARD) VALUES.remove(pos.asLong());
        else VALUES.put(pos.asLong(), temperature);
    }

    static void clear() {
        VALUES.clear();
    }
}
