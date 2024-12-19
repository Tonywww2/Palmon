package com.tonywww.palmon.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.palmon.registeries.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.items.ItemStackHandler;

import static net.minecraft.util.GsonHelper.convertToJsonObject;

public class RecipeUtils {

    public static NonNullList<Ingredient> itemsFromJson(JsonArray array) {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        for (int i = 0; i < array.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(array.get(i));
            JsonObject obj = convertToJsonObject(array.get(i), "item");
            if (obj.has("item")) {
                Item item = ShapedRecipe.itemFromJson(obj);
                if (item == ModItems.EMPTY.get()) {
                    continue;
                }
            }
            if (!ingredient.isEmpty()) {
                nonnulllist.add(ingredient);

            }

        }

        return nonnulllist;
    }

    public static void insertListToHandler(NonNullList<ItemStack> list, ItemStackHandler handler) {
        for (ItemStack stack : list) {
            stack = stack.copy();
            for (int i = 0; !stack.isEmpty() && i < handler.getSlots(); i++) {
                stack = handler.insertItem(i, stack, false);

            }
        }
    }
}
