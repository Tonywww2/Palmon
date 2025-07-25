package com.tonywww.palmon.events;

import com.tonywww.palmon.block.WorkingStationRenderer;
import com.tonywww.palmon.registeries.ModBlockEntities;
import com.tonywww.palmon.registeries.ModMenus;
import com.tonywww.palmon.screen.ProcessingStationScreen;
import com.tonywww.palmon.screen.ProductionMachineScreen;
import com.tonywww.palmon.screen.WorkingStationScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents {

    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.WORKING_STATION_CONTAINER.get(), WorkingStationScreen::new);
            MenuScreens.register(ModMenus.PRODUCTION_MACHINE_CONTAINER.get(), ProductionMachineScreen::new);
            MenuScreens.register(ModMenus.PROCESSING_STATION_CONTAINER.get(), ProcessingStationScreen::new);

            BlockEntityRenderers.register(ModBlockEntities.WORKING_STATION_BLOCK_ENTITY.get(), WorkingStationRenderer::new);

        });

    }
}
