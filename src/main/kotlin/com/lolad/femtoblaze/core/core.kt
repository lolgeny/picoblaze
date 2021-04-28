package com.lolad.femtoblaze.core

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource

object Events {
    val command: Hook<CommandDispatcher<ServerCommandSource>> = Hook()
}

open class Module {
    init {
        Events.command.subscribe(::command)
    }
    open fun command(dispatcher: CommandDispatcher<ServerCommandSource>) {}
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