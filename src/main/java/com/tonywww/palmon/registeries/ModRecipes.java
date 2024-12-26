package com.tonywww.palmon.registeries;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.recipes.ProcessingRecipe;
import com.tonywww.palmon.recipes.ProcessingRecipeSerializer;
import com.tonywww.palmon.recipes.ProductionRecipe;
import com.tonywww.palmon.recipes.ProductionRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_TYPE = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Palmon.MOD_ID);

    public static final RegistryObject<ProductionRecipeSerializer> PRODUCTION_SERIALIZER =
            RECIPE_TYPE.register(ProductionRecipe.ProductionRecipeType.ID, () -> ProductionRecipeSerializer.INSTANCE);

    public static final RegistryObject<ProcessingRecipeSerializer> PROCESSING_SERIALIZER =
            RECIPE_TYPE.register(ProcessingRecipe.ProcessingRecipeType.ID, () -> ProcessingRecipeSerializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPE.register(eventBus);

    }

}
