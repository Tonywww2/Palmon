package com.tonywww.palmon.block;

import com.tonywww.palmon.block.entites.ProcessingStationEntity;
import com.tonywww.palmon.block.entites.ProductionMachineEntity;
import com.tonywww.palmon.block.entites.WorkingStationEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoostFrame extends Block {
    public double efficiency;

    public BoostFrame(Properties arg, double efficiency) {
        super(arg);
        this.efficiency = efficiency;
    }

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        if (!pLevel.isClientSide) {
            BlockPos below = pPos.below();
            BlockEntity blockEntity = pLevel.getBlockEntity(below);
            if (blockEntity instanceof ProductionMachineEntity productionMachine) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, productionMachine, below);
                return InteractionResult.SUCCESS;
            } else if (blockEntity instanceof ProcessingStationEntity processingStationEntity) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, processingStationEntity, below);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;

        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> toolTips, TooltipFlag flag) {

        toolTips.add(Component.translatable("tooltip.palmon.boost_frame").append(String.valueOf(this.efficiency)));

        super.appendHoverText(itemStack, blockGetter, toolTips, flag);
    }
}
