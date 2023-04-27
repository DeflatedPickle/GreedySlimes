/* Copyright (c) 2022-2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.greedyslimes.ai

import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot.MAINHAND
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.ai.goal.Goal.Control.MOVE
import net.minecraft.entity.mob.MobEntity
import net.minecraft.item.ItemStack
import java.util.EnumSet
import java.util.function.Predicate

// Modified from: FoxEntity#PickupItemGoal
class PickupItemGoal(
    private val entity: MobEntity,
    private val predicate: Predicate<ItemEntity>,
    private val speed: Double,
    private val chance: Int,
    private val radius: Double,
) : Goal() {
    init {
        controls = EnumSet.of(MOVE)
    }

    override fun canStart(): Boolean {
        return if (!entity.getEquippedStack(MAINHAND).isEmpty) {
            false
        } else if (entity.target == null && entity.attacker == null) {
            if (entity.random.nextInt(toGoalTicks(chance)) != 0) {
                false
            } else {
                val list: List<ItemEntity> = entity.world.getEntitiesByClass(
                    ItemEntity::class.java,
                    entity.boundingBox.expand(radius, radius, radius),
                    predicate
                )
                list.isNotEmpty() && entity.getEquippedStack(MAINHAND).isEmpty
            }
        } else {
            false
        }
    }

    override fun tick() {
        val list: List<ItemEntity> = entity.world.getEntitiesByClass(
            ItemEntity::class.java,
            entity.boundingBox.expand(radius, radius, radius),
            predicate
        )
        val itemStack: ItemStack = entity.getEquippedStack(MAINHAND)
        if (itemStack.isEmpty && list.isNotEmpty()) {
            entity.navigation.startMovingTo(list.sortedBy { it.stack.rarity }.last() as Entity, speed)
        }
    }

    override fun start() {
        val list: List<ItemEntity> = entity.world.getEntitiesByClass(
            ItemEntity::class.java,
            entity.boundingBox.expand(radius, radius, radius),
            predicate
        )
        if (list.isNotEmpty()) {
            entity.navigation.startMovingTo(list.sortedBy { it.stack.rarity }.last() as Entity, speed)
        }
    }
}
