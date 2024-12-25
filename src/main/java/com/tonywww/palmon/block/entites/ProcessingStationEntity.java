package com.tonywww.palmon.block.entites;

import com.tonywww.palmon.api.IEnergyStorage;
import com.tonywww.palmon.block.entites.itemhandlers.ProcessingStationItemHandler;
import com.tonywww.palmon.menu.ProcessingStationContainer;
import com.tonywww.palmon.registeries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class ProcessingStationEntity extends BasicMachineEntity implements MenuProvider {

    public ItemStackHandler itemStackHandler;
    public IEnergyStorage energyStorage;
    public FluidTank fluidTank;

    private final LazyOptional<ItemStackHandler> itemOptional = LazyOptional.of(() -> this.itemStackHandler);
    private final LazyOptional<ProcessingStationItemHandler> itemInputOptional = LazyOptional.of(() -> new ProcessingStationItemHandler(this.itemStackHandler, Direction.UP));
    private final LazyOptional<ProcessingStationItemHandler> itemOutputOptional = LazyOptional.of(() -> new ProcessingStationItemHandler(this.itemStackHandler, Direction.DOWN));

    private final LazyOptional<EnergyStorage> energyOptional = LazyOptional.of(() -> this.energyStorage);
    private final LazyOptional<FluidTank> fluidOptional = LazyOptional.of(() -> this.fluidTank);

    protected final ContainerData dataAccess;

    private double boostMultiplier = 1.0;
    private double levelMultiplier = 1.0;
    private double ovaStatsMultiplier = 1.0;
    private double focusMultiplier = 0.0;
    private double efficiency = 0.0;

    private double currentTick = 0.0;
    private double targetTick = 0.0;

    public static int MAX_ENERGY = 5000000;
    public static int MAX_FLUID = 8000;

    public static final double ACCURACY = 100d;
    public static final int RADIUS = 2;
    public static final int HEIGHT = 4;

    // 0-7 input, 8-11 output
    private ItemStackHandler createInputItemHandler() {
        return new ItemStackHandler(12) {
            @Override
            protected void onContentsChanged(int slot) {
                inventoryChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return slot <= 7;
            }

            @Override
            public int getSlotLimit(int slot) {
                return super.getSlotLimit(slot);
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


    private IEnergyStorage createEnergyHandler() {
        return new IEnergyStorage(MAX_ENERGY) {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                int energySpace = this.getMaxEnergyStored() - this.getEnergyStored();
                int diff = Math.min(energySpace, maxReceive);
                if (!simulate) {
                    this.setEnergyStored(this.getEnergyStored() + diff);
                    if (diff != 0) {
                        inventoryChanged();
                    }
                }
                return diff;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return Math.max(0, Math.min(this.getMaxEnergyStored(), this.energy));
            }

            @Override
            public int getMaxEnergyStored() {
                return MAX_ENERGY;
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }

    private FluidTank createFluidTank() {
        return new FluidTank(MAX_FLUID) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                inventoryChanged();
            }
        };
    }

    public ProcessingStationEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PROCESSING_STATION_ENTITY.get(), pos, state);

        this.itemStackHandler = createInputItemHandler();
        this.energyStorage = createEnergyHandler();
        this.fluidTank = createFluidTank();

        this.dataAccess = new ContainerData() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0 -> {
                        return (int) (boostMultiplier * ACCURACY);
                    }
                    case 1 -> {
                        return (int) (levelMultiplier * ACCURACY);
                    }
                    case 2 -> {
                        return (int) (ovaStatsMultiplier * ACCURACY);
                    }
                    case 3 -> {
                        return (int) (focusMultiplier * ACCURACY);
                    }
                    case 4 -> {
                        return (int) (efficiency * ACCURACY);
                    }
                    case 5 -> {
                        return (int) (currentTick * ACCURACY);
                    }
                    case 6 -> {
                        return (int) (targetTick * ACCURACY);
                    }
                    case 7 -> {
                        return energyStorage.getEnergyStored();
                    }
                }
                return 0;
            }

            @Override
            public void set(int index, int val) {
                switch (index) {
                    case 0:
                        boostMultiplier = val / ACCURACY;
                        break;

                    case 1:
                        levelMultiplier = val / ACCURACY;
                        break;

                    case 2:
                        ovaStatsMultiplier = val / ACCURACY;
                        break;

                    case 3:
                        focusMultiplier = val / ACCURACY;
                        break;

                    case 4:
                        efficiency = val / ACCURACY;
                        break;

                    case 5:
                        currentTick = val;
                        break;

                    case 6:
                        targetTick = val;
                        break;

                    case 7:
                        energyStorage.setEnergyStored(val);
                        break;

                }

            }

            @Override
            public int getCount() {
                return 8;
            }
        };

    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ProcessingStationContainer(id, inventory, this, this.dataAccess);
    }

    @Override
    public void load(CompoundTag tag) {
        itemStackHandler.deserializeNBT(tag.getCompound("inv"));

        energyStorage.setEnergyStored(tag.getInt("energy"));
        fluidTank.readFromNBT(tag.getCompound("fluid"));

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inv", itemStackHandler.serializeNBT());

        tag.putInt("energy", energyStorage.getEnergyStored());
        tag.put("fluid", fluidTank.writeToNBT(new CompoundTag()));

        super.saveAdditional(tag);
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
            if (side == null) {
                // Player
                return this.itemOptional.cast();
            }
            if (side == Direction.DOWN) {
                return this.itemOutputOptional.cast();
            }
            if (side == Direction.NORTH || side == Direction.EAST || side == Direction.SOUTH || side == Direction.WEST) {
                return this.itemInputOptional.cast();
            }
        }

        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyOptional.cast();
        }

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return this.fluidOptional.cast();

        }

        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.palmon.processing_station");
    }
}
