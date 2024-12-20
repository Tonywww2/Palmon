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

public class ProductionRecipe implements Recipe<ProductionInput> {
    private final ResourceLocation id;

    private final Stats focusStat;
    private final int minLevel;

    private final ElementalType requiredType;

    private final int baseHP;
    private final int baseATK;
    private final int baseDEF;
    private final int baseSPA;
    private final int baseSPD;
    private final int baseSPE;

    private final NonNullList<Ingredient> areaBlocks;
    private final int blockCount;

    private final int tick;

    private final NonNullList<ItemStack> resultItems;
    private final int resultPower;
    private final FluidStack resultFluid;

    public ProductionRecipe(ResourceLocation id, Stats focusStat, int minLevel, ElementalType requiredType,
                            int baseHP, int baseATK, int baseDEF, int baseSPA, int baseSPD, int baseSPE,
                            NonNullList<Ingredient> areaBlocks, int blockCount, int tick,
                            NonNullList<ItemStack> resultItems, int resultPower, FluidStack resultFluid) {
        this.id = id;
        this.focusStat = focusStat;
        this.minLevel = minLevel;
        this.requiredType = requiredType;
        this.baseHP = baseHP;
        this.baseATK = baseATK;
        this.baseDEF = baseDEF;
        this.baseSPA = baseSPA;
        this.baseSPD = baseSPD;
        this.baseSPE = baseSPE;
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
                input.getBaseHP() >= this.baseHP && input.getBaseATK() >= this.baseATK && input.getBaseDEF() >= this.baseDEF &&
                input.getBaseSPA() >= this.baseSPA && input.getBaseSPD() >= this.baseSPD && input.getBaseSPE() >= this.baseSPE
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

    public int getBaseHP() {
        return baseHP;
    }

    public int getBaseATK() {
        return baseATK;
    }

    public int getBaseDEF() {
        return baseDEF;
    }

    public int getBaseSPA() {
        return baseSPA;
    }

    public int getBaseSPD() {
        return baseSPD;
    }

    public int getBaseSPE() {
        return baseSPE;
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
