package com.snowylov.temperatureapi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.List;

final class TemperatureState extends PersistentState {
    private static final Codec<StoredCell> CELL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("pos").forGetter(StoredCell::pos),
            Codec.INT.fieldOf("temperature").forGetter(StoredCell::temperature),
            Codec.INT.optionalFieldOf("fire_ticks", 0).forGetter(StoredCell::fireTicks)
    ).apply(instance, StoredCell::new));

    static final Codec<TemperatureState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CELL_CODEC.listOf().optionalFieldOf("cells", List.of()).forGetter(TemperatureState::storedCells)
    ).apply(instance, TemperatureState::new));

    private static final PersistentStateType<TemperatureState> TYPE = new PersistentStateType<>(
            "temperature_api_cells", TemperatureState::new, CODEC, null);

    private final Long2ObjectOpenHashMap<Cell> cells = new Long2ObjectOpenHashMap<>();

    TemperatureState() {
    }

    private TemperatureState(List<StoredCell> stored) {
        for (StoredCell cell : stored) {
            if (cell.temperature != 100) cells.put(cell.pos, new Cell(cell.temperature, cell.fireTicks));
        }
    }

    static TemperatureState get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(TYPE);
    }

    int get(long pos) {
        Cell cell = cells.get(pos);
        return cell == null ? 100 : cell.temperature;
    }

    int fireTicks(long pos) {
        Cell cell = cells.get(pos);
        return cell == null ? 0 : cell.fireTicks;
    }

    void set(long pos, int temperature, int fireTicks) {
        if (temperature == 100 && fireTicks == 0) {
            if (cells.remove(pos) != null) markDirty();
            return;
        }
        Cell previous = cells.put(pos, new Cell(temperature, Math.max(0, fireTicks)));
        if (previous == null || previous.temperature != temperature || previous.fireTicks != fireTicks) markDirty();
    }

    private List<StoredCell> storedCells() {
        List<StoredCell> result = new ArrayList<>(cells.size());
        cells.long2ObjectEntrySet().forEach(entry -> result.add(
                new StoredCell(entry.getLongKey(), entry.getValue().temperature, entry.getValue().fireTicks)));
        return result;
    }

    private record StoredCell(long pos, int temperature, int fireTicks) {
    }

    private record Cell(int temperature, int fireTicks) {
    }
}
