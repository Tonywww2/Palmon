package com.tonywww.palmon.events;

import com.tonywww.palmon.Palmon;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Palmon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetupEvents {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }
}
