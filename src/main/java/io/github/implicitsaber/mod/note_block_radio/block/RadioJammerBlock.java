package io.github.implicitsaber.mod.note_block_radio.block;

import com.mojang.serialization.MapCodec;
import io.github.implicitsaber.mod.note_block_radio.block_entity.RadioJammerBlockEntity;
import io.github.implicitsaber.mod.note_block_radio.reg.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RadioJammerBlock extends BaseEntityBlock {

    public static final MapCodec<BaseEntityBlock> CODEC = simpleCodec(RadioJammerBlock::new);
    public static final BooleanProperty JAMMING = BooleanProperty.create("jamming");

    public RadioJammerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(JAMMING, false));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(JAMMING, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
    }

    @Override
    protected void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean movedByPiston) {
        if(level.isClientSide()) return;
        boolean jamming = state.getValue(JAMMING);
        boolean shouldJam = level.hasNeighborSignal(pos);
        if(jamming != shouldJam) level.setBlock(pos, state.setValue(JAMMING, shouldJam), Block.UPDATE_CLIENTS);
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(JAMMING);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if(level.isClientSide()) return InteractionResult.SUCCESS;
        if(!(level.getBlockEntity(pos) instanceof RadioJammerBlockEntity be)) return InteractionResult.CONSUME;
        if(!player.isCrouching()) player.displayClientMessage(Component.translatable("note_block_radio.get_channel", be.getChannel()), true);
        else player.displayClientMessage(Component.translatable("note_block_radio.channel_changed", be.changeChannel()), true);
        return InteractionResult.CONSUME;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RadioJammerBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return !level.isClientSide() ? createTickerHelper(blockEntityType, ModBlockEntityTypes.RADIO_JAMMER.get(), RadioJammerBlockEntity::serverTick) : null;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

}
