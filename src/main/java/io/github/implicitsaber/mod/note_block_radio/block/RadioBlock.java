package io.github.implicitsaber.mod.note_block_radio.block;

import com.mojang.serialization.MapCodec;
import io.github.implicitsaber.mod.note_block_radio.block_entity.RadioBlockEntity;
import io.github.implicitsaber.mod.note_block_radio.util.RadioHelper;
import io.github.implicitsaber.mod.note_block_radio.util.RadioReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RadioBlock extends BaseEntityBlock implements RadioReceiver {

    public static final MapCodec<BaseEntityBlock> CODEC = simpleCodec(RadioBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty RECEIVING = BooleanProperty.create("receiving");

    public static final VoxelShape NORTH_SOUTH_SHAPE = Shapes.or(
            box(2, 0, 6, 14, 6, 10),
            box(3, 6, 7, 5, 7, 9),
            box(11, 6, 7, 13, 7, 9),
            box(3, 7, 7, 13, 9, 9)
    );

    public static final VoxelShape EAST_WEST_SHAPE = Shapes.or(
            box(6, 0, 2, 10, 6, 14),
            box(7, 6, 3, 9, 7, 5),
            box(7, 6, 11, 9, 7, 13),
            box(7, 7, 3, 9, 9, 13)
    );

    public RadioBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(RECEIVING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(FACING, RECEIVING);
    }

    @Override
    protected @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if(level.isClientSide()) return InteractionResult.SUCCESS;
        if(!(level.getBlockEntity(pos) instanceof RadioBlockEntity be)) return InteractionResult.CONSUME;
        if(player.isCrouching()) player.displayClientMessage(Component.translatable("note_block_radio.channel_changed", be.changeChannel()), true);
        else level.setBlock(pos, state.cycle(RECEIVING), Block.UPDATE_CLIENTS);
        return InteractionResult.CONSUME;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RadioBlockEntity(pos, state);
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
        if(!state.getValue(RECEIVING)) return;
        if(!(level.getBlockEntity(pos) instanceof RadioBlockEntity be)) return;
        if(channel != be.getChannel()) return;
        if(level instanceof ServerLevel sLvl && instrument.isTunable()) {
            sLvl.sendParticles(
                    ParticleTypes.NOTE,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    0,
                    note / 24.0,
                    0.0,
                    0.0,
                    1.0
            );
        }
        level.playSeededSound(
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                instrument.getSoundEvent(),
                SoundSource.RECORDS,
                3.0F,
                instrument.isTunable() ? NoteBlock.getPitchFromNote(note) : 1.0f,
                level.random.nextLong()
        );
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? NORTH_SOUTH_SHAPE : EAST_WEST_SHAPE;
    }

}
