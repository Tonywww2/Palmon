package com.tonywww.palmon.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoostFrame extends Block {
    public double efficiency;
    public BoostFrame(Properties arg, double efficiency) {
        super(arg);
        this.efficiency = efficiency;
    }

    private static final VoxelShape SHAPE =  Block.box(0, 0, 0, 16, 8, 16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> toolTips, TooltipFlag flag) {

        toolTips.add(Component.translatable("tooltip.palmon.boost_frame").append(String.valueOf(this.efficiency)));

        super.appendHoverText(itemStack, blockGetter, toolTips, flag);
    }
}
