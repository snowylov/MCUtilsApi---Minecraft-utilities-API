package com.snowylov.temperatureapi;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class TemperatureComponents {
    public static final ComponentType<Integer> TEMPERATURE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            TemperatureApi.id("temperature"),
            ComponentType.<Integer>builder().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).build());

    private TemperatureComponents() {
    }

    static void initialize() {
        // Class loading performs registration.
    }
}
