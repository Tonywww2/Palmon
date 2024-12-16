package com.tonywww.block.entites;

import com.tonywww.menu.WorkingStationContainer;
import com.tonywww.registeries.ModBlockEntities;
import com.tonywww.registeries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class WorkingStationEntity extends SyncedBlockEntity implements MenuProvider {
    public final ItemStackHandler itemStackHandler = createHandler();
    private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> itemStackHandler);

    public WorkingStationEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WORKING_STATION_BLOCK_ENTITY.get(), pos, state);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                inventoryChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return (slot == 0 && stack.is(ModItems.LABOR_CONTRACT.get()));
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValid(slot, stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Override
    public void load(CompoundTag compoundNBT) {
        itemStackHandler.deserializeNBT(compoundNBT.getCompound("inv"));
        super.load(compoundNBT);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.put("inv", itemStackHandler.serializeNBT());
        super.saveAdditional(compound);
    }

    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < itemStackHandler.getSlots(); ++i) {
            drops.add(itemStackHandler.getStackInSlot(i));
        }
        return drops;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.handler.cast();

        }
        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.palmon.working_station");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new WorkingStationContainer(id, inventory, this);
    }
}
