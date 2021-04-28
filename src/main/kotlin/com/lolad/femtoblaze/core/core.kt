package com.lolad.femtoblaze.core

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.client.MinecraftClient
import net.minecraft.server.command.ServerCommandSource

object Events {
    val command: Hook<CommandDispatcher<ServerCommandSource>> = Hook()
    val client: Hook<MinecraftClient> = Hook()
}

open class Module {
    init {
        Events.command.subscribe(::command)
        Events.client.subscribe(::client)
    }
    open fun command(dispatcher: CommandDispatcher<ServerCommandSource>) {}
    open fun client(client: MinecraftClient) {}
}

class Hook<A> {
    private val subscribers: MutableSet<(A) -> Unit> = mutableSetOf()
    fun subscribe(f: (A) -> Unit) {
        subscribers.add(f)
    }
    fun emit(arg: A) {
        for (subscriber in subscribers) {
            subscriber(arg)
        }
    }
}
