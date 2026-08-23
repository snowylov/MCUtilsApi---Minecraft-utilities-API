package com.snowylov.temperatureapi;

import com.snowylov.temperatureapi.api.AirTemperatureProvider;
import com.snowylov.temperatureapi.api.TemperatureProfile;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class TemperatureRegistries {
    private static final Map<Block, TemperatureProfile> BLOCKS = new ConcurrentHashMap<>();
    private static final Map<Fluid, TemperatureProfile> FLUIDS = new ConcurrentHashMap<>();
    private static final Map<Item, TemperatureProfile> ITEMS = new ConcurrentHashMap<>();
    private static final List<AirTemperatureProvider> AIR = new CopyOnWriteArrayList<>();

    private TemperatureRegistries() {
    }

    public static void registerBlock(Block block, TemperatureProfile profile) { BLOCKS.put(block, profile); }
    public static void registerFluid(Fluid fluid, TemperatureProfile profile) { FLUIDS.put(fluid, profile); }
    public static void registerItem(Item item, TemperatureProfile profile) { ITEMS.put(item, profile); }
    public static void registerAirProvider(AirTemperatureProvider provider) { AIR.add(provider); }
    public static TemperatureProfile block(Block block) { return BLOCKS.getOrDefault(block, TemperatureProfile.neutral()); }
    public static TemperatureProfile fluid(Fluid fluid) { return FLUIDS.getOrDefault(fluid, TemperatureProfile.neutral()); }
    public static TemperatureProfile item(Item item) { return ITEMS.getOrDefault(item, TemperatureProfile.neutral()); }
    static List<AirTemperatureProvider> airProviders() { return List.copyOf(AIR); }
}
