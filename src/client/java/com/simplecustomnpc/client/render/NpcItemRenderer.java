package com.simplecustomnpc.client.render;

import com.mojang.serialization.MapCodec;
import com.simplecustomnpc.SimpleCustomNpc;
import com.simplecustomnpc.entity.CustomNpcEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

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
            ItemDisplayContext displayContext,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light,
            int overlay,
            boolean glint,
            int i
    ) {
        if (entity == null) return;

        // Entity rendering via dispatcher is incompatible with OrderedRenderCommandQueue
        // in 1.21.11 — skip custom rendering, entity preview only works in GUI screen.
        // The item will fall back to the base builtin/entity appearance.
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        // No static geometry to collect — entity is rendered dynamically
    }

    // Unbaked factory for SpecialModelTypes registration
    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            return INSTANCE;
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> getCodec() {
            return CODEC;
        }
    }
}
