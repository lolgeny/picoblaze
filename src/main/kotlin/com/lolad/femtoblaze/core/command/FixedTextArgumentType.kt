package com.lolad.femtoblaze.core.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.command.argument.TextArgumentType
import net.minecraft.text.Text

class FixedTextArgumentType private constructor(private val base: TextArgumentType): ArgumentType<Text> {
    constructor(): this(TextArgumentType.text())

    override fun parse(reader: StringReader): Text {
        val text = base.parse(reader)
        reader.cursor = reader.cursor - 1
        return text
    }
}