package com.snowylov.temperatureapi.api;

/** Thermal defaults for a registered block, fluid, or item. */
public record TemperatureProfile(
        int restingTemperature,
        float heatCapacity,
        float conductivity,
        float emissivity,
        boolean metal,
        boolean tintable
) {
    public TemperatureProfile {
        if (heatCapacity <= 0.0F) throw new IllegalArgumentException("heatCapacity must be positive");
        conductivity = clamp01(conductivity);
        emissivity = clamp01(emissivity);
    }

    public static TemperatureProfile neutral() {
        return new TemperatureProfile(TemperatureScale.STANDARD, 1.0F, 0.5F, 0.5F, false, false);
    }

    public static TemperatureProfile metal() {
        return new TemperatureProfile(TemperatureScale.STANDARD, 4.0F, 0.9F, 0.35F, true, true);
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
