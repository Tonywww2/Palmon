package com.tonywww.palmon.menu;

import com.tonywww.palmon.block.entites.ProductionMachineEntity;
import com.tonywww.palmon.registeries.ModBlocks;
import com.tonywww.palmon.registeries.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.Objects;

public class ProductionMachineContainer extends AbstractContainerMenu {

    private final ProductionMachineEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    private final IItemHandler playerInventory;

    private final ContainerData data;

    public ProductionMachineContainer(int id, Inventory playerInventory, ProductionMachineEntity blockEntity, ContainerData dataAccess) {
        super(ModMenus.PRODUCTION_MACHINE_CONTAINER.get(), id);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.playerInventory = new InvWrapper(playerInventory);

        this.data = dataAccess;

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            int i, j;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 6; j++) {
                    addSlot(new SlotItemHandler(h, j + i * 6, 35 + j * 18, 51 + i * 18));
                }
            }

            addDataSlots(data);
        });

        layoutPlayerInventory(playerInventory);

    }

    private void layoutPlayerInventory(Inventory playerInventory) {
        int i, j;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 122 + i * 18));
            }
        }

        for (j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 180));
        }
    }

    public ProductionMachineContainer(final int id,
                                      final Inventory playerInventory,
                                      final FriendlyByteBuf data) {
        this(id, playerInventory, getTileEntity(playerInventory, data), new SimpleContainerData(8));

    }

    private static ProductionMachineEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof ProductionMachineEntity tile) {
            return tile;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotNumber) {
        int invCount = 18;
        var itemstack = ItemStack.EMPTY;
        var slot = this.slots.get(slotNumber);

        if (slot.hasItem()) {
            var itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (slotNumber == 0) {
                if (!this.moveItemStackTo(itemstack1, invCount, invCount + 36, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (slotNumber >= invCount && slotNumber < invCount + 36) {
                if (!this.moveItemStackTo(itemstack1, 0, invCount, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, invCount, invCount + 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, ModBlocks.PRODUCTION_MACHINE.get());
    }

    public ContainerData getData() {
        return data;
    }


    public ProductionMachineEntity getBlockEntity() {
        return blockEntity;
    }
}
