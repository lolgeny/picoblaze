package com.lolad.picoblaze.core.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType

class GreedyArgumentType: ArgumentType<String> {
    override fun parse(reader: StringReader): String {
        val remaining = reader.remaining
        reader.cursor = reader.totalLength
        return remaining
    }
}