package com.lolad.picoblaze.core.mixin;

import com.lolad.picoblaze.structure.duck.StructureManagerDuck;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Optional;

@Mixin(StructureManager.class)
public abstract class StructureManagerMixin implements StructureManagerDuck {
    @Accessor
    @Override
    public abstract @NotNull Map<Identifier, Optional<Structure>> getStructures();
}
