package com.tonywww.palmon.block.entites;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class WorkingStationRenderer implements BlockEntityRenderer<WorkingStationEntity> {
    public WorkingStationRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(WorkingStationEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity.getLevel() != null) {
            PokemonEntity pokemonEntity = blockEntity.getPokemonEntity();
            if (pokemonEntity != null) {
                poseStack.pushPose();
                poseStack.translate(0.5, 0.5, 0.5);

                float scale = 1f / pokemonEntity.getPokemon().getScaleModifier();
                poseStack.scale(scale, scale, scale);

                Minecraft.getInstance().getEntityRenderDispatcher().render(
                        pokemonEntity,
                        0, 0, 0,
                        0f,
                        partialTicks,
                        poseStack,
                        bufferSource,
                        combinedLight
                );
                poseStack.popPose();

            }
        }

    }

}
