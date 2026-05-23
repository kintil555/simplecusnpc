package com.simplecustomnpc;

import com.simplecustomnpc.block.NpcSpawnBlock;
import com.simplecustomnpc.block.NpcSpawnBlockEntity;
import com.simplecustomnpc.entity.CustomNpcEntity;
import com.simplecustomnpc.item.NpcSpawnItem;
import com.simplecustomnpc.network.NpcNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCustomNpc implements ModInitializer {

    public static final String MOD_ID = "simplecustomnpc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // ── Entity ────────────────────────────────────────────────────────────────
    // 1.21.2+: build() requires RegistryKey; FabricEntityTypeBuilder is deprecated
    public static final EntityType<CustomNpcEntity> CUSTOM_NPC_ENTITY_TYPE;
    static {
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "custom_npc"));
        EntityType<CustomNpcEntity> type = EntityType.Builder.<CustomNpcEntity>create(CustomNpcEntity::new, SpawnGroup.MISC)
                .dimensions(0.6f, 1.8f)
                .build(key);
        CUSTOM_NPC_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, key, type);
    }

    // ── Block ─────────────────────────────────────────────────────────────────
    public static final NpcSpawnBlock NPC_SPAWN_BLOCK =
            Registry.register(
                    Registries.BLOCK,
                    Identifier.of(MOD_ID, "npc_spawn_block"),
                    new NpcSpawnBlock(AbstractBlock.Settings.create().nonOpaque().noCollision())
            );

    // ── Block Item ────────────────────────────────────────────────────────────
    public static final Item NPC_SPAWN_ITEM =
            Registry.register(
                    Registries.ITEM,
                    Identifier.of(MOD_ID, "npc_spawn_block"),
                    new NpcSpawnItem(NPC_SPAWN_BLOCK, new Item.Settings().maxCount(1))
            );

    // ── Block Entity ──────────────────────────────────────────────────────────
    // 1.21.11: BlockEntityType.create() is private; use FabricBlockEntityTypeBuilder
    public static final BlockEntityType<NpcSpawnBlockEntity> NPC_SPAWN_BLOCK_ENTITY;
    static {
        RegistryKey<BlockEntityType<?>> key = RegistryKey.of(RegistryKeys.BLOCK_ENTITY_TYPE, Identifier.of(MOD_ID, "npc_spawn_block_entity"));
        BlockEntityType<NpcSpawnBlockEntity> type = FabricBlockEntityTypeBuilder
                .create(NpcSpawnBlockEntity::new, NPC_SPAWN_BLOCK)
                .build();
        NPC_SPAWN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, key, type);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("[SimpleCustomNPC] Initializing...");
        NpcNetworking.registerServerPackets();
        LOGGER.info("[SimpleCustomNPC] Ready!");
    }
}
