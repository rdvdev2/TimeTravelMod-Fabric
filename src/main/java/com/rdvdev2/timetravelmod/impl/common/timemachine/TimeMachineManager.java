package com.rdvdev2.timetravelmod.impl.common.timemachine;

import com.rdvdev2.timetravelmod.impl.ModRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TimeMachineManager {

    private static final TimeMachineManager INSTANCE = new TimeMachineManager();

    public static TimeMachineManager getInstance() {
        return INSTANCE;
    }

    private final Map<BlockState, TimeMachineStructure[]> possibleStructures = new ConcurrentHashMap<>();

    public void generateStructures() {
        possibleStructures.clear();

        ModRegistries.TIME_MACHINE.stream().parallel().forEach(tm -> {
            BlockState[] controllerStates = tm.getControllerStates();
            TimeMachineStructure[] structures = TimeMachineStructure.generateFromTimeMachine(tm);
            for (BlockState controllerState : controllerStates) {
                TimeMachineStructure[] all = ArrayUtils.addAll(possibleStructures.getOrDefault(controllerState, null), structures);
                possibleStructures.put(controllerState, all);
            }
        });
    }

    public Optional<TimeMachineExecutor> generateExecutor(World world, BlockPos rootPos) {
        BlockState rootState = world.getBlockState(rootPos);
        if (!possibleStructures.containsKey(rootState)) generateStructures();
        if (!possibleStructures.containsKey(rootState)) return Optional.empty();

        for (TimeMachineStructure structure: possibleStructures.get(rootState))
            if (structure.checkIfBuilt(world, rootPos)) return Optional.of(new TimeMachineExecutor(structure, rootPos, world));
        return Optional.empty();
    }
}
