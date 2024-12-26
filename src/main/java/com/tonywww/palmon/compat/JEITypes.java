package com.tonywww.palmon.compat;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.recipes.ProcessingRecipe;
import com.tonywww.palmon.recipes.ProductionRecipe;
import mezz.jei.api.recipe.RecipeType;

public class JEITypes {
    public static final RecipeType<ProductionRecipe> PRODUCTION = RecipeType.create(Palmon.MOD_ID, ProductionRecipe.ProductionRecipeType.ID, ProductionRecipe.class);
    public static final RecipeType<ProcessingRecipe> PROCESSING = RecipeType.create(Palmon.MOD_ID, ProcessingRecipe.ProcessingRecipeType.ID, ProcessingRecipe.class);

}
