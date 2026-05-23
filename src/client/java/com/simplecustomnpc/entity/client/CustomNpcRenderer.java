package com.simplecustomnpc.entity.client;

import com.simplecustomnpc.entity.CustomNpcEntity;
import com.simplecustomnpc.util.NpcPoseData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class CustomNpcRenderer extends LivingEntityRenderer<CustomNpcEntity, BipedEntityModel<CustomNpcEntity>> {

    private static final Identifier DEFAULT_SKIN =
            Identifier.of("minecraft", "textures/entity/player/wide/steve.png");

    /** Cache: skinUrl → Identifier. Downloads happen via Minecraft's skin system. */
    private static final Map<String, Identifier> skinCache = new HashMap<>();

    public CustomNpcRenderer(EntityRendererFactory.Context ctx) {
        super(ctx,
                new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER)),
                0.5f);
    }

    @Override
    public Identifier getTexture(CustomNpcEntity entity) {
        NpcPoseData pose = entity.getNpcPoseData();
        if (!pose.skinUrl.isEmpty()) {
            return skinCache.computeIfAbsent(pose.skinUrl, url -> {
                // Register the skin texture for download through MC's system
                Identifier id = Identifier.of("simplecustomnpc", "skin/" +
                        Integer.toHexString(url.hashCode()));
                MinecraftClient.getInstance().getTextureManager().registerTexture(
                        id,
                        new net.minecraft.client.texture.HttpTexture(
                                null, url, DEFAULT_SKIN, true, null
                        )
                );
                return id;
            });
        }
        return DEFAULT_SKIN;
    }

    @Override
    public void render(CustomNpcEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        NpcPoseData pose = entity.getNpcPoseData();
        BipedEntityModel<CustomNpcEntity> model = this.getModel();

        // ── Apply pose to model parts ────────────────────────────────────────

        // Head
        model.head.yaw   = (float) Math.toRadians(pose.headYaw);
        model.head.pitch = (float) Math.toRadians(pose.headPitch);

        // Body
        model.body.yaw   = (float) Math.toRadians(pose.bodyYaw);

        // Right Arm
        model.rightArm.pitch = (float) Math.toRadians(pose.rightArmPitch);
        model.rightArm.yaw   = (float) Math.toRadians(pose.rightArmYaw);
        model.rightArm.roll  = (float) Math.toRadians(pose.rightArmRoll);

        // Left Arm
        model.leftArm.pitch = (float) Math.toRadians(pose.leftArmPitch);
        model.leftArm.yaw   = (float) Math.toRadians(pose.leftArmYaw);
        model.leftArm.roll  = (float) Math.toRadians(pose.leftArmRoll);

        // Right Leg
        model.rightLeg.pitch = (float) Math.toRadians(pose.rightLegPitch);
        model.rightLeg.yaw   = (float) Math.toRadians(pose.rightLegYaw);
        model.rightLeg.roll  = (float) Math.toRadians(pose.rightLegRoll);

        // Left Leg
        model.leftLeg.pitch = (float) Math.toRadians(pose.leftLegPitch);
        model.leftLeg.yaw   = (float) Math.toRadians(pose.leftLegYaw);
        model.leftLeg.roll  = (float) Math.toRadians(pose.leftLegRoll);

        // Hat / outer layers follow head
        model.hat.copyTransform(model.head);

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
