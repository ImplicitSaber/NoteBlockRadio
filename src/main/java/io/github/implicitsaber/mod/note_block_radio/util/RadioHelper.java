package io.github.implicitsaber.mod.note_block_radio.util;

import com.mojang.logging.LogUtils;
import io.github.implicitsaber.mod.note_block_radio.config.NoteBlockRadioCommonConfig;
import io.github.implicitsaber.mod.note_block_radio.reg.ModAttachmentTypes;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Consumer;

public class RadioHelper {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void registerReceiver(Level level, BlockPos pos) {
        ChunkPos cPos = new ChunkPos(pos);
        ChunkAccess chunk = level.getChunk(cPos.x, cPos.z);
        ObjectOpenHashSet<BlockPos> set = chunk.getData(ModAttachmentTypes.RECEIVER_SET);
        set.add(pos);
    }

    public static void unRegisterReceiver(Level level, BlockPos pos) {
        ChunkPos cPos = new ChunkPos(pos);
        ChunkAccess chunk = level.getChunk(cPos.x, cPos.z);
        ObjectOpenHashSet<BlockPos> set = chunk.getExistingDataOrNull(ModAttachmentTypes.RECEIVER_SET);
        if(set == null) return;
        set.remove(pos);
        if(set.isEmpty()) chunk.removeData(ModAttachmentTypes.RECEIVER_SET);
    }

    public static void broadcast(Level level, BlockPos pos, int channel, NoteBlockInstrument instrument, int note) {
        Set<BlockPos> visited = new ObjectOpenHashSet<>();
        Queue<Relay> q = new ArrayDeque<>();
        q.offer(new Relay(pos, 0));
        while(!q.isEmpty()) {
            Relay r = q.poll();
            broadcastInternal(level, r.pos(), channel, instrument, note, visited, q::offer, r.depth());
        }
    }

    private static void broadcastInternal(Level level, BlockPos pos, int channel, NoteBlockInstrument instrument, int note, Set<BlockPos> visited, Consumer<Relay> relayAdder, int depth) {
        int totalRange = NoteBlockRadioCommonConfig.CONFIG.losslessRange.getAsInt() + NoteBlockRadioCommonConfig.CONFIG.lossyRange.getAsInt();
        int chunkScanSize = Math.ceilDiv(totalRange, 16);
        ChunkPos centerChunk = new ChunkPos(pos);
        int startX = centerChunk.x - chunkScanSize;
        int startZ = centerChunk.z - chunkScanSize;
        int endX = centerChunk.x + chunkScanSize;
        int endZ = centerChunk.z + chunkScanSize;
        double sqLossless = NoteBlockRadioCommonConfig.CONFIG.losslessRange.getAsInt() * NoteBlockRadioCommonConfig.CONFIG.losslessRange.getAsInt();
        double sqLossy = NoteBlockRadioCommonConfig.CONFIG.lossyRange.getAsInt() * NoteBlockRadioCommonConfig.CONFIG.lossyRange.getAsInt();
        double sqTotal = sqLossless + sqLossy;
        for(int cX = startX; cX <= endX; cX++) {
            for(int cZ = startZ; cZ <= endZ; cZ++) {
                ChunkAccess chunk = level.getChunk(cX, cZ, ChunkStatus.FULL, false);
                if(chunk == null) continue;
                ObjectOpenHashSet<BlockPos> set = chunk.getExistingDataOrNull(ModAttachmentTypes.RECEIVER_SET);
                if(set == null) continue;
                Iterator<BlockPos> it = set.iterator();
                while(it.hasNext()) {
                    BlockPos bPos = it.next();
                    if(visited.contains(bPos)) continue;
                    BlockState state = chunk.getBlockState(bPos);
                    if(!(state.getBlock() instanceof RadioReceiver r)) {
                        LOGGER.info("Had to cleanup receiver at {}. There's probably a bug here.", bPos);
                        it.remove();
                        continue;
                    }
                    int xDiff = bPos.getX() - pos.getX();
                    int zDiff = bPos.getZ() - pos.getZ();
                    double sqDist = xDiff * xDiff + zDiff * zDiff;
                    boolean received = false;
                    if(sqDist <= sqLossless) received = true;
                    else if(sqDist <= sqTotal) {
                        double lossProbability = (sqDist - sqLossless) / sqLossy;
                        if(level.random.nextDouble() > lossProbability) received = true;
                    }
                    if(received) {
                        visited.add(bPos);
                        r.receiveNote(level, state, bPos, channel, instrument, note);
                        if(depth < NoteBlockRadioCommonConfig.CONFIG.relayHopLimit.getAsInt() && r.isRelay(state)) relayAdder.accept(new Relay(bPos, depth + 1));
                    }
                }
                if(set.isEmpty()) chunk.removeData(ModAttachmentTypes.RECEIVER_SET);
            }
        }
    }

    private record Relay(BlockPos pos, int depth) {}

}
