package com.tonywww.palmon.registeries;

import com.tonywww.palmon.Palmon;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Palmon.MOD_ID);

    public static RegistryObject<CreativeModeTab> PALMON_TAB = CREATIVE_MODE_TABS.register("palmon_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.LABOR_CONTRACT.get()))
                    .title(Component.translatable("creativetab.palmon_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.EMPTY_CONTRACT.get());
                        pOutput.accept(ModItems.POKE_FOOD.get());

                        pOutput.accept(ModItems.M_CRYSTAL.get());
                        pOutput.accept(ModItems.WOOD.get());
                        pOutput.accept(ModItems.STONE.get());
                        pOutput.accept(ModItems.REFINED_COPPER_INGOT.get());
                        pOutput.accept(ModItems.REFINED_STEEL_INGOT.get());
                        pOutput.accept(ModItems.REFINED_M_STEEL_INGOT.get());
                        pOutput.accept(ModItems.POLYMER_PLATE.get());

                        pOutput.accept(ModItems.FE_SYMBOL.get());

                        pOutput.accept(ModBlocks.WORKING_STATION.get());
                        pOutput.accept(ModBlocks.PRODUCTION_MACHINE.get());
                        pOutput.accept(ModBlocks.PROCESSING_STATION.get());

                        pOutput.accept(ModBlocks.BOOST_FRAME_1.get());
                        pOutput.accept(ModBlocks.BOOST_FRAME_2.get());
                        pOutput.accept(ModBlocks.BOOST_FRAME_3.get());
                        pOutput.accept(ModBlocks.BOOST_FRAME_4.get());
                        pOutput.accept(ModBlocks.BOOST_FRAME_5.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

