package com.snowylov.temperatureapi.client;

import com.snowylov.temperatureapi.TemperatureApi;
import com.snowylov.temperatureapi.ClientTemperatureAccess;
import com.snowylov.temperatureapi.TemperaturePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.item.property.numeric.NumericProperties;

public final class TemperatureApiClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTemperatureAccess.install(ClientTemperatureCache::get);
        NumericProperties.ID_MAPPER.put(TemperatureApi.id("temperature"), TemperatureItemProperty.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(TemperaturePayload.ID, (payload, context) ->
                context.client().execute(() -> {
                    ClientTemperatureCache.set(payload.pos(), payload.temperature());
                    if (context.client().worldRenderer != null) {
                        int x = payload.pos().getX();
                        int y = payload.pos().getY();
                        int z = payload.pos().getZ();
                        context.client().worldRenderer.scheduleBlockRenders(x, y, z, x, y, z);
                    }
                }));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientTemperatureCache.clear());
    }
}
