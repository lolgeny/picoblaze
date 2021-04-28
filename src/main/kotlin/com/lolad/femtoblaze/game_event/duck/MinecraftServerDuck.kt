package com.lolad.femtoblaze.game_event.duck

import com.lolad.femtoblaze.game_event.GameEventManager

interface MinecraftServerDuck {
    fun getGameEventManager(): GameEventManager?
}