package com.tonywww.palmon.datagen;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.palmon.recipes.ProductionRecipeSerializer;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductionRecipeBuilder {

    private final ResourceLocation id;
    private Stats focusStat;
    private int minLevel;
    private ElementalType requiredType;
    private final int[] baseStats = new int[6];
    private final List<Ingredient> areaBlocks = new ArrayList<>();
    private int blockCount;
    private int tick;
    private final List<ItemStack> resultItems = new ArrayList<>();
    private int resultPower;
    private FluidStack resultFluid;

    private ProductionRecipeBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static ProductionRecipeBuilder create(ResourceLocation id) {
        return new ProductionRecipeBuilder(id);
    }

    public ProductionRecipeBuilder focusStat(Stats stat) {
        this.focusStat = stat;
        return this;
    }

    public ProductionRecipeBuilder minLevel(int level) {
        this.minLevel = level;
        return this;
    }

    public ProductionRecipeBuilder requiredType(ElementalType type) {
        this.requiredType = type;
        return this;
    }

    public ProductionRecipeBuilder baseStats(int hp, int atk, int def, int spa, int spd, int spe) {
        this.baseStats[0] = hp;
        this.baseStats[1] = atk;
        this.baseStats[2] = def;
        this.baseStats[3] = spa;
        this.baseStats[4] = spd;
        this.baseStats[5] = spe;
        return this;
    }

    public ProductionRecipeBuilder areaBlock(Ingredient ingredient) {
        this.areaBlocks.add(ingredient);
        return this;
    }

    public ProductionRecipeBuilder blockCount(int count) {
        this.blockCount = count;
        return this;
    }

    public ProductionRecipeBuilder tick(int tick) {
        this.tick = tick;
        return this;
    }

    public ProductionRecipeBuilder resultItem(ItemStack itemStack) {
        this.resultItems.add(itemStack);
        return this;
    }

    public ProductionRecipeBuilder resultPower(int power) {
        this.resultPower = power;
        return this;
    }

    public ProductionRecipeBuilder resultFluid(FluidStack fluidStack) {
        this.resultFluid = fluidStack;
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        consumer.accept(new FinishedRecipe() {
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

                if (!resultItems.isEmpty()) {
                    JsonArray results = new JsonArray();
                    for (ItemStack item : resultItems) {
                        JsonObject itemJson = new JsonObject();
                        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item.getItem());
                        if (itemId != null) {
                            itemJson.addProperty("item", itemId.toString());
                            itemJson.addProperty("count", item.getCount());
                            results.add(itemJson);
                        }
                    }
                    json.add("result_items", results);
                } else {
                    json.add("result_items", null);
                }

                json.addProperty("result_power", resultPower);

                if (resultFluid != null) {
                    JsonObject fluidJson = new JsonObject();
                    ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(resultFluid.getFluid());
                    if (fluidId != null) {
                        fluidJson.addProperty("fluid", fluidId.toString());
                        fluidJson.addProperty("amount", resultFluid.getAmount());
                        json.add("result_fluid", fluidJson);
                    }
                } else {
                    json.add("result_fluid", null);
                }
            }


            @Override
            public ResourceLocation getId() {
                return id;
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
        });
    }
}
