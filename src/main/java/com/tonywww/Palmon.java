package com.tonywww;

import com.tonywww.registeries.ModCreativeModTabs;
import com.tonywww.registeries.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.tonywww.Palmon.MOD_ID;

@Mod(MOD_ID)
public class Palmon {
    public static final String MOD_ID = "palmon";

    private static final Logger LOGGER = LogManager.getLogger();

    public Palmon() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModCreativeModTabs.register(eventBus);

        // Register the setup method for modloading
        eventBus.addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Palmon >> {}", ModItems.LABOR_CONTRACT.get());

    }

    public static Logger getLogger() {
        return LOGGER;
    }

}
