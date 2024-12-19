package com.tonywww.palmon.recipes;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.utils.RecipeUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class ProductionRecipeSerializer implements RecipeSerializer<ProductionRecipe> {
    public static final ProductionRecipeSerializer INSTANCE = new ProductionRecipeSerializer();
    public static final ResourceLocation ID = new ResourceLocation(Palmon.MOD_ID, ProductionRecipe.ProductionRecipeType.ID);


    @Override
    public ProductionRecipe fromJson(ResourceLocation id, JsonObject json) {
        Stats focusStat = Stats.valueOf(GsonHelper.getAsString(json, "focus_stat"));
        int minLevel = GsonHelper.getAsInt(json, "min_level");

        ElementalType requiredType = null;
        if (!json.get("required_type").isJsonNull()) {
            requiredType = ElementalTypes.INSTANCE.get(GsonHelper.getAsString(json, "required_type"));
        }

        int evHP = GsonHelper.getAsInt(json, "ev_hp");
        int evATK = GsonHelper.getAsInt(json, "ev_atk");
        int evDEF = GsonHelper.getAsInt(json, "ev_def");
        int evSPA = GsonHelper.getAsInt(json, "ev_spa");
        int evSPD = GsonHelper.getAsInt(json, "ev_spd");
        int evSPE = GsonHelper.getAsInt(json, "ev_spe");

        NonNullList<Ingredient> areaBlocks = RecipeUtils.itemsFromJson(GsonHelper.getAsJsonArray(json, "area_blocks"));
        int blockCount = GsonHelper.getAsInt(json, "block_count");

        int tick = GsonHelper.getAsInt(json, "tick");

        NonNullList<ItemStack> resultItem = NonNullList.create();
        if (!json.get("result_items").isJsonNull()) {
            JsonElement resultsArr = GsonHelper.getAsJsonArray(json, "result_items");
            for (JsonElement i : resultsArr.getAsJsonArray()) {
                if (i instanceof JsonObject jsonObject) {
                    resultItem.add(ShapedRecipe.itemStackFromJson(jsonObject));

                }

            }
        }

        int resultPower = GsonHelper.getAsInt(json, "result_power");
        FluidStack resultFluid = null;
        if (!json.get("result_fluid").isJsonNull()) {
            JsonElement fluidObject = GsonHelper.getAsJsonObject(json, "result_fluid");
            String fluidName = GsonHelper.getAsString(fluidObject.getAsJsonObject(), "fluid");
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
            if (fluid != null) {
                resultFluid = new FluidStack(fluid,
                        GsonHelper.getAsInt(fluidObject.getAsJsonObject(), "amount"));
            }
        }

        return new ProductionRecipe(id, focusStat, minLevel, requiredType,
                evHP, evATK, evDEF, evSPA, evSPD, evSPE,
                areaBlocks, blockCount, tick,
                resultItem, resultPower, resultFluid);
    }

    @Override
    public @Nullable ProductionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        Stats focusStat = Stats.valueOf(buffer.readResourceLocation().toString());
        int minLevel = buffer.readVarInt();

        ElementalType requiredType = ElementalTypes.INSTANCE.get(buffer.readUtf());

        int evHP = buffer.readVarInt();
        int evATK = buffer.readVarInt();
        int evDEF = buffer.readVarInt();
        int evSPA = buffer.readVarInt();
        int evSPD = buffer.readVarInt();
        int evSPE = buffer.readVarInt();

        int size = buffer.readVarInt();
        NonNullList<Ingredient> areaBlocks = NonNullList.create();
        for (int i = 0; i < size; i++) {
            areaBlocks.add(Ingredient.fromNetwork(buffer));
        }
        int blockCount = buffer.readVarInt();

        int tick = buffer.readInt();

        // result
        int size2 = buffer.readVarInt();
        NonNullList<ItemStack> resultItem = NonNullList.create();
        for (int i = 0; i < size2; i++) {
            resultItem.add(buffer.readItem());
        }
        int resultPower = buffer.readVarInt();
        FluidStack resultFluid = buffer.readFluidStack();


        return new ProductionRecipe(id, focusStat, minLevel, requiredType,
                evHP, evATK, evDEF, evSPA, evSPD, evSPE,
                areaBlocks, blockCount, tick,
                resultItem, resultPower, resultFluid);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, ProductionRecipe recipe) {
        buffer.writeResourceLocation(recipe.getFocusStat().getIdentifier());
        buffer.writeVarInt(recipe.getMinLevel());

        buffer.writeUtf(recipe.getRequiredType().getName());

        buffer.writeVarInt(recipe.getEvHP());
        buffer.writeVarInt(recipe.getEvATK());
        buffer.writeVarInt(recipe.getEvDEF());
        buffer.writeVarInt(recipe.getEvSPA());
        buffer.writeVarInt(recipe.getEvSPD());
        buffer.writeVarInt(recipe.getEvSPE());

        buffer.writeVarInt(recipe.getAreaBlocks().size());
        for (Ingredient i : recipe.getAreaBlocks()) {
            i.toNetwork(buffer);
        }
        buffer.writeVarInt(recipe.getBlockCount());

        buffer.writeInt(recipe.getTick());

        // result
        buffer.writeVarInt(recipe.getResultItems().size());
        for (ItemStack i : recipe.getResultItems()) {
            buffer.writeItemStack(i, false);
        }
        buffer.writeVarInt(recipe.getResultPower());
        buffer.writeFluidStack(recipe.getResultFluid());

    }
}
