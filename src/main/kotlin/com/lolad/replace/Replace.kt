package com.lolad.replace

import com.mojang.brigadier.arguments.BoolArgumentType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.command.argument.*
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal

class Replace : ModInitializer {
    override fun onInitialize() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _ ->
            dispatcher.register(
                literal("execute")
                    .then(
                        literal("replace")
                            .then(
                                argument("var", IdentifierArgumentType.identifier())
                                    .then(
                                        literal("score")
                                            .then(
                                                argument("target", ScoreHolderArgumentType.scoreHolder())
                                                    .suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
                                                    .then(
                                                        argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective())
                                                            .then(
                                                                argument("command", GreedyArgumentType())
                                                                    .executes {
                                                                        runReplaced(::replaceScore, it)
                                                                    }
                                                            )
                                                    )
                                            )
                                    )
                                    .then(
                                        literal("data")
                                            .then(
                                                literal("entity")
                                                    .then(
                                                        argument("target", EntityArgumentType.entity())
                                                            .then(
                                                                argument("path", NbtPathArgumentType.nbtPath())
                                                                    .then(
                                                                        argument("interpret", BoolArgumentType.bool())
                                                                            .then(
                                                                                argument("command", GreedyArgumentType())
                                                                                .executes {
                                                                                    runReplaced(
                                                                                        replaceData(DataType.Entity),
                                                                                        it
                                                                                    )
                                                                                }
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                            .then(
                                                literal("block")
                                                    .then(
                                                        argument("pos", BlockPosArgumentType.blockPos())
                                                            .then(
                                                                argument("path", NbtPathArgumentType.nbtPath())
                                                                    .then(
                                                                        argument("interpret", BoolArgumentType.bool())
                                                                            .then(
                                                                                argument("command", GreedyArgumentType())
                                                                                .executes {
                                                                                    runReplaced(
                                                                                        replaceData(DataType.Block),
                                                                                        it
                                                                                    )
                                                                                }
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                            .then(
                                                literal("storage")
                                                    .then(
                                                        argument("storage", IdentifierArgumentType.identifier())
                                                            .then(
                                                                argument("path", NbtPathArgumentType.nbtPath())
                                                                    .then(
                                                                        argument("interpret", BoolArgumentType.bool())
                                                                            .then(
                                                                                argument("command", GreedyArgumentType())
                                                                                .executes {
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
                                    )
                                    .then(
                                        literal("text")
                                            .then(
                                                argument("component", FixedTextArgumentType())
                                                    .then(
                                                        argument("command", GreedyArgumentType())
                                                            .executes {
                                                                runReplaced(::replaceComponent, it)
                                                            }
                                                    )
                                            )
                                    )
                            )
                    )
            )
            dispatcher.register(
                literal("eval")
                    .then(
                        argument("command", GreedyArgumentType())
                            .executes(eval(false))
                    )
            )
        })
    }
}