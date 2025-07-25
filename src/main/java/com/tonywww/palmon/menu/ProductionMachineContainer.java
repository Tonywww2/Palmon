package com.tonywww.palmon.menu;

import com.tonywww.palmon.api.IAbstractContainerMenu;
import com.tonywww.palmon.block.entites.ProductionPokemonMachineEntity;
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

public class ProductionMachineContainer extends IAbstractContainerMenu {

    private final ProductionPokemonMachineEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    private final IItemHandler playerInventory;

    private final ContainerData data;
    private final ContainerData tickData;

    public ProductionMachineContainer(int id, Inventory playerInventory, ProductionPokemonMachineEntity blockEntity, ContainerData dataAccess, ContainerData tickData) {
        super(ModMenus.PRODUCTION_MACHINE_CONTAINER.get(), id);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.playerInventory = new InvWrapper(playerInventory);

        this.data = dataAccess;
        this.tickData = tickData;

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            int i, j;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 6; j++) {
                    addSlot(new SlotItemHandler(h, j + i * 6, 35 + j * 18, 51 + i * 18));
                }
            }

            addDataSlots(data);
            addDataSlots(tickData);
        });

        ContainerUtils.layoutPlayerInventory(playerInventory, this);

    }

    public ProductionMachineContainer(final int id,
                                      final Inventory playerInventory,
                                      final FriendlyByteBuf data) {
        this(id, playerInventory, getTileEntity(playerInventory, data), new SimpleContainerData(7), new SimpleContainerData(2));

    }

    private static ProductionPokemonMachineEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof ProductionPokemonMachineEntity tile) {
            return tile;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotNumber) {
        int invCount = 18;
        return quickMoveHelper(this, player, slotNumber, invCount);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, ModBlocks.PRODUCTION_MACHINE.get());
    }

    public ContainerData getData() {
        return data;
    }

    public ProductionPokemonMachineEntity getBlockEntity() {
        return blockEntity;
    }

    public ContainerData getTickData() {
        return tickData;
    }
}
