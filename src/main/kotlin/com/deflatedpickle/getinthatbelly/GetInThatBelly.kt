/* Copyright (c) 2021-2022 DeflatedPickle under the CC0 license */

package com.deflatedpickle.getinthatbelly

import net.fabricmc.api.ModInitializer
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.mob.SlimeEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack

@Suppress("UNUSED")
object GetInThatBelly : ModInitializer {
    private const val MOD_ID = "$[id]"
    private const val NAME = "$[name]"
    private const val GROUP = "$[group]"
    private const val AUTHOR = "$[author]"
    private const val VERSION = "$[version]"

    override fun onInitialize() {
        println(listOf(MOD_ID, NAME, GROUP, AUTHOR, VERSION))
    }

    fun collideWithEntity(inventory: SimpleInventory, item: Entity, slime: SlimeEntity) {
        when (item) {
            is ItemEntity -> {
                if (item.world.isClient) {
                    return
                }

                val itemStack: ItemStack = item.stack
                val i = itemStack.count
                if (item.pickupDelay == 0 && (item.owner == null) && inventory
                    .addStack(itemStack) != ItemStack.EMPTY
                ) {
                    slime.sendPickup(item, i)

                    if (itemStack.isEmpty) {
                        item.discard()
                        itemStack.count = i
                    }
                }
            }
        }
    }
}
