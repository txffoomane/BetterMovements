package com.betterMovements;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.common.Mod.EventHandler;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


@Mod(modid = BetterMovements.MODID)

public class BetterMovements {

    public static final String MODID = "bettermovements";


    @EventHandler

    public void preInit(FMLPreInitializationEvent event) {

        // инициализация

    }


    @EventHandler

    public void init(FMLInitializationEvent event) {

        // регистрация обработчика

        MinecraftForge.EVENT_BUS.register(new PlayerMovementHandler.playerMovementHandler());

    }


    @EventHandler

    public void postInit(FMLPostInitializationEvent event) {

        // пост-инициализация

    }

}