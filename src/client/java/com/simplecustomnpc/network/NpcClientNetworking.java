package com.simplecustomnpc.network;

import com.simplecustomnpc.SimpleCustomNpc;
import com.simplecustomnpc.client.gui.NpcEditorScreen;
import com.simplecustomnpc.entity.CustomNpcEntity;
import com.simplecustomnpc.util.NpcPoseData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

@Environment(EnvType.CLIENT)
public class NpcClientNetworking {

    public static void register() {
        // Receive Open GUI packet from server
        ClientPlayNetworking.registerGlobalReceiver(
                NpcNetworking.OPEN_GUI_ID,
                (payload, context) -> {
                    int entityId = payload.entityId();
                    NpcPoseData pose = NpcPoseData.fromNbt(payload.poseNbt());
                    context.client().execute(() -> {
                        if (context.client().world == null) return;
                        if (context.client().world.getEntityById(entityId) instanceof CustomNpcEntity npc) {
                            context.client().setScreen(new NpcEditorScreen(npc, pose));
                        }
                    });
                }
        );
    }

    /** Send updated pose data to the server */
    public static void sendSaveNpc(int entityId, NpcPoseData pose) {
        NbtCompound nbt = pose.toNbt();
        ClientPlayNetworking.send(new NpcNetworking.SaveNpcPayload(entityId, nbt));
    }
}
