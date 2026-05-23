package com.simplecustomnpc.network;

import com.simplecustomnpc.SimpleCustomNpc;
import com.simplecustomnpc.entity.CustomNpcEntity;
import com.simplecustomnpc.util.NpcPoseData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class NpcNetworking {

    public static final CustomPayload.Id<OpenGuiPayload> OPEN_GUI_ID =
            new CustomPayload.Id<>(Identifier.of(SimpleCustomNpc.MOD_ID, "open_gui"));

    public static final CustomPayload.Id<SaveNpcPayload> SAVE_NPC_ID =
            new CustomPayload.Id<>(Identifier.of(SimpleCustomNpc.MOD_ID, "save_npc"));

    public record OpenGuiPayload(int entityId, NbtCompound poseNbt) implements CustomPayload {
        public static final PacketCodec<PacketByteBuf, OpenGuiPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, OpenGuiPayload::entityId,
                PacketCodecs.NBT_COMPOUND, OpenGuiPayload::poseNbt,
                OpenGuiPayload::new
        );
        @Override public Id<? extends CustomPayload> getId() { return OPEN_GUI_ID; }
    }

    public record SaveNpcPayload(int entityId, NbtCompound poseNbt) implements CustomPayload {
        public static final PacketCodec<PacketByteBuf, SaveNpcPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, SaveNpcPayload::entityId,
                PacketCodecs.NBT_COMPOUND, SaveNpcPayload::poseNbt,
                SaveNpcPayload::new
        );
        @Override public Id<? extends CustomPayload> getId() { return SAVE_NPC_ID; }
    }

    public static void registerServerPackets() {
        PayloadTypeRegistry.playS2C().register(OPEN_GUI_ID, OpenGuiPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SAVE_NPC_ID, SaveNpcPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SAVE_NPC_ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            context.server().execute(() -> {
                if (player.getWorld().getEntityById(payload.entityId()) instanceof CustomNpcEntity npc) {
                    if (player.squaredDistanceTo(npc) > 100) return;
                    NpcPoseData pose = NpcPoseData.fromNbt(payload.poseNbt());
                    npc.setNpcPoseData(pose);
                    if (!pose.skinUsername.isEmpty()) {
                        fetchAndApplySkin(npc, pose.skinUsername);
                    }
                }
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientPackets() {
        NpcClientNetworking.register();
    }

    public static void sendOpenGuiPacket(PlayerEntity player, CustomNpcEntity npc) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            OpenGuiPayload payload = new OpenGuiPayload(npc.getId(), npc.getNpcPoseData().toNbt());
            ServerPlayNetworking.send(serverPlayer, payload);
        }
    }

    private static void fetchAndApplySkin(CustomNpcEntity npc, String username) {
        Thread t = new Thread(() -> {
            try {
                String uuidJson = httpGet("https://api.mojang.com/users/profiles/minecraft/" + username);
                if (uuidJson == null) return;
                String rawUuid = extractJsonField(uuidJson, "id");
                if (rawUuid == null) return;

                String profileJson = httpGet("https://sessionserver.mojang.com/session/minecraft/profile/" + rawUuid + "?unsigned=false");
                if (profileJson == null) return;
                String value = extractJsonField(profileJson, "value");
                if (value == null) return;

                String decoded = new String(java.util.Base64.getDecoder().decode(value));
                String skinUrl = extractNestedJsonField(decoded, "SKIN", "url");
                if (skinUrl == null) return;

                NpcPoseData pose = npc.getNpcPoseData();
                pose.skinUrl = skinUrl;
                pose.slimModel = decoded.contains("\"slim\"");
                npc.setNpcPoseData(pose);
                SimpleCustomNpc.LOGGER.info("[SimpleCustomNPC] Skin fetched: {} -> {}", username, skinUrl);
            } catch (Exception e) {
                SimpleCustomNpc.LOGGER.error("[SimpleCustomNPC] Skin fetch failed for {}", username, e);
            }
        }, "NPC-SkinFetch");
        t.setDaemon(true);
        t.start();
    }

    private static String httpGet(String urlStr) throws Exception {
        java.net.URL url = new java.net.URL(urlStr);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("User-Agent", "SimpleCustomNPC/1.0");
        if (conn.getResponseCode() != 200) return null;
        try (java.io.InputStream is = conn.getInputStream()) {
            return new String(is.readAllBytes());
        }
    }

    private static String extractJsonField(String json, String key) {
        String search = "\"" + key + "\":\"";
        int idx = json.indexOf(search);
        if (idx < 0) return null;
        int start = idx + search.length();
        int end = json.indexOf("\"", start);
        return end < 0 ? null : json.substring(start, end);
    }

    private static String extractNestedJsonField(String json, String section, String key) {
        int idx = json.indexOf("\"" + section + "\"");
        if (idx < 0) return null;
        return extractJsonField(json.substring(idx), key);
    }
}
