package com.snowylov.temperatureapi;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record TemperaturePayload(BlockPos pos, int temperature) implements CustomPayload {
    public static final Id<TemperaturePayload> ID = new Id<>(TemperatureApi.id("block_temperature"));
    public static final PacketCodec<RegistryByteBuf, TemperaturePayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeBlockPos(value.pos);
                buf.writeVarInt(value.temperature);
            },
            buf -> new TemperaturePayload(buf.readBlockPos(), buf.readVarInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
