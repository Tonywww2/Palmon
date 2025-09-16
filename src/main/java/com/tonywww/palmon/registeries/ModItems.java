package com.tonywww.palmon.registeries;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.item.ButcherKnife;
import com.tonywww.palmon.item.EmptyContract;
import com.tonywww.palmon.item.LaborContract;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Palmon.MOD_ID);

    public static final RegistryObject<Item> EMPTY = ITEMS.register("empty",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
            ));

    public static final RegistryObject<Item> FE_SYMBOL = ITEMS.register("fe_symbol",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
            ));

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

    public static final RegistryObject<Item> BUTCHER_KNIFE = ITEMS.register("butcher_knife",
            () -> new ButcherKnife(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
            ));

    // Materials
    public static final RegistryObject<Item> POKE_FOOD = ITEMS.register("poke_food",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
            ));

    public static final RegistryObject<Item> M_CRYSTAL = ITEMS.register("m_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
            ));

    public static final RegistryObject<Item> WOOD = ITEMS.register("wood",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
            ));

    public static final RegistryObject<Item> STONE = ITEMS.register("stone",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
            ));

    public static final RegistryObject<Item> REFINED_COPPER_INGOT = ITEMS.register("refined_copper_ingot",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
            ));

    public static final RegistryObject<Item> REFINED_STEEL_INGOT = ITEMS.register("refined_steel_ingot",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
            ));

    public static final RegistryObject<Item> REFINED_M_STEEL_INGOT = ITEMS.register("refined_m_steel_ingot",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
            ));

    public static final RegistryObject<Item> POLYMER_PLATE = ITEMS.register("polymer_plate",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

    }


}
