package com.tonywww.palmon.events;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.datagen.ModRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Palmon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetupEvents {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }

// fk u architectury and kotlin
//    @SubscribeEvent
//    public void onGatherData(GatherDataEvent event) {
//        DataGenerator generator = event.getGenerator();
//        PackOutput output = generator.getPackOutput();
//        ExistingFileHelper fileHelper = event.getExistingFileHelper();
//
//        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
//
//        generator.addProvider(event.includeServer(), new ModRecipeProvider(output));
//    }
}
