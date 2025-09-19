package io.github.implicitsaber.mod.note_block_radio.reg;

import io.github.implicitsaber.mod.note_block_radio.NoteBlockRadio;
import io.github.implicitsaber.mod.note_block_radio.item.TransmitterMinecartItem;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(NoteBlockRadio.MOD_ID);

    public static final DeferredItem<BlockItem> TRANSMITTER = REGISTER.registerSimpleBlockItem(
            "transmitter",
            ModBlocks.TRANSMITTER
    );

    public static final DeferredItem<BlockItem> RADIO = REGISTER.registerSimpleBlockItem(
            "radio",
            ModBlocks.RADIO
    );

    public static final DeferredItem<BlockItem> RADIO_JAMMER = REGISTER.registerSimpleBlockItem(
            "radio_jammer",
            ModBlocks.RADIO_JAMMER
    );

    public static final DeferredItem<BlockItem> RELAY = REGISTER.registerSimpleBlockItem(
            "relay",
            ModBlocks.RELAY
    );

    public static final DeferredItem<TransmitterMinecartItem> TRANSMITTER_MINECART = REGISTER.registerItem(
            "transmitter_minecart",
            TransmitterMinecartItem::new
    );

}
