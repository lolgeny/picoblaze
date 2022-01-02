package com.lolad.picoblaze.game_event.duck

import com.lolad.picoblaze.game_event.GameEventManager

interface MinecraftServerDuck {
    fun getGameEventManager(): GameEventManager
}