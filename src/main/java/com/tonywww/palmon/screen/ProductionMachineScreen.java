package com.tonywww.palmon.screen;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.menu.ProductionMachineContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ProductionMachineScreen extends AbstractContainerScreen<ProductionMachineContainer> {

    private final ResourceLocation GUI = new ResourceLocation(Palmon.MOD_ID, "textures/gui/production_machine_gui.png");

    public ProductionMachineScreen(ProductionMachineContainer container, Inventory playerInventory, Component title) {
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
        guiGraphics.blit(GUI, i - 14, j - 23, 0, 0, 203, 222);

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pX, int pY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY - 22, 3012040, false);
    }
}
