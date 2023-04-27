/* Copyright (c) 2022-2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.greedyslimes.mixin;

import com.deflatedpickle.greedyslimes.client.ItemFeatureRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeEntityRenderer.class)
public abstract class MixinSlimeEntityRenderer
    extends MobEntityRenderer<SlimeEntity, SlimeEntityModel<SlimeEntity>> {
  public MixinSlimeEntityRenderer(
      Context context, SlimeEntityModel<SlimeEntity> entityModel, float f) {
    super(context, entityModel, f);
  }

  @Inject(method = "<init>", at = @At("TAIL"))
  public void addItemFeature(Context context, CallbackInfo ci) {
    addFeature(new ItemFeatureRenderer<>((SlimeEntityRenderer) (Object) this));
  }
}
