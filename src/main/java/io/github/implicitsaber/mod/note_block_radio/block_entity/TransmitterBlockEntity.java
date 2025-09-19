package io.github.implicitsaber.mod.note_block_radio.block_entity;

import io.github.implicitsaber.mod.note_block_radio.block.TransmitterBlock;
import io.github.implicitsaber.mod.note_block_radio.config.NoteBlockRadioCommonConfig;
import io.github.implicitsaber.mod.note_block_radio.reg.ModBlockEntityTypes;
import io.github.implicitsaber.mod.note_block_radio.util.TransmitterListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jetbrains.annotations.NotNull;

public class TransmitterBlockEntity extends BlockEntity implements GameEventListener.Provider<GameEventListener> {

    private final GameEventListener listener = new TransmitterListener(
            new BlockPositionSource(worldPosition),
            () -> this.getBlockState().getValue(TransmitterBlock.TRANSMITTING),
            () -> this.worldPosition,
            () -> this.channel
    );

    private int channel = 0;

    public TransmitterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.TRANSMITTER.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("channel", this.channel);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.channel = tag.getInt("channel");
    }

    public int changeChannel() {
        int channelCount = NoteBlockRadioCommonConfig.CONFIG.channelCount.getAsInt();
        this.channel = (this.channel + 1) % channelCount;
        return this.channel;
    }

    public int getChannel() {
        return channel;
    }

    @Override
    public @NotNull GameEventListener getListener() {
        return this.listener;
    }

}
