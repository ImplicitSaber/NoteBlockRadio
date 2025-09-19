package io.github.implicitsaber.mod.note_block_radio.reg;

import io.github.implicitsaber.mod.note_block_radio.NoteBlockRadio;
import io.github.implicitsaber.mod.note_block_radio.entity.TransmitterMinecartEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, NoteBlockRadio.MOD_ID);

    public static final Supplier<EntityType<TransmitterMinecartEntity>> TRANSMITTER_MINECART = REGISTER.register(
            "transmitter_minecart",
            () -> EntityType.Builder.<TransmitterMinecartEntity>of(TransmitterMinecartEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.7F)
                    .passengerAttachments(0.1875F)
                    .clientTrackingRange(8)
                    .build(ResourceLocation.fromNamespaceAndPath(NoteBlockRadio.MOD_ID, "transmitter_minecart").toString())
    );

}
