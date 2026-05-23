package com.simplecustomnpc.block;

import com.simplecustomnpc.SimpleCustomNpc;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class NpcSpawnBlockEntity extends BlockEntity {

    private UUID npcUuid = null;

    public NpcSpawnBlockEntity(BlockPos pos, BlockState state) {
        super(SimpleCustomNpc.NPC_SPAWN_BLOCK_ENTITY, pos, state);
    }

    public UUID getNpcUuid() { return npcUuid; }
    public void setNpcUuid(UUID uuid) { this.npcUuid = uuid; }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (npcUuid != null) {
            nbt.putUuid("NpcUuid", npcUuid);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.containsUuid("NpcUuid")) {
            npcUuid = nbt.getUuid("NpcUuid");
        }
    }
}
