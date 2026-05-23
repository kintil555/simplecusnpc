package com.simplecustomnpc.util;

import net.minecraft.nbt.NbtCompound;

/**
 * Stores all pose/rotation data for a CustomNpcEntity.
 * Each field is in degrees (Euler XYZ).
 */
public class NpcPoseData {

    // Head
    public float headYaw   = 0f;
    public float headPitch = 0f;

    // Body
    public float bodyYaw   = 0f;

    // Right Arm
    public float rightArmPitch = -10f;
    public float rightArmYaw   = 0f;
    public float rightArmRoll  = 0f;

    // Left Arm
    public float leftArmPitch = -10f;
    public float leftArmYaw   = 0f;
    public float leftArmRoll  = 0f;

    // Right Leg
    public float rightLegPitch = 0f;
    public float rightLegYaw   = 0f;
    public float rightLegRoll  = 0f;

    // Left Leg
    public float leftLegPitch = 0f;
    public float leftLegYaw   = 0f;
    public float leftLegRoll  = 0f;

    // ── Slim arms (Alex model) ────────────────────────────────────────────────
    public boolean slimModel = false;

    // ── Skin ──────────────────────────────────────────────────────────────────
    public String skinUsername = "";
    public String skinUrl = "";      // resolved URL after fetch

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putFloat("headYaw",    headYaw);
        nbt.putFloat("headPitch",  headPitch);
        nbt.putFloat("bodyYaw",    bodyYaw);

        nbt.putFloat("rightArmPitch", rightArmPitch);
        nbt.putFloat("rightArmYaw",   rightArmYaw);
        nbt.putFloat("rightArmRoll",  rightArmRoll);

        nbt.putFloat("leftArmPitch", leftArmPitch);
        nbt.putFloat("leftArmYaw",   leftArmYaw);
        nbt.putFloat("leftArmRoll",  leftArmRoll);

        nbt.putFloat("rightLegPitch", rightLegPitch);
        nbt.putFloat("rightLegYaw",   rightLegYaw);
        nbt.putFloat("rightLegRoll",  rightLegRoll);

        nbt.putFloat("leftLegPitch", leftLegPitch);
        nbt.putFloat("leftLegYaw",   leftLegYaw);
        nbt.putFloat("leftLegRoll",  leftLegRoll);

        nbt.putBoolean("slimModel", slimModel);
        nbt.putString("skinUsername", skinUsername);
        nbt.putString("skinUrl", skinUrl);
        return nbt;
    }

    public static NpcPoseData fromNbt(NbtCompound nbt) {
        NpcPoseData d = new NpcPoseData();
        d.headYaw    = nbt.getFloat("headYaw");
        d.headPitch  = nbt.getFloat("headPitch");
        d.bodyYaw    = nbt.getFloat("bodyYaw");

        d.rightArmPitch = nbt.contains("rightArmPitch") ? nbt.getFloat("rightArmPitch") : -10f;
        d.rightArmYaw   = nbt.getFloat("rightArmYaw");
        d.rightArmRoll  = nbt.getFloat("rightArmRoll");

        d.leftArmPitch = nbt.contains("leftArmPitch") ? nbt.getFloat("leftArmPitch") : -10f;
        d.leftArmYaw   = nbt.getFloat("leftArmYaw");
        d.leftArmRoll  = nbt.getFloat("leftArmRoll");

        d.rightLegPitch = nbt.getFloat("rightLegPitch");
        d.rightLegYaw   = nbt.getFloat("rightLegYaw");
        d.rightLegRoll  = nbt.getFloat("rightLegRoll");

        d.leftLegPitch = nbt.getFloat("leftLegPitch");
        d.leftLegYaw   = nbt.getFloat("leftLegYaw");
        d.leftLegRoll  = nbt.getFloat("leftLegRoll");

        d.slimModel     = nbt.getBoolean("slimModel");
        d.skinUsername  = nbt.getString("skinUsername");
        d.skinUrl       = nbt.getString("skinUrl");
        return d;
    }
}
