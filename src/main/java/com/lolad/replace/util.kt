package com.lolad.replace

import com.lolad.replace.duck.ServerCommandSourceDuck
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import net.minecraft.command.argument.*
import net.minecraft.nbt.*
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.text.Texts
import net.minecraft.text.TranslatableText

typealias Ctx = CommandContext<ServerCommandSource>

val SCORE_NOT_EXIST_EXCEPTION = Dynamic2CommandExceptionType { target, objective ->
    TranslatableText("commands.execute.replace.score.null", arrayOf(target, objective))
}
val NOT_BLOCK_ENTITY_EXCEPTION = DynamicCommandExceptionType { position ->
    TranslatableText("commands.execute.replace.block.null", position)
}

fun replaceScore(ctx: Ctx): String {
    val target = ScoreHolderArgumentType.getScoreHolder(ctx, "target")
    val obj = ScoreboardObjectiveArgumentType.getObjective(ctx, "objective")
    val scoreboard = ctx.source.minecraftServer.scoreboard
    if (scoreboard.playerHasObjective(target, obj)) {
        val score = scoreboard.getPlayerScore(target, obj)
        return score.score.toString()
    } else {
        throw SCORE_NOT_EXIST_EXCEPTION.create(target, obj)
    }
}

enum class DataType(val arg: String) {
    Block("pos"), Entity("target"), Storage("storage")
}

fun replaceData(ty: DataType): (Ctx) -> String {
    return { ctx ->
        var target = NbtCompound()
        when (ty) {
            DataType.Entity -> EntityArgumentType.getEntity(ctx, ty.arg).writeNbt(target)
            DataType.Block -> {
                val pos = BlockPosArgumentType.getBlockPos(ctx, ty.arg)
                (
                    ctx.source.world.getBlockEntity(pos) ?: throw NOT_BLOCK_ENTITY_EXCEPTION.create(pos)
                ).readNbt(target)
            }
            DataType.Storage -> {
                target = ctx.source.minecraftServer.dataCommandStorage.get(
                    IdentifierArgumentType.getIdentifier(ctx, ty.arg)
                )
            }
        }
        val path = NbtPathArgumentType.getNbtPath(ctx, "path")
        var data = when (val p = path.get(target).first()) {
            is NbtByte -> p.byteValue().toString()
            is NbtDouble -> p.doubleValue().toString()
            is NbtFloat -> p.floatValue().toString()
            is NbtInt -> p.intValue().toString()
            is NbtLong -> p.longValue().toString()
            is NbtShort -> p.shortValue().toString()
            else -> p.asString()
        }
        val interpret = BoolArgumentType.getBool(ctx, "interpret")
        if (interpret) {
            data = Texts.parse(ctx.source, Text.Serializer.fromJson(data), ctx.source.entity, 0).toString()
        }
        data
    }
}

fun replaceComponent(ctx: Ctx): String {
    val component = TextArgumentType.getTextArgument(ctx, "component")
    return Texts.parse(ctx.source, component, ctx.source.entity, 0).string
}

fun runReplaced(replace: (Ctx) -> String, ctx: Ctx): Int {
    val replacement = replace(ctx)
    ctx.source.position
    val name = IdentifierArgumentType.getIdentifier(ctx, "var")
    val varName = when (name.namespace) {
        "minecraft" -> name.path
        else -> name.toString()
    }
    (ctx.source as ServerCommandSourceDuck).replacements[varName] = replacement
    return eval(true)(ctx)
}

fun eval(execute: Boolean): (Ctx) -> Int {
    return { ctx ->
        println("eeee")
        var command = ctx.getArgument("command", String::class.java)
        if (execute) {
            command = "execute $command"
        }
        println("command $command, map ${(ctx.source as ServerCommandSourceDuck).replacements}")
        for ((varName, replacement) in (ctx.source as ServerCommandSourceDuck).replacements) {
            command = command.replace("$$varName", replacement)
        }
        println("now $command")
        ctx.source.minecraftServer.commandManager.execute(ctx.source, command)
    }
}