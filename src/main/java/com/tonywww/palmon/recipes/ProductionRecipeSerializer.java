package com.tonywww.palmon.recipes;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.google.gson.JsonArray;
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

    public static final int MAX_AREA_BLOCKS = 8;
    public static final int MAX_OUTPUT_ITEMS = 8;

    @Override
    public ProductionRecipe fromJson(ResourceLocation id, JsonObject json) {
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
                baseHP, baseATK, baseDEF, baseSPA, baseSPD, baseSPE,
                areaBlocks, blockCount, tick,
                resultItem, resultPower, resultFluid);
    }

    @Override
    public @Nullable ProductionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        Stats focusStat = Stats.valueOf(buffer.readResourceLocation().toString());
        int minLevel = buffer.readVarInt();

        ElementalType requiredType = ElementalTypes.INSTANCE.get(buffer.readUtf());

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
                baseHP, baseATK, baseDEF, baseSPA, baseSPD, baseSPE,
                areaBlocks, blockCount, tick,
                resultItem, resultPower, resultFluid);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, ProductionRecipe recipe) {
        buffer.writeResourceLocation(recipe.getFocusStat().getIdentifier());
        buffer.writeVarInt(recipe.getMinLevel());

        buffer.writeUtf(recipe.getRequiredType().getName());

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
