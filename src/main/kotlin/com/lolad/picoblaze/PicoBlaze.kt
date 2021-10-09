package com.lolad.picoblaze

import com.lolad.picoblaze.core.Events
import com.lolad.picoblaze.game_event.GameEvent
import com.lolad.picoblaze.replace.Replace
import com.lolad.picoblaze.ride.Ride
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback

class PicoBlaze : ModInitializer {
    override fun onInitialize() {
        // MODULES
        Replace
        Ride
        GameEvent
        // ------


        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _ ->
            Events.command.emit(dispatcher)
        })
    }
}