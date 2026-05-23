package com.simplecustomnpc.entity;

import com.simplecustomnpc.util.NpcPoseData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CustomNpcEntity extends LivingEntity {

    // Synced as NBT string so it works with STRING handler (always available)
    private static final TrackedData<String> POSE_NBT_STR =
            DataTracker.registerData(CustomNpcEntity.class, TrackedDataHandlerRegistry.STRING);

    private static final TrackedData<String> DISPLAY_NAME_KEY =
            DataTracker.registerData(CustomNpcEntity.class, TrackedDataHandlerRegistry.STRING);

    private NpcPoseData cachedPose = new NpcPoseData();

    public CustomNpcEntity(EntityType<? extends CustomNpcEntity> type, World world) {
        super(type, world);
        this.setInvulnerable(true);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(POSE_NBT_STR, "");
        builder.add(DISPLAY_NAME_KEY, "");
    }

    @Override
    public Arm getMainArm() { return Arm.RIGHT; }

    // ── Pose ──────────────────────────────────────────────────────────────────

    public NpcPoseData getNpcPoseData() {
        String raw = this.dataTracker.get(POSE_NBT_STR);
        if (raw == null || raw.isEmpty()) return cachedPose;
        try {
            NbtCompound nbt = StringNbtReader.parse(raw);
            cachedPose = NpcPoseData.fromNbt(nbt);
        } catch (Exception ignored) {}
        return cachedPose;
    }

    public void setNpcPoseData(NpcPoseData data) {
        this.cachedPose = data;
        this.dataTracker.set(POSE_NBT_STR, data.toNbt().toString());
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

    // ── Interaction: right-click entity → server sends open GUI packet ─────────

    @Override
    public ActionResult interactAt(PlayerEntity player, net.minecraft.util.math.Vec3d hitPos, Hand hand) {
        if (!this.getWorld().isClient()) {
            com.simplecustomnpc.network.NpcNetworking.sendOpenGuiPacket(player, this);
        }
        return ActionResult.SUCCESS;
    }

    // ── Tick ──────────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();
        this.setVelocity(0, 0, 0);
        this.fallDistance = 0;
    }

    @Override public boolean canTakeDamage() { return false; }

    @Override
    public boolean isInvulnerableTo(net.minecraft.server.world.ServerWorld world,
                                    net.minecraft.entity.damage.DamageSource source) {
        return true;
    }

    @Override public boolean isPushable() { return false; }

    // ── NBT persistence ───────────────────────────────────────────────────────

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.put("NpcPoseData", NbtCompound.CODEC, cachedPose.toNbt());
        view.putString("NpcDisplayName", getNpcDisplayName());
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        view.read("NpcPoseData", NbtCompound.CODEC).ifPresent(nbt -> {
            cachedPose = NpcPoseData.fromNbt(nbt);
            this.dataTracker.set(POSE_NBT_STR, nbt.toString());
        });
        String name = view.getString("NpcDisplayName", "");
        if (!name.isEmpty()) setNpcDisplayName(name);
    }

    @Override public boolean shouldSave() { return true; }
}
