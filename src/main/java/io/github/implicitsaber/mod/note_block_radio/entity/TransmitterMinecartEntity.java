package io.github.implicitsaber.mod.note_block_radio.entity;

import io.github.implicitsaber.mod.note_block_radio.NoteBlockRadio;
import io.github.implicitsaber.mod.note_block_radio.block.TransmitterBlock;
import io.github.implicitsaber.mod.note_block_radio.config.NoteBlockRadioCommonConfig;
import io.github.implicitsaber.mod.note_block_radio.reg.ModBlocks;
import io.github.implicitsaber.mod.note_block_radio.reg.ModEntityTypes;
import io.github.implicitsaber.mod.note_block_radio.reg.ModItems;
import io.github.implicitsaber.mod.note_block_radio.util.TransmitterListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class TransmitterMinecartEntity extends AbstractMinecart {

    private static final EntityDataAccessor<Boolean> TRANSMITTING = SynchedEntityData.defineId(TransmitterMinecartEntity.class, EntityDataSerializers.BOOLEAN);

    private final DynamicGameEventListener<TransmitterListener> listener;

    private int channel = 0;
    private boolean activated = false;

    public TransmitterMinecartEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.listener = new DynamicGameEventListener<>(new TransmitterListener(
                new EntityPositionSource(this, 0.0f),
                this::isTransmitting,
                this::blockPosition,
                this::getChannel
        ));
    }

    public TransmitterMinecartEntity(Level level, double x, double y, double z) {
        this(ModEntityTypes.TRANSMITTER_MINECART.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public @NotNull BlockState getDefaultDisplayBlockState() {
        return ModBlocks.TRANSMITTER.get().defaultBlockState().setValue(TransmitterBlock.TRANSMITTING, this.isTransmitting());
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TRANSMITTING, false);
    }

    @Override
    public @NotNull Type getMinecartType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPoweredCart() {
        return false;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    protected @NotNull Item getDropItem() {
        return ModItems.TRANSMITTER_MINECART.get();
    }

    @Override
    public @NotNull ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    @Override
    public void updateDynamicGameEventListener(@NotNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> listenerConsumer) {
        if(this.level() instanceof ServerLevel level) listenerConsumer.accept(this.listener, level);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.getEntityData().set(TRANSMITTING, compound.getBoolean("transmitting"));
        this.channel = compound.getInt("channel");
        this.activated = compound.getBoolean("activated");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("transmitting", this.isTransmitting());
        compound.putInt("channel", this.channel);
        compound.putBoolean("activated", this.activated);
    }

    @Override
    protected void moveAlongTrack(@NotNull BlockPos pos, @NotNull BlockState state) {
        if(state.getBlock() instanceof PoweredRailBlock b && b.isActivatorRail()) {
            boolean powered = state.getValue(PoweredRailBlock.POWERED);
            if(this.activated != powered) {
                this.activated = powered;
                if(powered) this.getEntityData().set(TRANSMITTING, !this.isTransmitting());
            }
        } else this.activated = false;
        super.moveAlongTrack(pos, state);
    }

    @Override
    protected void comeOffTrack() {
        this.activated = false;
        super.comeOffTrack();
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        InteractionResult result = super.interact(player, hand);
        if(result.consumesAction()) return result;
        if(player.isCrouching()) player.displayClientMessage(Component.translatable(NoteBlockRadio.MOD_ID + ".channel_changed", this.changeChannel()), true);
        else player.displayClientMessage(Component.translatable(NoteBlockRadio.MOD_ID + ".get_channel", this.channel), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void applyNaturalSlowdown() {
        Vec3 vel = this.getDeltaMovement().multiply(0.997, 0, 0.997);
        if(this.isInWater()) vel = vel.scale(0.95);
        this.setDeltaMovement(vel);
    }

    public int changeChannel() {
        int channelCount = NoteBlockRadioCommonConfig.CONFIG.channelCount.getAsInt();
        this.channel = (this.channel + 1) % channelCount;
        return this.channel;
    }

    public boolean isTransmitting() {
        return this.getEntityData().get(TRANSMITTING);
    }

    public int getChannel() {
        return this.channel;
    }

}
