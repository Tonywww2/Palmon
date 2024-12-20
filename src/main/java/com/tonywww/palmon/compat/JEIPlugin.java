package com.tonywww.palmon.compat;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.compat.jei.ProductionCategory;
import com.tonywww.palmon.recipes.ProductionRecipe;
import com.tonywww.palmon.registeries.ModBlocks;
import com.tonywww.palmon.screen.ProductionMachineScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@JeiPlugin
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JEIPlugin implements IModPlugin {
    private static final ResourceLocation PID = new ResourceLocation(Palmon.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ProductionCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        registration.addRecipes(JEITypes.PRODUCTION,
                rm.getAllRecipesFor(ProductionRecipe.ProductionRecipeType.INSTANCE).stream().toList());

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                ModBlocks.PRODUCTION_MACHINE.get().asItem().getDefaultInstance(),
                JEITypes.PRODUCTION
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ProductionMachineScreen.class, 118, 15, 65, 13, JEITypes.PRODUCTION);

    }
}
