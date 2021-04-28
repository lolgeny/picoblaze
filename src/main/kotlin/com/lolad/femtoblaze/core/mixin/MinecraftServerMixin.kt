package com.lolad.femtoblaze.core.mixin

import org.spongepowered.asm.mixin.Mixin
import net.minecraft.server.MinecraftServer
import com.lolad.femtoblaze.game_event.duck.MinecraftServerDuck
import net.minecraft.resource.ServerResourceManager
import com.lolad.femtoblaze.game_event.GameEventManager
import com.lolad.femtoblaze.game_event.duck.ServerResourceManagerDuck

@Mixin(MinecraftServer::class)
abstract class MinecraftServerMixin : MinecraftServerDuck {
    @get:Accessor("serverResourceManager")
    abstract val serverResourceManager: ServerResourceManager
    override fun getGameEventManager(): GameEventManager {
        return (serverResourceManager as ServerResourceManagerDuck).gameEventManager
    }
}