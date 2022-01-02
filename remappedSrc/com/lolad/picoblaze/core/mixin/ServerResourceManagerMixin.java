package com.lolad.picoblaze.core.mixin;

import com.lolad.picoblaze.game_event.GameEventManager;
import com.lolad.picoblaze.game_event.duck.ServerResourceManagerDuck;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerResourceManager.class)
abstract class ServerResourceManagerMixin implements ServerResourceManagerDuck {
    private Optional<GameEventManager> _gameEventManager = Optional.empty();
    @Override
    public GameEventManager getGameEventManager() {
        return _gameEventManager.orElseThrow();
    }

    @Accessor("resourceManager")
    abstract ReloadableResourceManager getResourceManager();
    @Inject(
        method = "<init>(Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/server/command/CommandManager$RegistrationEnvironment;I)V",
        at = @At("RETURN")
    )
    private void init(
        DynamicRegistryManager dynamicRegistryManager,
        RegistrationEnvironment registrationEnvironment,
        int i,
        CallbackInfo ci
    ) {
        _gameEventManager = Optional.of(new GameEventManager());
        getResourceManager().registerReloader(getGameEventManager());
    }
}