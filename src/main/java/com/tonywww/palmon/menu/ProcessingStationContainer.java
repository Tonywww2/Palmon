package com.tonywww.palmon.menu;

import com.tonywww.palmon.api.IAbstractContainerMenu;
import com.tonywww.palmon.block.entites.ProcessingStationEntityPokemon;
import com.tonywww.palmon.registeries.ModBlocks;
import com.tonywww.palmon.registeries.ModMenus;
import com.tonywww.palmon.utils.ContainerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.Objects;

import static com.tonywww.palmon.utils.ContainerUtils.quickMoveHelper;

public class ProcessingStationContainer extends IAbstractContainerMenu {

    private final ProcessingStationEntityPokemon blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    private final IItemHandler playerInventory;

    private final ContainerData data;
    private final ContainerData tickData;

    public ProcessingStationContainer(int id, Inventory playerInventory, ProcessingStationEntityPokemon blockEntity, ContainerData dataAccess, ContainerData tickData) {
        super(ModMenus.PROCESSING_STATION_CONTAINER.get(), id);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.playerInventory = new InvWrapper(playerInventory);

        this.data = dataAccess;
        this.tickData = tickData;

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(h -> {
            int i = 0;
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 4; k++) {
                    addSlot(new SlotItemHandler(h, i++, 37 + k * 18, 51 + j * 18));
                }
            }

            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    addSlot(new SlotItemHandler(h, i++, 128 + k * 18, 51 + j * 18));
                }
            }

            addDataSlots(this.data);
            addDataSlots(this.tickData);

        });

        ContainerUtils.layoutPlayerInventory(playerInventory, this);

    }

    public ProcessingStationContainer(final int id,
                                      final Inventory playerInventory,
                                      final FriendlyByteBuf data) {
        this(id, playerInventory, getTileEntity(playerInventory, data), new SimpleContainerData(7), new SimpleContainerData(2));

    }

    private static ProcessingStationEntityPokemon getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof ProcessingStationEntityPokemon tile) {
            return tile;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotNumber) {
        int invCount = 12;
        return quickMoveHelper(this, player, slotNumber, invCount);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, ModBlocks.PROCESSING_STATION.get());
    }

    public ContainerData getData() {
        return data;
    }

    public ProcessingStationEntityPokemon getBlockEntity() {
        return blockEntity;
    }

    public ContainerData getTickData() {
        return tickData;
    }
}
