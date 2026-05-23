package com.simplecustomnpc.client.render;

import com.mojang.serialization.MapCodec;
import com.simplecustomnpc.SimpleCustomNpc;
import com.simplecustomnpc.entity.CustomNpcEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class NpcItemRenderer implements SpecialModelRenderer<CustomNpcEntity> {

    public static final NpcItemRenderer INSTANCE = new NpcItemRenderer();

    @Override
    public @Nullable CustomNpcEntity getData(ItemStack stack) {
        World world = MinecraftClient.getInstance().world;
        if (world == null) return null;
        return new CustomNpcEntity(SimpleCustomNpc.CUSTOM_NPC_ENTITY_TYPE, world);
    }

    @Override
    public void render(
            @Nullable CustomNpcEntity entity,
            ModelTransformationMode modelTransformationMode,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            boolean glint
    ) {
        if (entity == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        var dispatcher = client.getEntityRenderDispatcher();

        matrices.push();
        // Center + scale to fit in item slot
        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(0.4f, 0.4f, 0.4f);
        // Rotate so entity faces forward in GUI
        matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(180f));

        dispatcher.render(entity, 0, 0, 0, 0.0f, 1.0f, matrices, vertexConsumers, light);

        matrices.pop();
    }

    // Unbaked factory for registration
    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
            return INSTANCE;
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> getCodec() {
            return CODEC;
        }
    }
}
