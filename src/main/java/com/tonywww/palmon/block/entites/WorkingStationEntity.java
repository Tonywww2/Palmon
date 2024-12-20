package com.tonywww.palmon.block.entites;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.tonywww.palmon.item.LaborContract;
import com.tonywww.palmon.menu.WorkingStationContainer;
import com.tonywww.palmon.registeries.ModBlockEntities;
import com.tonywww.palmon.registeries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Pose;
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
    public final ItemStackHandler itemStackHandler;
    private final LazyOptional<ItemStackHandler> handler;

    private PokemonEntity pokemonEntity;

    public WorkingStationEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WORKING_STATION_BLOCK_ENTITY.get(), pos, state);
        this.itemStackHandler = createHandler();
        this.handler = LazyOptional.of(() -> itemStackHandler);

    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                inventoryChanged();
                if (level != null && level instanceof ServerLevel serverLevel) {
                    serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
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
    public void load(CompoundTag tag) {
        itemStackHandler.deserializeNBT(tag.getCompound("inv"));
        if (!itemStackHandler.getStackInSlot(0).isEmpty() && this.getPokemonNBT() != null) {
            updatePokemonEntity();

        } else {
            this.pokemonEntity = null;
        }

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inv", itemStackHandler.serializeNBT());

        super.saveAdditional(tag);
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("inv")) {
            itemStackHandler.deserializeNBT(tag.getCompound("inv"));
            if (!itemStackHandler.getStackInSlot(0).isEmpty() && this.getPokemonNBT() != null) {
                updatePokemonEntity();

            } else {
                this.pokemonEntity = null;
            }

        }
    }


    private void updatePokemonEntity() {
        if (this.getLevel() != null) {
            this.pokemonEntity = new PokemonEntity(this.getLevel(), Pokemon.Companion.loadFromNBT(this.getPokemonNBT()), CobblemonEntities.POKEMON) {
                @Override
                public boolean shouldRender(double d, double e, double f) {
                    return true;
                }
            };
            this.pokemonEntity.setYBodyRot(0);
            this.pokemonEntity.setYHeadRot(0);
        }
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

    public CompoundTag getPokemonNBT() {
        return LaborContract.getPokemonNBT(this.itemStackHandler.getStackInSlot(0));

    }

    public PokemonEntity getPokemonEntity() {
        return pokemonEntity;
    }
}
