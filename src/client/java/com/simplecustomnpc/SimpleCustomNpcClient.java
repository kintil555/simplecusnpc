package com.simplecustomnpc;

import com.simplecustomnpc.entity.client.CustomNpcRenderer;
import com.simplecustomnpc.network.NpcClientNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class SimpleCustomNpcClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register entity renderer
        EntityRendererRegistry.register(
                SimpleCustomNpc.CUSTOM_NPC_ENTITY_TYPE,
                CustomNpcRenderer::new
        );

        // Register client-side networking
        NpcClientNetworking.register();
    }
}
