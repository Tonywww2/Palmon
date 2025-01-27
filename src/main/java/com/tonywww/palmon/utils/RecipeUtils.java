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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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

    public static void insertListToHandler(NonNullList<ItemStack> list, ItemStackHandler handler, int startIndex, int endIndex) {
        for (ItemStack stack : list) {
            stack = stack.copy();
            for (int i = startIndex; !stack.isEmpty() && i < endIndex; i++) {
                ItemStack stackInSlot = handler.getStackInSlot(i);
                if (stackInSlot.isEmpty()) {
                    handler.setStackInSlot(i, stack);
                    break;
                }
                if (stackInSlot.is(stack.getItem())) {
                    int exec = stackInSlot.getCount() + stack.getCount() - stackInSlot.getMaxStackSize();
                    if (exec <= 0) {
                        stackInSlot.grow(stack.getCount());
                        break;

                    } else {
                        stackInSlot.setCount(stackInSlot.getMaxStackSize());
                        stack.setCount(exec);

                    }
                }

            }
        }
    }

    // Writing UTF-8 string to byte array
    public static byte[] writeUtf(String data) {
        byte[] utf8Bytes = data.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + utf8Bytes.length); // 4 bytes for length + string bytes

        buffer.putInt(utf8Bytes.length); // First store the length of the string
        buffer.put(utf8Bytes);           // Then store the actual UTF-8 bytes

        return buffer.array(); // Return byte array
    }

    // Reading UTF-8 string from byte array
    public static String readUtf(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        int length = buffer.getInt();  // Read the length of the string
        byte[] utf8Bytes = new byte[length];
        buffer.get(utf8Bytes);         // Read the actual string bytes

        return new String(utf8Bytes, StandardCharsets.UTF_8); // Convert bytes to string
    }
}
