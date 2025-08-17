package com.tonywww.palmon.block;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.block.entites.WorkingStationEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import static com.tonywww.palmon.PalmonConfig.workingStationRenderDistant;

public class WorkingStationRenderer implements BlockEntityRenderer<WorkingStationEntity> {

    private static final ResourceLocation[] WORKING_TEXTURES = new ResourceLocation[]{
            new ResourceLocation(Palmon.MOD_ID, "textures/gui/production_working.png"),
            new ResourceLocation(Palmon.MOD_ID, "textures/gui/processing_working.png")
    };



    public WorkingStationRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(WorkingStationEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity.getLevel() != null) {
            PokemonEntity pokemonEntity = blockEntity.getPokemonEntity();
            if (pokemonEntity != null) {
                poseStack.pushPose();
                poseStack.translate(0.5, 0.0625, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(pokemonEntity.getYRot()));

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
//                if (machineType > 0) {
//                    renderWorking(machineType, poseStack, bufferSource, combinedLight);
//
//                }

            }
        }

    }

//    private static void renderWorking(int type, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
//        poseStack.pushPose();
//        poseStack.translate(0.5, 2.0, 0.5);
//        poseStack.scale(1.5f, 1.5f, 1.5f);
//        poseStack.mulPose(Axis.YP.rotationDegrees(0.5f));
//
//        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(WORKING_TEXTURES[type]));
//        Matrix4f matrix = poseStack.last().pose();
//
//        float size = 0.5f;
//
//        // 正面
//        vertexConsumer.vertex(matrix, -size, -size, 0)
//                .color(255, 255, 255, 255)
//                .uv(0, 1)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(combinedLight).normal(0, 0, 1)
//                .endVertex();
//        vertexConsumer.vertex(matrix, -size, size, 0)
//                .color(255, 255, 255, 255)
//                .uv(0, 0)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(combinedLight)
//                .normal(0, 0, 1)
//                .endVertex();
//        vertexConsumer.vertex(matrix, size, size, 0)
//                .color(255, 255, 255, 255)
//                .uv(1, 0)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(combinedLight)
//                .normal(0, 0, 1)
//                .endVertex();
//        vertexConsumer.vertex(matrix, size, -size, 0)
//                .color(255, 255, 255, 255)
//                .uv(1, 1)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(combinedLight)
//                .normal(0, 0, 1)
//                .endVertex();
//
//        // 背面
//        vertexConsumer.vertex(matrix, size, -size, 0)
//                .color(255, 255, 255, 255)
//                .uv(1, 1)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(combinedLight)
//                .normal(0, 0, 1)
//                .endVertex();
//        vertexConsumer.vertex(matrix, size, size, 0)
//                .color(255, 255, 255, 255)
//                .uv(1, 0)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(combinedLight)
//                .normal(0, 0, 1)
//                .endVertex();
//        vertexConsumer.vertex(matrix, -size, size, 0)
//                .color(255, 255, 255, 255)
//                .uv(0, 0)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(combinedLight)
//                .normal(0, 0, 1)
//                .endVertex();
//        vertexConsumer.vertex(matrix, -size, -size, 0)
//                .color(255, 255, 255, 255)
//                .uv(0, 1)
//                .overlayCoords(OverlayTexture.NO_OVERLAY)
//                .uv2(combinedLight)
//                .normal(0, 0, 1)
//                .endVertex();
//
//        poseStack.popPose();
//    }

    @Override
    public boolean shouldRenderOffScreen(WorkingStationEntity arg) {
        return true;
    }

    @Override
    public boolean shouldRender(WorkingStationEntity arg, Vec3 arg2) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return workingStationRenderDistant.get();
    }

}
