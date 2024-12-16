package com.tonywww.registeries;

import com.tonywww.Palmon;
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
                        pOutput.accept(ModItems.LABOR_CONTRACT.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

