package com.tonywww.palmon.block.entites;

import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.common.collect.Queues;
import com.tonywww.palmon.api.IEnergyStorage;
import com.tonywww.palmon.api.ProductionInput;
import com.tonywww.palmon.block.BoostFrame;
import com.tonywww.palmon.menu.ProductionMachineContainer;
import com.tonywww.palmon.recipes.ProductionRecipe;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
import java.util.HashMap;
import java.util.Optional;
import java.util.Queue;

import static com.tonywww.palmon.utils.RecipeUtils.insertListToHandler;

public class ProductionMachineEntity extends BasicMachineEntity implements MenuProvider {
    public ItemStackHandler itemStackHandler;
    public IEnergyStorage energyStorage;
    public FluidTank fluidTank;

    private final LazyOptional<ItemStackHandler> itemOptional = LazyOptional.of(() -> this.itemStackHandler);
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

    private ResourceLocation currentRecipe;

    public static int MAX_ENERGY = 5000000;
    public static int MAX_FLUID = 8000;

    public static int MAX_TRANSFER = 5000000;

    public static final double ACCURACY = 100d;
    public static final int RADIUS = 2;
    public static final int HEIGHT = 4;

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
                int diff = Math.min(energy, maxExtract);
                if (!simulate) {
                    this.setEnergyStored(this.getEnergyStored() + diff);
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

    public ProductionMachineEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRODUCTION_MACHINE_BLOCK_ENTITY.get(), pos, state);

        this.itemStackHandler = createItemHandler();
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
        return new ProductionMachineContainer(id, inventory, this, this.dataAccess);
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
                        if (cur.equals(ModBlocks.PRODUCTION_MACHINE.get())) return null;
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

    private final Queue<Direction> directionQueue = Queues.newArrayDeque(Direction.Plane.HORIZONTAL);

    private void distributeEnergy() {
        if (this.getLevel() != null) {
            if (this.energyStorage.getEnergyStored() <= 0) {
                return;
            }
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

    public static void tick(Level level, BlockPos pos, BlockState state, ProductionMachineEntity be) {
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
                        if (areaBlocks != null) {
                            ProductionInput input = new ProductionInput(areaBlocks, pokemonLevel, type1,
                                    baseStats.get(Stats.HP), baseStats.get(Stats.ATTACK), baseStats.get(Stats.DEFENCE),
                                    baseStats.get(Stats.SPECIAL_ATTACK), baseStats.get(Stats.SPECIAL_DEFENCE), baseStats.get(Stats.SPEED));

                            Optional<ProductionRecipe> recipe = serverLevel.getRecipeManager()
                                    .getRecipeFor(ProductionRecipe.ProductionRecipeType.INSTANCE, input, serverLevel);

                            if (recipe.isEmpty() && type2 != null) {
                                input.setType(type2);
                                recipe = serverLevel.getRecipeManager()
                                        .getRecipeFor(ProductionRecipe.ProductionRecipeType.INSTANCE, input, serverLevel);
                            }

                            be.boostMultiplier = be.getBoostMultiplier();
                            be.levelMultiplier = be.getLevelMultiplier(PokemonNBTUtils.getLevelFromNBT(pokemonNBT));
                            be.ovaStatsMultiplier = be.getOvaStatsMultiplier(ivs);

                            if (recipe.isPresent()) {
                                ProductionRecipe rec = recipe.get();
                                if (rec.getId().equals(be.currentRecipe)) {
                                    // keep ticking
                                    be.focusMultiplier = be.getFocusMultiplier(species.getBaseStats().get(rec.getFocusStat()), PokemonNBTUtils.getIVFromNBT(ivs, rec.getFocusStat()));
                                    // equation: boost(1-5) * level(1-2.5) * ovaStats(1-5.5, With sum of Ivs) * focusStats(1-11.0, with the Ev and Iv)
                                    be.efficiency = be.boostMultiplier * be.levelMultiplier * be.ovaStatsMultiplier * be.focusMultiplier;

                                    // Basic Machine multiplier
                                    be.currentTick += be.efficiency * be.tickPerOperation;

                                    // finished a cycle
                                    if (be.currentTick >= be.targetTick) {
                                        int times = (int) (be.currentTick / be.targetTick);
                                        for (int i = 0; i < times; i++) {
                                            if (!rec.getResultItems().isEmpty()) {
                                                insertListToHandler(rec.getResultItems(), be.itemStackHandler);

                                            }
                                            if (rec.getResultPower() > 0) {
                                                be.energyStorage.setEnergyStored(be.energyStorage.getEnergyStored() + rec.getResultPower());

                                            }

                                            if (rec.getResultFluid() != null) {
                                                be.fluidTank.fill(rec.getResultFluid().copy(), IFluidHandler.FluidAction.EXECUTE);

                                            }
                                        }
                                        be.currentTick = be.currentTick % be.targetTick;
                                    }

                                } else {
                                    // new recipe
                                    be.currentRecipe = rec.getId();
                                    be.targetTick = rec.getTick();

                                    be.currentTick = 0;
                                }
                                if (serverLevel.getRandom().nextDouble() < be.FOOD_CONSUME_CHANCE) {
                                    be.setFood(food - be.FOOD_PER_WORKING_TICK);
                                    serverLevel.playSound(null, pos, CobblemonSounds.BERRY_EAT, SoundSource.BLOCKS);

                                }

                            } else {
                                be.focusMultiplier = 0;
                                be.efficiency = 0;
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
                    be.distributeEnergy();
                    be.resetTicker();
                }
            }
        }

    }

}
