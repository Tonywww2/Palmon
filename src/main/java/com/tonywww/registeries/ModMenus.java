package com.tonywww.registeries;

import com.tonywww.Palmon;
import com.tonywww.menu.WorkingStationContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

    public static DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Palmon.MOD_ID);

    public static final RegistryObject<MenuType<WorkingStationContainer>> WORKING_STATION_CONTAINER = CONTAINERS.register("working_station_container",
            () -> IForgeMenuType.create(WorkingStationContainer::new));

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);

    }
}
