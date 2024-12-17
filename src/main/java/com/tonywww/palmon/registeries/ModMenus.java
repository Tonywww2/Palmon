package com.tonywww.palmon.registeries;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.menu.ProductionMachineContainer;
import com.tonywww.palmon.menu.WorkingStationContainer;
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

    public static final RegistryObject<MenuType<ProductionMachineContainer>> PRODUCTION_MACHINE_CONTAINER = CONTAINERS.register("production_machine_container",
            () -> IForgeMenuType.create(ProductionMachineContainer::new));

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);

    }
}
