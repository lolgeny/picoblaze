package com.lolad.picoblaze.core

import com.lolad.picoblaze.PicoBlaze
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.client.MinecraftClient
import net.minecraft.server.command.ServerCommandSource

object Events {
    val command: Hook<CommandDispatcher<ServerCommandSource>> = Hook()
    val client: Hook<MinecraftClient> = Hook()
    val init: Hook<PicoBlaze> = Hook()
}

open class Module {
    init {
        Events.command.subscribe(::command)
        Events.client.subscribe(::client)
        Events.init.subscribe(::init)
    }
    open fun command(dispatcher: CommandDispatcher<ServerCommandSource>) {}
    open fun client(client: MinecraftClient) {}
    open fun init(mod: PicoBlaze) {}
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
