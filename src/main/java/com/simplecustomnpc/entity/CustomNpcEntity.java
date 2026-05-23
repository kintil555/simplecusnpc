package com.simplecustomnpc.entity;

import com.simplecustomnpc.SimpleCustomNpc;
import com.simplecustomnpc.util.NpcPoseData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CustomNpcEntity extends LivingEntity {

    // 1.21.11: TrackedDataHandlerRegistry no longer has NBT_COMPOUND.
    // We create our own handler using PacketCodecs.NBT_COMPOUND (which is
    // PacketCodec<ByteBuf, NbtElement>). We xmap it to NbtCompound safely.
    private static final TrackedDataHandler<NbtCompound> NBT_COMPOUND_HANDLER;
    static {
        // PacketCodecs.NBT_COMPOUND encodes NbtElement but always produces NbtCompound
        // at the call sites we use. Cast to the needed generic type via xmap.
        PacketCodec<RegistryByteBuf, NbtCompound> codec = PacketCodecs.NBT_COMPOUND
                .cast();
        NBT_COMPOUND_HANDLER = TrackedDataHandler.create(codec);
        TrackedDataHandlerRegistry.register(NBT_COMPOUND_HANDLER);
    }

    private static final TrackedData<NbtCompound> POSE_DATA =
            DataTracker.registerData(CustomNpcEntity.class, NBT_COMPOUND_HANDLER);

    private static final TrackedData<String> DISPLAY_NAME_KEY =
            DataTracker.registerData(CustomNpcEntity.class, TrackedDataHandlerRegistry.STRING);

    public CustomNpcEntity(EntityType<? extends CustomNpcEntity> type, World world) {
        super(type, world);
        this.setInvulnerable(true);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(POSE_DATA, new NbtCompound());
        builder.add(DISPLAY_NAME_KEY, "");
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    // ── Pose getters / setters ────────────────────────────────────────────────

    public NpcPoseData getNpcPoseData() {
        NbtCompound nbt = this.dataTracker.get(POSE_DATA);
        if (nbt.isEmpty()) return new NpcPoseData();
        return NpcPoseData.fromNbt(nbt);
    }

    public void setNpcPoseData(NpcPoseData data) {
        this.dataTracker.set(POSE_DATA, data.toNbt());
    }

    public String getNpcDisplayName() {
        return this.dataTracker.get(DISPLAY_NAME_KEY);
    }

    public void setNpcDisplayName(String name) {
        this.dataTracker.set(DISPLAY_NAME_KEY, name);
        if (!name.isEmpty()) {
            this.setCustomName(Text.literal(name));
            this.setCustomNameVisible(true);
        } else {
            this.setCustomName(null);
            this.setCustomNameVisible(false);
        }
    }

    // ── Interaction ───────────────────────────────────────────────────────────

    @Override
    public ActionResult interactAt(PlayerEntity player, net.minecraft.util.math.Vec3d hitPos, Hand hand) {
        if (this.getEntityWorld().isClient()) {
            openEditGui(player);
        }
        return ActionResult.SUCCESS;
    }

    private void openEditGui(PlayerEntity player) {
        // No-op server-side; client entrypoint handles via networking event
    }

    // ── Tick / AI ─────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();
        this.setVelocity(0, 0, 0);
        this.fallDistance = 0;
    }

    protected void mobTick() {
        // No AI
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.server.world.ServerWorld world,
                                    net.minecraft.entity.damage.DamageSource source) {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    // ── NBT persistence — 1.21.11 uses WriteView/ReadView ─────────────────────

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.put("NpcPoseData", NbtCompound.CODEC, getNpcPoseData().toNbt());
        view.putString("NpcDisplayName", getNpcDisplayName());
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        view.read("NpcPoseData", NbtCompound.CODEC).ifPresent(nbt -> setNpcPoseData(NpcPoseData.fromNbt(nbt)));
        String name = view.getString("NpcDisplayName", "");
        if (!name.isEmpty()) setNpcDisplayName(name);
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
