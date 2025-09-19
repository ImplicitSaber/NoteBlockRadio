package io.github.implicitsaber.mod.note_block_radio;

import com.mojang.logging.LogUtils;
import io.github.implicitsaber.mod.note_block_radio.config.NoteBlockRadioCommonConfig;
import io.github.implicitsaber.mod.note_block_radio.reg.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import org.slf4j.Logger;

@Mod(NoteBlockRadio.MOD_ID)
public class NoteBlockRadio {

    public static final String MOD_ID = "note_block_radio";
    private static final Logger LOGGER = LogUtils.getLogger();

    private final IModInfo modInfo;

    public NoteBlockRadio(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        this.modInfo = modContainer.getModInfo();
        modContainer.registerConfig(ModConfig.Type.COMMON, NoteBlockRadioCommonConfig.CONFIG_SPEC);

        ModItems.REGISTER.register(modEventBus);
        ModBlocks.REGISTER.register(modEventBus);
        ModBlockEntityTypes.REGISTER.register(modEventBus);
        ModEntityTypes.REGISTER.register(modEventBus);
        ModCreativeTabs.REGISTER.register(modEventBus);
        ModAttachmentTypes.REGISTER.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Hello from {} v{}", this.modInfo.getDisplayName(), this.modInfo.getVersion());
    }

}
