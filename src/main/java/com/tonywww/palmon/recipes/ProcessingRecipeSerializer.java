package com.tonywww.palmon.recipes;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.api.CountableIngredient;
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

public class ProcessingRecipeSerializer implements RecipeSerializer<ProcessingRecipe> {
    public static final ProcessingRecipeSerializer INSTANCE = new ProcessingRecipeSerializer();
    public static final ResourceLocation ID = new ResourceLocation(Palmon.MOD_ID, ProcessingRecipe.ProcessingRecipeType.ID);

    public static final int MAX_AREA_BLOCKS = 8;
    public static final int MAX_INPUT_ITEMS = 8;
    public static final int MAX_OUTPUT_ITEMS = 4;

    @Override
    public ProcessingRecipe fromJson(ResourceLocation id, JsonObject json) {
        Stats focusStat = Stats.valueOf(GsonHelper.getAsString(json, "focus_stat"));
        int minLevel = GsonHelper.getAsInt(json, "min_level");

        ElementalType requiredType = null;
        if (!json.get("required_type").isJsonNull()) {
            requiredType = ElementalTypes.INSTANCE.get(GsonHelper.getAsString(json, "required_type"));
        }

        int baseHP = GsonHelper.getAsInt(json, "base_hp");
        int baseATK = GsonHelper.getAsInt(json, "base_atk");
        int baseDEF = GsonHelper.getAsInt(json, "base_def");
        int baseSPA = GsonHelper.getAsInt(json, "base_spa");
        int baseSPD = GsonHelper.getAsInt(json, "base_spd");
        int baseSPE = GsonHelper.getAsInt(json, "base_spe");

        NonNullList<Ingredient> areaBlocks = RecipeUtils.itemsFromJson(GsonHelper.getAsJsonArray(json, "area_blocks"));
        if (areaBlocks.size() > MAX_AREA_BLOCKS) {
            Palmon.getLogger().atError().log("Types of area_blocks should be less than " + MAX_AREA_BLOCKS);
            return null;
        }
        int blockCount = GsonHelper.getAsInt(json, "block_count");

        NonNullList<CountableIngredient> inputItem = NonNullList.create();
        if (!json.get("input_items").isJsonNull()) {
            JsonArray resultJsonArr = GsonHelper.getAsJsonArray(json, "input_items");
            if (resultJsonArr.size() > MAX_INPUT_ITEMS) {
                Palmon.getLogger().atError().log("Types of input_items should be less than " + MAX_OUTPUT_ITEMS);
                return null;
            }
            for (JsonElement i : resultJsonArr) {
                if (i instanceof JsonObject jsonObject) {
                    inputItem.add(new CountableIngredient(Ingredient.fromJson(i), GsonHelper.getAsInt(jsonObject, "count")));

                }

            }
        }

        int inputPower = GsonHelper.getAsInt(json, "input_power");

        FluidStack inputFluid = null;
        if (!json.get("input_fluid").isJsonNull()) {
            JsonElement fluidObject = GsonHelper.getAsJsonObject(json, "input_fluid");
            String fluidName = GsonHelper.getAsString(fluidObject.getAsJsonObject(), "fluid");
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
            if (fluid != null) {
                inputFluid = new FluidStack(fluid,
                        GsonHelper.getAsInt(fluidObject.getAsJsonObject(), "amount"));
            }
        }

        int tick = GsonHelper.getAsInt(json, "tick");

        NonNullList<ItemStack> resultItem = NonNullList.create();
        if (!json.get("result_items").isJsonNull()) {
            JsonArray resultJsonArr = GsonHelper.getAsJsonArray(json, "result_items");
            if (resultJsonArr.size() > MAX_OUTPUT_ITEMS) {
                Palmon.getLogger().atError().log("Types of result_items should be less than " + MAX_OUTPUT_ITEMS);
                return null;
            }
            for (JsonElement i : resultJsonArr) {
                if (i instanceof JsonObject jsonObject) {
                    resultItem.add(ShapedRecipe.itemStackFromJson(jsonObject));

                }

            }
        }

        return new ProcessingRecipe(id, focusStat, minLevel, requiredType,
                baseHP, baseATK, baseDEF, baseSPA, baseSPD, baseSPE,
                areaBlocks, blockCount, inputItem, inputPower, inputFluid, tick,
                resultItem);
    }

    @Override
    public @Nullable ProcessingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
//        Stats focusStat = Stats.valueOf(buffer.readResourceLocation().toString());
        Stats focusStat = buffer.readEnum(Stats.class);
        int minLevel = buffer.readVarInt();

        // 1
        ElementalType requiredType = null;
        if (buffer.readBoolean()) {
            requiredType = ElementalTypes.INSTANCE.get(buffer.readUtf(128));
//            requiredType = ElementalTypes.INSTANCE.get(RecipeUtils.readUtf(buffer.readByteArray()));
        }

        int baseHP = buffer.readVarInt();
        int baseATK = buffer.readVarInt();
        int baseDEF = buffer.readVarInt();
        int baseSPA = buffer.readVarInt();
        int baseSPD = buffer.readVarInt();
        int baseSPE = buffer.readVarInt();

        int size = buffer.readVarInt();
        NonNullList<Ingredient> areaBlocks = NonNullList.create();
        for (int i = 0; i < size; i++) {
            areaBlocks.add(Ingredient.fromNetwork(buffer));
        }
        int blockCount = buffer.readVarInt();

        int size1 = buffer.readVarInt();
        NonNullList<CountableIngredient> inputItems = NonNullList.create();
        for (int i = 0; i < size1; i++) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int count = buffer.readVarInt();

            inputItems.add(new CountableIngredient(ingredient, count));
        }

        int inputPower = buffer.readVarInt();

        FluidStack inputFluid = null;
        if (buffer.readBoolean()) {
            inputFluid = buffer.readFluidStack();

        }

        int tick = buffer.readInt();

        // result
        int size2 = buffer.readVarInt();
        NonNullList<ItemStack> resultItem = NonNullList.create();
        for (int i = 0; i < size2; i++) {
            resultItem.add(buffer.readItem());
        }


        return new ProcessingRecipe(id, focusStat, minLevel, requiredType,
                baseHP, baseATK, baseDEF, baseSPA, baseSPD, baseSPE,
                areaBlocks, blockCount, inputItems, inputPower, inputFluid, tick,
                resultItem);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, ProcessingRecipe recipe) {
//        buffer.writeResourceLocation(recipe.getFocusStat().getIdentifier());
        buffer.writeEnum(recipe.getFocusStat());
        buffer.writeVarInt(recipe.getMinLevel());

        // 1
        if (recipe.getRequiredType() == null) {
            buffer.writeBoolean(false);

        } else {
            buffer.writeBoolean(true);
            buffer.writeUtf(recipe.getRequiredType().getName(), 128);
//            byte[] arr = RecipeUtils.writeUtf(recipe.getRequiredType().getName());
//            buffer.writeByteArray(arr);
        }

        buffer.writeVarInt(recipe.getBaseHP());
        buffer.writeVarInt(recipe.getBaseATK());
        buffer.writeVarInt(recipe.getBaseDEF());
        buffer.writeVarInt(recipe.getBaseSPA());
        buffer.writeVarInt(recipe.getBaseSPD());
        buffer.writeVarInt(recipe.getBaseSPE());

        buffer.writeVarInt(recipe.getAreaBlocks().size());
        for (Ingredient i : recipe.getAreaBlocks()) {
            i.toNetwork(buffer);
        }
        buffer.writeVarInt(recipe.getBlockCount());

        buffer.writeVarInt(recipe.getInputItems().size());
        for (CountableIngredient i : recipe.getInputItems()) {
            i.getIngredient().toNetwork(buffer);
            buffer.writeVarInt(i.getCount());
        }
        buffer.writeVarInt(recipe.getInputEnergy());

        // has input fluid
        if (recipe.getInputFluid() == null) {
            buffer.writeBoolean(false);

        } else {
            buffer.writeBoolean(true);
            buffer.writeFluidStack(recipe.getInputFluid());

        }

        buffer.writeInt(recipe.getTick());

        // result
        buffer.writeVarInt(recipe.getResultItems().size());
        for (ItemStack i : recipe.getResultItems()) {
            buffer.writeItemStack(i, false);
        }

    }
}
