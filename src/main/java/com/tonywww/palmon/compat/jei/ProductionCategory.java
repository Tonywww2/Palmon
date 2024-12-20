package com.tonywww.palmon.compat.jei;

import com.cobblemon.mod.common.client.gui.TypeIcon;
import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.compat.JEITypes;
import com.tonywww.palmon.recipes.ProductionRecipe;
import com.tonywww.palmon.registeries.ModBlocks;
import com.tonywww.palmon.registeries.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class ProductionCategory implements IRecipeCategory<ProductionRecipe> {
    public static ResourceLocation BG = new ResourceLocation(Palmon.MOD_ID, "textures/gui/production_jei.png");

    private final IDrawable bg;
    private final IDrawable icon;

    public ProductionCategory(IGuiHelper helper) {
        this.bg = helper.createDrawable(BG, 0, 0, 192, 128);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ModBlocks.PRODUCTION_MACHINE.get().asItem().getDefaultInstance());

    }

    @Override
    public int getWidth() {
        return 192;
    }

    @Override
    public int getHeight() {
        return 128;
    }

    @Override
    public RecipeType<ProductionRecipe> getRecipeType() {
        return JEITypes.PRODUCTION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.palmon.production");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ProductionRecipe recipe, IFocusGroup focuses) {
        var level = Minecraft.getInstance().level;
        assert level != null;

        var inputs = recipe.getAreaBlocks();
        var outputs = recipe.getResultItems();

        int x = 99;
        int y = 35;
        OUTER:
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                int index = j + (4 * i);
                if (index >= inputs.size()) break OUTER;
                builder.addSlot(RecipeIngredientRole.INPUT, x + (18 * j), y + (18 * i)).addIngredients(inputs.get(index));

            }
        }

        x = 16;
        y = 81;
        for (int i = 0; i < outputs.size(); i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y + (18 * i)).addItemStack(outputs.get(i));

        }

        FluidStack fluidStack = recipe.getResultFluid();
        if (fluidStack != null) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 16, 101).addFluidStack(fluidStack.getFluid(), fluidStack.getAmount());

        }

        if (recipe.getResultPower() > 0) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 101).addItemStack(ModItems.FE_SYMBOL.get().getDefaultInstance());

        }

    }

    @Override
    public void draw(ProductionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.bg.draw(guiGraphics);
        Font font = Minecraft.getInstance().font;

        if (recipe.getRequiredType() != null) {
            TypeIcon typeIcon = new TypeIcon(98, 4, recipe.getRequiredType(), null, false, false, 0F, 0F, 1F);
            typeIcon.render(guiGraphics);

        }
        guiGraphics.drawString(font, Component.literal("LV >= " + recipe.getMinLevel()), 117, 9, 3012040);

        guiGraphics.drawString(font, Component.translatable("jei.palmon.area_blocks"), 98, 24, 3012040);
        guiGraphics.drawString(font, Component.literal("* " + recipe.getBlockCount()), 172, 48, 3012040);

        if (recipe.getResultFluid() != null) {
            guiGraphics.drawString(font, Component.literal(recipe.getResultFluid().getAmount() + "mb"), 35, 109, 3012040);

        }
        if (recipe.getResultPower() >= 0) {
            guiGraphics.drawString(font, Component.literal(recipe.getResultPower() + "FE"), 107, 109, 16711680);

        }

        guiGraphics.drawString(font, Component.literal("HP >= " + recipe.getBaseHP()), 17, 5, 3955720);
        guiGraphics.blit(BG, 17, 5, 0, 140, getBarWidth(recipe.getBaseHP()), 8);
        guiGraphics.drawString(font, Component.literal("ATK >= " + recipe.getBaseATK()), 17, 17, 6764548);
        guiGraphics.blit(BG, 17, 17, 0, 148, getBarWidth(recipe.getBaseATK()), 8);
        guiGraphics.drawString(font, Component.literal("DEF >= " + recipe.getBaseDEF()), 17, 29, 6699796);
        guiGraphics.blit(BG, 17, 29, 0, 156, getBarWidth(recipe.getBaseDEF()), 8);
        guiGraphics.drawString(font, Component.literal("SPA >= " + recipe.getBaseSPA()), 17, 41, 2121322);
        guiGraphics.blit(BG, 17, 41, 0, 164, getBarWidth(recipe.getBaseSPA()), 8);
        guiGraphics.drawString(font, Component.literal("SPD >= " + recipe.getBaseSPD()), 17, 53, 2252146);
        guiGraphics.blit(BG, 17, 53, 0, 172, getBarWidth(recipe.getBaseSPD()), 8);
        guiGraphics.drawString(font, Component.literal("SPE >= " + recipe.getBaseSPE()), 17, 65, 4995679);
        guiGraphics.blit(BG, 17, 65, 0, 180, getBarWidth(recipe.getBaseSPE()), 8);

    }

    private int getBarWidth(int stat) {
        return (int) 70d * stat / 255;
    }
}
