package com.lolad.femtoblaze.game_event

import com.google.common.collect.ImmutableSet
import net.minecraft.loot.condition.LootCondition
import net.minecraft.server.function.CommandFunction.LazyContainer
import net.minecraft.world.event.GameEvent

class GameEventFunction(val trigger: GameEvent, val function: LazyContainer, val predicate: ImmutableSet<LootCondition>)