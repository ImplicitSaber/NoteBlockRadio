package io.github.implicitsaber.mod.note_block_radio.block_entity;

import io.github.implicitsaber.mod.note_block_radio.config.NoteBlockRadioCommonConfig;
import io.github.implicitsaber.mod.note_block_radio.reg.ModBlockEntityTypes;
import io.github.implicitsaber.mod.note_block_radio.util.RadioHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RelayBlockEntity extends BlockEntity {

    private int channel = 0;

    public RelayBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.RELAY.get(), pos, blockState);
    }

    @Override
    public void setLevel(@NotNull Level level) {
        super.setLevel(level);
        RadioHelper.registerReceiver(level, worldPosition);
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

}
