package com.rdvdev2.timetravelmod.impl.common.timemachine

import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade
import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineUpgradeBlock
import com.rdvdev2.timetravelmod.impl.ModRegistries
import net.minecraft.block.BlockState
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.*
import java.util.stream.Collectors

class TimeMachineStructure(
    private val basicPos: Array<BlockPos>,
    private val corePos: Array<BlockPos>,
    private val controllerPos: Array<BlockPos>,
    private val airPos: Array<BlockPos>,
    val timeMachine: ITimeMachine
    ) {

    private val upgradeStates: Array<BlockState> = generateUpgradeStates(timeMachine)
    private val controllerStates: Array<BlockState>
        get() = timeMachine.controllerStates
    private val coreStates: Array<BlockState>
        get() = timeMachine.coreStates
    private val basicStates: Array<BlockState>
        get() = timeMachine.basicStates

    fun checkIfBuilt(world: World, rootPos: BlockPos): Boolean {
        val basicStates = listOf(*basicStates.clone(), *upgradeStates.clone())
        val coreStates = listOf(*coreStates.clone())
        val controllerStates = listOf(*controllerStates.clone())
        return (
                Arrays.stream(controllerPos)
                    .map { it.add(rootPos) }
                    .allMatch { controllerStates.contains(world.getBlockState(it)) }
                && Arrays.stream(corePos)
                    .map { it.add(rootPos) }
                    .allMatch { coreStates.contains(world.getBlockState(it)) }
                && Arrays.stream(airPos)
                    .map { it.add(rootPos) }
                    .allMatch { world.isAir(it) }
                && Arrays.stream(basicPos)
                    .map { it.add(rootPos) }
                    .allMatch { basicStates.contains(world.getBlockState(it)) }
                )
    }

    fun getUpgrades(world: World, rootPos: BlockPos): Map<ITimeMachineUpgrade, BlockPos> {
        val upgradeStates = listOf(*upgradeStates.clone())
        return Arrays.stream(basicPos)
            .map { it.add(rootPos) }
            .filter { upgradeStates.contains(world.getBlockState(it)) }
            .collect(Collectors.toMap({ (world.getBlockState(it).block as TimeMachineUpgradeBlock).upgrade }, { it }))
    }

    fun getStructurePos(rootPos: BlockPos): Array<BlockPos> {
        val allPos = controllerPos + corePos + airPos + basicPos
        return Arrays.stream(allPos).parallel()
            .map { it.add(rootPos) }
            .toArray { arrayOfNulls<BlockPos>(it) }
    }

    companion object {

        private val UPGRADE_STATE_CACHE: MutableMap<ITimeMachine, Array<BlockState>> = HashMap()

        private fun generateUpgradeStates(timeMachine: ITimeMachine): Array<BlockState> {
            if (UPGRADE_STATE_CACHE.containsKey(timeMachine)) return UPGRADE_STATE_CACHE[timeMachine]!!
            val upgrades = ModRegistries.TIME_MACHINE_UPGRADE.stream().parallel()
                .filter { it.isTimeMachineCompatible(timeMachine) }
                .collect(Collectors.toList())
            UPGRADE_STATE_CACHE[timeMachine] = Registry.BLOCK.stream().parallel()
                .filter { it is TimeMachineUpgradeBlock && upgrades.contains(it.upgrade) }
                .flatMap { it.stateManager.states.parallelStream() }
                .toArray { arrayOfNulls<BlockState>(it) }
            return generateUpgradeStates(timeMachine)
        }

        fun generateFromTimeMachine(timeMachine: ITimeMachine): Array<TimeMachineStructure> {
            val layers = timeMachine.structureLayers
            val basicPos = mutableListOf<BlockPos>()
            val corePos = mutableListOf<BlockPos>()
            val controllerPos = mutableListOf<BlockPos>()
            val airPos = mutableListOf<BlockPos>()

            // Decompose the string arrays in BlockPos lists
            val maxY = layers.size - 1
            for (y in layers.indices) {
                for (z in 0 until layers[y].size) {
                    for (x in 0 until layers[y][z].length) {
                        val pos = BlockPos(x, maxY - y, z)
                        when (layers[y][z][x]) {
                            'Z' -> controllerPos.add(pos)
                            'C' -> corePos.add(pos)
                            'B' -> basicPos.add(pos)
                            ' ' -> airPos.add(pos)
                            '*' -> { /* IGNORE */}
                            else -> throw RuntimeException("Invalid Time Machine structure descriptor")
                        }
                    }
                }
            }
            val variants = mutableListOf<TimeMachineStructure>()

            // Generate all variants of the template
            for (rootPos in controllerPos) {
                val translation = Vec3i(-rootPos.x, -rootPos.y, -rootPos.z) // Make root be on (0, 0, 0)
                val translatedBasicPos = translatePosList(ArrayList(basicPos), translation)
                val translatedCorePos = translatePosList(ArrayList(corePos), translation)
                val translatedControllerPos = translatePosList(ArrayList(controllerPos), translation)
                val translatedAirPos = translatePosList(ArrayList(airPos), translation)
                for (rotation in BlockRotation.values()) { // Apply all possible rotations
                    val rotatedBasicPos = rotatePosList(ArrayList(translatedBasicPos), rotation)
                    val rotatedCorePos = rotatePosList(ArrayList(translatedCorePos), rotation)
                    val rotatedControllerPos = rotatePosList(ArrayList(translatedControllerPos), rotation)
                    val rotatedAirPos = rotatePosList(ArrayList(translatedAirPos), rotation)

                    // Build the variant and add it to the return list
                    val structure = TimeMachineStructure(
                        rotatedBasicPos.toTypedArray(),
                        rotatedCorePos.toTypedArray(),
                        rotatedControllerPos.toTypedArray(),
                        rotatedAirPos.toTypedArray(),
                        timeMachine
                    )
                    variants.add(structure)
                }
            }
            return variants.toTypedArray()
        }

        private fun translatePosList(posList: List<BlockPos>, translation: Vec3i): List<BlockPos> {
            return posList.parallelStream()
                .map { it.add(translation) }
                .collect(Collectors.toList())
        }

        private fun rotatePosList(posList: List<BlockPos>, rotation: BlockRotation): List<BlockPos> {
            return if (rotation == BlockRotation.NONE) posList
            else posList.parallelStream()
                .map { it.rotate(rotation) }
                .collect(Collectors.toList())
        }
    }

}