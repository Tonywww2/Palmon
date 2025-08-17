package com.tonywww.palmon.menu;

import com.tonywww.palmon.api.IAbstractContainerMenu;
import com.tonywww.palmon.block.entites.WorkingStationEntity;
import com.tonywww.palmon.registeries.ModBlocks;
import com.tonywww.palmon.registeries.ModMenus;
import com.tonywww.palmon.utils.ContainerUtils;
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

import static com.tonywww.palmon.utils.ContainerUtils.quickMoveHelper;

public class WorkingStationContainer extends IAbstractContainerMenu {

    private final BlockEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    private final IItemHandler playerInventory;

    private final ContainerData data;

    public WorkingStationContainer(int id, Inventory playerInventory, WorkingStationEntity blockEntity, ContainerData dataAccess) {
        super(ModMenus.WORKING_STATION_CONTAINER.get(), id);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.playerInventory = new InvWrapper(playerInventory);

        this.data = dataAccess;

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            addSlot(new SlotItemHandler(h, 0, 56, -2));
            addSlot(new SlotItemHandler(h, 1, 56, 66));

            addDataSlots(data);
        });

        ContainerUtils.layoutPlayerInventory(playerInventory, this);

    }

    public WorkingStationContainer(final int id,
                                   final Inventory playerInventory,
                                   final FriendlyByteBuf data) {
        this(id, playerInventory, getTileEntity(playerInventory, data), new SimpleContainerData(1));

    }

    private static WorkingStationEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof WorkingStationEntity tile) {
            return tile;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotNumber) {
        int invCount = 2;
        return quickMoveHelper(this, player, slotNumber, invCount);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, ModBlocks.WORKING_STATION.get());
    }

    public ContainerData getData() {
        return data;
    }

}
