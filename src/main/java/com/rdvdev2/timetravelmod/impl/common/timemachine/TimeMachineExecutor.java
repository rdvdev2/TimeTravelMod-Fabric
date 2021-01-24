package com.rdvdev2.timetravelmod.impl.common.timemachine;

import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine;
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade;
import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineCoreBlock;
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineCoreBlockEntity;
import com.rdvdev2.timetravelmod.impl.common.timemachine.exception.TimeMachineExecutionException;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TimeMachineExecutor {

    private final BlockPos[] structurePos;
    private final TimeMachineStructure structure;
    private final BlockPos root;
    private final World world;
    private final ITimeMachine timeMachine;
    private final Map<ITimeMachineUpgrade, BlockPos> upgradeMap;

    public TimeMachineExecutor(TimeMachineStructure structure, BlockPos root, World world) {
        this.structurePos = structure.getStructurePos(root);
        this.structure = structure;
        this.root = root;
        this.world = world;
        this.timeMachine = structure.getTimeMachine();
        this.upgradeMap = structure.getUpgrades(world, root);
    }

    private boolean checkIfBuilt() {
        return structure.checkIfBuilt(world, root);
    }

    private boolean checkIfCooledDown() {
        for (BlockPos pos: structurePos) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof TimeMachineCoreBlockEntity && !((TimeMachineCoreBlockEntity) be).isReady()) return false;
        }
        return true;
    }

    private boolean checkIfPlayerIsInside(PlayerEntity player) {
        List<Entity> detectedEntities = new ArrayList<>();
        for (BlockPos pos: structurePos) {
            detectedEntities.addAll(world.getEntitiesByClass(PlayerEntity.class, new Box(pos), player::equals));
        }
        return !detectedEntities.isEmpty();
    }

    private boolean checkIfOverloaded() {
        Set<Entity> detectedEntities = new HashSet<>();
        for (BlockPos pos: structurePos) {
            detectedEntities.addAll(world.getNonSpectatingEntities(Entity.class, new Box(pos)));
        }
        return detectedEntities.size() <= timeMachine.getEntityMaxLoad();
    }

    public boolean isTimeMachineReady(@Nullable PlayerEntity player) {
        return !runChecks(player).isPresent();
    }

    public Optional<TimeMachineError> runChecks(@Nullable PlayerEntity player) {
        if (!checkIfBuilt()) return Optional.of(TimeMachineError.NOT_BUILT);
        if (!checkIfCooledDown()) return Optional.of(TimeMachineError.HOT_CORES);
        if (!checkIfOverloaded()) return Optional.of(TimeMachineError.OVERLOADED);
        if (player != null && !checkIfPlayerIsInside(player)) return Optional.of(TimeMachineError.PLAYER_OUTSIDE);
        return Optional.empty();
    }

    public int getMaxTier() {
        return timeMachine.getTier();
    }

    public void checkAndRun(ITimeline destTl, @Nullable ServerPlayerEntity player, MinecraftServer server) throws TimeMachineExecutionException {

        // Run checks and throw errors
        Optional<TimeMachineError> error = runChecks(player);
        if (error.isPresent()) throw error.get().getException();
        if (timeMachine.getTier() < destTl.getMinTier()) throw TimeMachineError.UNREACHABLE_TIMELINE.getException();

        ServerWorld destWorld = server.getWorld(destTl.getWorld());
        if (destWorld == world) throw TimeMachineError.SAME_TIMELINE.getException();
        ITimeline origTl = ITimeline.getTimelineForWorld(world.getRegistryKey());

        upgradeMap.forEach((u, p) -> u.beforeTeleporting(structure, root, p, world, destWorld, origTl, destTl));

        // Copy the structure on the new dimension with hot cores
        for (BlockPos pos: structurePos) {
            BlockState state = world.getBlockState(pos);
            destWorld.setBlockState(pos, state);
            if (state.getBlock() instanceof TimeMachineCoreBlock) {
                ((TimeMachineCoreBlockEntity) destWorld.getBlockEntity(pos)).setRemainingTicks(timeMachine.getCooldownTime());
            }
        }

        // Teleport all entities
        Set<Entity> detectedEntities = new HashSet<>();
        for (BlockPos pos: structurePos) {
            detectedEntities.addAll(world.getNonSpectatingEntities(Entity.class, new Box(pos)));
        }
        for (Entity entity: detectedEntities) {
            if (entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) entity).teleport(destWorld, entity.getX(), entity.getY(), entity.getZ(), entity.yaw, entity.pitch);
            } else {
                FabricDimensions.teleport(entity, destWorld, new TeleportTarget(entity.getPos(), entity.getVelocity(), entity.yaw, entity.pitch));
            }
        }

        // Remove the original structure
        for (BlockPos pos: structurePos) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

        // Apply corruption
        int corruption = Math.abs(origTl.getMinTier() - destTl.getMinTier()) * timeMachine.getCorruptionMultiplier();
        origTl.getCorruption(server).increaseCorruptionLevel(corruption);
        destTl.getCorruption(server).increaseCorruptionLevel(corruption);

        upgradeMap.forEach((u, p) -> u.afterTeleporting(structure, root, p, world, destWorld, origTl, destTl));
    }

    public enum TimeMachineError {
        NOT_BUILT,
        HOT_CORES,
        PLAYER_OUTSIDE,
        OVERLOADED,
        UNREACHABLE_TIMELINE,
        SAME_TIMELINE;

        public TranslatableText getClientError() {
            return new TranslatableText(String.format("time_travel_mod.error.%s.client", this.name().toLowerCase()));
        }

        public TranslatableText getCheaterReport(ServerPlayerEntity player) {
            return new TranslatableText("time_travel_mod.cheater_report", player.getDisplayName(), String.format("time_travel_mod.error.%s.server", this.name().toLowerCase()));
        }

        private static TranslatableText getBanButton(ServerPlayerEntity player) {
            TranslatableText text = new TranslatableText("time_travel_mod.ban");
            text.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/ban %s", player.getName().asString()))).withColor(Formatting.RED));
            return text;
        }

        public TimeMachineExecutionException getException() {
            return new TimeMachineExecutionException(this);
        }
    }
}
