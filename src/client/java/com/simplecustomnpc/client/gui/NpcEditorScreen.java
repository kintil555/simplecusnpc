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
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class NpcEditorScreen extends Screen {

    // ── Texture (vanilla GUI style) ─────────────────────────────────────────
    private static final Identifier BG_TEXTURE =
            Identifier.of("minecraft", "textures/gui/demo_background.png");

    // ── Layout constants ────────────────────────────────────────────────────
    // Panel is 248 wide × 224 tall — same as standard chest GUI
    private static final int GUI_W = 248;
    private static final int GUI_H = 224;
    private static final int PREVIEW_W = 72;    // Left column: entity preview
    private static final int CONTROLS_X_OFF = PREVIEW_W + 8; // Controls start here
    private static final int CONTROLS_W = GUI_W - CONTROLS_X_OFF - 6;

    private int guiX, guiY;

    // ── Tabs ────────────────────────────────────────────────────────────────
    private static final int TAB_POSE = 0;
    private static final int TAB_SKIN = 1;
    private int activeTab = TAB_POSE;

    // ── State ────────────────────────────────────────────────────────────────
    private final CustomNpcEntity npc;
    private NpcPoseData pose;

    // ── Widgets ─────────────────────────────────────────────────────────────
    private final List<PoseSliderEntry> poseSliders = new ArrayList<>();
    private TextFieldWidget skinUsernameField;
    private TextFieldWidget displayNameField;
    private ButtonWidget slimToggleBtn;
    private ButtonWidget tabPoseBtn;
    private ButtonWidget tabSkinBtn;

    // ── Skin tab scroll ──────────────────────────────────────────────────────
    // (not needed — skin tab is short)

    public NpcEditorScreen(CustomNpcEntity npc, NpcPoseData pose) {
        super(Text.translatable("gui.simplecustomnpc.editor.title"));
        this.npc  = npc;
        this.pose = pose;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Init
    // ────────────────────────────────────────────────────────────────────────
    @Override
    protected void init() {
        guiX = (this.width  - GUI_W) / 2;
        guiY = (this.height - GUI_H) / 2;

        int cx = guiX + CONTROLS_X_OFF;  // Controls left edge
        int cw = CONTROLS_W;

        // ── Tab Buttons (top of control column) ──────────────────────────
        int tabBtnW = cw / 2;
        tabPoseBtn = ButtonWidget.builder(
                Text.translatable("gui.simplecustomnpc.tab.pose"),
                b -> setTab(TAB_POSE)
        ).dimensions(cx, guiY + 6, tabBtnW, 16).build();

        tabSkinBtn = ButtonWidget.builder(
                Text.translatable("gui.simplecustomnpc.tab.skin"),
                b -> setTab(TAB_SKIN)
        ).dimensions(cx + tabBtnW, guiY + 6, tabBtnW, 16).build();

        addDrawableChild(tabPoseBtn);
        addDrawableChild(tabSkinBtn);

        // ── Display Name (always visible, bottom of panel) ────────────────
        int bottomRowY = guiY + GUI_H - 28;
        displayNameField = new TextFieldWidget(
                this.textRenderer,
                cx, bottomRowY, cw, 12,
                Text.translatable("gui.simplecustomnpc.displayname")
        );
        displayNameField.setPlaceholder(
                Text.translatable("gui.simplecustomnpc.displayname.hint"));
        displayNameField.setText(npc.getNpcDisplayName());
        displayNameField.setMaxLength(32);
        addDrawableChild(displayNameField);

        // ── Save / Close ──────────────────────────────────────────────────
        int btnW = (cw - 4) / 2;
        addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.simplecustomnpc.save"),
                b -> save()
        ).dimensions(cx, guiY + GUI_H - 14, btnW, 12).build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.simplecustomnpc.close"),
                b -> close()
        ).dimensions(cx + btnW + 4, guiY + GUI_H - 14, btnW, 12).build());

        // ── Pose sliders ──────────────────────────────────────────────────
        buildPoseSliders(cx, cw);

        // ── Skin tab widgets ──────────────────────────────────────────────
        buildSkinTab(cx, cw);

        setTab(activeTab);
    }

    private void buildPoseSliders(int cx, int cw) {
        poseSliders.clear();
        int y = guiY + 26;
        int sliderH = 14;
        int gap = 16;

        poseSliders.add(new PoseSliderEntry(cx, y,          cw, sliderH, "Head Yaw",   -180, 180, () -> pose.headYaw,       v -> pose.headYaw       = v));
        poseSliders.add(new PoseSliderEntry(cx, y + gap,    cw, sliderH, "Head Pitch",  -90,  90, () -> pose.headPitch,     v -> pose.headPitch     = v));
        poseSliders.add(new PoseSliderEntry(cx, y+gap*2,    cw, sliderH, "Body Yaw",   -180, 180, () -> pose.bodyYaw,       v -> pose.bodyYaw       = v));
        poseSliders.add(new PoseSliderEntry(cx, y+gap*3,    cw, sliderH, "R.Arm Pitch",-180, 180, () -> pose.rightArmPitch, v -> pose.rightArmPitch = v));
        poseSliders.add(new PoseSliderEntry(cx, y+gap*4,    cw, sliderH, "R.Arm Yaw",  -180, 180, () -> pose.rightArmYaw,   v -> pose.rightArmYaw   = v));
        poseSliders.add(new PoseSliderEntry(cx, y+gap*5,    cw, sliderH, "R.Arm Roll", -180, 180, () -> pose.rightArmRoll,  v -> pose.rightArmRoll  = v));
        poseSliders.add(new PoseSliderEntry(cx, y+gap*6,    cw, sliderH, "L.Arm Pitch",-180, 180, () -> pose.leftArmPitch,  v -> pose.leftArmPitch  = v));
        poseSliders.add(new PoseSliderEntry(cx, y+gap*7,    cw, sliderH, "L.Arm Yaw",  -180, 180, () -> pose.leftArmYaw,    v -> pose.leftArmYaw    = v));
        poseSliders.add(new PoseSliderEntry(cx, y+gap*8,    cw, sliderH, "L.Arm Roll", -180, 180, () -> pose.leftArmRoll,   v -> pose.leftArmRoll   = v));
        poseSliders.add(new PoseSliderEntry(cx, y+gap*9,    cw, sliderH, "R.Leg Pitch", -90,  90, () -> pose.rightLegPitch, v -> pose.rightLegPitch = v));
        poseSliders.add(new PoseSliderEntry(cx, y+gap*10,   cw, sliderH, "L.Leg Pitch", -90,  90, () -> pose.leftLegPitch,  v -> pose.leftLegPitch  = v));

        for (PoseSliderEntry e : poseSliders) {
            addDrawableChild(e.widget);
        }
    }

    private void buildSkinTab(int cx, int cw) {
        skinUsernameField = new TextFieldWidget(
                this.textRenderer,
                cx, guiY + 46, cw, 12,
                Text.translatable("gui.simplecustomnpc.skin.username")
        );
        skinUsernameField.setPlaceholder(Text.literal("e.g. Notch"));
        skinUsernameField.setText(pose.skinUsername);
        skinUsernameField.setMaxLength(16);
        skinUsernameField.setVisible(false);
        addDrawableChild(skinUsernameField);

        slimToggleBtn = ButtonWidget.builder(
                slimLabel(),
                b -> {
                    pose.slimModel = !pose.slimModel;
                    b.setMessage(slimLabel());
                }
        ).dimensions(cx, guiY + 66, cw, 14).build();
        slimToggleBtn.visible = false;
        addDrawableChild(slimToggleBtn);
    }

    private Text slimLabel() {
        return Text.translatable(pose.slimModel
                ? "gui.simplecustomnpc.slim.on"
                : "gui.simplecustomnpc.slim.off");
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Tab switching
    // ────────────────────────────────────────────────────────────────────────
    private void setTab(int tab) {
        activeTab = tab;
        boolean isPose = (tab == TAB_POSE);

        for (PoseSliderEntry e : poseSliders) e.widget.visible = isPose;
        skinUsernameField.setVisible(!isPose);
        slimToggleBtn.visible = !isPose;

        // Visual: dim inactive tab button
        tabPoseBtn.active = !isPose;
        tabSkinBtn.active = isPose;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Rendering
    // ────────────────────────────────────────────────────────────────────────
    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        // Dim the world behind
        renderBackground(ctx, mx, my, delta);

        // ── Panel background (dark inset Minecraft style) ──────────────────
        // Outer frame
        ctx.fill(guiX, guiY, guiX + GUI_W, guiY + GUI_H, 0xFFC6C6C6);
        // Top/left highlight
        ctx.fill(guiX + 1,       guiY + 1,       guiX + GUI_W - 1, guiY + 2,       0xFFFFFFFF);
        ctx.fill(guiX + 1,       guiY + 1,       guiX + 2,         guiY + GUI_H - 1, 0xFFFFFFFF);
        // Bottom/right shadow
        ctx.fill(guiX + 1,       guiY + GUI_H - 2, guiX + GUI_W - 1, guiY + GUI_H - 1, 0xFF555555);
        ctx.fill(guiX + GUI_W - 2, guiY + 1,      guiX + GUI_W - 1, guiY + GUI_H - 1, 0xFF555555);
        // Inner fill
        ctx.fill(guiX + 2, guiY + 2, guiX + GUI_W - 2, guiY + GUI_H - 2, 0xFF8B8B8B);

        // ── Preview area ───────────────────────────────────────────────────
        // Slightly darker inset for the preview column
        int prevX1 = guiX + 3;
        int prevY1 = guiY + 3;
        int prevX2 = guiX + PREVIEW_W - 2;
        int prevY2 = guiY + GUI_H - 3;
        // Inset shadow border
        ctx.fill(prevX1,     prevY1, prevX2,     prevY1 + 1, 0xFF373737);
        ctx.fill(prevX1,     prevY1, prevX1 + 1, prevY2,     0xFF373737);
        ctx.fill(prevX1,     prevY2 - 1, prevX2, prevY2,     0xFFFFFFFF);
        ctx.fill(prevX2 - 1, prevY1, prevX2,     prevY2,     0xFFFFFFFF);
        ctx.fill(prevX1 + 1, prevY1 + 1, prevX2 - 1, prevY2 - 1, 0xFF000000);

        // ── Controls area inset ────────────────────────────────────────────
        int ctrlX1 = guiX + PREVIEW_W + 2;
        int ctrlY1 = guiY + 3;
        int ctrlX2 = guiX + GUI_W - 3;
        int ctrlY2 = guiY + GUI_H - 3;
        ctx.fill(ctrlX1, ctrlY1, ctrlX2, ctrlY1 + 1, 0xFF373737);
        ctx.fill(ctrlX1, ctrlY1, ctrlX1 + 1, ctrlY2, 0xFF373737);
        ctx.fill(ctrlX1, ctrlY2 - 1, ctrlX2, ctrlY2, 0xFFFFFFFF);
        ctx.fill(ctrlX2 - 1, ctrlY1, ctrlX2, ctrlY2, 0xFFFFFFFF);
        ctx.fill(ctrlX1 + 1, ctrlY1 + 1, ctrlX2 - 1, ctrlY2 - 1, 0xFF8B8B8B);

        // ── Divider above bottom row ───────────────────────────────────────
        int divY = guiY + GUI_H - 32;
        ctx.fill(ctrlX1 + 1, divY,     ctrlX2 - 1, divY + 1,     0xFF373737);
        ctx.fill(ctrlX1 + 1, divY + 1, ctrlX2 - 1, divY + 2,     0xFFFFFFFF);

        // ── Title ──────────────────────────────────────────────────────────
        ctx.drawCenteredTextWithShadow(textRenderer, this.title,
                guiX + GUI_W / 2, guiY - 10, 0xFFFFFF);

        // ── "Display Name:" label ──────────────────────────────────────────
        ctx.drawTextWithShadow(textRenderer,
                Text.translatable("gui.simplecustomnpc.displayname"),
                guiX + CONTROLS_X_OFF, guiY + GUI_H - 40, 0x404040);

        // ── Skin tab labels ────────────────────────────────────────────────
        if (activeTab == TAB_SKIN) {
            ctx.drawTextWithShadow(textRenderer,
                    Text.translatable("gui.simplecustomnpc.skin.username"),
                    guiX + CONTROLS_X_OFF, guiY + 36, 0x404040);
        }

        // ── 3D Entity Preview ──────────────────────────────────────────────
        int cx = guiX + PREVIEW_W / 2;
        int bot = guiY + GUI_H - 20;
        InventoryScreen.drawEntity(
                ctx,
                prevX1 + 2, prevY1 + 2,
                prevX2 - 2, prevY2 - 2,
                36,           // size
                0.0625f,
                (float) cx,
                (float) (guiY + GUI_H / 2),
                npc
        );

        super.render(ctx, mx, my, delta);
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Save
    // ────────────────────────────────────────────────────────────────────────
    private void save() {
        pose.skinUsername = skinUsernameField.getText().trim();
        NpcClientNetworking.sendSaveNpc(npc.getId(), pose);
        close();
    }

    @Override
    public boolean shouldPause() { return false; }

    // ────────────────────────────────────────────────────────────────────────
    //  Inner: PoseSliderEntry
    // ────────────────────────────────────────────────────────────────────────
    private static class PoseSliderEntry {
        final SliderWidget widget;

        PoseSliderEntry(int x, int y, int w, int h,
                        String label, float min, float max,
                        Supplier<Float> getter, Consumer<Float> setter) {
            double norm = (getter.get() - min) / (max - min);
            widget = new SliderWidget(x, y, w, h,
                    Text.literal(label + ": " + fmt(getter.get())), norm) {
                @Override
                protected void updateMessage() {
                    float v = (float)(min + value * (max - min));
                    setMessage(Text.literal(label + ": " + fmt(v)));
                    setter.accept(v);
                }
                @Override
                protected void applyValue() {
                    setter.accept((float)(min + value * (max - min)));
                }
            };
        }

        private static String fmt(float v) {
            return String.valueOf((int) v);
        }
    }
}
