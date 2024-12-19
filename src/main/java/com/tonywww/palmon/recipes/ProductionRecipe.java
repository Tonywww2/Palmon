package com.tonywww.palmon.recipes;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.tonywww.palmon.api.ProductionInput;
import com.tonywww.palmon.registeries.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;

public class ProductionRecipe implements Recipe<ProductionInput> {
    private final ResourceLocation id;

    private final Stats focusStat;
    private final int minLevel;

    private final ElementalType requiredType;

    private final int evHP;
    private final int evATK;
    private final int evDEF;
    private final int evSPA;
    private final int evSPD;
    private final int evSPE;

    private final NonNullList<Ingredient> areaBlocks;
    private final int blockCount;

    private final int tick;

    private final NonNullList<ItemStack> resultItems;
    private final int resultPower;
    private final FluidStack resultFluid;

    public ProductionRecipe(ResourceLocation id, Stats focusStat, int minLevel, ElementalType requiredType,
                            int evHP, int evATK, int evDEF, int evSPA, int evSPD, int evSPE,
                            NonNullList<Ingredient> areaBlocks, int blockCount, int tick,
                            NonNullList<ItemStack> resultItems, int resultPower, FluidStack resultFluid) {
        this.id = id;
        this.focusStat = focusStat;
        this.minLevel = minLevel;
        this.requiredType = requiredType;
        this.evHP = evHP;
        this.evATK = evATK;
        this.evDEF = evDEF;
        this.evSPA = evSPA;
        this.evSPD = evSPD;
        this.evSPE = evSPE;
        this.areaBlocks = areaBlocks;
        this.blockCount = blockCount;
        this.tick = tick;

        this.resultItems = resultItems;
        this.resultPower = resultPower;
        this.resultFluid = resultFluid;
    }

    @Override
    public boolean matches(ProductionInput input, Level level) {
        if (input.getLevel() >= this.minLevel && (this.requiredType == null || this.requiredType.equals(input.getType())) &&
                input.getEVHP() >= this.evHP && input.getEVATK() >= this.evATK && input.getEVDEF() >= this.evDEF &&
                input.getEVSPA() >= this.evSPA && input.getEVSPD() >= this.evSPD && input.getEVSPE() >= this.evSPE
        ) {
            int[] matches = new int[this.areaBlocks.size()];
            for (int i = 0; i < this.areaBlocks.size(); i++) {
                boolean match = false;
                for (int j = 0; j < input.getContainerSize(); j++) {
                    ItemStack item = input.getItem(j);
                    if (!item.isEmpty() && this.areaBlocks.get(i).test(item)) {
                        matches[i] += item.getCount();
                        match = true;

                    }
                }
                if (!match) return false;
            }

            for (int match : matches) {
                if (match < this.blockCount) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack assemble(ProductionInput arg, RegistryAccess arg2) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess arg) {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PRODUCTION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ProductionRecipeType.INSTANCE;
    }

    public Stats getFocusStat() {
        return focusStat;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getEvHP() {
        return evHP;
    }

    public int getEvATK() {
        return evATK;
    }

    public int getEvDEF() {
        return evDEF;
    }

    public int getEvSPA() {
        return evSPA;
    }

    public int getEvSPD() {
        return evSPD;
    }

    public int getEvSPE() {
        return evSPE;
    }

    public NonNullList<Ingredient> getAreaBlocks() {
        return areaBlocks;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public int getTick() {
        return tick;
    }

    public ElementalType getRequiredType() {
        return requiredType;
    }

    public NonNullList<ItemStack> getResultItems() {
        return resultItems;
    }

    public int getResultPower() {
        return resultPower;
    }

    public FluidStack getResultFluid() {
        return resultFluid;
    }

    public static class ProductionRecipeType implements RecipeType<ProductionRecipe> {
        public static final ProductionRecipeType INSTANCE = new ProductionRecipeType();
        public static final String ID = "production";

    }

}
