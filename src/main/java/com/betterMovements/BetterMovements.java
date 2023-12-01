package com.betterMovements;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = BetterMovements.MODID, name = BetterMovements.NAME, version = BetterMovements.VERSION)
public class BetterMovements
{
    public static final String MODID = "bettermovements";
    public static final String NAME = "Better movements";
    public static final String VERSION = "0.0.3";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        RenderTickEvent renderTickEvent = new RenderTickEvent();
        renderTickEvent.setRollSensitivity(0.04f);  // Задайте желаемую чувствительность при стрейфах

        MinecraftForge.EVENT_BUS.register(renderTickEvent);
    }
}


