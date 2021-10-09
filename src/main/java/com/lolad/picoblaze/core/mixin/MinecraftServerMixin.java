package com.lolad.picoblaze.core.mixin;

import com.lolad.picoblaze.game_event.GameEventManager;
import com.lolad.picoblaze.game_event.duck.MinecraftServerDuck;
import com.lolad.picoblaze.game_event.duck.ServerResourceManagerDuck;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
abstract class MinecraftServerMixin implements MinecraftServerDuck {
    @Accessor("serverResourceManager")
    abstract ServerResourceManager getServerResourceManager();
    @NotNull
    @Override
    public GameEventManager getGameEventManager() {
        return ((ServerResourceManagerDuck) getServerResourceManager()).getGameEventManager();
    }
}