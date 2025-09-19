package io.github.implicitsaber.mod.note_block_radio.reg;

import io.github.implicitsaber.mod.note_block_radio.NoteBlockRadio;
import io.github.implicitsaber.mod.note_block_radio.block_entity.RadioBlockEntity;
import io.github.implicitsaber.mod.note_block_radio.block_entity.RadioJammerBlockEntity;
import io.github.implicitsaber.mod.note_block_radio.block_entity.RelayBlockEntity;
import io.github.implicitsaber.mod.note_block_radio.block_entity.TransmitterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NoteBlockRadio.MOD_ID);

    public static final Supplier<BlockEntityType<TransmitterBlockEntity>> TRANSMITTER = REGISTER.register(
            "transmitter",
            () -> BlockEntityType.Builder.of(
                    TransmitterBlockEntity::new,
                    ModBlocks.TRANSMITTER.get()
            ).build(null)
    );

    public static final Supplier<BlockEntityType<RadioBlockEntity>> RADIO = REGISTER.register(
            "radio",
            () -> BlockEntityType.Builder.of(
                    RadioBlockEntity::new,
                    ModBlocks.RADIO.get()
            ).build(null)
    );

    public static final Supplier<BlockEntityType<RadioJammerBlockEntity>> RADIO_JAMMER = REGISTER.register(
            "radio_jammer",
            () -> BlockEntityType.Builder.of(
                    RadioJammerBlockEntity::new,
                    ModBlocks.RADIO_JAMMER.get()
            ).build(null)
    );

    public static final Supplier<BlockEntityType<RelayBlockEntity>> RELAY = REGISTER.register(
            "relay",
            () -> BlockEntityType.Builder.of(
                    RelayBlockEntity::new,
                    ModBlocks.RELAY.get()
            ).build(null)
    );

}
