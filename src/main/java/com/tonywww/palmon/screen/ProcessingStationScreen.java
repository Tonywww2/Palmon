package com.tonywww.palmon.screen;

import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.block.entites.ProcessingStationEntity;
import com.tonywww.palmon.menu.ProcessingStationContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ProcessingStationScreen extends AbstractContainerScreen<ProcessingStationContainer> {

    private final ResourceLocation GUI = new ResourceLocation(Palmon.MOD_ID, "textures/gui/processing_station_gui.png");

    public static final int FLUID_TANK_X = 15;
    public static final int FLUID_TANK_Y = 51;
    public static final int FLUID_TANK_HEIGHT = 52;

    public static final int ENERGY_TANK_X = -6;
    public static final int ENERGY_TANK_Y = 51;
    public static final int ENERGY_TANK_HEIGHT = 52;

    public ProcessingStationScreen(ProcessingStationContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
        this.renderTooltip(guiGraphics, pMouseX, pMouseY);

        if (isHovering(ENERGY_TANK_X, ENERGY_TANK_Y, 16, ENERGY_TANK_HEIGHT, pMouseX, pMouseY)) {
            Component component = Component.literal("(%s/%sFE)".formatted(this.menu.getData().get(7), this.menu.getBlockEntity().energyStorage.getMaxEnergyStored()));

            guiGraphics.renderTooltip(this.font, component, pMouseX, pMouseY);

        }

        FluidTank tank = this.menu.getBlockEntity().fluidTank;
        FluidStack fluidStack = tank.getFluid();
        if (isHovering(FLUID_TANK_X, FLUID_TANK_Y, 16, FLUID_TANK_HEIGHT, pMouseX, pMouseY)) {
            Component component = MutableComponent.create(fluidStack.getDisplayName().getContents())
                    .append(" (%s/%smb)".formatted(fluidStack.getAmount(), tank.getCapacity()));

            guiGraphics.renderTooltip(this.font, component, pMouseX, pMouseY);

        }

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTicks, int pX, int pY) {
        if (this.minecraft == null) return;

        ScreenUtils.init(GUI);

        guiGraphics.blit(GUI, this.leftPos - 14, this.topPos - 23, 0, 0, 202, 220);

        FluidTank tank = this.menu.getBlockEntity().fluidTank;
        FluidStack fluidStack = tank.getFluid();
        if (!fluidStack.isEmpty()) {
            int fluidHeight = getFluidHeight(tank);
            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            ResourceLocation texture = extensions.getStillTexture(fluidStack);
            if (texture != null) {
                TextureAtlasSprite sprite = this.minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
                int tColor = extensions.getTintColor(fluidStack);
                float a = (((tColor >> 24)) & 0xFF) / 255f;
                float r = (((tColor >> 16)) & 0xFF) / 255f;
                float g = (((tColor >> 8)) & 0xFF) / 255f;
                float b = ((tColor) & 0xFF) / 255f;

                guiGraphics.setColor(r, g, b, a);
                guiGraphics.blit(this.leftPos + FLUID_TANK_X, getFluidY(fluidHeight), 0, 16, fluidHeight, sprite);
                guiGraphics.setColor(1f, 1f, 1f, 1f);

            }

        }

        ContainerData data = this.menu.getData();
        int energyHeight = getEnergyHeight(data.get(7), this.menu.getBlockEntity().energyStorage.getMaxEnergyStored());
        guiGraphics.blit(GUI, this.leftPos + ENERGY_TANK_X, getEnergyY(energyHeight), 204, ENERGY_TANK_HEIGHT - energyHeight + 1, 16, energyHeight);

        guiGraphics.blit(GUI, this.leftPos + 37, this.topPos + 92, 0, 220, (int) (125d * decodeData(data, 5) / decodeData(data, 6)), 4);


    }

    private int getEnergyHeight(int energy, int max) {
        return (int) (1.0f * ENERGY_TANK_HEIGHT * energy / max);
    }

    private int getEnergyY(int height) {
        return this.topPos + ENERGY_TANK_Y + (ENERGY_TANK_HEIGHT - height);

    }

    private int getFluidHeight(FluidTank tank) {
        return (int) (1.0f * FLUID_TANK_HEIGHT * tank.getFluidAmount() / tank.getCapacity());
    }

    private int getFluidY(int height) {
        return this.topPos + FLUID_TANK_Y + (FLUID_TANK_HEIGHT - height);

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pX, int pY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY - 22, 3012040, false);
        ContainerData data = this.getMenu().getData();

        guiGraphics.drawString(this.font, Component.translatable("ui.palmon.boost_multiplier"), this.titleLabelX, -3, 3012040, false);
        guiGraphics.drawString(this.font, Component.literal(String.valueOf(decodeData(data, 0))), this.titleLabelX + 78, -3, 3012040, false);

        guiGraphics.drawString(this.font, Component.translatable("ui.palmon.level_multiplier"), this.titleLabelX, 10, 3012040, false);
        guiGraphics.drawString(this.font, Component.literal(String.valueOf(decodeData(data, 1))), this.titleLabelX + 78, 10, 3012040, false);

        guiGraphics.drawString(this.font, Component.translatable("ui.palmon.stats_multiplier"), this.titleLabelX, 23, 3012040, false);
        guiGraphics.drawString(this.font, Component.literal(String.valueOf(decodeData(data, 2))), this.titleLabelX + 78, 23, 3012040, false);

        guiGraphics.drawString(this.font, Component.translatable("ui.palmon.focus_multiplier"), this.titleLabelX, 36, 3012040, false);
        guiGraphics.drawString(this.font, Component.literal(String.valueOf(decodeData(data, 3))), this.titleLabelX + 78, 36, 3012040, false);

        guiGraphics.drawString(this.font, Component.translatable("ui.palmon.overall_multiplier"), this.titleLabelX + 111, this.titleLabelY - 22, 3012040, false);
        guiGraphics.drawString(this.font, Component.literal(String.valueOf(decodeData(data, 4))), this.titleLabelX + 111, this.titleLabelY - 10, 3012040, false);

        guiGraphics.drawString(this.font, Component.literal(decodeData(data, 5) + "/ " + decodeData(data, 6)), this.titleLabelX + 111, this.titleLabelY, 3012040, false);

    }

    double decodeData(ContainerData data, int index) {
        return data.get(index) / ProcessingStationEntity.ACCURACY;

    }

}
