package com.betterMovements;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerMovementHandler {
    private Vec3d velocity = new Vec3d(0, 0, 0);
    private static final double MAX_SPEED_WALKING = 0.08;
    private static final double MAX_SPEED_RUNNING = 0.13;
    private static final double MAX_SPEED_SNEAKING = 0.06;

    private static final double ACCELERATION = 0.02;
    private static final double DECELERATION = 0.025;
    private static final double AIR_RESISTANCE = 0.04;

    private static final double STOP_MULTIPLIER = 1.5;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.side != Side.CLIENT) return;

        EntityPlayer player = event.player;
        if (player != Minecraft.getMinecraft().player) return;

        updateMovement(player);
    }

    private void updateMovement(EntityPlayer player) {
        float forward = player.moveForward;
        float strafe = player.moveStrafing;

        double maxSpeed = getMaxSpeed(player);
        double[] movement = calculateMovement(forward, strafe, player.rotationYaw);

        // Нормализация вектора движения
        double length = Math.sqrt(movement[0] * movement[0] + movement[1] * movement[1]);
        if (length > 0) {
            movement[0] /= length;
            movement[1] /= length;
            applyAcceleration(movement);
        } else {
            applyDeceleration();
        }

        // Применение сопротивления воздуха
        applyAirResistance();

        // Ограничение максимальной скорости
        limitMaxSpeed(maxSpeed);

        // Применение движения к игроку
        player.motionX = velocity.x;
        player.motionZ = velocity.z;
    }

    private double getMaxSpeed(EntityPlayer player) {
        if (player.isSprinting()) return MAX_SPEED_RUNNING;
        if (player.isSneaking()) return MAX_SPEED_SNEAKING;
        return MAX_SPEED_WALKING;
    }

    private double[] calculateMovement(float forward, float strafe, float yaw) {
        double moveX = -Math.sin(Math.toRadians(yaw)) * forward + Math.cos(Math.toRadians(yaw)) * strafe;
        double moveZ = Math.cos(Math.toRadians(yaw)) * forward + Math.sin(Math.toRadians(yaw)) * strafe;
        return new double[]{moveX, moveZ};
    }

    private void applyAcceleration(double[] movement) {
        velocity = new Vec3d(
                velocity.x + movement[0] * ACCELERATION,
                0,
                velocity.z + movement[1] * ACCELERATION
        );
    }

    private void applyDeceleration() {
        velocity = new Vec3d(
                velocity.x * (1.0 - (DECELERATION * STOP_MULTIPLIER)),
                0,
                velocity.z * (1.0 - (DECELERATION * STOP_MULTIPLIER))
        );

        if (velocity.lengthVector() < 0.01) {
            velocity = new Vec3d(0, 0, 0);
        }
    }

    private void applyAirResistance() {
        velocity = velocity.scale(1.0 - AIR_RESISTANCE);
    }

    private void limitMaxSpeed(double maxSpeed) {
        double currentSpeed = velocity.lengthVector();
        if (currentSpeed > maxSpeed) {
            velocity = velocity.scale(maxSpeed / currentSpeed);
        }
    }
}