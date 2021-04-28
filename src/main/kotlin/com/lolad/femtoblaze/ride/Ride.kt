package com.lolad.femtoblaze.ride

import com.lolad.femtoblaze.core.Module
import com.lolad.femtoblaze.core.command.Ctx
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.Entity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.TranslatableText

object Ride: Module() {
    override fun command(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal("ride")
                .then(
                    argument("targets", EntityArgumentType.entities())
                        .then(
                            literal("start_riding")
                                .then(
                                    argument("ride", EntityArgumentType.entity())
                                        .then(
                                            addStart("teleport_ride")
                                        )
                                        .then(
                                            addStart("teleport_rider")
                                        )
                                )
                        )
                        .then(
                            literal("stop_riding")
                                .executes(::stopRiding)
                        )
                        .then(
                            literal("evict_riders")
                                .executes(::evictRiders)
                        )
                )
        )
    }

    private fun addStart(teleport: String): LiteralArgumentBuilder<ServerCommandSource> {
        return literal(teleport)
            .then(
                literal("if_group_fits")
                    .executes {startRiding(it, teleport, "if_group_fits")}
            )
            .then(
                literal("until_full")
                    .executes {startRiding(it, teleport, "until_full") }
            )
    }
    private val CANNOT_RIDE_EXCEPTION = Dynamic2CommandExceptionType { rider, ride ->
        TranslatableText("commands.ride.cannot_ride", (rider as Entity).name, (ride as Entity).name)
    }
    private val TOO_LARGE_GROUP_EXCEPTION = DynamicCommandExceptionType { ride ->
        TranslatableText("commands.ride.too_large", (ride as Entity).name)
    }
    private val NOT_RIDING_EXCEPTION = SimpleCommandExceptionType (TranslatableText("commands.ride.not_riding"))
    private val NO_RIDERS_EXCEPTION = SimpleCommandExceptionType (TranslatableText("commands.ride.no_riders"))
    private fun getTargets(ctx: Ctx): Collection<Entity> = EntityArgumentType.getEntities(ctx, "targets")
    private fun startRiding(ctx: Ctx, tp: String, fill: String): Int {
        val riders = getTargets(ctx)
        val ride = EntityArgumentType.getEntity(ctx, "ride")
        when (tp) {
            "teleport_ride" -> {
                if (riders.size > 1) throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create()
                val rider = riders.first()
                ride.teleport(rider.x, rider.y, rider.z)
                if (!rider.startRiding(ride)) throw CANNOT_RIDE_EXCEPTION.create(rider, ride)
                rider.teleport(rider.x, rider.y, rider.z)
            }
            "teleport_rider" -> {
                when (fill) {
                    "if_group_fits" -> {
                        if (riders.size < 2 || (ride is BoatEntity && riders.size < 3)) {
                            for (rider in riders) if (!rider.startRiding(ride)) throw CANNOT_RIDE_EXCEPTION.create(rider, ride)
                        } else throw TOO_LARGE_GROUP_EXCEPTION.create(ride)
                    }
                    "until_full" -> {
                        for (rider in riders) if (!rider.startRiding(ride)) throw CANNOT_RIDE_EXCEPTION.create(rider, ride)
                    }
                }
            }
        }
        ctx.source.sendFeedback(TranslatableText("commands.ride.start_riding.success"), true)
        return riders.size
    }
    private fun stopRiding(ctx: Ctx): Int {
        val riders = getTargets(ctx)
        val riding = riders.count {it.hasVehicle()}
        if (riding == 0) throw NOT_RIDING_EXCEPTION.create()
        for (rider in riders) {
            rider.stopRiding()
        }
        ctx.source.sendFeedback(TranslatableText("commands.ride.stop_riding.success"), true)
        return riding
    }
    private fun evictRiders(ctx: Ctx): Int {
        val rides = getTargets(ctx)
        val passengers = rides.sumOf {it.passengerList.size}
        if (passengers == 0) throw NO_RIDERS_EXCEPTION.create()
        for (vehicle in rides) {
            vehicle.removeAllPassengers()
        }
        ctx.source.sendFeedback(TranslatableText("commands.ride.evict_riders.success"), true)
        return passengers
    }
}