package com.snowylov.temperatureapi.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemperatureScaleTest {
    @Test
    void metalIsOrangeAtThirtySeconds() {
        assertEquals(400, TemperatureScale.heatedMetalTemperature(600));
        TemperatureScale.Tint tint = TemperatureScale.metalTint(400);
        assertEquals(0xFF7900, tint.rgb());
        assertEquals(0.30F, tint.strength(), 0.0001F);
    }

    @Test
    void redPhaseStartsAfterThirtySecondsAndNeverExceedsThirtyPercent() {
        assertTrue(TemperatureScale.heatedMetalTemperature(601) > 400);
        for (int temperature = 100; temperature <= 2000; temperature++) {
            assertTrue(TemperatureScale.metalTint(temperature).strength() <= 0.30F);
        }
        assertEquals(0xFF0000, TemperatureScale.metalTint(1000).rgb());
    }

    @Test
    void snowyColdIsEightPercentAndExtremeColdCapsAtFortyPercent() {
        assertEquals(80, TemperatureScale.biomeAirTemperature(0.0F));
        assertEquals(0.08F, TemperatureScale.coldTint(80).strength(), 0.0001F);
        assertEquals(0.40F, TemperatureScale.coldTint(0).strength(), 0.0001F);
        assertEquals(0.40F, TemperatureScale.coldTint(-1000).strength(), 0.0001F);
        assertEquals(0.0F, TemperatureScale.coldTint(100).strength(), 0.0001F);
        for (int temperature = -1000; temperature <= 100; temperature++) {
            assertTrue(TemperatureScale.coldTint(temperature).strength() <= 0.40F);
        }
    }
}
