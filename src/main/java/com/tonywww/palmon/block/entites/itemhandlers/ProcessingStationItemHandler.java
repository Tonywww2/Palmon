package com.tonywww.palmon.block.entites.itemhandlers;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProcessingStationItemHandler implements IItemHandler {

    private final IItemHandler itemHandler;
    private final Direction side;

    public ProcessingStationItemHandler(IItemHandler itemHandler, @Nullable Direction side) {
        this.itemHandler = itemHandler;
        this.side = side;
    }

    @Override
    public int getSlots() {
        return this.itemHandler.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int i) {
        return this.itemHandler.getStackInSlot(i);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack itemStack, boolean simulate) {
        if (this.side == Direction.UP || this.side == null) {
            if (slot < 8) {
                return this.itemHandler.insertItem(slot, itemStack, simulate);
            }
        }
        return itemStack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean bl) {
        if (this.side == null) {
            return this.itemHandler.extractItem(slot, amount, bl);
        }

        if (this.side == Direction.UP) {
            if (slot < 8) {
                return this.itemHandler.extractItem(slot, amount, bl);
            }
        }

        if (this.side == Direction.DOWN) {
            if (slot >= 8) {
                return this.itemHandler.extractItem(slot, amount, bl);
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int i) {
        return this.itemHandler.getSlotLimit(i);
    }

    @Override
    public boolean isItemValid(int i, @NotNull ItemStack arg) {
        return this.itemHandler.isItemValid(i, arg);
    }
}
