package com.betterMovements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CameraRoll {

    public static float EXTRA_ROLL_Z = 0;
    private float lastYaw;
    // Настройка ограничений угла наклона
    private static final float MIN_ROLL_ANGLE = -1.0F;
    private static final float MAX_ROLL_ANGLE = 1.0F;

    // Чувствительность срабатывания наклона камеры при повороте игрока
    private float rollSensitivity = 0.005F;

    public static float renderTickTime;
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderTickEvent(TickEvent.RenderTickEvent event) {
        if(event.phase == TickEvent.Phase.START)
            renderTickTime = event.renderTickTime;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTickEvent(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.phase != TickEvent.Phase.START)
            return;

        if(mc.player == null)
            return;

        if(mc.isGamePaused())
            return;

        float f1 = (mc.player.distanceWalkedModified - mc.player.prevDistanceWalkedModified);
        float f2 = -(mc.player.distanceWalkedModified + f1 * renderTickTime);
        float f3 = (mc.player.prevCameraYaw + (mc.player.cameraYaw - mc.player.prevCameraYaw) * renderTickTime);
        EXTRA_ROLL_Z += (MathHelper.cos(f2 * (float) Math.PI) * f3) * 2;

        float balancing_speed_x = 0.1f * renderTickTime;

        if (mc.player.moveStrafing > 0) {
            EXTRA_ROLL_Z = Math.min(1.0F, EXTRA_ROLL_Z + balancing_speed_x);
        } else if (mc.player.moveStrafing < 0) {
            EXTRA_ROLL_Z = Math.max(-1.0F, EXTRA_ROLL_Z - balancing_speed_x);
        } else if (EXTRA_ROLL_Z != 0F) {
            if (EXTRA_ROLL_Z > 0F) {
                EXTRA_ROLL_Z = Math.max(0, EXTRA_ROLL_Z - balancing_speed_x);
            } else if (EXTRA_ROLL_Z < 0F) {
                EXTRA_ROLL_Z = Math.min(0, EXTRA_ROLL_Z + balancing_speed_x);
            }
        }

        // Следим за изменением угла поворота игрока (rotationYaw)
        float deltaYaw = MathHelper.wrapDegrees(mc.player.rotationYaw - lastYaw);
        lastYaw = mc.player.rotationYaw;

        // Добавляем эффект наклона при поворотах
        if (Math.abs(deltaYaw) > 0.1) {
            // Применяем чувствительность на изменение угла наклона
            float rollChange = -deltaYaw * rollSensitivity;
            EXTRA_ROLL_Z = Math.max(MIN_ROLL_ANGLE, Math.min(MAX_ROLL_ANGLE, EXTRA_ROLL_Z + rollChange));
        }
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup cameraSetup) {
        GlStateManager.rotate(-EXTRA_ROLL_Z * 2.0f, 0.0F, 0.0F, 1.0F);
    }

    public void setRollSensitivity(float rollSensitivity) {
        this.rollSensitivity = rollSensitivity;
    }
}