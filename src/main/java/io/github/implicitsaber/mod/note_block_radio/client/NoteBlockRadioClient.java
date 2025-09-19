package io.github.implicitsaber.mod.note_block_radio.client;

import io.github.implicitsaber.mod.note_block_radio.NoteBlockRadio;
import io.github.implicitsaber.mod.note_block_radio.reg.ModEntityTypes;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = NoteBlockRadio.MOD_ID, dist = Dist.CLIENT)
public class NoteBlockRadioClient {

    public static final ModelLayerLocation TRANSMITTER_MINECART_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NoteBlockRadio.MOD_ID, "transmitter_minecart"),
            "main"
    );

    public NoteBlockRadioClient(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::onRegisterLayers);
        modEventBus.addListener(this::onRegisterEntityRenderers);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TRANSMITTER_MINECART_LAYER, MinecartModel::createBodyLayer);
    }

    public void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.TRANSMITTER_MINECART.get(), ctx -> new MinecartRenderer<>(ctx, TRANSMITTER_MINECART_LAYER));
    }

}
