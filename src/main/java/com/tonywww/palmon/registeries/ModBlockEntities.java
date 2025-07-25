package com.tonywww.palmon.registeries;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.block.entites.ProcessingStationEntityPokemon;
import com.tonywww.palmon.block.entites.ProductionPokemonMachineEntity;
import com.tonywww.palmon.block.entites.WorkingStationEntityPokemon;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Palmon.MOD_ID);

    public static RegistryObject<BlockEntityType<WorkingStationEntityPokemon>> WORKING_STATION_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("working_station",
                    () -> BlockEntityType.Builder.of(WorkingStationEntityPokemon::new, ModBlocks.WORKING_STATION.get()).build(null));

    public static RegistryObject<BlockEntityType<ProductionPokemonMachineEntity>> PRODUCTION_MACHINE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("production_machine",
                    () -> BlockEntityType.Builder.of(ProductionPokemonMachineEntity::new, ModBlocks.PRODUCTION_MACHINE.get()).build(null));

    public static RegistryObject<BlockEntityType<ProcessingStationEntityPokemon>> PROCESSING_STATION_ENTITY =
            BLOCK_ENTITIES.register("processing_station",
                    () -> BlockEntityType.Builder.of(ProcessingStationEntityPokemon::new, ModBlocks.PROCESSING_STATION.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);

    }
}
