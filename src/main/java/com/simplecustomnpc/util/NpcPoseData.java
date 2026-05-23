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

    // Slim arms (Alex model)
    public boolean slimModel = false;

    // Skin
    public String skinUsername = "";
    public String skinUrl = "";

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

    // In 1.21.5+, getFloat(key) returns Optional<Float>
    // Use getFloat(key, fallback) overload that returns float directly
    public static NpcPoseData fromNbt(NbtCompound nbt) {
        NpcPoseData d = new NpcPoseData();
        d.headYaw    = nbt.getFloat("headYaw",   0f);
        d.headPitch  = nbt.getFloat("headPitch", 0f);
        d.bodyYaw    = nbt.getFloat("bodyYaw",   0f);

        d.rightArmPitch = nbt.getFloat("rightArmPitch", -10f);
        d.rightArmYaw   = nbt.getFloat("rightArmYaw",   0f);
        d.rightArmRoll  = nbt.getFloat("rightArmRoll",  0f);

        d.leftArmPitch = nbt.getFloat("leftArmPitch", -10f);
        d.leftArmYaw   = nbt.getFloat("leftArmYaw",   0f);
        d.leftArmRoll  = nbt.getFloat("leftArmRoll",  0f);

        d.rightLegPitch = nbt.getFloat("rightLegPitch", 0f);
        d.rightLegYaw   = nbt.getFloat("rightLegYaw",   0f);
        d.rightLegRoll  = nbt.getFloat("rightLegRoll",  0f);

        d.leftLegPitch = nbt.getFloat("leftLegPitch", 0f);
        d.leftLegYaw   = nbt.getFloat("leftLegYaw",   0f);
        d.leftLegRoll  = nbt.getFloat("leftLegRoll",  0f);

        d.slimModel    = nbt.getBoolean("slimModel", false);
        d.skinUsername = nbt.getString("skinUsername", "");
        d.skinUrl      = nbt.getString("skinUrl",      "");
        return d;
    }
}
