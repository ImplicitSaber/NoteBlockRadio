package io.github.implicitsaber.mod.note_block_radio.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TransmitterListener implements GameEventListener {

    private final PositionSource positionSource;
    private final Supplier<Boolean> shouldTransmit;
    private final Supplier<BlockPos> blockPosSupplier;
    private final Supplier<Integer> channelSupplier;

    public TransmitterListener(PositionSource positionSource, Supplier<Boolean> shouldTransmit, Supplier<BlockPos> blockPosSupplier, Supplier<Integer> channelSupplier) {
        this.positionSource = positionSource;
        this.shouldTransmit = shouldTransmit;
        this.blockPosSupplier = blockPosSupplier;
        this.channelSupplier = channelSupplier;
    }

    @Override
    public @NotNull PositionSource getListenerSource() {
        return this.positionSource;
    }

    @Override
    public int getListenerRadius() {
        return 32;
    }

    @Override
    public boolean handleGameEvent(@NotNull ServerLevel level, Holder<GameEvent> gameEvent, @NotNull GameEvent.Context context, @NotNull Vec3 pos) {
        if(!gameEvent.is(GameEvent.NOTE_BLOCK_PLAY)) return false;
        if(!this.shouldTransmit.get()) return false;
        BlockPos bPos = new BlockPos((int) (pos.x - 0.5), (int) (pos.y - 0.5), (int) (pos.z - 0.5));
        BlockState state = level.getBlockState(bPos);
        if(!state.is(Blocks.NOTE_BLOCK)) return false;
        NoteBlockInstrument instrument = state.getValue(NoteBlock.INSTRUMENT);
        int note = state.getValue(NoteBlock.NOTE);
        RadioHelper.broadcast(level, this.blockPosSupplier.get(), this.channelSupplier.get(), instrument, note);
        return true;
    }

}
