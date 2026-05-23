package com.simplecustomnpc.block;

import com.simplecustomnpc.SimpleCustomNpc;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class NpcSpawnBlockEntity extends BlockEntity {

    private UUID npcUuid = null;

    public NpcSpawnBlockEntity(BlockPos pos, BlockState state) {
        super(SimpleCustomNpc.NPC_SPAWN_BLOCK_ENTITY, pos, state);
    }

    public UUID getNpcUuid() { return npcUuid; }
    public void setNpcUuid(UUID uuid) { this.npcUuid = uuid; }

    // 1.21.11: override writeData(WriteView) / readData(ReadView)
    // UUID: stored as two longs (most/least significant bits)
    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (npcUuid != null) {
            view.putLong("NpcUuidMost", npcUuid.getMostSignificantBits());
            view.putLong("NpcUuidLeast", npcUuid.getLeastSignificantBits());
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        long most  = view.getLong("NpcUuidMost",  0L);
        long least = view.getLong("NpcUuidLeast", 0L);
        if (most != 0L || least != 0L) {
            npcUuid = new UUID(most, least);
        }
    }
}
