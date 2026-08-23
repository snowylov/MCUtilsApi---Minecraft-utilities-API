package com.snowylov.temperatureapi;

import com.snowylov.temperatureapi.api.TemperatureProperties;
import com.snowylov.temperatureapi.api.TemperatureScale;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

final class TemperatureEngine {
    private static final TagKey<net.minecraft.block.Block> METAL = TagKey.of(
            RegistryKeys.BLOCK, TemperatureApi.id("metal"));
    private static final int SCAN_HORIZONTAL = 8;
    private static final int SCAN_VERTICAL = 4;

    private TemperatureEngine() {
    }

    static void initialize() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getTime() % 20L == 0L) tick(world);
        });
    }

    private static void tick(ServerWorld world) {
        LongOpenHashSet visited = new LongOpenHashSet();
        for (ServerPlayerEntity player : world.getPlayers()) {
            BlockPos center = player.getBlockPos();
            for (int x = -SCAN_HORIZONTAL; x <= SCAN_HORIZONTAL; x++) {
                for (int y = -SCAN_VERTICAL; y <= SCAN_VERTICAL; y++) {
                    for (int z = -SCAN_HORIZONTAL; z <= SCAN_HORIZONTAL; z++) {
                        BlockPos pos = center.add(x, y, z);
                        if (visited.add(pos.asLong())) tickPosition(world, pos);
                    }
                }
            }
        }
    }

    private static void tickPosition(ServerWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        boolean metal = state.isIn(METAL) || TemperatureRegistries.block(state.getBlock()).metal();
        TemperatureState storage = TemperatureState.get(world);
        int current = storage.get(pos.asLong());
        int fireTicks = storage.fireTicks(pos.asLong());

        if (metal && touchesFire(world, pos)) {
            fireTicks = Math.min(TemperatureScale.RED_AFTER_TICKS, fireTicks + 20);
            set(world, pos, TemperatureScale.heatedMetalTemperature(fireTicks), fireTicks);
            return;
        }

        int resting = TemperatureRegistries.block(state.getBlock()).restingTemperature();
        if (resting != TemperatureScale.STANDARD) {
            set(world, pos, resting, 0);
        } else if (current != TemperatureScale.STANDARD || fireTicks != 0) {
            int air = TemperatureApi.getAirTemperature(world, pos);
            int cooled = current + Math.round((air - current) * 0.10F);
            if (Math.abs(cooled - TemperatureScale.STANDARD) <= 1) cooled = TemperatureScale.STANDARD;
            set(world, pos, cooled, Math.max(0, fireTicks - 20));
        }
    }

    private static boolean touchesFire(ServerWorld world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = pos.offset(direction);
            BlockState state = world.getBlockState(neighbor);
            if (state.isOf(Blocks.FIRE) || state.isOf(Blocks.SOUL_FIRE)
                    || state.isOf(Blocks.CAMPFIRE) || state.isOf(Blocks.SOUL_CAMPFIRE)
                    || world.getFluidState(neighbor).isIn(FluidTags.LAVA)) return true;
        }
        return false;
    }

    static void set(ServerWorld world, BlockPos pos, int temperature, int fireTicks) {
        TemperatureState storage = TemperatureState.get(world);
        int old = storage.get(pos.asLong());
        storage.set(pos.asLong(), temperature, fireTicks);
        updateModelStage(world, pos, temperature);
        if (old == temperature) return;
        TemperatureApi.fireChanged(world, pos, old, temperature);
        TemperaturePayload payload = new TemperaturePayload(pos.toImmutable(), temperature);
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    private static void updateModelStage(ServerWorld world, BlockPos pos, int temperature) {
        BlockState state = world.getBlockState(pos);
        if (!state.contains(TemperatureProperties.TEMPERATURE_STAGE)) return;
        int stage = TemperatureScale.stage(temperature);
        if (state.get(TemperatureProperties.TEMPERATURE_STAGE) != stage) {
            world.setBlockState(pos, state.with(TemperatureProperties.TEMPERATURE_STAGE, stage), 3);
        }
    }
}
