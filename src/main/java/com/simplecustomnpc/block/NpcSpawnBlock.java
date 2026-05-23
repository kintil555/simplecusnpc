package com.simplecustomnpc.block;

import com.mojang.serialization.MapCodec;
import com.simplecustomnpc.SimpleCustomNpc;
import com.simplecustomnpc.entity.CustomNpcEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class NpcSpawnBlock extends BlockWithEntity {

    public static final BooleanProperty SPAWNED = BooleanProperty.of("spawned");

    // 1.21.2+: BlockWithEntity requires getCodec()
    private static final MapCodec<NpcSpawnBlock> CODEC = createCodec(NpcSpawnBlock::new);

    public NpcSpawnBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SPAWNED, false));
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SPAWNED);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 0.5, 0.75);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(SPAWNED, false);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable net.minecraft.entity.LivingEntity placer, net.minecraft.item.ItemStack itemStack) {
        if (!world.isClient()) {
            spawnNpc((ServerWorld) world, pos);
            world.setBlockState(pos, state.with(SPAWNED, true));
        }
    }

    private void spawnNpc(ServerWorld world, BlockPos pos) {
        CustomNpcEntity npc = new CustomNpcEntity(SimpleCustomNpc.CUSTOM_NPC_ENTITY_TYPE, world);
        npc.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0f, 0f);
        world.spawnEntity(npc);

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof NpcSpawnBlockEntity npcBe) {
            npcBe.setNpcUuid(npc.getUuid());
            npcBe.markDirty();
        }
    }

    // 1.21.11: onStateReplaced(BlockState, ServerWorld, BlockPos, boolean) - no newState param
    @Override
    public void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        // Remove the linked NPC when the block is broken
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof NpcSpawnBlockEntity npcBe) {
            java.util.UUID uuid = npcBe.getNpcUuid();
            if (uuid != null) {
                world.getEntitiesByClass(
                        CustomNpcEntity.class,
                        new Box(pos).expand(1),
                        npc -> npc.getUuid().equals(uuid)
                ).forEach(net.minecraft.entity.Entity::discard);
            }
        }
        super.onStateReplaced(state, world, pos, moved);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new NpcSpawnBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof NpcSpawnBlockEntity npcBe) {
                java.util.UUID uuid = npcBe.getNpcUuid();
                if (uuid != null) {
                    ((ServerWorld) world).getEntitiesByClass(
                            CustomNpcEntity.class,
                            new Box(pos).expand(2),
                            npc -> npc.getUuid().equals(uuid)
                    ).stream().findFirst().ifPresent(npc ->
                            com.simplecustomnpc.network.NpcNetworking.sendOpenGuiPacket(player, npc)
                    );
                }
            }
        }
        return ActionResult.SUCCESS;
    }
}
