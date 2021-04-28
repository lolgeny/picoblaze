package com.lolad.femtoblaze

import com.lolad.femtoblaze.core.Events
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient

class FemtoblazeClient: ClientModInitializer {
    override fun onInitializeClient() {
        Events.client.emit(MinecraftClient.getInstance())
    }
}