package com.lolad.picoblaze.core.command

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource

typealias Ctx = CommandContext<ServerCommandSource>

fun isOp(ctx: ServerCommandSource): Boolean = ctx.hasPermissionLevel(2)

fun <T> defaultGet(ctx: Ctx, name: String, default: T, get: (Ctx, String) -> T): T {
    return try {
        get(ctx, name)
    } catch (_: IllegalArgumentException) {
        default
    }
}