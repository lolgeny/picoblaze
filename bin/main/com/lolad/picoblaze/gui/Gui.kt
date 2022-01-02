package com.lolad.picoblaze.gui

import com.lolad.picoblaze.core.Module
import com.lolad.picoblaze.core.command.Ctx
import com.lolad.picoblaze.core.command.isOp
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.TranslatableText

object Gui: Module() {
    private fun openGui(ctx: Ctx): Int {
        val targets = EntityArgumentType.getPlayers(ctx, "targets")
        val block = BlockPosArgumentType.getBlockPos(ctx, "block")
        val screenHandlerFactory = ctx.source.world.getBlockEntity(block) as? NamedScreenHandlerFactory
            ?: run {
                ctx.source.sendFeedback(TranslatableText("commands.gui.no_screen"), false)
                return 0
            }
        for (target in targets) {
            target.openHandledScreen(screenHandlerFactory)
        }
        ctx.source.sendFeedback(TranslatableText("commands.gui.success", targets.size), true)
        return targets.size
    }
    override fun command(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal("gui").requires(::isOp).then(
                literal("open").then(
                    argument("targets", EntityArgumentType.players()).then(
                        argument("block", BlockPosArgumentType.blockPos()).executes(::openGui)
                    )
                )
            )
        )
    }
}