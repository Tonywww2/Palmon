package com.tonywww.palmon.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public final class CuriosUtils {
    /**
     * From MagicHarp/confluence
     */
    public static boolean noSameCurio(LivingEntity living, Predicate<ItemStack> predicate) {
        AtomicBoolean isEmpty = new AtomicBoolean(true);
        CuriosApi.getCuriosInventory(living).ifPresent(handler -> {
            for (ICurioStacksHandler curioStacksHandler : handler.getCurios().values()) {
                IDynamicStackHandler stackHandler = curioStacksHandler.getStacks();
                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    if (!stack.isEmpty() && predicate.test(stack)) {
                        isEmpty.set(false);
                        return;
                    }
                }
            }
        });
        return isEmpty.get();
    }

    /**
     * Returns an value of [0-1] * multiplier
     *
     * @param iv iv of Pokemon, 0-31
     * @param ev ev of Pokemon, 0-252
     * @param lv level of Pokemon,1-100
     * @param multiplier custom final multiplier
     * @return
     */
    public static double computeMultiplier(int iv, int ev, int lv, double multiplier) {
        double ivMultiplier = 1d * iv * iv / 961d; // (iv / 31) ^ 2
        double evMultiplier = 1d * ev / 150d; // ev / 150
        double lvMultiplier = 1d + (4d * lv * lv / 10000d); // 1 + 4 * (lv / 100) ^ 2

        return (ivMultiplier + evMultiplier) * lvMultiplier * multiplier / 10d;

    }

}