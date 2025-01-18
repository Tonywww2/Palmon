package com.tonywww.palmon.curios;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosCapability;

public class LaborContractCapProvider implements ICapabilityProvider {

    private final ItemStack stack;
    private final LaborContractCuriosWrapper curiosInstance;

    public LaborContractCapProvider(ItemStack stack) {
        this.stack = stack;
        this.curiosInstance = new LaborContractCuriosWrapper(stack);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == CuriosCapability.ITEM) {
            return LazyOptional.of(this::getCuriosInstance).cast();
        }

        return LazyOptional.empty();
    }

    public ItemStack getStack() {
        return stack;
    }

    public LaborContractCuriosWrapper getCuriosInstance() {
        return curiosInstance;
    }
}
