package io.github.implicitsaber.mod.note_block_radio.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class NoteBlockRadioCommonConfig {

    public static final NoteBlockRadioCommonConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.IntValue losslessRange;
    public final ModConfigSpec.IntValue lossyRange;
    public final ModConfigSpec.IntValue channelCount;
    public final ModConfigSpec.IntValue relayHopLimit;

    private NoteBlockRadioCommonConfig(ModConfigSpec.Builder builder) {
        this.losslessRange = builder
                .comment(" The range at which a transmission is received the same as it was sent.")
                .defineInRange("lossless_range", 128, 1, 4096);
        this.lossyRange = builder
                .comment(" The range added onto the lossless range at which a transmission is received, but some notes may be lost.")
                .defineInRange("lossy_range", 64, 1, 4096);
        this.channelCount = builder
                .comment(" The amount of channels available to select from.")
                .defineInRange("channel_count", 16, 1, 128);
        this.relayHopLimit = builder
                .comment(" The maximum amount of relays a signal can hop between to reach a radio.")
                .defineInRange("relay_hop_limit", 8, 1, 64);
    }

    static {
        Pair<NoteBlockRadioCommonConfig, ModConfigSpec> p = new ModConfigSpec.Builder().configure(NoteBlockRadioCommonConfig::new);
        CONFIG = p.getLeft();
        CONFIG_SPEC = p.getRight();
    }

}
