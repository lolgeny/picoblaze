package com.lolad.femtoblaze.core.mixin

import com.lolad.femtoblaze.replace.duck.ServerCommandSourceDuck
import net.minecraft.server.command.ServerCommandSource
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(ServerCommandSource::class)
abstract class ServerCommandSourceMixin: ServerCommandSourceDuck {
    private fun createReplacements(): MutableMap<String, String> = mutableMapOf() // kotlin mixin hack
    override var replacements: MutableMap<String, String> = createReplacements()

    @Inject(
        method = [
            "Lnet/minecraft/server/command/ServerCommandSource;withOutput(Lnet/minecraft/server/command/CommandOutput;)Lnet/minecraft/server/command/ServerCommandSource;",
            "Lnet/minecraft/server/command/ServerCommandSource;withEntity(Lnet/minecraft/entity/Entity;)Lnet/minecraft/server/command/ServerCommandSource;",
            "Lnet/minecraft/server/command/ServerCommandSource;withPosition(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/server/command/ServerCommandSource;",
            "Lnet/minecraft/server/command/ServerCommandSource;withRotation(Lnet/minecraft/util/math/Vec2f;)Lnet/minecraft/server/command/ServerCommandSource;",
            "Lnet/minecraft/server/command/ServerCommandSource;withConsumer(Lcom/mojang/brigadier/ResultConsumer;)Lnet/minecraft/server/command/ServerCommandSource;",
            "Lnet/minecraft/server/command/ServerCommandSource;withSilent()Lnet/minecraft/server/command/ServerCommandSource;",
            "Lnet/minecraft/server/command/ServerCommandSource;withLevel(I)Lnet/minecraft/server/command/ServerCommandSource;",
            "Lnet/minecraft/server/command/ServerCommandSource;withMaxLevel(I)Lnet/minecraft/server/command/ServerCommandSource;",
            "Lnet/minecraft/server/command/ServerCommandSource;withEntityAnchor(Lnet/minecraft/command/argument/EntityAnchorArgumentType\$EntityAnchor;)Lnet/minecraft/server/command/ServerCommandSource;",
            "Lnet/minecraft/server/command/ServerCommandSource;withWorld(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/server/command/ServerCommandSource;"
        ],
        at = [At("RETURN")]
    )
    fun copy(cir: CallbackInfoReturnable<ServerCommandSource>) {
        (cir.returnValue as ServerCommandSourceDuck).replacements = this.replacements
    }
}