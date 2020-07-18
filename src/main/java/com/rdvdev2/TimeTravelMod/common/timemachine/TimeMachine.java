package com.rdvdev2.TimeTravelMod.common.timemachine;

import com.rdvdev2.TimeTravelMod.ModRegistries;
import com.rdvdev2.TimeTravelMod.ModTriggers;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachineTemplate;
import com.rdvdev2.TimeTravelMod.api.timemachine.block.TimeMachineCoreBlock;
import com.rdvdev2.TimeTravelMod.api.timemachine.block.TimeMachineUpgradeBlock;
import com.rdvdev2.TimeTravelMod.api.timemachine.exception.IncompatibleTimeMachineHooksException;
import com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade;
import com.rdvdev2.TimeTravelMod.common.block.blockentity.TMCooldownBlockEntity;
import com.rdvdev2.TimeTravelMod.common.networking.OpenTmGuiPKT;
import com.rdvdev2.TimeTravelMod.common.util.TimeMachineUtils;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TimeMachine implements com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine {

    private final TimeMachineTemplate template;
    private ArrayList<TimeMachineUpgrade> upgrades;

    public TimeMachine(TimeMachineTemplate template) {
        this.template = template;
    }

    @Override
    public TranslatableText getName() {
        Identifier id = ModRegistries.TIME_MACHINES.getId(this);
        return new TranslatableText(String.format("tm.%s.%s.name", id.getNamespace(), id.getPath()));
    }

    @Override
    public TranslatableText getDescription() {
        Identifier id = ModRegistries.TIME_MACHINES.getId(this);
        return new TranslatableText(String.format("tm.%s.%s.description", id.getNamespace(), id.getPath()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public final BlockState[] getUpgradeBlocks() {
        TimeMachineUpgradeBlock[] blocks = new TimeMachineUpgradeBlock[0];
        try {
            for (TimeMachineUpgrade upgrade : getCompatibleUpgrades()) {
                Map<TimeMachineUpgrade, TimeMachineUpgradeBlock[]> hm = TimeMachineManager.getUpgradeToBlocks();
                blocks = blocks == null ? hm.get(upgrade) : ArrayUtils.addAll(blocks, hm.get(upgrade));
            }
            BlockState[] states = new BlockState[0];
            for (TimeMachineUpgradeBlock block : blocks) {
                states = states == null ? new BlockState[]{block.getDefaultState()} : ArrayUtils.addAll(states, new BlockState[]{block.getDefaultState()});
            }
            return states;
        } catch (NullPointerException e) {
            return new BlockState[]{};
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final TimeMachineUpgrade[] getCompatibleUpgrades() {
        if (upgrades == null) {
            upgrades = new ArrayList<>();
            ModRegistries.UPGRADES.forEach(upgrade -> {
                for(com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine tm: upgrade.getCompatibleTMs()) {
                    if (tm == this) {
                        upgrades.add(upgrade);
                        break;
                    }
                }
            });
        }
        return upgrades.toArray(new TimeMachineUpgrade[0]);
    }

    @Override
    public List<BlockPos> getCoreBlocksPos(Direction side) {
        return applySide(coreBlocksPos(), side);
    }

    @Override
    public List<BlockPos> getBasicBlocksPos(Direction side) {
        return applySide(basicBlocksPos(), side);
    }

    @Override
    public List<BlockPos> getAirBlocksPos(Direction side) {
        return applySide(airBlocksPos(), side);
    }

    @Override
    public BlockState[] getBlocks() {
        if (getUpgradeBlocks().length != 0) {
            return ArrayUtils.addAll(ArrayUtils.addAll(getControllerBlocks(), getCoreBlocks()), ArrayUtils.addAll(getBasicBlocks(), getUpgradeBlocks()));
        } else {
            return ArrayUtils.addAll(ArrayUtils.addAll(getControllerBlocks(), getCoreBlocks()), getBasicBlocks());
        }
    }

    @Override
    public final com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine hook(World world, BlockPos controllerPos, Direction side) throws IncompatibleTimeMachineHooksException {
        TimeMachineHookRunner generated;
        generated = new TimeMachineHookRunner(this, getUpgrades(world, controllerPos, side));
        HashSet<TimeMachineUpgrade> incompatibilities = generated.checkIncompatibilities();
        if (incompatibilities.isEmpty()) {
            return generated;
        } else {
            throw new IncompatibleTimeMachineHooksException(incompatibilities);
        }
    }

    @Override
    public final Map<TimeMachineUpgrade, HashSet<BlockPos>> getUpgrades(World world, BlockPos controllerPos, Direction side) {
        HashMap<TimeMachineUpgrade, HashSet<BlockPos>> upgrades = new HashMap<>(0);
        for (BlockPos pos:getBasicBlocksPos(side))
            for (BlockState state:getUpgradeBlocks())
                if (world.getBlockState(controllerPos.add(pos)) == state) {
                    TimeMachineUpgrade upgrade = ((TimeMachineUpgradeBlock) state.getBlock()).getUpgrade();
                    upgrades.putIfAbsent(upgrade, new HashSet<>());
                    upgrades.get(upgrade).add(controllerPos.add(pos));
                    break;
                }
        return upgrades;
    }

    @Override
    public void run(World world, PlayerEntity playerIn, BlockPos controllerPos, Direction side) {
        if (!world.isClient) {
            TimeMachineUtils.Check error = TimeMachineUtils.check(this, world, playerIn, controllerPos, side);
            if (error == null) {
                if (!triggerTemporalExplosion(world, controllerPos, side)) {
                    if (playerIn instanceof ServerPlayerEntity) {
                        ModTriggers.ACCESS_TIME_MACHINE.trigger((ServerPlayerEntity) playerIn);
                    }
                    OpenTmGuiPKT pkt = new OpenTmGuiPKT(this, controllerPos, side, getEntitiesInside(world, controllerPos, side).stream()
                            .filter(entity -> !entity.equals(playerIn))
                            .map(Entity::getUuid)
                            .collect(Collectors.toList()).toArray(new UUID[]{}));
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerIn, OpenTmGuiPKT.ID, pkt.encode());
                }
            } else {
                if (playerIn instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) playerIn).sendMessage(error.getClientError(), true);
                }
            }
        }
    }

    @Override
    public boolean triggerTemporalExplosion(World world, BlockPos controllerPos, Direction side) {
        for (BlockPos pos:getCoreBlocksPos(side)) {
            TimeMachineCoreBlock core = (TimeMachineCoreBlock) world.getBlockState(controllerPos.add(pos)).getBlock();
            if (core.randomExplosion(world, controllerPos.add(pos)))
                return true;
        }
        return false;
    }

    @Override
    public boolean isBuilt(World world, BlockPos controllerPos, Direction side) {
        if (isComponentTypeBuilt(TimeMachineComponentType.CORE, world, controllerPos, side) &&
                isComponentTypeBuilt(TimeMachineComponentType.BASIC, world, controllerPos, side)) {
            List<BlockPos> airPos = getAirBlocksPos(side);
            for (BlockPos pos: airPos) {
                if (world.getBlockState(controllerPos.add(pos)) != Blocks.AIR.getDefaultState()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isCooledDown(World world, BlockPos controllerPos, Direction side) {
        for(BlockPos pos:getCoreBlocksPos(side)) {
            boolean coincidence = false;
            for(BlockState state:getCoreBlocks()) {
                if (world.getBlockState(controllerPos.add(pos)).getBlock() instanceof TimeMachineCoreBlock) {
                    if (world.getBlockState(controllerPos.add(pos)) == state.with(TimeMachineCoreBlock.TM_READY, true)) {
                        coincidence = true;
                        break;
                    }
                }
            }
            if(!coincidence)
                return false;
        }
        return true;
    }

    @Override
    public boolean isOverloaded(World world, BlockPos controllerPos, Direction side) {
        return getEntitiesInside(world, controllerPos, side).size() > getEntityMaxLoad();
    }

    @Override
    public boolean isPlayerInside(World world, BlockPos controllerPos, Direction side, PlayerEntity player) {
        for (Entity entity:getEntitiesInside(world, controllerPos, side)){
            if (entity.getEntityId() == (player.getEntityId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Entity> getEntitiesInside(World world, BlockPos controllerPos, Direction side) {
        Box airSpace = getAirSpace(controllerPos, side);
        return world.getEntities(null, airSpace);
    }

    @Override
    public Box getAirSpace(BlockPos controllerPos, Direction side) {
        // Get the air blocks
        List<BlockPos> relativeAirBlocks = applySide(airBlocksPos(), side);
        // First block is the min and max block by default
        BlockPos minPos = relativeAirBlocks.get(0);
        BlockPos maxPos = relativeAirBlocks.get(0);
        // Check for the correct min and max block
        for (BlockPos pos: relativeAirBlocks) {
            if (pos.getX() < minPos.getX() ||
                    pos.getY() < minPos.getY() ||
                    pos.getZ() < minPos.getZ()) {
                minPos = pos;
            } else
            if (pos.getX() > maxPos.getX() ||
                    pos.getY() > maxPos.getY() ||
                    pos.getZ() > maxPos.getZ()) {
                maxPos = pos;
            }
        }
        // Convert the relative positions to real ones
        minPos = minPos.add(controllerPos);
        maxPos = maxPos.add(controllerPos);
        // Return the Air Space
        float offset = 0.3f;
        return new Box(
                minPos.getX() + offset,
                minPos.getY() + offset,
                minPos.getZ() + offset,
                maxPos.getX() + 1-offset,
                maxPos.getY() + 1-offset,
                maxPos.getZ() + 1-offset
        );
    }

    @Override
    public void teleporterTasks(Entity entity, World worldIn, World worldOut, BlockPos controllerPos, Direction side, boolean shouldBuild) {
        Chunk chunk = worldIn.getChunk(controllerPos);
        worldIn.getChunkManager().getChunk(chunk.getPos().x, chunk.getPos().z, ChunkStatus.FULL, true);
        if (shouldBuild) {
            List<BlockPos> posData = getPosData(controllerPos, side);
            Map<BlockPos, BlockState> blockData = getBlockData(worldOut, posData);
            destroyTM(worldOut, posData);
            buildTM(worldIn, blockData);
            doCooldown(worldIn, controllerPos, side);
        }
    }

    @Override
    public final void doCooldown(World worldIn, BlockPos controllerPos, Direction side) {
        for (BlockPos relativePos:getCoreBlocksPos(side)) {
            worldIn.setBlockState(controllerPos.add(relativePos), worldIn.getBlockState(controllerPos.add(relativePos)).with(TimeMachineCoreBlock.TM_READY, false));
            ((TMCooldownBlockEntity)worldIn.getBlockEntity(controllerPos.add(relativePos))).setTime(getCooldownTime());
        }
    }

    @Override
    public String toString() {
        return ModRegistries.TIME_MACHINES.getId(this).toString();
    }

    @Override
    public com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine removeHooks() {
        return this;
    }

    @Override
    public HashSet<BlockPos> getUpgradePos(TimeMachineUpgrade upgrade) {
        return new HashSet<>();
    }

    // Private utils methods

    private static List<BlockPos> applySide(List<BlockPos> posList, Direction side) {
        posList = new ArrayList<>(posList);
        if (side == Direction.NORTH) return posList;
        for (int i = 0; i < posList.size(); i++) {
            switch (side) {
                case SOUTH:
                    posList.set(i, posList.get(i).rotate(BlockRotation.CLOCKWISE_180));
                    break;
                case WEST:
                    posList.set(i, posList.get(i).rotate(BlockRotation.COUNTERCLOCKWISE_90));
                    break;
                case EAST:
                    posList.set(i, posList.get(i).rotate(BlockRotation.CLOCKWISE_90));
                    break;
            }
        }
        return posList;
    }

    private boolean isComponentTypeBuilt(TimeMachineComponentType type, World world, BlockPos controllerPos, Direction side) {
        List<BlockPos> positions;
        BlockState[] states;

        switch (type) {
            case CORE:
                positions = getCoreBlocksPos(side);
                states = getCoreBlocks();
                break;
            case BASIC:
            case UPGRADE:
                positions = getBasicBlocksPos(side);
                states = ArrayUtils.addAll(getBasicBlocks(), getUpgradeBlocks());
                break;
            case CONTROLPANEL:
                positions = Collections.singletonList(BlockPos.ORIGIN);
                states = getControllerBlocks();
                break;
            default:
                throw new IllegalArgumentException("EnumMachineComponentType can't be null");
        }

        for (BlockPos pos:positions) {
            boolean coincidence = false;
            for (BlockState state:states) {
                if (type == TimeMachineComponentType.CORE ?
                        world.getBlockState(controllerPos.add(pos)).getBlock().getDefaultState() == state.getBlock().getDefaultState() :
                        world.getBlockState(controllerPos.add(pos)) == state) {
                    coincidence = true;
                    break;
                }
            }
            if (!coincidence) return false;
        }
        return true;
    }

    private List<BlockPos> getPosData(BlockPos controllerPos, Direction side) {
        ArrayList<BlockPos> posData = new ArrayList<>();
        posData.add(BlockPos.ORIGIN);
        posData.addAll(getCoreBlocksPos(side));
        posData.addAll(getBasicBlocksPos(side));
        posData.addAll(getAirBlocksPos(side));
        for (int i = 0; i < posData.size(); i++) {
            posData.set(i, controllerPos.add(posData.get(i)));
        }
        return posData;
    }

    private Map<BlockPos, BlockState> getBlockData(World world, List<BlockPos> posData) {
        Map<BlockPos, BlockState> blockData = new HashMap<>(posData.size());
        for (BlockPos pos: posData) {
            blockData.put(pos, world.getBlockState(pos));
        }
        return blockData;
    }

    private void destroyTM(World world, List<BlockPos> posData) {
        for (BlockPos pos: posData) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    private void buildTM(World world, Map<BlockPos, BlockState> blockData) {
        for (BlockPos pos: blockData.keySet()) {
            world.setBlockState(pos, blockData.get(pos));
        }
    }

    // Template delegates

    @Override
    public int getCooldownTime() {
        return template.getCooldownTime();
    }

    @Override
    public int getTier() {
        return template.getTier();
    }

    @Override
    public List<BlockPos> coreBlocksPos() {
        return template.coreBlocksPos();
    }

    @Override
    public List<BlockPos> basicBlocksPos() {
        return template.basicBlocksPos();
    }

    @Override
    public List<BlockPos> airBlocksPos() {
        return template.airBlocksPos();
    }

    @Override
    public BlockState[] getControllerBlocks() {
        return template.getControllerBlocks();
    }

    @Override
    public BlockState[] getCoreBlocks() {
        return template.getCoreBlocks();
    }

    @Override
    public BlockState[] getBasicBlocks() {
        return template.getBasicBlocks();
    }

    @Override
    public int getEntityMaxLoad() {
        return template.getEntityMaxLoad();
    }

    @Override
    public int getCorruptionMultiplier() {
        return template.getCorruptionMultiplier();
    }

    public enum TimeMachineComponentType {
        BASIC, CORE, CONTROLPANEL, UPGRADE, AIR
    }
}