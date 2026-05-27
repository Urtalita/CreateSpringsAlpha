package com.Portality.createsprings.client.menus.PortativeEngine;

import com.Portality.createsprings.client.CSpringsGuiTextures;
import com.mojang.math.Axis;
import com.simibubi.create.AllSoundEvents;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import org.joml.Vector2i;

import java.util.function.Consumer;

public class AnalogLeverSelector extends AbstractSimiWidget {

    private float angle;
    private float maxAngle;
    private float angleBetweenStates;
    final int states;
    private int state;
    private boolean isClicked = false;
    private Consumer<Integer> Callback;

    private CSpringsGuiTextures lever = CSpringsGuiTextures.LEVER_HEAD;
    private CSpringsGuiTextures lever_base = CSpringsGuiTextures.LEVER_BASE;

    protected AnalogLeverSelector(int x, int y, int angle, int states, float standartState, boolean ignoreFirstClick) {
        super(x, y, 180, 100);

        this.maxAngle = angle;
        this.states = states;
        this.angleBetweenStates = maxAngle / states;
        this.angle = -90 + 45 + standartState * (angleBetweenStates + 3);
        state = (int) standartState;
    }

    public AnalogLeverSelector withCallback(Consumer<Integer> Callback) {
        this.Callback = Callback;
        return this;
    }


    public int getState() {
        return state;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        isClicked = true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int pButton) {
        Vector2i shift = new Vector2i(50, 100);
        Vector2i leverShift = new Vector2i(40 + shift.x, 20 + shift.y);

        int x = getX();
        int y = getY();

        isClicked = false;
        if(Callback != null){
            Callback.accept(state);
        }
        angle = stickToState(applyLimits(getAngle((int) mouseX, (int) mouseY, x + leverShift.x, y + leverShift.y)), 0.5f);
        return super.mouseReleased(mouseX, mouseY, pButton);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, int sx, int sy) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        Vector2i shift = new Vector2i(50 + sx, 100 + sy);
        Vector2i leverShift = new Vector2i(40 + shift.x, 20 + shift.y);

        int x = getX();
        int y = getY();

        if(!isHovered){
            if(isClicked){
                isClicked = false;
                if(Callback != null){
                    Callback.accept(state);
                }
                angle = stickToState(applyLimits(getAngle(mouseX, mouseY, x + leverShift.x, y + leverShift.y)), 0.5f);
            }
        }

        if(isClicked) angle = stickToState(applyLimits(getAngle(mouseX, mouseY, x + leverShift.x, y + leverShift.y)), 0.2f);

        graphics.pose().pushPose();
        graphics.pose().rotateAround(Axis.ZP.rotationDegrees(angle), x + leverShift.x, y + leverShift.y, 0);
        lever.render(graphics, x + leverShift.x - 10, y - (100 - leverShift.y));
        graphics.pose().popPose();

        lever_base.render(graphics, x + shift.x, y + shift.y);
    }

    private float stickToState(float angle, float Threshold) {
        float normalizedAngle = normalizeAngle(angle + 90);

        float startAngle = 180 - maxAngle / 2 * 3;
        float step = maxAngle / (states - 1);

        int closestState = 0;
        float minDistance = Float.MAX_VALUE;

        for (int i = 0; i < states; i++) {
            float stateAngle = startAngle + i * step;
            float distance = Math.abs(normalizedAngle - stateAngle);

            if (distance < minDistance) {
                minDistance = distance;
                closestState = i;
            }
        }

        float magnetThreshold = step * Threshold;

        if (minDistance <= magnetThreshold) {
            state = closestState;
            float targetAngle = startAngle + closestState * step;

            if(this.angle != normalizeAngle(targetAngle - 90)){
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(
                        AllSoundEvents.SCROLL_VALUE.getMainEvent(), 1.5F + 0.1F * (float) (this.state))
                );
            }

            return normalizeAngle(targetAngle - 90);
        }

        return angle;
    }

    private float getAngle(int mouseX, int mouseY, int x, int y){
        double dX = mouseX - x;
        double dZ = mouseY - y;

        double distance = Math.sqrt(dX * dX + dZ * dZ);

        if (distance == 0) {
            return 0.0f;
        }

        double sin = dX / distance;
        double asin = Math.asin(sin);

        if(dZ < 0){
            return normalizeAngle ((float) (Math.toDegrees(asin)));
        }
        return normalizeAngle ((float) -Math.toDegrees(asin) + 180);
    }

    private float applyLimits(float angle){
        float correctedAngle = normalizeAngle(angle + 90);

        correctedAngle = Math.max(180 - (maxAngle / 2 * 3), Math.min(180 - (maxAngle / 2), correctedAngle));

        return normalizeAngle(correctedAngle - 90);
    }

    private static float normalizeAngle(float angle) {
        angle %= 360;
        return Math.abs(angle + (angle < 0 ? 360 : 0));
    }
}
