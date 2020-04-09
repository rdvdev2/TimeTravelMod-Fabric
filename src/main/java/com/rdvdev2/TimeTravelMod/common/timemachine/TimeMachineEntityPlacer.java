package com.rdvdev2.TimeTravelMod.common.timemachine;

import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import net.fabricmc.fabric.api.dimension.v1.EntityPlacer;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TimeMachineEntityPlacer implements EntityPlacer {

    private final com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine tm;
    private final World currentWorld;
    private final BlockPos pos;
    private final Direction side;
    private final boolean shouldBuild;
    
    public TimeMachineEntityPlacer(TimeMachine tm, World currentWorld, BlockPos pos, Direction side, boolean shouldBuild) {
        this.tm = tm;
        this.currentWorld = currentWorld;
        this.pos = pos;
        this.side = side;
        this.shouldBuild = shouldBuild;
    }
    
    @Override
    public BlockPattern.TeleportTarget placeEntity(Entity teleported, ServerWorld destination, Direction portalDir, double horizontalOffset, double verticalOffset) {
        tm.teleporterTasks(teleported, destination, currentWorld, pos, side, shouldBuild);
        return new BlockPattern.TeleportTarget(teleported.getPos(), teleported.getVelocity(), (int) teleported.getHeadYaw());
    }
}