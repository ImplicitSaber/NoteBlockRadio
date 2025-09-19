package io.github.implicitsaber.mod.note_block_radio.reg;

import io.github.implicitsaber.mod.note_block_radio.NoteBlockRadio;
import io.github.implicitsaber.mod.note_block_radio.block.RadioBlock;
import io.github.implicitsaber.mod.note_block_radio.block.RadioJammerBlock;
import io.github.implicitsaber.mod.note_block_radio.block.RelayBlock;
import io.github.implicitsaber.mod.note_block_radio.block.TransmitterBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(NoteBlockRadio.MOD_ID);

    public static final DeferredBlock<TransmitterBlock> TRANSMITTER = REGISTER.register(
            "transmitter",
            () -> new TransmitterBlock(BlockBehaviour.Properties.of())
    );

    public static final DeferredBlock<RadioBlock> RADIO = REGISTER.register(
            "radio",
            () -> new RadioBlock(BlockBehaviour.Properties.of().noOcclusion())
    );

    public static final DeferredBlock<RadioJammerBlock> RADIO_JAMMER = REGISTER.register(
            "radio_jammer",
            () -> new RadioJammerBlock(BlockBehaviour.Properties.of())
    );

    public static final DeferredBlock<RelayBlock> RELAY = REGISTER.register(
            "relay",
            () -> new RelayBlock(BlockBehaviour.Properties.of().noOcclusion())
    );

}
