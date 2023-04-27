/* Copyright (c) 2022-2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.greedyslimes.client

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.SlimeEntityModel
import net.minecraft.client.render.model.json.ModelTransformation.Mode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.mob.SlimeEntity
import net.minecraft.util.math.Quaternion

class ItemFeatureRenderer<T : SlimeEntity, M : SlimeEntityModel<T>>(
    context: FeatureRendererContext<T, M>
) : FeatureRenderer<T, M>(context) {
    override fun render(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        entity: T,
        limbAngle: Float,
        limbDistance: Float,
        tickDelta: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        matrices.push()

        matrices.translate(0.0, 1.0, 0.0)
        val s = 0.4f
        matrices.scale(s, s, s)
        matrices.multiply(Quaternion.fromEulerXyz(0f, 0f, 45f))

        MinecraftClient.getInstance()
            .itemRenderer
            .renderItem(
                entity,
                entity.mainHandStack,
                Mode.NONE,
                false,
                matrices,
                vertexConsumers,
                entity.world,
                light,
                OverlayTexture.DEFAULT_UV,
                0
            )

        matrices.pop()
    }
}
