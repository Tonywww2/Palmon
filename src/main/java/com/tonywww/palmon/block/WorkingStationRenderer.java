package com.tonywww.palmon.block;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tonywww.palmon.block.entites.WorkingStationEntityPokemon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class WorkingStationRenderer implements BlockEntityRenderer<WorkingStationEntityPokemon> {
    public WorkingStationRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(WorkingStationEntityPokemon blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity.getLevel() != null) {
            PokemonEntity pokemonEntity = blockEntity.getPokemonEntity();
            if (pokemonEntity != null) {
                poseStack.pushPose();
                poseStack.translate(0.5, 0.125, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(pokemonEntity.getYRot()));

//                float scale = 0.75f;
                float scale = blockEntity.getEntityScale();
                poseStack.scale(scale, scale, scale);

                Minecraft.getInstance().getEntityRenderDispatcher().render(
                        pokemonEntity,
                        0, 0, 0,
                        0,
                        partialTicks,
                        poseStack,
                        bufferSource,
                        combinedLight
                );
                poseStack.popPose();

            }
        }

    }

    @Override
    public boolean shouldRenderOffScreen(WorkingStationEntityPokemon arg) {
        return true;
    }

    @Override
    public boolean shouldRender(WorkingStationEntityPokemon arg, Vec3 arg2) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

}
