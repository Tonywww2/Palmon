package com.tonywww.palmon.block.entites;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.tonywww.palmon.item.LaborContract;
import com.tonywww.palmon.menu.WorkingStationContainer;
import com.tonywww.palmon.registeries.ModBlockEntities;
import com.tonywww.palmon.registeries.ModItems;
import com.tonywww.palmon.utils.PokemonNBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class WorkingStationEntity extends BasicPokemonMachineEntity implements MenuProvider {
    public final ItemStackHandler itemStackHandler;
    private final LazyOptional<ItemStackHandler> handler;

    protected final ContainerData dataAccess;

    public int food;
    public static final int MAX_FOOD = 1000;
    public static final int FOOD_VALUE = 8;

    private PokemonEntity pokemonEntity;
    private float entityScale;

    public static final float SCALE_THRESHOLD = 1.25f;

    private int machineType = -1;

    public WorkingStationEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WORKING_STATION_BLOCK_ENTITY.get(), pos, state);
        this.itemStackHandler = createHandler();
        this.handler = LazyOptional.of(() -> itemStackHandler);

        this.food = 0;


        this.dataAccess = new ContainerData() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0 -> {
                        return food;
                    }
                }
                return 0;
            }

            @Override
            public void set(int index, int val) {
                switch (index) {
                    case 0:
                        food = val;
                        break;
                }

            }

            @Override
            public int getCount() {
                return 1;
            }
        };

    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                inventoryChanged();
                if (level != null && level instanceof ServerLevel serverLevel) {
                    if (slot == 0) {
                        serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                    }
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return (slot == 0 && stack.is(ModItems.LABOR_CONTRACT.get()) ||
                        slot == 1 && stack.is(ModItems.POKE_FOOD.get()));
            }

            @Override
            public int getSlotLimit(int slot) {
                return switch (slot) {
                    case 0 -> 1;
                    case 1 -> 64;
                    default -> 1;
                };
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

    public static void tick(Level level, BlockPos pos, BlockState state, WorkingStationEntity be) {
        if (level instanceof ServerLevel serverLevel) {
            be.tickBase(1);
            if (be.isWorkingTick()) {
                ItemStack itemStack = be.itemStackHandler.getStackInSlot(1);
                if (itemStack.is(ModItems.POKE_FOOD.get())) {
                    int numToUse = Math.min((MAX_FOOD - be.food) / FOOD_VALUE, itemStack.getCount());
                    itemStack.shrink(numToUse);

                    be.food += numToUse * FOOD_VALUE;

                }
                be.resetTicker();
            }
        } else {
            if (be.pokemonEntity != null) {
                be.pokemonEntity.tick();

            }

        }
    }

    @Override
    public void load(CompoundTag tag) {
        itemStackHandler.deserializeNBT(tag.getCompound("inv"));
        if (!itemStackHandler.getStackInSlot(0).isEmpty() && this.getPokemonNBT() != null) {
            updatePokemonEntity();

        } else {
            this.pokemonEntity = null;
        }

        this.food = tag.getInt("food");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inv", itemStackHandler.serializeNBT());

        tag.putInt("food", food);

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
        if (this.getLevel() != null && this.getPokemonNBT() != null && !this.getPokemonNBT().isEmpty()) {
            Pokemon pokemon = PokemonNBTUtils.loadSafePokemon(this.getPokemonNBT(), this.getLevel().isClientSide());
            if (pokemon == null) return;
            float angle = switch (this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                case NORTH -> 0;
                case EAST -> 90f;
                case SOUTH -> 180f;
                case WEST -> 270f;
                default -> 0f;
            };
            this.pokemonEntity = new PokemonEntity(this.getLevel(), pokemon, CobblemonEntities.POKEMON) {
                @Override
                public boolean shouldRender(double d, double e, double f) {
                    return true;
                }

                @Override
                public void tick() {
                    super.tick();
                }

                @Override
                public void onAddedToWorld() {
                    super.onAddedToWorld();
                    this.yHeadRot = angle;
                    this.yHeadRotO = angle;
                    this.yBodyRot = angle;
                    this.yBodyRotO = angle;
                    this.setTicksLived(200);
                }
            };
            this.pokemonEntity.setNoAi(true);
            this.pokemonEntity.onAddedToWorld();

            if (this.pokemonEntity.getBoundingBox().getXsize() > SCALE_THRESHOLD || this.pokemonEntity.getBoundingBox().getYsize() > SCALE_THRESHOLD) {
                this.entityScale = (float) (2f /
                        (this.pokemonEntity.getBoundingBox().getXsize() + this.pokemonEntity.getBoundingBox().getZsize()));

            } else {
                this.entityScale = 1f;
            }

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
        return new WorkingStationContainer(id, inventory, this, dataAccess);
    }

    public CompoundTag getPokemonNBT() {
        return LaborContract.getPokemonNBT(this.itemStackHandler.getStackInSlot(0));

    }

    public PokemonEntity getPokemonEntity() {
        return this.pokemonEntity;
    }

    public float getEntityScale() {
        return entityScale;
    }

    public int getMachineType() {
        return machineType;
    }

    public void setMachineType(int machineType) {
        this.machineType = machineType;
    }
}
