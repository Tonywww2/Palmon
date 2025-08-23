package com.tonywww.palmon.block;

import com.tonywww.palmon.block.entites.ProcessingStationEntity;
import com.tonywww.palmon.registeries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProcessingStation extends BaseEntityBlock {
    public ProcessingStation(Properties arg) {
        super(arg);
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    /* FACING */

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    //    @Override
//    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
//        if (pLevel instanceof ServerLevel serverLevel) {
//            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
//            if (blockEntity instanceof ProcessingStationEntity entity) {
//                ItemStack stackInHand = pPlayer.getItemInHand(pHand);
//                AtomicBoolean flag = new AtomicBoolean(false);
//                if (!stackInHand.isEmpty()) {
//                    LazyOptional<IFluidHandlerItem> fluidItem = stackInHand.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
//                    fluidItem.ifPresent(handler -> {
//                        flag.set(true);
//                        int amountToDrain = entity.fluidTank.getCapacity() - entity.fluidTank.getFluidAmount();
//                        int amount = handler.drain(amountToDrain, IFluidHandler.FluidAction.SIMULATE).getAmount();
//                        if (amount > 0) {
//                            entity.fluidTank.fill(handler.drain(amount, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
//                            if (amount <= amountToDrain) {
//                                stackInHand.shrink(1);
//                                pPlayer.getInventory().add(handler.getContainer());
//                            }
//                            entity.inventoryChanged();
//                        }
//
//                    });
//
//                }
//                if (!flag.get()) {
//                    NetworkHooks.openScreen((ServerPlayer) pPlayer, entity, pPos);
//
//                }
//
//            } else {
//                throw new IllegalStateException("Container provider is missing");
//            }
//
//        }
//
//        return InteractionResult.SUCCESS;
//    }
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ProcessingStationEntity entity) {
                ItemStack stackInHand = pPlayer.getItemInHand(pHand);
                if (!stackInHand.isEmpty() && !pPlayer.isShiftKeyDown()) {
                    LazyOptional<IFluidHandlerItem> fluidHandlerItem = stackInHand.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
                    LazyOptional<IItemHandler> playerInventory = pPlayer.getCapability(ForgeCapabilities.ITEM_HANDLER);

                    return fluidHandlerItem.map(handler -> {
                        // Attempt to empty the container into the fluid tank of the block entity
                        FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(stackInHand, entity.fluidTank, playerInventory.orElse(null), Integer.MAX_VALUE, pPlayer, true);
                        if (fluidActionResult.isSuccess()) {
                            // Update the player's hand with the new item container after the transfer
                            pPlayer.setItemInHand(pHand, fluidActionResult.getResult());
                            entity.setChanged(); // Notify that the entity's state has changed
                            pLevel.sendBlockUpdated(pPos, pState, pState, Block.UPDATE_ALL);
                            return InteractionResult.SUCCESS;
                        }
                        return InteractionResult.FAIL;
                    }).orElseGet(() -> {
                        // Open the GUI if the item in hand doesn't have a fluid handler
                        NetworkHooks.openScreen((ServerPlayer) pPlayer, entity, pPos);
                        return InteractionResult.SUCCESS;
                    });
                } else {
                    NetworkHooks.openScreen((ServerPlayer) pPlayer, entity, pPos);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public void onRemove(BlockState pState, Level level, BlockPos pos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof ProcessingStationEntity tile) {
                Containers.dropContents(level, pos, tile.getDroppableInventory());
                level.updateNeighbourForOutputSignal(pos, this);

            }

            super.onRemove(pState, level, pos, pNewState, pIsMoving);
        }
    }

    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.PROCESSING_STATION_ENTITY.get(), ProcessingStationEntity::tick);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.PROCESSING_STATION_ENTITY.get().create(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> toolTips, TooltipFlag flag) {

        toolTips.add(Component.translatable("tooltip.palmon.processing_station"));

        super.appendHoverText(itemStack, blockGetter, toolTips, flag);
    }
}
