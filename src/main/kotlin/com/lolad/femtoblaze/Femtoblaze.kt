package com.lolad.femtoblaze

import com.lolad.femtoblaze.core.Events
import com.lolad.femtoblaze.game_event.GameEvent
import com.lolad.femtoblaze.replace.Replace
import com.lolad.femtoblaze.ride.Ride
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback

class Femtoblaze : ModInitializer {
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