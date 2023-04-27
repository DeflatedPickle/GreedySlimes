/* Copyright (c) 2021-2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.greedyslimes.mixin;

import com.deflatedpickle.greedyslimes.ai.PickupItemGoal;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"UnusedMixin", "unused", "rawtypes", "SpellCheckingInspection"})
@Mixin(SlimeEntity.class)
public abstract class MixinSlimeEntity extends MobEntity {
  private static final Predicate<ItemEntity> PICKABLE_DROP_FILTER =
      (item) -> !item.cannotPickup() && item.isAlive();

  protected MixinSlimeEntity(EntityType<? extends MobEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "<init>", at = @At("TAIL"))
  public void onInit(EntityType entityType, World world, CallbackInfo ci) {
    this.setCanPickUpLoot(true);
  }

  @Inject(method = "initGoals", at = @At("TAIL"))
  public void onInitGoals(CallbackInfo ci) {
    this.goalSelector.add(2, new PickupItemGoal(this, PICKABLE_DROP_FILTER, 1.0, 10, 10.0));
  }

  @Override
  public boolean canPickupItem(ItemStack stack) {
    Item item = stack.getItem();
    ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
    return itemStack.isEmpty();
  }

  private void spit(ItemStack stack) {
    if (!stack.isEmpty() && !this.world.isClient) {
      ItemEntity itemEntity =
          new ItemEntity(
              this.world,
              this.getX() + this.getRotationVector().x,
              this.getY() + 1.0,
              this.getZ() + this.getRotationVector().z,
              stack);
      itemEntity.setPickupDelay(40);
      itemEntity.setThrower(this.getUuid());
      this.world.spawnEntity(itemEntity);
    }
  }

  private void dropItem(ItemStack stack) {
    ItemEntity itemEntity =
        new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), stack);
    this.world.spawnEntity(itemEntity);
  }

  protected void loot(ItemEntity item) {
    ItemStack itemStack = item.getStack();
    if (this.canPickupItem(itemStack)) {
      int i = itemStack.getCount();
      if (i > 1) {
        this.dropItem(itemStack.split(i - 1));
      }

      this.spit(this.getEquippedStack(EquipmentSlot.MAINHAND));
      this.triggerItemPickedUpByEntityCriteria(item);
      this.equipStack(EquipmentSlot.MAINHAND, itemStack.split(1));
      this.updateDropChances(EquipmentSlot.MAINHAND);
      this.sendPickup(item, itemStack.getCount());
      item.discard();
    }
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    this.getMainHandStack().writeNbt(nbt);
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    this.setStackInHand(Hand.MAIN_HAND, ItemStack.fromNbt(nbt));
  }
}
