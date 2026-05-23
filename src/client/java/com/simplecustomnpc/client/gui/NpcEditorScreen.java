package com.simplecustomnpc.client.gui;

import com.simplecustomnpc.entity.CustomNpcEntity;
import com.simplecustomnpc.network.NpcClientNetworking;
import com.simplecustomnpc.util.NpcPoseData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class NpcEditorScreen extends Screen {

    private final CustomNpcEntity npc;
    private NpcPoseData pose;

    // Tabs
    private static final int TAB_POSE = 0;
    private static final int TAB_SKIN = 1;
    private int activeTab = TAB_POSE;

    // Skin input
    private TextFieldWidget skinUsernameField;
    private TextFieldWidget displayNameField;

    // Sliders
    private final List<PoseSlider> sliders = new ArrayList<>();

    // Panel layout
    private int panelX, panelY, panelW, panelH;

    // Preview area (left side)
    private static final int PREVIEW_W = 80;

    public NpcEditorScreen(CustomNpcEntity npc, NpcPoseData pose) {
        super(Text.translatable("gui.simplecustomnpc.editor.title"));
        this.npc = npc;
        this.pose = pose;
    }

    @Override
    protected void init() {
        panelW = 340;
        panelH = 300;
        panelX = (this.width  - panelW) / 2;
        panelY = (this.height - panelH) / 2;

        // ── Tab Buttons ──────────────────────────────────────────────────────
        // Controls start after the preview column
        int controlsX = panelX + PREVIEW_W + 10;
        int controlsW = panelW - PREVIEW_W - 20;

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.simplecustomnpc.tab.pose"),
                btn -> switchTab(TAB_POSE)
        ).dimensions(controlsX, panelY, controlsW / 2, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.simplecustomnpc.tab.skin"),
                btn -> switchTab(TAB_SKIN)
        ).dimensions(controlsX + controlsW / 2, panelY, controlsW / 2, 20).build());

        // ── Save Button ──────────────────────────────────────────────────────
        addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.simplecustomnpc.save"),
                btn -> save()
        ).dimensions(panelX + panelW - 80, panelY + panelH - 25, 80, 20).build());

        // ── Close Button ─────────────────────────────────────────────────────
        addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.simplecustomnpc.close"),
                btn -> this.close()
        ).dimensions(panelX, panelY + panelH - 25, 80, 20).build());

        // ── Display Name Field ────────────────────────────────────────────────
        displayNameField = new TextFieldWidget(this.textRenderer,
                controlsX, panelY + panelH - 50, controlsW, 16,
                Text.translatable("gui.simplecustomnpc.displayname"));
        displayNameField.setPlaceholder(Text.translatable("gui.simplecustomnpc.displayname.hint"));
        displayNameField.setText(npc.getNpcDisplayName());
        displayNameField.setMaxLength(32);
        addDrawableChild(displayNameField);

        buildPoseSliders();
        buildSkinTab();
    }

    private void buildPoseSliders() {
        sliders.clear();
        int controlsX = panelX + PREVIEW_W + 10;
        int controlsW = panelW - PREVIEW_W - 20;
        int y = panelY + 30;

        sliders.add(new PoseSlider(controlsX, y,       controlsW, "Head Yaw",    -180, 180, pose.headYaw,        v -> pose.headYaw        = v));
        sliders.add(new PoseSlider(controlsX, y + 22,  controlsW, "Head Pitch",  -90,  90,  pose.headPitch,      v -> pose.headPitch      = v));
        sliders.add(new PoseSlider(controlsX, y + 50,  controlsW, "Body Yaw",    -180, 180, pose.bodyYaw,        v -> pose.bodyYaw        = v));
        sliders.add(new PoseSlider(controlsX, y + 78,  controlsW, "R.Arm Pitch", -180, 180, pose.rightArmPitch,  v -> pose.rightArmPitch  = v));
        sliders.add(new PoseSlider(controlsX, y + 100, controlsW, "R.Arm Yaw",   -180, 180, pose.rightArmYaw,    v -> pose.rightArmYaw    = v));
        sliders.add(new PoseSlider(controlsX, y + 122, controlsW, "R.Arm Roll",  -180, 180, pose.rightArmRoll,   v -> pose.rightArmRoll   = v));
        sliders.add(new PoseSlider(controlsX, y + 148, controlsW, "L.Arm Pitch", -180, 180, pose.leftArmPitch,   v -> pose.leftArmPitch   = v));
        sliders.add(new PoseSlider(controlsX, y + 170, controlsW, "L.Arm Yaw",   -180, 180, pose.leftArmYaw,     v -> pose.leftArmYaw     = v));
        sliders.add(new PoseSlider(controlsX, y + 192, controlsW, "L.Arm Roll",  -180, 180, pose.leftArmRoll,    v -> pose.leftArmRoll    = v));
        sliders.add(new PoseSlider(controlsX, y + 218, controlsW, "R.Leg Pitch", -90,  90,  pose.rightLegPitch,  v -> pose.rightLegPitch  = v));
        sliders.add(new PoseSlider(controlsX, y + 240, controlsW, "L.Leg Pitch", -90,  90,  pose.leftLegPitch,   v -> pose.leftLegPitch   = v));

        for (PoseSlider s : sliders) {
            addDrawableChild(s.slider);
        }
    }

    private void buildSkinTab() {
        int controlsX = panelX + PREVIEW_W + 10;
        int controlsW = panelW - PREVIEW_W - 20;

        skinUsernameField = new TextFieldWidget(this.textRenderer,
                controlsX, panelY + 60, controlsW, 18,
                Text.translatable("gui.simplecustomnpc.skin.username"));
        skinUsernameField.setPlaceholder(Text.translatable("gui.simplecustomnpc.skin.username.hint"));
        skinUsernameField.setText(pose.skinUsername);
        skinUsernameField.setMaxLength(16);
        skinUsernameField.setVisible(false);
        addDrawableChild(skinUsernameField);

        // Slim model toggle
        addDrawableChild(ButtonWidget.builder(
                Text.translatable(pose.slimModel ? "gui.simplecustomnpc.slim.on" : "gui.simplecustomnpc.slim.off"),
                btn -> {
                    pose.slimModel = !pose.slimModel;
                    btn.setMessage(Text.translatable(pose.slimModel
                            ? "gui.simplecustomnpc.slim.on"
                            : "gui.simplecustomnpc.slim.off"));
                }
        ).dimensions(controlsX, panelY + 90, controlsW, 18).build());
    }

    private void switchTab(int tab) {
        this.activeTab = tab;
        boolean isPose = (tab == TAB_POSE);
        for (PoseSlider s : sliders) s.slider.visible = isPose;
        skinUsernameField.setVisible(!isPose);
    }

    private void save() {
        pose.skinUsername = skinUsernameField.getText().trim();
        NpcClientNetworking.sendSaveNpc(npc.getId(), pose);
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Background panel
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xCC000000);
        // Border
        context.fill(panelX,               panelY,              panelX + panelW,      panelY + 1,           0xFFAAAAAA);
        context.fill(panelX,               panelY + panelH - 1, panelX + panelW,      panelY + panelH,      0xFFAAAAAA);
        context.fill(panelX,               panelY,              panelX + 1,           panelY + panelH,      0xFFAAAAAA);
        context.fill(panelX + panelW - 1,  panelY,              panelX + panelW,      panelY + panelH,      0xFFAAAAAA);

        // ── 3D Player Preview (left column) ──────────────────────────────────
        // Divider between preview and controls
        context.fill(panelX + PREVIEW_W, panelY + 1, panelX + PREVIEW_W + 1, panelY + panelH - 1, 0xFF555555);

        // drawEntity(context, x1, y1, x2, y2, size, scale, mouseX, mouseY, entity)
        // x1/y1 = top-left of bounding box, x2/y2 = bottom-right
        // size controls the rendered height; scale is additional multiplier
        // mouseX/mouseY relative to the bounding box center affect head/body rotation
        int previewCenterX = panelX + PREVIEW_W / 2;
        int previewBottom  = panelY + panelH - 30;
        InventoryScreen.drawEntity(
                context,
                panelX + 5,        // x1
                panelY + 10,       // y1
                panelX + PREVIEW_W - 5,  // x2
                previewBottom,     // y2
                40,                // size (pixel height of model)
                0.0625f,           // scale
                (float) previewCenterX,  // mouseX — center = no rotation
                (float) (panelY + panelH / 2), // mouseY
                npc
        );

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer,
                this.title, this.width / 2, panelY - 12, 0xFFFFFF);

        // Tab hints
        if (activeTab == TAB_POSE) {
            context.drawTextWithShadow(this.textRenderer,
                    Text.translatable("gui.simplecustomnpc.pose.hint"),
                    panelX + PREVIEW_W + 10, panelY + 22, 0xAAAAAA);
        } else {
            context.drawTextWithShadow(this.textRenderer,
                    Text.translatable("gui.simplecustomnpc.skin.hint"),
                    panelX + PREVIEW_W + 10, panelY + 40, 0xAAAAAA);
            context.drawTextWithShadow(this.textRenderer,
                    Text.translatable("gui.simplecustomnpc.skin.username"),
                    panelX + PREVIEW_W + 10, panelY + 50, 0xFFFFFF);
        }

        // Display name label
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("gui.simplecustomnpc.displayname"),
                panelX + PREVIEW_W + 10, panelY + panelH - 62, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // ── Inner: pose slider wrapper ─────────────────────────────────────────────

    private static class PoseSlider {
        final net.minecraft.client.gui.widget.SliderWidget slider;

        PoseSlider(int x, int y, int w, String label, float min, float max, float initial,
                   java.util.function.Consumer<Float> onChange) {
            double normalized = (initial - min) / (max - min);
            slider = new net.minecraft.client.gui.widget.SliderWidget(x, y, w, 18,
                    Text.literal(label + ": " + (int) initial), normalized) {
                @Override
                protected void updateMessage() {
                    float val = (float) (min + value * (max - min));
                    setMessage(Text.literal(label + ": " + (int) val));
                    onChange.accept(val);
                }
                @Override
                protected void applyValue() {
                    float val = (float) (min + value * (max - min));
                    onChange.accept(val);
                }
            };
        }
    }
}
