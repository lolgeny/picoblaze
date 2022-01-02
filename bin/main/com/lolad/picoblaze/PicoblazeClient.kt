package com.lolad.picoblaze

import com.lolad.picoblaze.core.Events
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient

class PicoblazeClient: ClientModInitializer {
    override fun onInitializeClient() {
        Events.client.emit(MinecraftClient.getInstance())
    }
}