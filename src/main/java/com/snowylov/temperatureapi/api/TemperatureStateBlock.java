package com.snowylov.temperatureapi.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;

/** Base block for mods that want temperature-driven blockstate model variants. */
public class TemperatureStateBlock extends Block {
    public TemperatureStateBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(TemperatureProperties.TEMPERATURE_STAGE, 1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(TemperatureProperties.TEMPERATURE_STAGE);
    }
}
