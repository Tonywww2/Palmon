package com.tonywww.palmon.utils;

import com.tonywww.palmon.api.IAbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerUtils {
    public static @NotNull ItemStack quickMoveHelper(IAbstractContainerMenu menu, Player player, int slotNumber, int invCount) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = menu.slots.get(slotNumber);

        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemstack = itemStack1.copy();

            if (slotNumber == 0) {
                if (!menu.moveItemStackTo(itemStack1, invCount, invCount + 36, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemStack1, itemstack);
            } else if (slotNumber >= invCount && slotNumber < invCount + 36) {
                if (!menu.moveItemStackTo(itemStack1, 0, invCount, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!menu.moveItemStackTo(itemStack1, invCount, invCount + 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack1);
        }

        return itemstack;
    }
}
