package com.lolad.femtoblaze.game_event

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.gson.JsonElement
import net.minecraft.loot.LootGsons
import net.minecraft.loot.condition.LootCondition
import net.minecraft.resource.JsonDataLoader
import net.minecraft.resource.ResourceManager
import net.minecraft.server.function.CommandFunction.LazyContainer
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import java.util.function.Consumer

class GameEventManager : JsonDataLoader(GSON, "game_events") {
    var gameEvents: Map<Identifier, GameEventFunction> = ImmutableMap.of()
    override fun apply(loader: Map<Identifier, JsonElement>, manager: ResourceManager, profiler: Profiler) {
        val game_events = ImmutableMap.builder<Identifier, GameEventFunction>()
        loader.forEach { (id: Identifier, json: JsonElement) ->
            try {
                val predicates = ImmutableSet.builder<LootCondition>()
                if (json.asJsonObject.has("conditions")) {
                    json.asJsonObject["conditions"].asJsonArray.forEach(
                        Consumer { predicate: JsonElement? ->
                            predicates.add(
                                GSON.fromJson(
                                    predicate,
                                    LootCondition::class.java
                                )
                            )
                        }
                    )
                }
                game_events.put(
                    id, GameEventFunction(
                        Registry.GAME_EVENT[Identifier.tryParse(
                            json
                                .asJsonObject["event"]
                                .asString
                        )],
                        LazyContainer(
                            Identifier.tryParse(
                                json
                                    .asJsonObject["function"]
                                    .asString
                            )
                        ),
                        predicates.build()
                    )
                )
            } catch (e: Exception) {
                LOGGER.error("Couldn't pass game event handler {}", id, e)
            }
        }
        this.gameEvents = game_events.build()
    }

    companion object {
        private val LOGGER = LogManager.getLogger()
        private val GSON = LootGsons.getTableGsonBuilder().create()
    }
}