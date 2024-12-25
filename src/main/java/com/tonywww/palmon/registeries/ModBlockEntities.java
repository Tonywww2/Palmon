package com.tonywww.palmon.registeries;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.block.entites.ProcessingStationEntity;
import com.tonywww.palmon.block.entites.ProductionMachineEntity;
import com.tonywww.palmon.block.entites.WorkingStationEntity;
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

    public static RegistryObject<BlockEntityType<ProductionMachineEntity>> PRODUCTION_MACHINE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("production_machine",
                    () -> BlockEntityType.Builder.of(ProductionMachineEntity::new, ModBlocks.PRODUCTION_MACHINE.get()).build(null));

    public static RegistryObject<BlockEntityType<ProcessingStationEntity>> PROCESSING_STATION_ENTITY =
            BLOCK_ENTITIES.register("processing_station",
                    () -> BlockEntityType.Builder.of(ProcessingStationEntity::new, ModBlocks.PROCESSING_STATION.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);

    }
}
