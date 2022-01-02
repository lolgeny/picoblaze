package com.lolad.picoblaze.structure.duck

import net.minecraft.structure.Structure
import net.minecraft.util.Identifier
import java.util.*

interface StructureManagerDuck {
    val structures: Map<Identifier, Optional<Structure>>
}