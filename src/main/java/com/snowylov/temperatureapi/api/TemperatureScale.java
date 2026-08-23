package com.snowylov.temperatureapi.api;

/** Pure temperature math shared by simulation, rendering, and tests. */
public final class TemperatureScale {
    public static final int STANDARD = 100;
    public static final int ORANGE_TEMPERATURE = 400;
    public static final int RED_TEMPERATURE = 1000;
    public static final int ORANGE_AFTER_TICKS = 20 * 30;
    public static final int RED_AFTER_TICKS = 20 * 60;
    public static final float MAX_METAL_TINT = 0.30F;

    private TemperatureScale() {
    }

    public static int heatedMetalTemperature(int fireTicks) {
        int ticks = Math.max(0, fireTicks);
        if (ticks <= ORANGE_AFTER_TICKS) {
            return STANDARD + Math.round((ORANGE_TEMPERATURE - STANDARD)
                    * (ticks / (float) ORANGE_AFTER_TICKS));
        }
        int redTicks = Math.min(ORANGE_AFTER_TICKS, ticks - ORANGE_AFTER_TICKS);
        return ORANGE_TEMPERATURE + Math.round((RED_TEMPERATURE - ORANGE_TEMPERATURE)
                * (redTicks / (float) ORANGE_AFTER_TICKS));
    }

    public static int stage(int temperature) {
        if (temperature < 75) return 0;
        if (temperature < 150) return 1;
        if (temperature < ORANGE_TEMPERATURE) return 2;
        if (temperature < 700) return 3;
        return 4;
    }

    public static Tint metalTint(int temperature) {
        if (temperature <= STANDARD) return new Tint(0xFFFFFF, 0.0F);
        if (temperature <= ORANGE_TEMPERATURE) {
            float progress = (temperature - STANDARD) / (float) (ORANGE_TEMPERATURE - STANDARD);
            return new Tint(0xFF7900, MAX_METAL_TINT * progress);
        }
        float progress = Math.min(1.0F,
                (temperature - ORANGE_TEMPERATURE) / (float) (RED_TEMPERATURE - ORANGE_TEMPERATURE));
        int green = Math.round(0x79 * (1.0F - progress));
        return new Tint(0xFF0000 | (green << 8), MAX_METAL_TINT);
    }

    public record Tint(int rgb, float strength) {
    }
}
