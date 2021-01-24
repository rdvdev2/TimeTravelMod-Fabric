package com.rdvdev2.timetravelmod.impl.common.timemachine;

import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine;
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade;
import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineUpgradeBlock;
import com.rdvdev2.timetravelmod.impl.ModRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class TimeMachineStructure {

    private static final Map<ITimeMachine, BlockState[]> UPGRADE_STATE_CACHE = new HashMap<>();

    private final BlockPos[] basicPos;
    private final BlockPos[] corePos;
    private final BlockPos[] controllerPos;
    private final BlockPos[] airPos;
    private final BlockState[] upgradeStates;
    private final ITimeMachine timeMachine;

    public TimeMachineStructure(BlockPos[] basicPos, BlockPos[] corePos, BlockPos[] controllerPos, BlockPos[] airPos, ITimeMachine timeMachine) {
        this.basicPos = basicPos;
        this.corePos = corePos;
        this.controllerPos = controllerPos;
        this.airPos = airPos;
        this.upgradeStates = generateUpgradeStates(timeMachine);
        this.timeMachine = timeMachine;
    }

    private static BlockState[] generateUpgradeStates(ITimeMachine timeMachine) {
        if (UPGRADE_STATE_CACHE.containsKey(timeMachine)) return UPGRADE_STATE_CACHE.get(timeMachine);

        List<ITimeMachineUpgrade> upgrades = ModRegistries.TIME_MACHINE_UPGRADE.stream().parallel()
                .filter(u -> u.isTimeMachineCompatible(timeMachine))
                .collect(Collectors.toList());

        UPGRADE_STATE_CACHE.put(
                timeMachine,
                Registry.BLOCK.stream().parallel()
                        .filter(b -> b instanceof TimeMachineUpgradeBlock && upgrades.contains(((TimeMachineUpgradeBlock) b).getUpgrade()))
                        .flatMap(b -> b.getStateManager().getStates().parallelStream())
                        .toArray(BlockState[]::new)
        );
        return generateUpgradeStates(timeMachine);
    }

    public BlockPos[] getBasicPos() {
        return basicPos;
    }

    public BlockPos[] getCorePos() {
        return corePos;
    }

    public BlockPos[] getControllerPos() {
        return controllerPos;
    }

    public BlockPos[] getAirPos() {
        return airPos;
    }

    public BlockState[] getControllerStates() {
        return timeMachine.getControllerStates();
    }

    public BlockState[] getCoreStates() {
        return timeMachine.getCoreStates();
    }

    public BlockState[] getBasicStates() {
        return timeMachine.getBasicStates();
    }

    @Nullable
    public BlockState[] getUpgradeStates() {
        return upgradeStates;
    }

    public ITimeMachine getTimeMachine() {
        return timeMachine;
    }

    public boolean checkIfBuilt(World world, BlockPos rootPos) {
        List<BlockState> basicStates = new ArrayList<>(Arrays.asList(getBasicStates().clone()));
        if (getUpgradeStates() != null) basicStates.addAll(Arrays.asList(getUpgradeStates().clone()));
        List<BlockState> coreStates = new ArrayList<>(Arrays.asList(getCoreStates().clone()));
        List<BlockState> controllerStates = Arrays.asList(getControllerStates().clone());
        return
                Arrays.stream(getControllerPos())
                        .map(p -> p.add(rootPos))
                        .allMatch(p -> controllerStates.contains(world.getBlockState(p)))
                && Arrays.stream(getCorePos())
                        .map(p -> p.add(rootPos))
                        .allMatch(p -> coreStates.contains(world.getBlockState(p)))
                && Arrays.stream(getAirPos())
                        .map(p -> p.add(rootPos))
                        .allMatch(world::isAir)
                && Arrays.stream(getBasicPos())
                        .map(p -> p.add(rootPos))
                        .allMatch(p -> basicStates.contains(world.getBlockState(p)));
    }

    public Map<ITimeMachineUpgrade, BlockPos> getUpgrades(World world, BlockPos rootPos) {
        if (getUpgradeStates() == null) return Collections.emptyMap();
        List<BlockState> upgradeStates = Arrays.asList(getUpgradeStates().clone());

        return Arrays.stream(getBasicPos())
                .map(p -> p.add(rootPos))
                .filter(p -> upgradeStates.contains(world.getBlockState(p)))
                .collect(Collectors.toMap(p -> ((TimeMachineUpgradeBlock) world.getBlockState(p).getBlock()).getUpgrade(), p -> p));
    }

    public BlockPos[] getStructurePos(BlockPos rootPos) {
        BlockPos[] allPos = ArrayUtils.addAll(
                ArrayUtils.addAll(getControllerPos(), getCorePos()),
                ArrayUtils.addAll(getAirPos(), getBasicPos()));
        return Arrays.stream(allPos).parallel()
                .map(p -> p.add(rootPos))
                .toArray(BlockPos[]::new);
    }

    public static TimeMachineStructure[] generateFromTimeMachine(ITimeMachine timeMachine) {
        String[][] layers = timeMachine.getStructureLayers();
        List<BlockPos> basicPos = new ArrayList<>();
        List<BlockPos> corePos = new ArrayList<>();
        List<BlockPos> controllerPos = new ArrayList<>();
        List<BlockPos> airPos = new ArrayList<>();

        // Decompose the string arrays in BlockPos lists
        int maxY = layers.length - 1;
        for (int y = 0; y < layers.length; y++) {
            for (int z = 0; z < layers[y].length; z++) {
                for (int x = 0; x < layers[y][z].length(); x++) {
                    BlockPos pos = new BlockPos(x, maxY - y, z);
                    switch (layers[y][z].charAt(x)) {
                        case 'Z':
                            controllerPos.add(pos);
                            break;
                        case 'C':
                            corePos.add(pos);
                            break;
                        case 'B':
                            basicPos.add(pos);
                            break;
                        case ' ':
                            airPos.add(pos);
                            break;
                        case '*':
                            break;
                        default:
                            throw new RuntimeException("Invalid Time Machine structure descriptor");
                    }
                }
            }
        }

        List<TimeMachineStructure> variants = new ArrayList<>();

        // Generate all variants of the template
        for (BlockPos rootPos: controllerPos) {
            Vec3i translation = new Vec3i(-rootPos.getX(), -rootPos.getY(), -rootPos.getZ()); // Make root be on (0, 0, 0)
            List<BlockPos> translatedBasicPos = translatePosList(new ArrayList<>(basicPos), translation);
            List<BlockPos> translatedCorePos = translatePosList(new ArrayList<>(corePos), translation);
            List<BlockPos> translatedControllerPos = translatePosList(new ArrayList<>(controllerPos), translation);
            List<BlockPos> translatedAirPos = translatePosList(new ArrayList<>(airPos), translation);

            for (BlockRotation rotation: BlockRotation.values()) { // Apply all possible rotations
                List<BlockPos> rotatedBasicPos = rotatePosList(new ArrayList<>(translatedBasicPos), rotation);
                List<BlockPos> rotatedCorePos = rotatePosList(new ArrayList<>(translatedCorePos), rotation);
                List<BlockPos> rotatedControllerPos = rotatePosList(new ArrayList<>(translatedControllerPos), rotation);
                List<BlockPos> rotatedAirPos = rotatePosList(new ArrayList<>(translatedAirPos), rotation);

                // Build the variant and add it to the return list
                TimeMachineStructure structure = new TimeMachineStructure(
                        rotatedBasicPos.toArray(new BlockPos[]{}),
                        rotatedCorePos.toArray(new BlockPos[]{}),
                        rotatedControllerPos.toArray(new BlockPos[]{}),
                        rotatedAirPos.toArray(new BlockPos[]{}),
                        timeMachine);
                variants.add(structure);
            }
        }

        return variants.toArray(new TimeMachineStructure[]{});
    }

    private static List<BlockPos> translatePosList(List<BlockPos> posList, Vec3i translation) {
        return posList.parallelStream()
                .map(p -> p.add(translation))
                .collect(Collectors.toList());
    }

    private static List<BlockPos> rotatePosList(List<BlockPos> posList, BlockRotation rotation) {
        if (rotation == BlockRotation.NONE) return posList;
        return posList.parallelStream()
                .map(p -> p.rotate(rotation))
                .collect(Collectors.toList());
    }
}
