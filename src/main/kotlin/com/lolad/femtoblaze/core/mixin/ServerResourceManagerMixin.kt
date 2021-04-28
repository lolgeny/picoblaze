package com.lolad.femtoblaze.core.mixin

import org.spongepowered.asm.mixin.Mixin
import net.minecraft.resource.ServerResourceManager
import com.lolad.femtoblaze.game_event.duck.ServerResourceManagerDuck
import com.lolad.femtoblaze.game_event.GameEventManager
import net.minecraft.resource.ReloadableResourceManager
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import net.minecraft.util.registry.DynamicRegistryManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ServerResourceManager::class)
abstract class ServerResourceManagerMixin : ServerResourceManagerDuck {
    private var gameEventManager: GameEventManager? = null
    override fun getGameEventManager(): GameEventManager {
        return gameEventManager!!
    }

    @get:Accessor("resourceManager")
    abstract val resourceManager: ReloadableResourceManager
    @Inject(
        method = ["<init>(Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/server/command/CommandManager\$RegistrationEnvironment;I)V"],
        at = [At("RETURN")]
    )
    private fun init(
        dynamicRegistryManager: DynamicRegistryManager,
        registrationEnvironment: RegistrationEnvironment,
        i: Int,
        ci: CallbackInfo
    ) {
        gameEventManager = GameEventManager()
        resourceManager.registerReloader(gameEventManager)
    }
}