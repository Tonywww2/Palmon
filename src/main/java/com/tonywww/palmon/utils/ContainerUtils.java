package com.tonywww.palmon.utils;

import com.tonywww.palmon.api.IAbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerUtils {

    public static short splitIntToShortLow(int value) {
        return (short) (value & 0xFFFF);
    }
    public static short splitIntToShortHigh(int value) {
        return (short) ((value >>> 16) & 0xFFFF);
    }

    /**
     * 将2个short合并为一个int，低16位在前，高16位在后
     *
     * @param low  低位short
     * @param high 高位short
     * @return 合并后的int
     */
    public static int combineShortsToInt(short low, short high) {
        return ((high & 0xFFFF) << 16) | (low & 0xFFFF);
    }

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

    public static void layoutPlayerInventory(Inventory playerInventory, IAbstractContainerMenu menu) {
        int i, j;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 9; j++) {
                menu.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 122 + i * 18));
            }
        }

        for (j = 0; j < 9; j++) {
            menu.addSlot(new Slot(playerInventory, j, 8 + j * 18, 180));
        }
    }

}
