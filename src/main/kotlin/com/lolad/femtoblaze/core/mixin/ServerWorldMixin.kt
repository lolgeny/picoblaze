package com.lolad.femtoblaze.core.mixin

import org.spongepowered.asm.mixin.Mixin
import net.minecraft.server.world.ServerWorld
import org.spongepowered.asm.mixin.Shadow
import net.minecraft.server.MinecraftServer
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import net.minecraft.util.math.BlockPos
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.lolad.femtoblaze.game_event.duck.MinecraftServerDuck
import com.lolad.femtoblaze.game_event.GameEventFunction
import net.minecraft.entity.Entity
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.context.LootContext
import net.minecraft.util.math.Vec3d
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.server.function.CommandFunction
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.loot.context.LootContextType
import net.minecraft.util.Identifier
import net.minecraft.world.event.GameEvent

@Mixin(ServerWorld::class)
abstract class ServerWorldMixin {
    @get:Shadow
    abstract val server: MinecraftServer
    @Inject(
        method = ["emitGameEvent(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/util/math/BlockPos;)V"],
        at = [At("TAIL")]
    )
    private fun emitGameEvent(entity: Entity?, event: GameEvent, pos: BlockPos, ci: CallbackInfo) {
        (server as MinecraftServerDuck).gameEventManager.game_events.forEach { (id: Identifier?, game_event_function: GameEventFunction) ->
            if (game_event_function.trigger.id == event.id) {
                for (predicate in game_event_function.predicate) {
                    val context = LootContext.Builder(this as ServerWorld)
                        .parameter(LootContextParameters.ORIGIN, Vec3d.of(pos))
                        .optionalParameter(LootContextParameters.THIS_ENTITY, entity)
                        .build(EVENT_CONTEXT)
                    if (!predicate.test(context)) {
                        return@forEach
                    }
                }
                server.commandFunctionManager.getFunction(game_event_function.function.id)
                    .ifPresent { command: CommandFunction? ->
                        var source = server.commandFunctionManager.taggedFunctionSource
                            .withPosition(Vec3d.of(pos))
                        if (entity != null) {
                            source = source.withEntity(entity)
                        }
                        server.commandFunctionManager.execute(
                            command,
                            source
                        )
                    }
            }
        }
    }

    companion object {
        private val EVENT_CONTEXT = LootContextType.Builder()
            .require(LootContextParameters.ORIGIN)
            .allow(LootContextParameters.THIS_ENTITY)
            .build()
    }
}