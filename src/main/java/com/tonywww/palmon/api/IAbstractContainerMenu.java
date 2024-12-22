package com.tonywww.palmon.api;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public abstract class IAbstractContainerMenu extends AbstractContainerMenu {
    protected IAbstractContainerMenu(@Nullable MenuType<?> arg, int i) {
        super(arg, i);
    }

    @Override
    public boolean moveItemStackTo(ItemStack arg, int k, int l, boolean bl) {
        return super.moveItemStackTo(arg, k, l, bl);
    }
}
