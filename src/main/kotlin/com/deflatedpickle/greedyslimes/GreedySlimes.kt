/* Copyright (c) 2021-2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.greedyslimes

import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

@Suppress("UNUSED")
object GreedySlimes : ModInitializer {
    private const val MOD_ID = "$[id]"
    private const val NAME = "$[name]"
    private const val GROUP = "$[group]"
    private const val AUTHOR = "$[author]"
    private const val VERSION = "$[version]"

    override fun onInitialize(mod: ModContainer) {
        println(listOf(MOD_ID, NAME, GROUP, AUTHOR, VERSION))
    }
}
