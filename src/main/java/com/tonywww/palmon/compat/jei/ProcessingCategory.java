package com.tonywww.palmon.compat.jei;

import com.cobblemon.mod.common.client.gui.TypeIcon;
import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.api.CountableIngredient;
import com.tonywww.palmon.compat.JEITypes;
import com.tonywww.palmon.recipes.ProcessingRecipe;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class ProcessingCategory implements IRecipeCategory<ProcessingRecipe> {
    public static ResourceLocation BG = new ResourceLocation(Palmon.MOD_ID, "textures/gui/processing_jei.png");

    private final IDrawable bg;
    private final IDrawable icon;

    public ProcessingCategory(IGuiHelper helper) {
        this.bg = helper.createDrawable(BG, 0, 0, 192, 152);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ModBlocks.PROCESSING_STATION.get().asItem().getDefaultInstance());

    }

    @Override
    public int getWidth() {
        return 192;
    }

    @Override
    public int getHeight() {
        return 152;
    }

    @Override
    public RecipeType<ProcessingRecipe> getRecipeType() {
        return JEITypes.PROCESSING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.palmon.processing");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ProcessingRecipe recipe, IFocusGroup focuses) {
        var level = Minecraft.getInstance().level;
        assert level != null;

        var areaBlocks = recipe.getAreaBlocks();
        var inputs = recipe.getInputItems();
        var outputs = recipe.getResultItems();

        int x = 99;
        int y = 36;
        OUTER:
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                int index = j + (4 * i);
                if (index >= areaBlocks.size()) break OUTER;
                builder.addSlot(RecipeIngredientRole.INPUT, x + (18 * j), y + (18 * i)).addIngredients(areaBlocks.get(index));

            }
        }

        x = 16;
        y = 81;
        OUTER:
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                int index = j + (4 * i);
                if (index >= inputs.size()) break OUTER;
                CountableIngredient countableIngredient = inputs.get(index);
                ItemStack[] itemStacks = new ItemStack[countableIngredient.getIngredient().getItems().length];

                for (int k = 0; k < countableIngredient.getIngredient().getItems().length; k++) {
                    itemStacks[k] = countableIngredient.getIngredient().getItems()[k].copy();
                    itemStacks[k].setCount(countableIngredient.getCount());
                }
                Ingredient finalIngredient = Ingredient.of(itemStacks);
                builder.addSlot(RecipeIngredientRole.INPUT, x + (18 * j), y + (18 * i)).addIngredients(finalIngredient);

            }
        }

        x = 135;
        y = 81;
        for (int i = 0; i < outputs.size(); i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y + (18 * i)).addItemStack(outputs.get(i));

        }

        FluidStack fluidStack = recipe.getInputFluid();
        if (fluidStack != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, 16, 125).addFluidStack(fluidStack.getFluid(), fluidStack.getAmount());

        }

        if (recipe.getInputEnergy() > 0) {
            builder.addSlot(RecipeIngredientRole.INPUT, 88, 125).addItemStack(ModItems.FE_SYMBOL.get().getDefaultInstance());

        }

    }

    @Override
    public void draw(ProcessingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.bg.draw(guiGraphics);
        Font font = Minecraft.getInstance().font;

        if (recipe.getRequiredType() != null) {
            TypeIcon typeIcon = new TypeIcon(98, 4, recipe.getRequiredType(), null, false, false, 0F, 0F, 1F);
            typeIcon.render(guiGraphics);

        }
        guiGraphics.drawString(font, Component.literal("LV >= " + recipe.getMinLevel()), 117, 9, 3012040);

        guiGraphics.drawString(font, Component.literal("" + recipe.getTick()), 156, 9, 252525);

        guiGraphics.drawString(font, Component.translatable("jei.palmon.area_blocks"), 98, 24, 3012040);
        guiGraphics.drawString(font, Component.literal("* " + recipe.getBlockCount()), 172, 49, 3012040);

        if (recipe.getInputFluid() != null) {
            guiGraphics.drawString(font, Component.literal(recipe.getInputFluid().getAmount() + "mb"), 35, 133, 3012040);

        }
        if (recipe.getInputEnergy() >= 0) {
            guiGraphics.drawString(font, Component.literal(recipe.getInputEnergy() + "FE"), 107, 133, 16711680);

        }

        guiGraphics.drawString(font, Component.literal("HP >= " + recipe.getBaseHP()), 17, 5, 3955720);
        guiGraphics.blit(BG, 17, 5, 0, 164, getBarWidth(recipe.getBaseHP()), 8);
        guiGraphics.drawString(font, Component.literal("ATK >= " + recipe.getBaseATK()), 17, 17, 6764548);
        guiGraphics.blit(BG, 17, 17, 0, 172, getBarWidth(recipe.getBaseATK()), 8);
        guiGraphics.drawString(font, Component.literal("DEF >= " + recipe.getBaseDEF()), 17, 29, 6699796);
        guiGraphics.blit(BG, 17, 29, 0, 180, getBarWidth(recipe.getBaseDEF()), 8);
        guiGraphics.drawString(font, Component.literal("SPA >= " + recipe.getBaseSPA()), 17, 41, 2121322);
        guiGraphics.blit(BG, 17, 41, 0, 188, getBarWidth(recipe.getBaseSPA()), 8);
        guiGraphics.drawString(font, Component.literal("SPD >= " + recipe.getBaseSPD()), 17, 53, 2252146);
        guiGraphics.blit(BG, 17, 53, 0, 196, getBarWidth(recipe.getBaseSPD()), 8);
        guiGraphics.drawString(font, Component.literal("SPE >= " + recipe.getBaseSPE()), 17, 65, 4995679);
        guiGraphics.blit(BG, 17, 65, 0, 204, getBarWidth(recipe.getBaseSPE()), 8);

        int yPos = switch (recipe.getFocusStat()) {
            case HP -> 3;
            case ATTACK -> 15;
            case DEFENCE -> 27;
            case SPECIAL_ATTACK -> 39;
            case SPECIAL_DEFENCE -> 51;
            case SPEED -> 63;
            default -> 0;
        };
        guiGraphics.blit(BG, 15, yPos, 0, 152, 81, 12);

    }

    private int getBarWidth(int stat) {
        return (int) 70d * stat / 255;
    }
}
