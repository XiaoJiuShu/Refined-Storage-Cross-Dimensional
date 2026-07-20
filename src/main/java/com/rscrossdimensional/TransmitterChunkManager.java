package com.rscrossdimensional;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeNetworkTransmitter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TransmitterChunkManager {
    private static final Map<NetworkNodeNetworkTransmitter, ForcedChunk> forcedChunks = new HashMap<>();

    public static void update(NetworkNodeNetworkTransmitter transmitter) {
        BlockPos receiver = transmitter.getReceiver();
        if (transmitter.getWorld().isRemote
            || !transmitter.canUpdate()
            || receiver == null
            || transmitter.isSameDimension()) {
            release(transmitter);
            return;
        }

        int receiverDimension = transmitter.getReceiverDimension();
        WorldServer receiverWorld = DimensionManager.getWorld(receiverDimension);
        if (receiverWorld == null && DimensionManager.isDimensionRegistered(receiverDimension)) {
            DimensionManager.initDimension(receiverDimension);
            receiverWorld = DimensionManager.getWorld(receiverDimension);
        }
        if (receiverWorld == null) {
            release(transmitter);
            return;
        }

        ChunkPos receiverChunk = new ChunkPos(receiver);
        ForcedChunk current = forcedChunks.get(transmitter);
        if (current != null
            && current.dimension == receiverDimension
            && current.chunk.equals(receiverChunk)) {
            return;
        }

        release(transmitter);
        ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(
            RSCrossDimensional.instance,
            receiverWorld,
            ForgeChunkManager.Type.NORMAL
        );
        if (ticket != null) {
            ForgeChunkManager.forceChunk(ticket, receiverChunk);
            forcedChunks.put(transmitter, new ForcedChunk(receiverDimension, receiverChunk, ticket));
        }
    }

    public static void release(World world, BlockPos pos) {
        forcedChunks.entrySet().removeIf(entry -> {
            NetworkNodeNetworkTransmitter transmitter = entry.getKey();
            if (transmitter.getWorld() == world && transmitter.getPos().equals(pos)) {
                ForgeChunkManager.releaseTicket(entry.getValue().ticket);
                return true;
            }
            return false;
        });
    }

    public static void releaseForWorld(World world) {
        forcedChunks.entrySet().removeIf(entry -> {
            ForcedChunk forcedChunk = entry.getValue();
            if (entry.getKey().getWorld() == world || forcedChunk.ticket.world == world) {
                ForgeChunkManager.releaseTicket(forcedChunk.ticket);
                return true;
            }
            return false;
        });
    }

    public static void releaseAll() {
        forcedChunks.values().forEach(forcedChunk -> ForgeChunkManager.releaseTicket(forcedChunk.ticket));
        forcedChunks.clear();
    }

    public static void releaseLoadedTickets(List<ForgeChunkManager.Ticket> tickets, World world) {
        tickets.forEach(ForgeChunkManager::releaseTicket);
    }

    private static void release(NetworkNodeNetworkTransmitter transmitter) {
        ForcedChunk forcedChunk = forcedChunks.remove(transmitter);
        if (forcedChunk != null) {
            ForgeChunkManager.releaseTicket(forcedChunk.ticket);
        }
    }
}
