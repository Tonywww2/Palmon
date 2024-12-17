package com.tonywww.palmon.registeries;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.item.EmptyContract;
import com.tonywww.palmon.item.LaborContract;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Palmon.MOD_ID);

    public static final RegistryObject<Item> EMPTY_CONTRACT = ITEMS.register("empty_contract",
            () -> new EmptyContract(new Item.Properties()
                    .stacksTo(16)
                    .fireResistant()
            ));
    public static final RegistryObject<Item> LABOR_CONTRACT = ITEMS.register("labor_contract",
            () -> new LaborContract(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
            ));

    public static final RegistryObject<Item> M_CRYSTAL = ITEMS.register("m_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
            ));

    public static void register(IEventBus eventBus) {

        ITEMS.register(eventBus);

    }

}
