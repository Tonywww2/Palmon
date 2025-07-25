package com.tonywww.palmon.block.entites;

import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.common.collect.Queues;
import com.tonywww.palmon.api.IEnergyStorage;
import com.tonywww.palmon.recipes.wrappers.ProductionInput;
import com.tonywww.palmon.menu.ProductionMachineContainer;
import com.tonywww.palmon.recipes.ProductionRecipe;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import static com.tonywww.palmon.utils.RecipeUtils.insertListToHandler;

public class ProductionPokemonMachineEntity extends BasicPokemonMachineEntity implements MenuProvider {
    public ItemStackHandler itemStackHandler;
    public IEnergyStorage energyStorage;
    public FluidTank fluidTank;

    private final LazyOptional<ItemStackHandler> itemOptional = LazyOptional.of(() -> this.itemStackHandler);
    private final LazyOptional<EnergyStorage> energyOptional = LazyOptional.of(() -> this.energyStorage);
    private final LazyOptional<FluidTank> fluidOptional = LazyOptional.of(() -> this.fluidTank);

    protected final ContainerData dataAccess;
    protected final ContainerData tickData;

    private ResourceLocation currentRecipe;

    public static int MAX_ENERGY = 5000000;
    public static int MAX_FLUID = 8000;

    public static int MAX_TRANSFER = 5000000;

    public static final double ACCURACY = BasicPokemonMachineEntity.ACCURACY;

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
                return 0;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                int energy = this.getEnergyStored();
                if (energy <= 0) return 0;
                int diff = Math.min(energy, maxExtract);
                if (!simulate) {
                    this.setEnergyStored(this.getEnergyStored() - diff);
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
        return new FluidTank(MAX_FLUID) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                inventoryChanged();
            }
        };
    }

    public ProductionPokemonMachineEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRODUCTION_MACHINE_BLOCK_ENTITY.get(), pos, state);

        this.itemStackHandler = createItemHandler();
        this.energyStorage = createEnergyHandler();
        this.fluidTank = createFluidTank();

        this.dataAccess = new ContainerData() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0 -> {
                        return (int) (ProductionPokemonMachineEntity.this.boostMultiplier * ProductionPokemonMachineEntity.ACCURACY);
                    }
                    case 1 -> {
                        return (int) (ProductionPokemonMachineEntity.this.levelMultiplier * ProductionPokemonMachineEntity.ACCURACY);
                    }
                    case 2 -> {
                        return (int) (ProductionPokemonMachineEntity.this.individualMultiplier * ProductionPokemonMachineEntity.ACCURACY);
                    }
                    case 3 -> {
                        return (int) (ProductionPokemonMachineEntity.this.focusMultiplier * ProductionPokemonMachineEntity.ACCURACY);
                    }
                    case 4 -> {
                        return (int) (ProductionPokemonMachineEntity.this.efficiency * ProductionPokemonMachineEntity.ACCURACY);
                    }
                    case 5 -> {
                        return ContainerUtils.splitIntToShortLow(ProductionPokemonMachineEntity.this.energyStorage.getEnergyStored());
                    }
                    case 6 -> {
                        return ContainerUtils.splitIntToShortHigh(ProductionPokemonMachineEntity.this.energyStorage.getEnergyStored());
                    }
                }
                return 0;
            }

            @Override
            public void set(int index, int val) {
                switch (index) {
                    case 0:
                        ProductionPokemonMachineEntity.this.boostMultiplier = val / ProductionPokemonMachineEntity.ACCURACY;
                        break;

                    case 1:
                        ProductionPokemonMachineEntity.this.levelMultiplier = val / ProductionPokemonMachineEntity.ACCURACY;
                        break;

                    case 2:
                        ProductionPokemonMachineEntity.this.individualMultiplier = val / ProductionPokemonMachineEntity.ACCURACY;
                        break;

                    case 3:
                        ProductionPokemonMachineEntity.this.focusMultiplier = val / ProductionPokemonMachineEntity.ACCURACY;
                        break;

                    case 4:
                        ProductionPokemonMachineEntity.this.efficiency = val / ProductionPokemonMachineEntity.ACCURACY;
                        break;

                    case 5:
                        ProductionPokemonMachineEntity.this.energyStorage.setEnergyStored(ContainerUtils.combineShortsToInt((short) val, (short) ProductionPokemonMachineEntity.this.dataAccess.get(6)));
                        break;

                    case 6:
                        ProductionPokemonMachineEntity.this.energyStorage.setEnergyStored(ContainerUtils.combineShortsToInt((short) ProductionPokemonMachineEntity.this.dataAccess.get(5), (short) val));
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
                        return (int) (ProductionPokemonMachineEntity.this.currentTick);
                    }
                    case 1 -> {
                        return (int) (ProductionPokemonMachineEntity.this.targetTick);
                    }
                }
                return 0;
            }

            @Override
            public void set(int index, int val) {
                switch (index) {
                    case 0:
                        ProductionPokemonMachineEntity.this.currentTick = val;
                        break;
                    case 1:
                        ProductionPokemonMachineEntity.this.targetTick = val;
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
            return this.itemOptional.cast();
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
        return Component.translatable("screen.palmon.production_machine");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ProductionMachineContainer(id, inventory, this, this.dataAccess, this.tickData);
    }

    public ItemStackHandler getAreaBlocks() {
        return super.getAreaBlocks(ModBlocks.PRODUCTION_MACHINE.get());
    }

    private final Queue<Direction> directionQueue = Queues.newArrayDeque(Direction.Plane.HORIZONTAL);

    private void distributeEnergy() {
        if (this.getLevel() != null) {
            if (this.energyStorage.getEnergyStored() <= 0) return;
            this.directionQueue.offer(this.directionQueue.remove());
            for (Direction dir : directionQueue) {
                BlockEntity be = this.getLevel().getBlockEntity(this.getBlockPos().offset(dir.getNormal()));
                if (be != null) {
                    be.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(e -> {
                        if (e.canReceive()) {
                            int diff = e.receiveEnergy(Math.min(MAX_TRANSFER, this.energyStorage.getEnergyStored()), false);
                            if (diff != 0) {
                                this.energyStorage.setEnergyStored(this.energyStorage.getEnergyStored() - diff);
                                this.inventoryChanged();
                            }
                        }
                    });
                }

            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ProductionPokemonMachineEntity be) {
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

            PokemonData pokemonData = be.extractPokemonData(pokemonNBT);
            if (pokemonData == null) {
                be.resetMultipliers();
                be.inventoryChanged();
                be.resetTicker();
                return;
            }

            ItemStackHandler areaBlocks = be.getAreaBlocks();

            ProductionInput input = new ProductionInput(areaBlocks,
                    pokemonData.level,
                    pokemonData.type1,
                    pokemonData.baseStats.get(Stats.HP),
                    pokemonData.baseStats.get(Stats.ATTACK),
                    pokemonData.baseStats.get(Stats.DEFENCE),
                    pokemonData.baseStats.get(Stats.SPECIAL_ATTACK),
                    pokemonData.baseStats.get(Stats.SPECIAL_DEFENCE),
                    pokemonData.baseStats.get(Stats.SPEED));

            Optional<ProductionRecipe> recipe = findRecipe(serverLevel, input, pokemonData.type2);

            if (recipe.isPresent()) {
                ProductionRecipe rcp = recipe.get();
                if (rcp.getId().equals(be.currentRecipe)) {
                    Stats focusStat = rcp.getFocusStat();
                    int focusEv = pokemonData.form.getBaseStats().get(focusStat);
                    int focusIv = PokemonNBTUtils.getIVFromNBT(pokemonData.ivs, focusStat);
                    be.updateMultipliers(
                            pokemonNBT,
                            pokemonData.ivs,
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

            be.distributeEnergy();
            be.resetTicker();
        }
    }

    private static Optional<ProductionRecipe> findRecipe(ServerLevel serverLevel, ProductionInput input, ElementalType type2) {
        Optional<ProductionRecipe> recipe = serverLevel.getRecipeManager()
                .getRecipeFor(ProductionRecipe.ProductionRecipeType.INSTANCE, input, serverLevel);
        if (recipe.isEmpty() && type2 != null) {
            input.setType(type2);
            recipe = serverLevel.getRecipeManager()
                    .getRecipeFor(ProductionRecipe.ProductionRecipeType.INSTANCE, input, serverLevel);
        }
        return recipe;
    }

    private void processRecipe(ProductionRecipe rec, ServerLevel serverLevel, BlockPos pos) {
        this.currentTick += this.efficiency * this.tickPerOperation;
        if (this.currentTick >= this.targetTick) {
            int times = (int) (this.currentTick / this.targetTick);
            for (int i = 0; i < times; i++) {
                if (!rec.getResultItems().isEmpty()) {
                    insertListToHandler(rec.getResultItems(), this.itemStackHandler, 0, this.itemStackHandler.getSlots());
                }
                if (rec.getResultPower() > 0) {
                    this.energyStorage.setEnergyStored(this.energyStorage.getEnergyStored() + rec.getResultPower());
                }
                if (rec.getResultFluid() != null) {
                    this.fluidTank.fill(rec.getResultFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                }
            }
            this.currentTick = this.currentTick % this.targetTick;
            serverLevel.playSound(null, pos, CobblemonSounds.EVOLVE, SoundSource.BLOCKS);
        }
    }

    private void startNewRecipe(ProductionRecipe rec) {
        this.currentRecipe = rec.getId();
        this.targetTick = rec.getTick();
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
