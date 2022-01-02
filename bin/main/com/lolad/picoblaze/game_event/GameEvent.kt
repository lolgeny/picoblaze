package com.lolad.picoblaze.game_event

import com.lolad.picoblaze.core.Module
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextType

object GameEvent: Module() {
    val EVENT_CONTEXT: LootContextType = LootContextType.Builder()
        .require(LootContextParameters.ORIGIN)
        .allow(LootContextParameters.THIS_ENTITY)
        .build()
}