/* Copyright (c) 2021-2022 DeflatedPickle under the CC0 license */

package com.deflatedpickle.getinthatbelly.mixin;

import com.deflatedpickle.getinthatbelly.GetInThatBelly;
import java.util.List;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"UnusedMixin", "unused", "rawtypes"})
@Mixin(SlimeEntity.class)
public abstract class MixinSlimeEntity extends MobEntity
    implements InventoryOwner, InventoryChangedListener {
  @Shadow @Final private static TrackedData<Integer> SLIME_SIZE;
  private SimpleInventory inventory;

  protected MixinSlimeEntity(EntityType<? extends MobEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "<init>", at = @At("TAIL"))
  public void onInit(EntityType entityType, World world, CallbackInfo ci) {
    inventory = new SimpleInventory(this.dataTracker.get(SLIME_SIZE));
    inventory.addListener(this);
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.put("Inventory", this.inventory.toNbtList());
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    this.inventory.readNbtList(nbt.getList("Inventory", 10));
  }

  @Override
  public void tickMovement() {
    super.tickMovement();

    Box box =
        this.hasVehicle() && !Objects.requireNonNull(this.getVehicle()).isRemoved()
            ? this.getBoundingBox().union(this.getVehicle().getBoundingBox()).expand(1.0, 0.0, 1.0)
            : this.getBoundingBox().expand(0.5, 0.5, 0.5);
    List<Entity> list = this.world.getOtherEntities(this, box);
    for (Entity entity : list) {
      if (entity.isRemoved()) continue;
      this.collideWithEntity(entity);
    }
  }

  @Override
  protected void dropInventory() {
    super.dropInventory();
    this.inventory.clearToList().forEach(this::dropStack);
  }

  @Override
  public SimpleInventory getInventory() {
    return inventory;
  }

  public void onInventoryChanged(Inventory var1) {}

  private void collideWithEntity(Entity entity) {
    if (inventory == null) return;
    GetInThatBelly.INSTANCE.collideWithEntity(inventory, entity, (SlimeEntity) (Object) this);
  }
}
