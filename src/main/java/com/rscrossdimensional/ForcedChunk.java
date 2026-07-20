package com.rscrossdimensional;

import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;

public final class ForcedChunk {
    public final int dimension;
    public final ChunkPos chunk;
    public final ForgeChunkManager.Ticket ticket;

    public ForcedChunk(int dimension, ChunkPos chunk, ForgeChunkManager.Ticket ticket) {
        this.dimension = dimension;
        this.chunk = chunk;
        this.ticket = ticket;
    }
}