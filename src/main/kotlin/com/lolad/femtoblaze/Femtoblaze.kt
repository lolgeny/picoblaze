package com.lolad.femtoblaze

import com.lolad.femtoblaze.core.Events
import com.lolad.femtoblaze.replace.Replace
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback

class Femtoblaze : ModInitializer {
    override fun onInitialize() {
        // MODULES
        Replace
        // ------


        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _ ->
            Events.command.emit(dispatcher)
        })
    }
}