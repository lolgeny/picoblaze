package com.lolad.picoblaze.structure

import com.lolad.picoblaze.core.Module
import com.lolad.picoblaze.core.command.Ctx
import com.lolad.picoblaze.core.command.EnumArgumentType
import com.lolad.picoblaze.core.command.defaultGet
import com.lolad.picoblaze.core.command.isOp
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.structure.StructurePlacementData
import net.minecraft.text.TranslatableText
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Util
import java.util.*

object Structure: Module() {
    enum class Rotation(override val arg: String): EnumArgumentType.EnumArgument {
        D0("0"),
        D90("90"),
        D180("180"),
        D270("270");
        fun toBlockRotation(): BlockRotation = when (this) {
            D0 -> BlockRotation.NONE
            D90 -> BlockRotation.CLOCKWISE_90
            D180 -> BlockRotation.CLOCKWISE_180
            D270 -> BlockRotation.COUNTERCLOCKWISE_90
        }
    }
    enum class Mirror(override val arg: String): EnumArgumentType.EnumArgument {
        FB("front_back"),
        LR("left_right"),
        None("none");
        fun toBlockMirror(): BlockMirror = when (this) {
            FB -> BlockMirror.FRONT_BACK
            LR -> BlockMirror.LEFT_RIGHT
            None -> BlockMirror.NONE
        }
    }
    private fun save(ctx: Ctx): Int {
        val name = IdentifierArgumentType.getIdentifier(ctx, "name")
        val from = BlockPosArgumentType.getBlockPos(ctx, "from")
        val to = BlockPosArgumentType.getBlockPos(ctx, "to")
        val entities = BoolArgumentType.getBool(ctx, "entities")
        val server = ctx.source.server
        val structureManager = server.structureManager
        structureManager.getStructureOrBlank(name).apply {
            saveFromWorld(ctx.source.world, from, to.subtract(from).add(1, 1, 1), entities, Blocks.STRUCTURE_VOID)
        }
        structureManager.saveStructure(name)
        ctx.source.sendFeedback(TranslatableText("commands.structure.save.success", name), true)
        return 0
    }
    private fun load(ctx: Ctx): Int {
        // copied from StructureBlockBlockEntity
        fun createRandom(seed: Long): Random =
            if (seed == 0L) Random(Util.getMeasuringTimeMs()) else Random(seed)
        val name = IdentifierArgumentType.getIdentifier(ctx, "name")
        val to = BlockPosArgumentType.getBlockPos(ctx, "to")
        val rotation = defaultGet(ctx, "rotation", Rotation.D0)
            {ctx_, name_ -> EnumArgumentType.getEnumArgument<Rotation>(ctx_, name_)}
        val mirror = defaultGet(ctx, "mirror", Mirror.None)
        {ctx_, name_ -> EnumArgumentType.getEnumArgument<Mirror>(ctx_, name_)}
        val entities = defaultGet(ctx, "includeEntities", true, BoolArgumentType::getBool)
        val neighbours = defaultGet(ctx, "updateNeighbours", false, BoolArgumentType::getBool)
        val integrity = defaultGet(ctx, "integrity", 0, FloatArgumentType::getFloat)
        val seed = defaultGet(ctx, "seed", 0, LongArgumentType::getLong)

        val server = ctx.source.server
        val structureManager = server.structureManager
        structureManager.getStructure(name).also {
            if (it.isPresent) {
                val notify = if (neighbours) Block.NOTIFY_LISTENERS else Block.NOTIFY_NEIGHBORS
                val placementData = StructurePlacementData()
                    .setRotation(rotation.toBlockRotation())
                    .setMirror(mirror.toBlockMirror())
                    .setIgnoreEntities(!entities)
                    .setPosition(to)
                    .setUpdateNeighbors(neighbours)
                it.get().place(ctx.source.world, to, to, placementData, createRandom(seed), notify)
                ctx.source.sendFeedback(TranslatableText("commands.structure.load.success", name), true)
            } else {
                ctx.source.sendFeedback(TranslatableText("commands.structure.load.no_structure", name), false)
                return 1
            }
        }
        return 0
    }
    override fun command(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal("structure")
                .requires(::isOp)
                .then(
                    literal("save").then(
                        argument("name", IdentifierArgumentType.identifier()).then(
                            argument("from", BlockPosArgumentType.blockPos()).then(
                                argument("to", BlockPosArgumentType.blockPos()).then(
                                    argument("entities", BoolArgumentType.bool()).executes(::save)
                                )
                            )
                        )
                    )
                )
                .then(
                    literal("load").then(
                        argument("name", IdentifierArgumentType.identifier()).then(
                            argument("to", BlockPosArgumentType.blockPos()).then(
                                argument("rotation", EnumArgumentType.enumArgument<Rotation>()).then(
                                    argument("mirror", EnumArgumentType.enumArgument<Mirror>()).then(
                                        argument("includeEntities", BoolArgumentType.bool()).then(
                                            argument("updateNeighbours", BoolArgumentType.bool()).then(
                                                argument("integrity", FloatArgumentType.floatArg()).then(
                                                    argument("seed", LongArgumentType.longArg())
                                                        .executes(::load)
                                                ).executes(::load)
                                            ).executes(::load)
                                        ).executes(::load)
                                    ).executes(::load)
                                ).executes(::load)
                            ).executes(::load)
                        )
                    )
                )
        )
    }
}