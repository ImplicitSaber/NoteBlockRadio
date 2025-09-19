package io.github.implicitsaber.mod.note_block_radio.reg;

import io.github.implicitsaber.mod.note_block_radio.NoteBlockRadio;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, NoteBlockRadio.MOD_ID);

    public static final Supplier<AttachmentType<ObjectOpenHashSet<BlockPos>>> RECEIVER_SET = REGISTER.register(
            "receiver_set",
            () -> AttachmentType.builder(() -> new ObjectOpenHashSet<BlockPos>()).build()
    );

}
