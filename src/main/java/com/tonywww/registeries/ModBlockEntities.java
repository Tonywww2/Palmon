package com.tonywww.registeries;

import com.tonywww.Palmon;
import com.tonywww.block.entites.WorkingStationEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Palmon.MOD_ID);

    public static RegistryObject<BlockEntityType<WorkingStationEntity>> WORKING_STATION_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("working_station",
                    () -> BlockEntityType.Builder.of(WorkingStationEntity::new, ModBlocks.WORKING_STATION.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);

    }
}
