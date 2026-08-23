package com.snowylov.temperatureapi;

import com.snowylov.temperatureapi.api.AirTemperatureProvider;
import com.snowylov.temperatureapi.api.TemperatureChangeCallback;
import com.snowylov.temperatureapi.api.TemperatureProfile;
import com.snowylov.temperatureapi.api.TemperatureScale;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class TemperatureApi implements ModInitializer {
    public static final String MOD_ID = "temperature_api";

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        TemperatureComponents.initialize();
        PayloadTypeRegistry.playS2C().register(TemperaturePayload.ID, TemperaturePayload.CODEC);
        TemperatureEngine.initialize();
    }

    public static int getBlockTemperature(World world, BlockPos pos) {
        if (world instanceof ServerWorld serverWorld) return TemperatureState.get(serverWorld).get(pos.asLong());
        return ClientTemperatureAccess.get(pos);
    }

    public static void setBlockTemperature(ServerWorld world, BlockPos pos, int temperature) {
        TemperatureEngine.set(world, pos, temperature, TemperatureState.get(world).fireTicks(pos.asLong()));
    }

    public static int getFluidTemperature(World world, BlockPos pos) {
        int stored = getBlockTemperature(world, pos);
        if (stored != TemperatureScale.STANDARD) return stored;
        FluidState fluidState = world.getFluidState(pos);
        TemperatureProfile profile = TemperatureRegistries.fluid(fluidState.getFluid());
        return profile.restingTemperature();
    }

    public static int getAirTemperature(World world, BlockPos pos) {
        int temperature = Math.round(100.0F + (world.getBiome(pos).value().getTemperature() - 0.8F) * 25.0F);
        for (AirTemperatureProvider provider : TemperatureRegistries.airProviders()) {
            Integer replacement = provider.getTemperature(world, pos, temperature);
            if (replacement != null) temperature = replacement;
        }
        return temperature;
    }

    public static int getItemTemperature(ItemStack stack) {
        return stack.getOrDefault(TemperatureComponents.TEMPERATURE, TemperatureScale.STANDARD);
    }

    public static void setItemTemperature(ItemStack stack, int temperature) {
        if (temperature == TemperatureScale.STANDARD) stack.remove(TemperatureComponents.TEMPERATURE);
        else stack.set(TemperatureComponents.TEMPERATURE, temperature);
    }

    static void fireChanged(World world, BlockPos pos, int oldTemperature, int newTemperature) {
        TemperatureChangeCallback.EVENT.invoker().onChanged(world, pos, oldTemperature, newTemperature);
    }
}
