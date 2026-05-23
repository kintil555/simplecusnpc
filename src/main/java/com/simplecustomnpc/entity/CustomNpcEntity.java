package com.simplecustomnpc.entity;

import com.simplecustomnpc.SimpleCustomNpc;
import com.simplecustomnpc.util.NpcPoseData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CustomNpcEntity extends LivingEntity {

    // ── Tracked data keys (synced server → client) ────────────────────────────
    private static final TrackedData<NbtCompound> POSE_DATA =
            DataTracker.registerData(CustomNpcEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);

    private static final TrackedData<String> DISPLAY_NAME_KEY =
            DataTracker.registerData(CustomNpcEntity.class, TrackedDataHandlerRegistry.STRING);

    public CustomNpcEntity(EntityType<? extends CustomNpcEntity> type, World world) {
        super(type, world);
        this.setInvulnerable(true);
        this.noClip = true; // doesn't push other entities
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(POSE_DATA, new NbtCompound());
        builder.add(DISPLAY_NAME_KEY, "");
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

    // ── Interaction: right-click opens GUI ───────────────────────────────────

    @Override
    public ActionResult interactAt(PlayerEntity player, net.minecraft.util.math.Vec3d hitPos, Hand hand) {
        if (this.getWorld().isClient()) {
            // Client-side: open GUI (handled in client entrypoint)
            openEditGui(player);
        }
        return ActionResult.SUCCESS;
    }

    /**
     * Called client-side. Opens the NPC editor screen.
     * Actual screen opening is delegated to the client module to avoid
     * classloading client-only classes on the server.
     */
    private void openEditGui(PlayerEntity player) {
        // No-op on server; client module overrides via event
    }

    // ── AI: no movement, always look forward ─────────────────────────────────

    @Override
    public void tick() {
        super.tick();
        // Freeze the NPC – no gravity, no movement
        this.setVelocity(0, 0, 0);
        this.fallDistance = 0;
    }

    @Override
    protected void mobTick() {
        // Intentionally empty – NPC has no AI
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.entity.damage.DamageSource source) {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    // ── NBT persistence ───────────────────────────────────────────────────────

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        NpcPoseData pose = getNpcPoseData();
        nbt.put("NpcPoseData", pose.toNbt());
        nbt.putString("NpcDisplayName", getNpcDisplayName());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("NpcPoseData")) {
            NpcPoseData pose = NpcPoseData.fromNbt(nbt.getCompound("NpcPoseData"));
            setNpcPoseData(pose);
        }
        if (nbt.contains("NpcDisplayName")) {
            setNpcDisplayName(nbt.getString("NpcDisplayName"));
        }
    }

    @Override
    public boolean shouldSave() {
        return true; // persist NPC across restarts
    }
}
