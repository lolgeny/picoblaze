package com.lolad.replace.mixin

import com.lolad.replace.duck.ServerCommandSourceDuck
import com.mojang.brigadier.ResultConsumer
import net.minecraft.command.argument.EntityAnchorArgumentType
import net.minecraft.entity.Entity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandOutput
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(ServerCommandSource::class)
class ServerCommandSourceMixin: ServerCommandSourceDuck {
    private fun createReplacements(): MutableMap<String, String> = mutableMapOf() // kotlin mixin hack
    override var replacements: MutableMap<String, String> = createReplacements()

    @Redirect(
        method = ["*"],
        at = At(value = "NEW", target = "Lnet/minecraft/server/command/ServerCommandSource;")
    )
    fun copy(output: CommandOutput, pos: Vec3d, rot: Vec2f, world: ServerWorld, level: Int, simpleName: String, name: Text, server: MinecraftServer, entity: Entity?, silent: Boolean, consumer: ResultConsumer<ServerCommandSource>, entityAnchor: EntityAnchorArgumentType.EntityAnchor): ServerCommandSource {
        try {
            ServerCommandSource::class.java.getConstructor(
                CommandOutput::class.java,
                Vec3d::class.java,
                Vec2f::class.java,
                ServerWorld::class.java,
                Int::class.java,
                String::class.java,
                Text::class.java,
                MinecraftServer::class.java,
                Entity::class.java,
                Boolean::class.java,
                ResultConsumer::class.java,
                EntityAnchorArgumentType.EntityAnchor::class.java
            )
                .let {
                    it.isAccessible = true
                    return it.newInstance(
                        output,
                        pos,
                        rot,
                        world,
                        level,
                        simpleName,
                        name,
                        server,
                        entity,
                        silent,
                        consumer,
                        entityAnchor
                    ).also {
                        (it as ServerCommandSourceDuck).replacements = (this as ServerCommandSourceDuck).replacements
                    }
                }
        } catch (e: Exception) {
            println("[BUG] Exception in ServerCommandSource mixin:\n$e")
            throw e
        }
    }
}