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
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CustomNpcEntity extends LivingEntity {

    private static final TrackedData<NbtCompound> POSE_DATA =
            DataTracker.registerData(CustomNpcEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);

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

    // Required abstract method from LivingEntity
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
        if (this.getWorld().isClient()) {
            openEditGui(player);
        }
        return ActionResult.SUCCESS;
    }

    private void openEditGui(PlayerEntity player) {
        // No-op server-side; client entrypoint handles via event
    }

    // ── Tick / AI ─────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();
        this.setVelocity(0, 0, 0);
        this.fallDistance = 0;
    }

    @Override
    protected void mobTick() {
        // No AI
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
        nbt.put("NpcPoseData", getNpcPoseData().toNbt());
        nbt.putString("NpcDisplayName", getNpcDisplayName());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("NpcPoseData")) {
            NpcPoseData pose = NpcPoseData.fromNbt(nbt.getCompoundOrEmpty("NpcPoseData"));
            setNpcPoseData(pose);
        }
        if (nbt.contains("NpcDisplayName")) {
            setNpcDisplayName(nbt.getString("NpcDisplayName", ""));
        }
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
