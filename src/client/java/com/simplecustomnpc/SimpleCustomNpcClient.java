package com.simplecustomnpc;

import com.simplecustomnpc.client.render.NpcItemRenderer;
import com.simplecustomnpc.entity.client.CustomNpcRenderer;
import com.simplecustomnpc.network.NpcClientNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.util.Identifier;

public class SimpleCustomNpcClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register entity renderer
        EntityRendererRegistry.register(
                SimpleCustomNpc.CUSTOM_NPC_ENTITY_TYPE,
                CustomNpcRenderer::new
        );

        // Register special item renderer for 3D entity display in item slot
        SpecialModelTypes.ID_MAPPER.put(
                Identifier.of(SimpleCustomNpc.MOD_ID, "npc_item_renderer"),
                NpcItemRenderer.Unbaked.CODEC
        );

        // Register client-side networking
        NpcClientNetworking.register();
    }
}
