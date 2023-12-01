package com.betterMovements;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "bettermovements")
public class PlayerInertia {

    // Настройки скорости игрока
    private static final float WALK_SPEED = 0.08f; // Максимальная скорость при ходьбе
    private static final float RUN_SPEED = 0.11f; // Максимальная скорость при беге
    private static final float SNEAK_SPEED = 0.1f; // Максимальная скорость при присяде
    private static final float ACCELERATION = 0.0009f; // Ускорение перехода от ходьбы к бегу
    private static final float SMOOTHNESS = 1.0f; // Плавность разгона и сброса

    private static float currentSpeed = WALK_SPEED; // Текущая скорость игрока

    @SubscribeEvent
    public static void onPlayerUpdate(PlayerEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (!player.world.isRemote && player.equals(Minecraft.getMinecraft().player)) {
                // Плавный набор скорости в зависимости от действия игрока
                if (player.isSprinting()) {
                    if (currentSpeed < RUN_SPEED) {
                        currentSpeed += ACCELERATION;
                    } else {
                        currentSpeed = RUN_SPEED;
                    }
                } else if (player.isSneaking()) {
                    if (currentSpeed > SNEAK_SPEED) {
                        currentSpeed -= ACCELERATION;
                    } else {
                        currentSpeed = SNEAK_SPEED;
                    }
                } else {
                    if (currentSpeed > WALK_SPEED) {
                        currentSpeed -= ACCELERATION;
                    } else {
                        currentSpeed = WALK_SPEED;
                    }
                }

                // Плавная смена скорости передвижения
                if (Math.abs(player.capabilities.getWalkSpeed() - currentSpeed) > SMOOTHNESS) {
                    if (player.capabilities.getWalkSpeed() < currentSpeed) {
                        player.capabilities.setPlayerWalkSpeed(player.capabilities.getWalkSpeed() + SMOOTHNESS);
                    } else {
                        player.capabilities.setPlayerWalkSpeed(player.capabilities.getWalkSpeed() - SMOOTHNESS);
                    }
                } else {
                    player.capabilities.setPlayerWalkSpeed(currentSpeed);
                }
            }
        }
    }
}





