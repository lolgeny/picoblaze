package com.lolad.picoblaze.core.mixin;

import com.lolad.picoblaze.game_event.duck.MinecraftServerDuck;
import net.minecraft.entity.Entity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
abstract class ServerWorldMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(
        method = "emitGameEvent(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/util/math/BlockPos;)V",
        at = @At("TAIL")
    )
    private void emitGameEvent(@Nullable Entity entity, GameEvent event, BlockPos pos, CallbackInfo _ci) {
        ((MinecraftServerDuck) ((ServerWorld) (Object) this).getServer()).getGameEventManager().getGameEvents().forEach((k, game_event_function) -> {
            if (game_event_function.getTrigger().getId().equals(event.getId())) {
                for (LootCondition predicate : game_event_function.getPredicate()) {
                    LootContext context = new LootContext.Builder((ServerWorld) (Object) this)
                        .parameter(LootContextParameters.ORIGIN, Vec3d.of(pos))
                        .optionalParameter(LootContextParameters.THIS_ENTITY, entity)
                        .build(com.lolad.picoblaze.game_event.GameEvent.INSTANCE.getEVENT_CONTEXT());
                    if (!predicate.test(context)) {
                        return;
                    }
                }
                server.getCommandFunctionManager().getFunction(game_event_function.getFunction().getId())
                    .ifPresent((CommandFunction command) -> {
                        var source = server.getCommandSource()
                            .withPosition(Vec3d.of(pos));
                        if (entity != null) {
                            source = source.withEntity(entity);
                        }
                        server.getCommandFunctionManager().execute(
                            command,
                            source
                        );
                    });
            }
        });
    }

}