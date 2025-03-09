package com.tonywww.palmon.block.entites;

import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Species;
import com.tonywww.palmon.api.CountableIngredient;
import com.tonywww.palmon.api.IEnergyStorage;
import com.tonywww.palmon.block.BoostFrame;
import com.tonywww.palmon.block.entites.itemhandlers.ProcessingStationItemHandler;
import com.tonywww.palmon.menu.ProcessingStationContainer;
import com.tonywww.palmon.recipes.ProcessingRecipe;
import com.tonywww.palmon.recipes.wrappers.ProcessingInput;
import com.tonywww.palmon.registeries.ModBlockEntities;
import com.tonywww.palmon.registeries.ModBlocks;
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
import net.minecraft.world.level.block.Block;
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

    private double boostMultiplier = 1.0;
    private double levelMultiplier = 1.0;
    private double ovaStatsMultiplier = 1.0;
    private double focusMultiplier = 0.0;
    private double efficiency = 0.0;

    private double currentTick = 0.0;
    private double targetTick = 0.0;

    private ResourceLocation currentRecipe;

    public static final int ITEM_INPUT_SIZE = 8;
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
                        return (int) (ProcessingStationEntity.this.boostMultiplier * ProcessingStationEntity.ACCURACY);
                    }
                    case 1 -> {
                        return (int) (ProcessingStationEntity.this.levelMultiplier * ProcessingStationEntity.ACCURACY);
                    }
                    case 2 -> {
                        return (int) (ProcessingStationEntity.this.ovaStatsMultiplier * ProcessingStationEntity.ACCURACY);
                    }
                    case 3 -> {
                        return (int) (ProcessingStationEntity.this.focusMultiplier * ProcessingStationEntity.ACCURACY);
                    }
                    case 4 -> {
                        return (int) (ProcessingStationEntity.this.efficiency * ProcessingStationEntity.ACCURACY);
                    }
                    case 5 -> {
                        return ProcessingStationEntity.this.energyStorage.getEnergyStored();
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
                        ProcessingStationEntity.this.ovaStatsMultiplier = val / ProcessingStationEntity.ACCURACY;
                        break;

                    case 3:
                        ProcessingStationEntity.this.focusMultiplier = val / ProcessingStationEntity.ACCURACY;
                        break;

                    case 4:
                        ProcessingStationEntity.this.efficiency = val / ProcessingStationEntity.ACCURACY;
                        break;

                    case 5:
                        ProcessingStationEntity.this.energyStorage.setEnergyStored(val);
                        break;

                }

            }

            @Override
            public int getCount() {
                return 6;
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

    public double getBoostMultiplier() {
        BlockState state = null;
        if (this.level != null) {
            state = this.level.getBlockState(this.getBlockPos().above());
        }
        if (state != null && state.getBlock() instanceof BoostFrame boostFrame) {
            return boostFrame.efficiency;
        }
        return 1.0d;

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

    public ItemStackHandler getAreaBlocks() {
        HashMap<Block, Integer> map = new HashMap<>();
        if (this.getLevel() != null) {
            for (int i = 0; i <= HEIGHT; i++) {
                for (int j = -RADIUS; j <= RADIUS; j++) {
                    for (int k = -RADIUS; k <= RADIUS; k++) {
                        if (i == 0 && j == 0 && k == 0) continue;
                        Block cur = this.getLevel().getBlockState(this.getBlockPos().offset(j, i, k)).getBlock();
                        if (cur.equals(ModBlocks.PROCESSING_STATION.get())) return null;
                        map.put(cur, map.getOrDefault(cur, 0) + 1);

                    }
                }
            }
        }

        ItemStackHandler stackHandler = new ItemStackHandler(map.size());
        int i = 0;
        for (Block key : map.keySet()) {
            stackHandler.setStackInSlot(i++, new ItemStack(key, map.get(key)));

        }
        return stackHandler;
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
            be.tick(1);
            int food = be.getFood();
            if (be.isWorkingTick() && food > be.FOOD_PER_WORKING_TICK) {
                CompoundTag pokemonNBT = be.getPokemonNBT();
                if (pokemonNBT != null) {
                    Species species = PokemonNBTUtils.getSpeciesFromNBT(pokemonNBT);
                    if (species != null) {

                        ElementalType type1 = PokemonNBTUtils.getType1FromSpecies(species);
                        ElementalType type2 = PokemonNBTUtils.getType2FromSpecies(species);

                        int pokemonLevel = PokemonNBTUtils.getLevelFromNBT(pokemonNBT);

                        CompoundTag ivs = PokemonNBTUtils.getAllIVsFromNBT(pokemonNBT);
                        HashMap<Stat, Integer> baseStats = species.getBaseStats();

                        ItemStackHandler areaBlocks = be.getAreaBlocks();
                        ItemStackHandler itemInput = be.getItemInput();

                        if (areaBlocks != null) {
                            ProcessingInput input = new ProcessingInput(areaBlocks, pokemonLevel, type1,
                                    baseStats.get(Stats.HP), baseStats.get(Stats.ATTACK), baseStats.get(Stats.DEFENCE),
                                    baseStats.get(Stats.SPECIAL_ATTACK), baseStats.get(Stats.SPECIAL_DEFENCE), baseStats.get(Stats.SPEED),
                                    itemInput, be.fluidTank.getFluid(), be.energyStorage.getEnergyStored());

                            Optional<ProcessingRecipe> recipe = serverLevel.getRecipeManager()
                                    .getRecipeFor(ProcessingRecipe.ProcessingRecipeType.INSTANCE, input, serverLevel);

                            if (recipe.isEmpty() && type2 != null) {
                                input.setType(type2);
                                recipe = serverLevel.getRecipeManager()
                                        .getRecipeFor(ProcessingRecipe.ProcessingRecipeType.INSTANCE, input, serverLevel);
                            }

                            be.boostMultiplier = be.getBoostMultiplier();
                            be.levelMultiplier = be.getLevelMultiplier(PokemonNBTUtils.getLevelFromNBT(pokemonNBT));
                            be.ovaStatsMultiplier = be.getOvaStatsMultiplier(ivs);

                            if (recipe.isPresent()) {
                                ProcessingRecipe rcp = recipe.get();
                                if (rcp.getId().equals(be.currentRecipe)) {
                                    // keep ticking
                                    be.focusMultiplier = be.getFocusMultiplier(species.getBaseStats().get(rcp.getFocusStat()), PokemonNBTUtils.getIVFromNBT(ivs, rcp.getFocusStat()));
                                    // equation: boost(1-5) * level(1-2.5) * ovaStats(1-5.5, With sum of Ivs) * focusStats(1-11.0, with the Ev and Iv)
                                    be.efficiency = be.boostMultiplier * be.levelMultiplier * be.ovaStatsMultiplier * be.focusMultiplier;

                                    // Basic Machine multiplier
                                    be.currentTick += be.efficiency * be.tickPerOperation;

                                    // finished a cycle
                                    if (be.currentTick >= be.targetTick) {
                                        if (!rcp.getResultItems().isEmpty()) {
                                            insertListToHandler(rcp.getResultItems(), be.itemStackHandler, ITEM_INPUT_SIZE, be.itemStackHandler.getSlots());
                                        }

                                        if (!rcp.getInputItems().isEmpty()) {
                                            for (CountableIngredient recipeStack : rcp.getInputItems()) {
                                                Ingredient ingredient = recipeStack.getIngredient();
                                                int still = recipeStack.getCount();

                                                for (int i = 0; i < ITEM_INPUT_SIZE && still > 0; i++) {
                                                    ItemStack stackInSlot = be.itemStackHandler.getStackInSlot(i);
                                                    if (!stackInSlot.isEmpty() && ingredient.test(stackInSlot)) {
                                                        int count = stackInSlot.getCount();
                                                        if (still - stackInSlot.getCount() > 0) {
                                                            stackInSlot.shrink(count);
                                                        } else {
                                                            stackInSlot.shrink(still);
                                                        }
                                                        still -= count;
                                                    }
                                                }

                                            }
                                        }

                                        be.energyStorage.setEnergyStored(be.energyStorage.getEnergyStored() - rcp.getInputEnergy());

                                        if (rcp.getInputFluid() != null) {
                                            be.fluidTank.drain(rcp.getInputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);
                                        }

                                        be.currentTick -= be.targetTick;
                                        serverLevel.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS);

                                    }

                                } else {
                                    // new recipe
                                    be.currentRecipe = rcp.getId();
                                    be.targetTick = rcp.getTick();

                                    be.currentTick = 0;
                                }
                                if (serverLevel.getRandom().nextDouble() < be.FOOD_CONSUME_CHANCE) {
                                    be.setFood(food - serverLevel.getRandom().nextInt(be.FOOD_PER_WORKING_TICK) - 1);
                                    serverLevel.playSound(null, pos, CobblemonSounds.BERRY_EAT, SoundSource.BLOCKS);

                                }

                            } else {
                                be.focusMultiplier = 0;
                                be.efficiency = 0;
                                be.currentTick = 0;
                                be.targetTick = 0;
                                be.currentRecipe = null;
                            }
                        }
                    } else {
                        be.boostMultiplier = 1.0;
                        be.levelMultiplier = 1.0;
                        be.ovaStatsMultiplier = 1.0;
                        be.focusMultiplier = 0;
                        be.efficiency = 0;

                    }
                    be.inventoryChanged();
                    be.resetTicker();
                }
            }
        }

    }


}
