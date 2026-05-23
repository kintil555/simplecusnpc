package com.simplecustomnpc.entity.client;

import com.simplecustomnpc.entity.CustomNpcEntity;
import com.simplecustomnpc.util.NpcPoseData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CustomNpcRenderer extends LivingEntityRenderer<CustomNpcEntity, BipedEntityRenderState, BipedEntityModel<BipedEntityRenderState>> {

    private static final Identifier DEFAULT_SKIN =
            Identifier.of("minecraft", "textures/entity/player/wide/steve.png");

    public CustomNpcRenderer(EntityRendererFactory.Context ctx) {
        // PLAYER (wide/steve) — replaces removed PLAYER_INNER_ARMOR in 1.21.11
        super(ctx,
                new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER)),
                0.5f);
    }

    @Override
    public BipedEntityRenderState createRenderState() {
        return new BipedEntityRenderState();
    }

    @Override
    public void updateRenderState(CustomNpcEntity entity, BipedEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);

        NpcPoseData pose = entity.getNpcPoseData();
        BipedEntityModel<BipedEntityRenderState> model = this.model;

        model.head.yaw   = (float) Math.toRadians(pose.headYaw);
        model.head.pitch = (float) Math.toRadians(pose.headPitch);
        model.body.yaw   = (float) Math.toRadians(pose.bodyYaw);

        model.rightArm.pitch = (float) Math.toRadians(pose.rightArmPitch);
        model.rightArm.yaw   = (float) Math.toRadians(pose.rightArmYaw);
        model.rightArm.roll  = (float) Math.toRadians(pose.rightArmRoll);

        model.leftArm.pitch = (float) Math.toRadians(pose.leftArmPitch);
        model.leftArm.yaw   = (float) Math.toRadians(pose.leftArmYaw);
        model.leftArm.roll  = (float) Math.toRadians(pose.leftArmRoll);

        model.rightLeg.pitch = (float) Math.toRadians(pose.rightLegPitch);
        model.rightLeg.yaw   = (float) Math.toRadians(pose.rightLegYaw);
        model.rightLeg.roll  = (float) Math.toRadians(pose.rightLegRoll);

        model.leftLeg.pitch = (float) Math.toRadians(pose.leftLegPitch);
        model.leftLeg.yaw   = (float) Math.toRadians(pose.leftLegYaw);
        model.leftLeg.roll  = (float) Math.toRadians(pose.leftLegRoll);

        model.hat.yaw    = model.head.yaw;
        model.hat.pitch  = model.head.pitch;
        model.hat.roll   = model.head.roll;
        model.hat.originX = model.head.originX;
        model.hat.originY = model.head.originY;
        model.hat.originZ = model.head.originZ;
    }

    @Override
    public Identifier getTexture(BipedEntityRenderState state) {
        return DEFAULT_SKIN;
    }
}
