package io.github.implicitsaber.mod.note_block_radio.block;

import com.mojang.serialization.MapCodec;
import io.github.implicitsaber.mod.note_block_radio.NoteBlockRadio;
import io.github.implicitsaber.mod.note_block_radio.block_entity.RelayBlockEntity;
import io.github.implicitsaber.mod.note_block_radio.util.RadioHelper;
import io.github.implicitsaber.mod.note_block_radio.util.RadioReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RelayBlock extends BaseEntityBlock implements RadioReceiver {

    public static final MapCodec<BaseEntityBlock> CODEC = simpleCodec(RelayBlock::new);
    public static final BooleanProperty RELAYING = BooleanProperty.create("relaying");

    public RelayBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(RELAYING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(RELAYING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(RELAYING, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
    }

    @Override
    protected void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean movedByPiston) {
        if(level.isClientSide()) return;
        boolean relaying = state.getValue(RELAYING);
        boolean shouldRelay = level.hasNeighborSignal(pos);
        if(relaying != shouldRelay) level.setBlock(pos, state.setValue(RELAYING, shouldRelay), Block.UPDATE_CLIENTS);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if(level.isClientSide()) return InteractionResult.SUCCESS;
        if(!(level.getBlockEntity(pos) instanceof RelayBlockEntity be)) return InteractionResult.CONSUME;
        if(!player.isCrouching()) player.displayClientMessage(Component.translatable(NoteBlockRadio.MOD_ID + ".get_channel", be.getChannel()), true);
        else player.displayClientMessage(Component.translatable(NoteBlockRadio.MOD_ID + ".channel_changed", be.changeChannel()), true);
        return InteractionResult.CONSUME;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RelayBlockEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if(!state.is(newState.getBlock())) RadioHelper.unRegisterReceiver(level, pos);
    }

    @Override
    public void receiveNote(Level level, BlockState state, BlockPos pos, int channel, NoteBlockInstrument instrument, int note) {
        // no-op
    }

    @Override
    public boolean isRelay(BlockState state) {
        return state.getValue(RELAYING);
    }

}
