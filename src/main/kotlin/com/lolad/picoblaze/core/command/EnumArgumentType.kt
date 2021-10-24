package com.lolad.picoblaze.core.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException

class EnumArgumentType<E>(val instances: Array<E>): ArgumentType<E>
    where E: Enum<E>,
          E: EnumArgumentType.EnumArgument {
    interface EnumArgument {
        val arg: String
    }

    companion object {
        inline fun <reified E> enumArgument(): EnumArgumentType<E>
            where E: Enum<E>,
                  E: EnumArgument {
            return EnumArgumentType(enumValues())
        }
        inline fun <reified E> getEnumArgument(ctx: Ctx, name: String): E
            where E: Enum<E>,
                  E: EnumArgument {
            return ctx.getArgument(name, E::class.java)
        }
    }

    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): E {
        val lit = reader.readString()
        return instances.firstOrNull { it.arg == lit }
            ?: throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()
                .createWithContext(reader, lit)
    }

    // TODO: track structureManager or send a packet here as ClientCommandSource is sent
//    override fun <S : Any> listSuggestions(
//        context: CommandContext<S>,
//        builder: SuggestionsBuilder
//    ): CompletableFuture<Suggestions> {
//        when (val source = context.source) {
//            is ServerCommandSource -> {
//                val structureManager = source.server.structureManager
//                val structures = (structureManager as StructureManagerDuck).structures.entries
//                return structures.fold(builder) { acc, structure -> acc.suggest(structure.key.toString()) }
//                    .buildFuture()
//            }
//            else -> throw NotImplementedError("use ServerCommandSource")
//        }
//    }
}