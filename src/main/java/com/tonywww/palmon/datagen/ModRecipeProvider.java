package com.tonywww.palmon.datagen;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.palmon.recipes.ProductionRecipeSerializer;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        generateRecipes(consumer);
    }

    private void generateRecipes(Consumer<FinishedRecipe> consumer) {
        // Diamond Recipe
        consumer.accept(createRecipe(
                "diamond_1",
                Stats.ATTACK,
                5,
                null,
                new int[]{100, 120, 150, 100, 100, 100},
                new Ingredient[]{
                        Ingredient.of(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", "storage_blocks/coal")))
                },
                32,
                8000,
                new ItemStack[]{new ItemStack(Items.DIAMOND, 1)},
                0,
                null
        ));

        // Power Recipe
        consumer.accept(createRecipe(
                "power_1",
                Stats.ATTACK,
                5,
                ElementalTypes.INSTANCE.getELECTRIC(),
                new int[]{0, 90, 0, 0, 50, 0},
                new Ingredient[]{
                        Ingredient.of(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", "storage_blocks/copper"))),
                        Ingredient.of(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", "storage_blocks/redstone")))
                },
                4,
                400,
                null,
                500,
                null
        ));

        // Water Recipe
        consumer.accept(createRecipe(
                "water_1",
                Stats.HP,
                1,
                ElementalTypes.INSTANCE.getWATER(),
                new int[]{0, 0, 0, 0, 0, 0},
                new Ingredient[]{
                        Ingredient.of(Items.BRICKS)
                },
                1,
                200,
                null,
                0,
                new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation("minecraft:water")), 500)
        ));
    }

    private FinishedRecipe createRecipe(
            String name,
            Stats focusStat,
            int minLevel,
            ElementalType requiredType,
            int[] baseStats,
            Ingredient[] areaBlocks,
            int blockCount,
            int tick,
            ItemStack[] resultItems,
            int resultPower,
            FluidStack resultFluid
    ) {
        return new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                json.addProperty("type", "palmon:production");
                json.addProperty("focus_stat", focusStat.toString());
                json.addProperty("min_level", minLevel);

                if (requiredType != null) {
                    json.addProperty("required_type", requiredType.getName());
                } else {
                    json.add("required_type", null);
                }

                json.addProperty("base_hp", baseStats[0]);
                json.addProperty("base_atk", baseStats[1]);
                json.addProperty("base_def", baseStats[2]);
                json.addProperty("base_spa", baseStats[3]);
                json.addProperty("base_spd", baseStats[4]);
                json.addProperty("base_spe", baseStats[5]);

                JsonArray blocks = new JsonArray();
                for (Ingredient block : areaBlocks) {
                    blocks.add(block.toJson());
                }
                json.add("area_blocks", blocks);

                json.addProperty("block_count", blockCount);
                json.addProperty("tick", tick);

                if (resultItems != null) {
                    JsonArray results = new JsonArray();
                    for (ItemStack result : resultItems) {
                        JsonObject resultObj = new JsonObject();
                        resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(result.getItem()).toString());
                        resultObj.addProperty("count", result.getCount());
                        results.add(resultObj);
                    }
                    json.add("result_items", results);
                } else {
                    json.add("result_items", null);
                }

                json.addProperty("result_power", resultPower);

                if (resultFluid != null) {
                    JsonObject fluid = new JsonObject();
                    fluid.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(resultFluid.getFluid()).toString());
                    fluid.addProperty("amount", resultFluid.getAmount());
                    json.add("result_fluid", fluid);
                } else {
                    json.add("result_fluid", null);
                }
            }

            @Override
            public ResourceLocation getId() {
                return new ResourceLocation("palmon", name);
            }

            @Override
            public RecipeSerializer<?> getType() {
                return ProductionRecipeSerializer.INSTANCE;
            }

            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        };
    }
}
