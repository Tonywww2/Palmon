package com.tonywww.palmon.block.entites;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Species;
import com.tonywww.palmon.block.BoostFrame;
import com.tonywww.palmon.menu.ProductionMachineContainer;
import com.tonywww.palmon.registeries.ModBlockEntities;
import com.tonywww.palmon.utils.PokemonNBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class ProductionMachineEntity extends BasicMachineEntity implements MenuProvider {
    public ItemStackHandler itemStackHandler;
    public EnergyStorage energyStorage;
    public FluidTank fluidTank;

    private final LazyOptional<ItemStackHandler> itemOptional = LazyOptional.of(() -> this.itemStackHandler);
    private final LazyOptional<FluidTank> fluidOptional = LazyOptional.of(() -> this.fluidTank);

    private double boostMultiplier = 1.0;
    private double levelMultiplier = 1.0;
    private double ovaStatsMultiplier = 1.0;
    private double focusMultiplier = 1.0;

    public static double[] levelToEfficiency = new double[]{1.0, 1.5, 2.25, 3.5, 5};

    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(18) {
            @Override
            protected void onContentsChanged(int slot) {
                inventoryChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return true;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
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

    private EnergyStorage createEnergyHandler() {
        int MAX_ENERGY = 5000000;
        return new EnergyStorage(MAX_ENERGY) {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return 0;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                int energy = this.getEnergyStored();
                int diff = Math.min(energy, maxExtract);
                if (!simulate) {
                    this.energy += diff;
                    if (diff != 0) {
                        inventoryChanged();
                    }
                }
                return diff;
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
                return true;
            }

            @Override
            public boolean canReceive() {
                return false;
            }
        };
    }

    private FluidTank createFluidTank() {
        return new FluidTank(8000) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                inventoryChanged();
            }
        };
    }

    public ProductionMachineEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRODUCTION_MACHINE_BLOCK_ENTITY.get(), pos, state);

        this.itemStackHandler = createItemHandler();
        this.energyStorage = createEnergyHandler();
        this.fluidTank = createFluidTank();

    }

    @Override
    public void load(CompoundTag tag) {
        itemStackHandler.deserializeNBT(tag.getCompound("inv"));

        energyStorage.deserializeNBT(tag.get("energy"));
//        fluidTank.readFromNBT(tag.getCompound("fluid"));

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inv", itemStackHandler.serializeNBT());

        tag.put("energy", energyStorage.serializeNBT());
//        tag.put("fluid", fluidTank.writeToNBT(new CompoundTag()));

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
            return this.itemOptional.cast();

        }

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return this.fluidOptional.cast();

        }

        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.palmon.production_machine");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ProductionMachineContainer(id, inventory, this);
    }

    public CompoundTag getPokemonNBT() {
        int x = 0, z = 0;
        switch (this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            case NORTH:
                z--;
                break;

            case EAST:
                x++;
                break;

            case SOUTH:
                z++;
                break;

            case WEST:
                x--;
                break;
        }
        if (this.level != null &&
                this.level.getBlockEntity(this.getBlockPos().offset(x, 0, z)) instanceof WorkingStationEntity workingStationEntity) {
            return workingStationEntity.getPokemonNBT();
        }
        return null;
    }

    public double getBoostMultiplier() {
        BlockState state = null;
        if (this.level != null) {
            state = this.level.getBlockState(this.getBlockPos().above());
        }
        if (state != null && state.getBlock() instanceof BoostFrame boostFrame) {
            return levelToEfficiency[boostFrame.level];
        }
        return levelToEfficiency[0];

    }

    public double getLevelMultiplier(int level) {
        return (0.00015d * level * level) + 1;
    }

    public double getFocusMultiplier(int ev, int iv) {
        return Math.max(1, (Math.pow(ev, 3) + (1000d * Math.pow(iv - 1, 3))) / 4000000d);
    }

    public double getOvaStatsMultiplier(CompoundTag ivs) {
        int sumIvs = PokemonNBTUtils.getIVFromNBT(ivs, Stats.HP) +
                PokemonNBTUtils.getIVFromNBT(ivs, Stats.ATTACK) +
                PokemonNBTUtils.getIVFromNBT(ivs, Stats.DEFENCE) +
                PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_ATTACK) +
                PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_DEFENCE) +
                PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPEED);

        return Math.max(1, (Math.pow(sumIvs - 5, 3) / 1000000d) + 0.3d);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ProductionMachineEntity be) {
        if (level instanceof ServerLevel) {
            be.tick(1);
            if (be.isWorkingTick()) {
                CompoundTag pokemonNBT = be.getPokemonNBT();
                if (pokemonNBT != null) {
                    Species species = PokemonNBTUtils.getSpeciesFromNBT(pokemonNBT);
                    CompoundTag ivs = PokemonNBTUtils.getAllIVsFromNBT(pokemonNBT);

                    be.boostMultiplier = be.getBoostMultiplier();
                    be.levelMultiplier = be.getLevelMultiplier(PokemonNBTUtils.getLevelFromNBT(pokemonNBT));
                    be.ovaStatsMultiplier = be.getOvaStatsMultiplier(ivs);

                    if (true) {
                        Stats recipeFocusStat = Stats.HP;
                        be.focusMultiplier = be.getFocusMultiplier(species.getEvYield().get(recipeFocusStat), PokemonNBTUtils.getIVFromNBT(ivs, recipeFocusStat));
                        // equation: boost(1-5) * level(1-2.5) * ovaStats(1-5.5, With sum of Ivs) * focusStats(1-11.0, with the Ev and Iv)
                        double efficiency = be.boostMultiplier * be.levelMultiplier * be.ovaStatsMultiplier * be.focusMultiplier;

                        System.out.println(efficiency);
                    } else {
                        be.focusMultiplier = 0;
                    }
                } else {
                    be.boostMultiplier = 1.0;
                    be.levelMultiplier = 1.0;
                    be.ovaStatsMultiplier = 1.0;
                    be.focusMultiplier = 1.0;

                }

                be.resetTicker();

            }
        }

    }

}
