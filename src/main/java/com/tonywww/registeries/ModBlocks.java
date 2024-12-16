package com.tonywww.registeries;

import com.tonywww.Palmon;
import com.tonywww.block.WorkingStation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Palmon.MOD_ID);

    public static final RegistryObject<Block> WORKING_STATION = registerBlocks("working_station",
            () -> new WorkingStation(BlockBehaviour.Properties
                    .copy(Blocks.STONE)
                    .strength(2f, 4f)
                    .noOcclusion()
            ));

    private static <T extends Block> RegistryObject<T> registerBlocks(String name, Supplier<T> block) {
        RegistryObject<T> tRegistryObject = BLOCKS.register(name, block);

        registerBlockItem(name, tRegistryObject);

        return tRegistryObject;

    }

    private static <T extends Block> void registerBlockItem(String name, Supplier<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));

    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);

    }

}
