package com.snowylov.temperatureapi.api;

import net.minecraft.state.property.IntProperty;

public final class TemperatureProperties {
    /** Five texture/model stages: cold, normal, warm, hot, and glowing. */
    public static final IntProperty TEMPERATURE_STAGE = IntProperty.of("temperature_stage", 0, 4);

    private TemperatureProperties() {
    }
}
