package io.github.implicitsaber.mod.note_block_radio.reg;

import io.github.implicitsaber.mod.note_block_radio.NoteBlockRadio;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NoteBlockRadio.MOD_ID);

    public static final Supplier<CreativeModeTab> MAIN = REGISTER.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + NoteBlockRadio.MOD_ID + ".main"))
                    .icon(() -> new ItemStack(ModItems.RADIO.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.RADIO);
                        output.accept(ModItems.TRANSMITTER);
                        output.accept(ModItems.RADIO_JAMMER);
                        output.accept(ModItems.RELAY);
                        output.accept(ModItems.TRANSMITTER_MINECART);
                    })
                    .build()
    );

}
