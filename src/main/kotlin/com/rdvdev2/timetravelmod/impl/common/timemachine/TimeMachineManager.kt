package com.rdvdev2.timetravelmod.impl.common.timemachine

import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine
import com.rdvdev2.timetravelmod.impl.ModRegistries
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.apache.commons.lang3.ArrayUtils
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object TimeMachineManager {

    private val possibleStructures: MutableMap<BlockState, Array<TimeMachineStructure>> = ConcurrentHashMap()

    private fun generateStructures() {
        possibleStructures.clear()
        ModRegistries.TIME_MACHINE.stream().parallel().forEach { tm: ITimeMachine ->
            val controllerStates = tm.controllerStates
            val structures = TimeMachineStructure.generateFromTimeMachine(tm)
            for (controllerState in controllerStates) {
                val all = ArrayUtils.addAll(possibleStructures.getOrDefault(controllerState, null), *structures)
                possibleStructures[controllerState] = all
            }
        }
    }

    fun generateExecutor(world: World, rootPos: BlockPos): Optional<TimeMachineExecutor> {
        val rootState = world.getBlockState(rootPos)
        if (!possibleStructures.containsKey(rootState)) generateStructures()
        possibleStructures[rootState]?.forEach { structure ->
            if (structure.checkIfBuilt(world, rootPos)) return Optional.of(TimeMachineExecutor(structure, rootPos, world))
        }
        return Optional.empty()
    }
}