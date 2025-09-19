package io.github.implicitsaber.mod.note_block_radio.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public interface RadioReceiver {

    void receiveNote(Level level, BlockState state, BlockPos pos, int channel, NoteBlockInstrument instrument, int note);
    default boolean isRelay(BlockState state) { return false; }

}
