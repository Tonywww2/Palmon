package com.tonywww.palmon.api;

import net.minecraft.world.item.crafting.Ingredient;

public class CountableIngredient {
    private Ingredient ingredient;
    private int count;
    public CountableIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getCount() {
        return count;
    }
}
