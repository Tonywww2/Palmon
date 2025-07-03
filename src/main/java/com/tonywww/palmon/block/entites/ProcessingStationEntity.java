package com.tonywww.palmon.block.entites;

import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Species;
import com.tonywww.palmon.api.CountableIngredient;
import com.tonywww.palmon.api.IEnergyStorage;
import com.tonywww.palmon.block.entites.itemhandlers.ProcessingStationItemHandler;
import com.tonywww.palmon.menu.ProcessingStationContainer;
import com.tonywww.palmon.recipes.ProcessingRecipe;
import com.tonywww.palmon.recipes.wrappers.ProcessingInput;
import com.tonywww.palmon.registeries.ModBlockEntities;
import com.tonywww.palmon.registeries.ModBlocks;
import com.tonywww.palmon.utils.ContainerUtils;
import com.tonywww.palmon.utils.PokemonNBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Optional;

import static com.tonywww.palmon.utils.RecipeUtils.insertListToHandler;

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
    protected final ContainerData tickData;

    private ResourceLocation currentRecipe;

    public static final int ITEM_INPUT_SIZE = 8;
    public static int MAX_ENERGY = 100000000;
    public static int MAX_FLUID = 12000;

    public static final double ACCURACY = BasicMachineEntity.ACCURACY;

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
                        return (int) (ProcessingStationEntity.this.boostMultiplier * ProcessingStationEntity.ACCURACY);
                    }
                    case 1 -> {
                        return (int) (ProcessingStationEntity.this.levelMultiplier * ProcessingStationEntity.ACCURACY);
                    }
                    case 2 -> {
                        return (int) (ProcessingStationEntity.this.individualMultiplier * ProcessingStationEntity.ACCURACY);
                    }
                    case 3 -> {
                        return (int) (ProcessingStationEntity.this.focusMultiplier * ProcessingStationEntity.ACCURACY);
                    }
                    case 4 -> {
                        return (int) (ProcessingStationEntity.this.efficiency * ProcessingStationEntity.ACCURACY);
                    }
                    case 5 -> {
                        return ContainerUtils.splitIntToShortLow(ProcessingStationEntity.this.energyStorage.getEnergyStored());
                    }
                    case 6 -> {
                        return ContainerUtils.splitIntToShortHigh(ProcessingStationEntity.this.energyStorage.getEnergyStored());
                    }
                }
                return 0;
            }

            @Override
            public void set(int index, int val) {
                switch (index) {
                    case 0:
                        ProcessingStationEntity.this.boostMultiplier = val / ProcessingStationEntity.ACCURACY;
                        break;

                    case 1:
                        ProcessingStationEntity.this.levelMultiplier = val / ProcessingStationEntity.ACCURACY;
                        break;

                    case 2:
                        ProcessingStationEntity.this.individualMultiplier = val / ProcessingStationEntity.ACCURACY;
                        break;

                    case 3:
                        ProcessingStationEntity.this.focusMultiplier = val / ProcessingStationEntity.ACCURACY;
                        break;

                    case 4:
                        ProcessingStationEntity.this.efficiency = val / ProcessingStationEntity.ACCURACY;
                        break;

                    case 5:
                        ProcessingStationEntity.this.energyStorage.setEnergyStored(ContainerUtils.combineShortsToInt((short) val, (short) ProcessingStationEntity.this.dataAccess.get(6)));
                        break;

                    case 6:
                        ProcessingStationEntity.this.energyStorage.setEnergyStored(ContainerUtils.combineShortsToInt((short) ProcessingStationEntity.this.dataAccess.get(5), (short) val));
                        break;

                }

            }

            @Override
            public int getCount() {
                return 7;
            }
        };

        this.tickData = new ContainerData() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0 -> {
                        return (int) (ProcessingStationEntity.this.currentTick);
                    }
                    case 1 -> {
                        return (int) (ProcessingStationEntity.this.targetTick);
                    }
                }
                return 0;
            }

            @Override
            public void set(int index, int val) {
                switch (index) {
                    case 0:
                        ProcessingStationEntity.this.currentTick = val;
                        break;
                    case 1:
                        ProcessingStationEntity.this.targetTick = val;
                        break;
                }

            }

            @Override
            public int getCount() {
                return 2;
            }
        };

    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ProcessingStationContainer(id, inventory, this, this.dataAccess, this.tickData);
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
        return super.getDroppableInventory(itemStackHandler);
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

    public ItemStackHandler getAreaBlocks() {
        return super.getAreaBlocks(ModBlocks.PROCESSING_STATION.get());
    }

    public ItemStackHandler getItemInput() {
        ItemStackHandler stackHandler = new ItemStackHandler(8);
        for (int i = 0; i < ITEM_INPUT_SIZE; i++) {
            stackHandler.setStackInSlot(i, this.itemStackHandler.getStackInSlot(i));
        }
        return stackHandler;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ProcessingStationEntity be) {
        if (level instanceof ServerLevel serverLevel) {
            be.tickBase(1);
            int food = be.getFood();
            if (!be.isWorkingTick() || food <= be.FOOD_PER_WORKING_TICK) return;

            CompoundTag pokemonNBT = be.getPokemonNBT();
            if (pokemonNBT == null) {
                be.resetMultipliers();
                be.inventoryChanged();
                be.resetTicker();
                return;
            }

            Species species = PokemonNBTUtils.getSpeciesFromNBT(pokemonNBT);
            if (species == null) {
                be.resetMultipliers();
                be.inventoryChanged();
                be.resetTicker();
                return;
            }

            ElementalType type1 = PokemonNBTUtils.getType1FromSpecies(species);
            ElementalType type2 = PokemonNBTUtils.getType2FromSpecies(species);
            int pokemonLevel = PokemonNBTUtils.getLevelFromNBT(pokemonNBT);
            CompoundTag ivs = PokemonNBTUtils.getAllIVsFromNBT(pokemonNBT);
            HashMap<Stat, Integer> baseStats = species.getBaseStats();
            ItemStackHandler areaBlocks = be.getAreaBlocks();
            ItemStackHandler itemInput = be.getItemInput();

            if (areaBlocks == null) {
                be.resetMultipliers();
                be.inventoryChanged();
                be.resetTicker();
                return;
            }

            ProcessingInput input = new ProcessingInput(areaBlocks, pokemonLevel, type1,
                    baseStats.get(Stats.HP), baseStats.get(Stats.ATTACK), baseStats.get(Stats.DEFENCE),
                    baseStats.get(Stats.SPECIAL_ATTACK), baseStats.get(Stats.SPECIAL_DEFENCE), baseStats.get(Stats.SPEED),
                    itemInput, be.fluidTank.getFluid(), be.energyStorage.getEnergyStored());

            Optional<ProcessingRecipe> recipe = findRecipe(serverLevel, input, type2);

            if (recipe.isPresent()) {
                ProcessingRecipe rcp = recipe.get();
                if (rcp.getId().equals(be.currentRecipe)) {
                    Stats focusStat = rcp.getFocusStat();
                    int focusEv = species.getBaseStats().get(focusStat);
                    int focusIv = PokemonNBTUtils.getIVFromNBT(ivs, focusStat);
                    be.updateMultipliers(
                            pokemonNBT,
                            ivs,
                            true,
                            focusEv,
                            focusIv,
                            rcp.getTick()
                    );
                    be.processRecipe(rcp, serverLevel, pos);
                    be.tryConsumeFood(serverLevel, pos, food);
                } else {
                    be.resetRecipeState();
                    be.startNewRecipe(rcp);
                }

            } else {
                be.resetRecipeState();
            }

            be.inventoryChanged();
            be.resetTicker();
        }
    }

    private static Optional<ProcessingRecipe> findRecipe(ServerLevel serverLevel, ProcessingInput input, ElementalType type2) {
        Optional<ProcessingRecipe> recipe = serverLevel.getRecipeManager()
                .getRecipeFor(ProcessingRecipe.ProcessingRecipeType.INSTANCE, input, serverLevel);
        if (recipe.isEmpty() && type2 != null) {
            input.setType(type2);
            recipe = serverLevel.getRecipeManager()
                    .getRecipeFor(ProcessingRecipe.ProcessingRecipeType.INSTANCE, input, serverLevel);
        }
        return recipe;
    }

    private void processRecipe(ProcessingRecipe rcp, ServerLevel serverLevel, BlockPos pos) {
        this.currentTick += this.efficiency * this.tickPerOperation;
        if (this.currentTick >= this.targetTick) {
            if (!rcp.getResultItems().isEmpty()) {
                insertListToHandler(rcp.getResultItems(), this.itemStackHandler, ITEM_INPUT_SIZE, this.itemStackHandler.getSlots());
            }
            if (!rcp.getInputItems().isEmpty()) {
                consumeInputItems(rcp);
            }
            this.energyStorage.setEnergyStored(this.energyStorage.getEnergyStored() - rcp.getInputEnergy());
            if (rcp.getInputFluid() != null) {
                this.fluidTank.drain(rcp.getInputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);
            }
            this.currentTick -= this.targetTick;
            serverLevel.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS);
        }
    }

    private void consumeInputItems(ProcessingRecipe rcp) {
        for (CountableIngredient recipeStack : rcp.getInputItems()) {
            Ingredient ingredient = recipeStack.getIngredient();
            int still = recipeStack.getCount();
            for (int i = 0; i < ITEM_INPUT_SIZE && still > 0; i++) {
                ItemStack stackInSlot = this.itemStackHandler.getStackInSlot(i);
                if (!stackInSlot.isEmpty() && ingredient.test(stackInSlot)) {
                    int count = stackInSlot.getCount();
                    if (still - count > 0) {
                        stackInSlot.shrink(count);
                    } else {
                        stackInSlot.shrink(still);
                    }
                    still -= count;
                }
            }
        }
    }

    private void startNewRecipe(ProcessingRecipe rcp) {
        this.currentRecipe = rcp.getId();
        this.targetTick = rcp.getTick();
        this.currentTick = 0;
    }

    private void tryConsumeFood(ServerLevel serverLevel, BlockPos pos, int food) {
        if (serverLevel.getRandom().nextDouble() < this.FOOD_CONSUME_CHANCE) {
            setFood(food - serverLevel.getRandom().nextInt(this.FOOD_PER_WORKING_TICK) - 1);
            serverLevel.playSound(null, pos, CobblemonSounds.BERRY_EAT, SoundSource.BLOCKS);
        }
    }

    private void resetMultipliers() {
        this.boostMultiplier = 1.0;
        this.levelMultiplier = 1.0;
        this.individualMultiplier = 1.0;
        this.focusMultiplier = 0;
        this.efficiency = 0;
    }

    private void resetRecipeState() {
        this.focusMultiplier = 0;
        this.efficiency = 0;
        this.currentTick = 0;
        this.targetTick = 0;
        this.currentRecipe = null;
    }

}
