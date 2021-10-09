package com.lolad.picoblaze.replace

import com.lolad.picoblaze.core.Module
import com.lolad.picoblaze.core.command.Ctx
import com.lolad.picoblaze.core.command.FixedTextArgumentType
import com.lolad.picoblaze.core.command.GreedyArgumentType
import com.lolad.picoblaze.replace.duck.ServerCommandSourceDuck
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.argument.*
import net.minecraft.nbt.*
import net.minecraft.scoreboard.ScoreboardObjective
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.text.Texts
import net.minecraft.text.TranslatableText


object Replace: Module() {
    override fun command(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("execute")
                .then(
                    CommandManager.literal("replace")
                        .then(
                            CommandManager.argument("var", IdentifierArgumentType.identifier())
                                .then(
                                    CommandManager.literal("score")
                                        .then(
                                            CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder())
                                                .suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
                                                .then(
                                                    CommandManager.argument(
                                                        "objective",
                                                        ScoreboardObjectiveArgumentType.scoreboardObjective()
                                                    )
                                                        .redirect(dispatcher.findNode(listOf("execute"))) {
                                                            runReplaced(::replaceScore, it)
                                                        }
                                                )
                                        )
                                )
                                .then(
                                    CommandManager.literal("data")
                                        .then(
                                            CommandManager.literal("entity")
                                                .then(
                                                    CommandManager.argument("target", EntityArgumentType.entity())
                                                        .then(
                                                            CommandManager.argument(
                                                                "path",
                                                                NbtPathArgumentType.nbtPath()
                                                            )
                                                                .then(
                                                                    CommandManager.argument(
                                                                        "interpret",
                                                                        BoolArgumentType.bool()
                                                                    )
                                                                        .redirect(dispatcher.findNode(listOf("execute"))) {
                                                                            runReplaced(
                                                                                replaceData(DataType.Entity),
                                                                                it
                                                                            )
                                                                        }
                                                                )
                                                        )
                                                )
                                        )
                                        .then(
                                            CommandManager.literal("block")
                                                .then(
                                                    CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                                        .then(
                                                            CommandManager.argument(
                                                                "path",
                                                                NbtPathArgumentType.nbtPath()
                                                            )
                                                                .then(
                                                                    CommandManager.argument(
                                                                        "interpret",
                                                                        BoolArgumentType.bool()
                                                                    )
                                                                        .redirect(dispatcher.findNode(listOf("execute"))) {
                                                                            runReplaced(
                                                                                replaceData(DataType.Block),
                                                                                it
                                                                            )
                                                                        }
                                                                )
                                                        )
                                                )
                                        )
                                        .then(
                                            CommandManager.literal("storage")
                                                .then(
                                                    CommandManager.argument(
                                                        "storage",
                                                        IdentifierArgumentType.identifier()
                                                    )
                                                        .then(
                                                            CommandManager.argument(
                                                                "path",
                                                                NbtPathArgumentType.nbtPath()
                                                            )
                                                                .then(
                                                                    CommandManager.argument(
                                                                        "interpret",
                                                                        BoolArgumentType.bool()
                                                                    )
                                                                        .redirect(dispatcher.findNode(listOf("execute"))) {
                                                                            runReplaced(
                                                                                replaceData(DataType.Storage),
                                                                                it
                                                                            )
                                                                        }
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    CommandManager.literal("text")
                                        .then(
                                            CommandManager.argument("component", FixedTextArgumentType())
                                                .redirect(dispatcher.findNode(listOf("execute"))) {
                                                    runReplaced(::replaceComponent, it)
                                                }
                                        )
                                )
                        )
                )
        )
        dispatcher.register(
            CommandManager.literal("eval")
                .then(
                    CommandManager.argument("command", GreedyArgumentType())
                        .executes(::eval)
                )
        )
    }
}

val SCORE_NOT_EXIST_EXCEPTION = Dynamic2CommandExceptionType { target, objective ->
    TranslatableText("commands.execute.replace.score.null", (objective as ScoreboardObjective).name, target)
}
val NOT_BLOCK_ENTITY_EXCEPTION = SimpleCommandExceptionType(
    TranslatableText("commands.execute.replace.block.null")
)

fun replaceScore(ctx: Ctx): String {
    val target = ScoreHolderArgumentType.getScoreHolder(ctx, "target")
    val obj = ScoreboardObjectiveArgumentType.getObjective(ctx, "objective")
    val scoreboard = ctx.source.server.scoreboard
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
                    ctx.source.world.getBlockEntity(pos) ?: throw NOT_BLOCK_ENTITY_EXCEPTION.create()
                ).readNbt(target)
            }
            DataType.Storage -> {
                target = ctx.source.server.dataCommandStorage.get(
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

fun runReplaced(replace: (Ctx) -> String, ctx: Ctx): ServerCommandSource {
    val replacement = replace(ctx)
    val name = IdentifierArgumentType.getIdentifier(ctx, "var")
    val varName = when (name.namespace) {
        "minecraft" -> name.path
        else -> name.toString()
    }
    (ctx.source as ServerCommandSourceDuck).replacements[varName] = replacement
    return ctx.source
}

fun eval(ctx: Ctx): Int {
    var command = ctx.getArgument("command", String::class.java)
    for ((varName, replacement) in (ctx.source as ServerCommandSourceDuck).replacements) {
        command = command.replace("$$varName", replacement)
    }
    return ctx.source.server.commandManager.execute(ctx.source, command)
}