package com.simplecustomnpc.client.gui;

import com.simplecustomnpc.entity.CustomNpcEntity;
import com.simplecustomnpc.network.NpcClientNetworking;
import com.simplecustomnpc.util.NpcPoseData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
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
    private static final int TAB_POSE  = 0;
    private static final int TAB_SKIN  = 1;
    private int activeTab = TAB_POSE;

    // Skin input
    private TextFieldWidget skinUsernameField;
    private TextFieldWidget displayNameField;

    // Sliders state (list of SliderEntry)
    private final List<PoseSlider> sliders = new ArrayList<>();

    private int panelX, panelY, panelW, panelH;

    public NpcEditorScreen(CustomNpcEntity npc, NpcPoseData pose) {
        super(Text.translatable("gui.simplecustomnpc.editor.title"));
        this.npc  = npc;
        this.pose = pose;
    }

    @Override
    protected void init() {
        panelW = 260;
        panelH = 300;
        panelX = (this.width  - panelW) / 2;
        panelY = (this.height - panelH) / 2;

        // ── Tab Buttons ──────────────────────────────────────────────────────
        addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.simplecustomnpc.tab.pose"),
                btn -> switchTab(TAB_POSE)
        ).dimensions(panelX, panelY, 130, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.simplecustomnpc.tab.skin"),
                btn -> switchTab(TAB_SKIN)
        ).dimensions(panelX + 130, panelY, 130, 20).build());

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
                panelX + 10, panelY + panelH - 50, panelW - 20, 16,
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
        int y = panelY + 30;
        int x = panelX + 10;
        int w = panelW - 20;

        // Head
        sliders.add(new PoseSlider(x, y,      w, "Head Yaw",       -180, 180, pose.headYaw,    v -> pose.headYaw    = v));
        sliders.add(new PoseSlider(x, y + 22, w, "Head Pitch",     -90,  90,  pose.headPitch,  v -> pose.headPitch  = v));

        // Body
        sliders.add(new PoseSlider(x, y + 50, w, "Body Yaw",       -180, 180, pose.bodyYaw,    v -> pose.bodyYaw    = v));

        // Right Arm
        sliders.add(new PoseSlider(x, y + 78,  w, "R.Arm Pitch",   -180, 180, pose.rightArmPitch, v -> pose.rightArmPitch = v));
        sliders.add(new PoseSlider(x, y + 100, w, "R.Arm Yaw",     -180, 180, pose.rightArmYaw,   v -> pose.rightArmYaw   = v));
        sliders.add(new PoseSlider(x, y + 122, w, "R.Arm Roll",    -180, 180, pose.rightArmRoll,  v -> pose.rightArmRoll  = v));

        // Left Arm
        sliders.add(new PoseSlider(x, y + 148, w, "L.Arm Pitch",   -180, 180, pose.leftArmPitch,  v -> pose.leftArmPitch  = v));
        sliders.add(new PoseSlider(x, y + 170, w, "L.Arm Yaw",     -180, 180, pose.leftArmYaw,    v -> pose.leftArmYaw    = v));
        sliders.add(new PoseSlider(x, y + 192, w, "L.Arm Roll",    -180, 180, pose.leftArmRoll,   v -> pose.leftArmRoll   = v));

        // Right Leg
        sliders.add(new PoseSlider(x, y + 218, w, "R.Leg Pitch",   -90,  90,  pose.rightLegPitch, v -> pose.rightLegPitch = v));
        sliders.add(new PoseSlider(x, y + 240, w, "L.Leg Pitch",   -90,  90,  pose.leftLegPitch,  v -> pose.leftLegPitch  = v));

        for (PoseSlider s : sliders) {
            addDrawableChild(s.slider);
        }
    }

    private void buildSkinTab() {
        skinUsernameField = new TextFieldWidget(this.textRenderer,
                panelX + 10, panelY + 60, panelW - 20, 18,
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
        ).dimensions(panelX + 10, panelY + 90, panelW - 20, 18).build());
    }

    private void switchTab(int tab) {
        this.activeTab = tab;

        // Show/hide pose sliders
        boolean isPose = (tab == TAB_POSE);
        for (PoseSlider s : sliders) {
            s.slider.visible = isPose;
        }

        // Show/hide skin fields
        skinUsernameField.setVisible(!isPose);
    }

    private void save() {
        // Collect skin username
        pose.skinUsername = skinUsernameField.getText().trim();

        // Send to server
        NpcClientNetworking.sendSaveNpc(npc.getId(), pose);

        // Apply display name
        // (server will set the custom name when it processes the packet)
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Semi-transparent background panel
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xCC000000);
        context.drawBorder(panelX, panelY, panelW, panelH, 0xFFAAAAAA);

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer,
                this.title, this.width / 2, panelY - 12, 0xFFFFFF);

        // Tab labels
        if (activeTab == TAB_POSE) {
            context.drawTextWithShadow(this.textRenderer,
                    Text.translatable("gui.simplecustomnpc.pose.hint"),
                    panelX + 10, panelY + 22, 0xAAAAAA);
        } else {
            context.drawTextWithShadow(this.textRenderer,
                    Text.translatable("gui.simplecustomnpc.skin.hint"),
                    panelX + 10, panelY + 40, 0xAAAAAA);
            context.drawTextWithShadow(this.textRenderer,
                    Text.translatable("gui.simplecustomnpc.skin.username"),
                    panelX + 10, panelY + 50, 0xFFFFFF);
        }

        // Display name label
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("gui.simplecustomnpc.displayname"),
                panelX + 10, panelY + panelH - 62, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false; // Don't pause game in singleplayer
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
                    float val = (float)(min + value * (max - min));
                    setMessage(Text.literal(label + ": " + (int) val));
                    onChange.accept(val);
                }
                @Override
                protected void applyValue() {
                    float val = (float)(min + value * (max - min));
                    onChange.accept(val);
                }
            };
        }
    }
}
