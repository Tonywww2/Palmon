package com.tonywww.palmon.block;

import com.tonywww.palmon.block.entites.ProductionMachineEntity;
import com.tonywww.palmon.registeries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class ProductionMachine extends BaseEntityBlock {
    public ProductionMachine(Properties arg) {
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

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.PRODUCTION_MACHINE_BLOCK_ENTITY.get().create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        if (pLevel instanceof ServerLevel serverLevel) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ProductionMachineEntity entity) {
                ItemStack stackInHand = pPlayer.getItemInHand(pHand);
                AtomicBoolean flag = new AtomicBoolean(false);
                if (!stackInHand.isEmpty()) {
                    LazyOptional<IFluidHandlerItem> fluidItem = stackInHand.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
                    fluidItem.ifPresent(handler -> {
                        flag.set(true);
                        FluidActionResult result = FluidUtil.tryFillContainer(stackInHand, entity.fluidTank, 1000, pPlayer, true);
                        if (result.isSuccess()) {
                            stackInHand.shrink(1);
                            if (!pPlayer.getInventory().add(result.getResult())) {
                                serverLevel.addFreshEntity(new ItemEntity(serverLevel, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), result.getResult()));
                            }
                            entity.inventoryChanged();
                        }

                    });

                }
                if (!flag.get()) {
                    NetworkHooks.openScreen((ServerPlayer) pPlayer, entity, pPos);

                }

            } else {
                throw new IllegalStateException("Container provider is missing");
            }

        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState pState, Level level, BlockPos pos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof ProductionMachineEntity tile) {
                Containers.dropContents(level, pos, tile.getDroppableInventory());
                level.updateNeighbourForOutputSignal(pos, this);

            }

            super.onRemove(pState, level, pos, pNewState, pIsMoving);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.PRODUCTION_MACHINE_BLOCK_ENTITY.get(), ProductionMachineEntity::tick);
    }
}
