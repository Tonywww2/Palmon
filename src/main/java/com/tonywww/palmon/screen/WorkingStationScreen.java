package com.tonywww.palmon.screen;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.block.entites.WorkingStationEntityPokemon;
import com.tonywww.palmon.menu.WorkingStationContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

public class WorkingStationScreen extends AbstractContainerScreen<WorkingStationContainer> {

    private final ResourceLocation GUI = new ResourceLocation(Palmon.MOD_ID, "textures/gui/working_station_gui.png");

    public WorkingStationScreen(WorkingStationContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
        this.renderTooltip(guiGraphics, pMouseX, pMouseY);

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTicks, int pX, int pY) {
        if (this.minecraft == null) return;

        ScreenUtils.init(GUI);

        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(GUI, i - 14, j - 23, 0, 0, 183, 220);

        ContainerData data = this.getMenu().getData();
        guiGraphics.blit(GUI, i + 56, j + 86, 0, 220, (int) (67d * data.get(0) / WorkingStationEntityPokemon.MAX_FOOD), 6);

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pX, int pY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX + 52, this.titleLabelY - 18, 3012040, false);

        ContainerData data = this.getMenu().getData();
        guiGraphics.drawString(this.font, data.get(0) + "/ " + WorkingStationEntityPokemon.MAX_FOOD, this.titleLabelX + 48, this.titleLabelY + 88, 16769411, false);
    }
}
