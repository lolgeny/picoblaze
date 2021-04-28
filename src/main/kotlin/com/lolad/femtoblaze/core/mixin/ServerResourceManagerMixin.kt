package com.lolad.femtoblaze.core.mixin

import com.lolad.femtoblaze.game_event.GameEventManager
import com.lolad.femtoblaze.game_event.duck.ServerResourceManagerDuck
import net.minecraft.resource.ReloadableResourceManager
import net.minecraft.resource.ServerResourceManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.util.registry.DynamicRegistryManager
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ServerResourceManager::class)
abstract class ServerResourceManagerMixin : ServerResourceManagerDuck {
    private var _gameEventManager: GameEventManager? = null
    override fun getGameEventManager(): GameEventManager {
        return _gameEventManager!!
    }

    @Accessor("resourceManager")
    abstract fun getResourceManager(): ReloadableResourceManager
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
        _gameEventManager = GameEventManager()
        getResourceManager().registerReloader(_gameEventManager)
    }
}