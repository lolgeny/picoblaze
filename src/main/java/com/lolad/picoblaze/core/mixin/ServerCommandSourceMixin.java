package com.lolad.picoblaze.core.mixin;

import com.lolad.picoblaze.replace.duck.ServerCommandSourceDuck;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(ServerCommandSource.class)
abstract class ServerCommandSourceMixin implements ServerCommandSourceDuck {
    private HashMap<String, String> replacements = new HashMap<>();
    @Override
    public @NotNull HashMap<String, String> getReplacements() {
        return replacements;
    }
    @Override
    public void setReplacements(@NotNull HashMap<String, String> to) {
        this.replacements = to;
    }

    @Inject(
        method = {
            "withOutput(Lnet/minecraft/server/command/CommandOutput;)Lnet/minecraft/server/command/ServerCommandSource;",
            "withEntity(Lnet/minecraft/entity/Entity;)Lnet/minecraft/server/command/ServerCommandSource;",
            "withPosition(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/server/command/ServerCommandSource;",
            "withRotation(Lnet/minecraft/util/math/Vec2f;)Lnet/minecraft/server/command/ServerCommandSource;",
            "withConsumer(Lcom/mojang/brigadier/ResultConsumer;)Lnet/minecraft/server/command/ServerCommandSource;",
            "withSilent()Lnet/minecraft/server/command/ServerCommandSource;",
            "withLevel(I)Lnet/minecraft/server/command/ServerCommandSource;",
            "withMaxLevel(I)Lnet/minecraft/server/command/ServerCommandSource;",
            "withEntityAnchor(Lnet/minecraft/command/argument/EntityAnchorArgumentType$EntityAnchor;)Lnet/minecraft/server/command/ServerCommandSource;",
            "withWorld(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/server/command/ServerCommandSource;"
        },
        at = @At("RETURN")
    )
    void copy(CallbackInfoReturnable<ServerCommandSource> cir) {
        ((ServerCommandSourceDuck) cir.getReturnValue()).setReplacements(this.replacements);
    }
}