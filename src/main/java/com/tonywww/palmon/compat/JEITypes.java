package com.tonywww.palmon.compat;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.recipes.ProductionRecipe;
import mezz.jei.api.recipe.RecipeType;

public class JEITypes {
    public static final RecipeType<ProductionRecipe> PRODUCTION = RecipeType.create(Palmon.MOD_ID, ProductionRecipe.ProductionRecipeType.ID, ProductionRecipe.class);

}