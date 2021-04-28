package com.lolad.femtoblaze.core.mixin

import com.lolad.femtoblaze.game_event.GameEventManager
import com.lolad.femtoblaze.game_event.duck.MinecraftServerDuck
import com.lolad.femtoblaze.game_event.duck.ServerResourceManagerDuck
import net.minecraft.resource.ServerResourceManager
import net.minecraft.server.MinecraftServer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(MinecraftServer::class)
abstract class MinecraftServerMixin : MinecraftServerDuck {
    @Accessor("serverResourceManager")
    abstract fun getServerResourceManager(): ServerResourceManager
    override fun getGameEventManager(): GameEventManager? {
        return (getServerResourceManager() as ServerResourceManagerDuck).getGameEventManager()
    }
}