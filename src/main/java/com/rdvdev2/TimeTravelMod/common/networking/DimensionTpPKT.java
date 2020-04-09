package com.rdvdev2.TimeTravelMod.common.networking;

import com.rdvdev2.TimeTravelMod.Mod;
import com.rdvdev2.TimeTravelMod.ModRegistries;
import com.rdvdev2.TimeTravelMod.ModTimeLines;
import com.rdvdev2.TimeTravelMod.api.dimension.AbstractTimeLineDimension;
import com.rdvdev2.TimeTravelMod.api.dimension.TimeLine;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import com.rdvdev2.TimeTravelMod.api.timemachine.exception.IncompatibleTimeMachineHooksException;
import com.rdvdev2.TimeTravelMod.common.timemachine.TimeMachineEntityPlacer;
import com.rdvdev2.TimeTravelMod.common.util.TimeMachineUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DimensionTpPKT {

    public static final Identifier ID = new Identifier(Mod.MODID, "dimensiontp");

    public DimensionTpPKT() {
        additionalEntities = new HashSet<>();
    }

    private TimeLine tl;
    private TimeMachine tm;
    private BlockPos pos;
    private Direction side;
    private Set<UUID> additionalEntities;

    public DimensionTpPKT(TimeLine tl, TimeMachine tm, BlockPos pos, Direction side, UUID... additionalEntities) {
        this();
        this.tl = tl;
        this.tm = tm.removeHooks();
        this.pos = pos;
        this.side = side;
        if (additionalEntities != null && additionalEntities.length != 0) this.additionalEntities = Arrays.stream(additionalEntities).collect(Collectors.toSet());
    }

    public PacketByteBuf encode() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeIdentifier(ModRegistries.TIME_LINES.getId(tl));
        buf.writeIdentifier(ModRegistries.TIME_MACHINES.getId(tm));
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(side);
        buf.writeInt(additionalEntities.size());
        additionalEntities.forEach(buf::writeUuid);
        return buf;
    }
    
    public static void decode(PacketContext ctx, PacketByteBuf buf) {
        DimensionTpPKT pkt = new DimensionTpPKT();
        pkt.tl = ModRegistries.TIME_LINES.get(buf.readIdentifier());
        pkt.tm = ModRegistries.TIME_MACHINES.get(buf.readIdentifier());
        pkt.pos = buf.readBlockPos();
        pkt.side = buf.readEnumConstant(Direction.class);
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUuid();
            pkt.additionalEntities.add(uuid);
        }
        
        handle(pkt, ctx);
    }

    public static void handle(DimensionTpPKT pkt, PacketContext ctx) {
        ctx.getTaskQueue().execute(() -> {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) ctx.getPlayer();
            DimensionType dim = pkt.tl.getDimensionType();
            BlockPos pos = pkt.pos;
            Direction side = pkt.side;
            ServerWorld origin = serverPlayer.getServerWorld();
            TimeMachine tm;
            try {
                tm = pkt.tm.hook(serverPlayer.world, pos, side);
            } catch (IncompatibleTimeMachineHooksException e) {
                throw new RuntimeException("Time travel was triggered with invalid upgrade configuration");
            }
            List<Entity> entities = tm.getEntitiesInside(origin, pos, side);
            AtomicBoolean entitiesFlag = new AtomicBoolean(true);
            pkt.additionalEntities.forEach( entity -> {
                if (!entities.contains(entity)) {
                    entitiesFlag.set(false);
                }
            });
            if (entitiesFlag.get() &&
                    serverPlayer.world.isChunkLoaded(pos) &&
                    TimeMachineUtils.serverCheck(serverPlayer.server, tm, serverPlayer.world, serverPlayer, pos, side)) {
                if (tm.getTier() >= pkt.tl.getMinTier()) {
                    applyCorruption(tm, serverPlayer.dimension, dim, serverPlayer.server);
                    FabricDimensions.teleport(serverPlayer, dim, new TimeMachineEntityPlacer(tm, origin , pos, side, true));
                    pkt.additionalEntities.stream()
                            .map(origin::getEntity)
                            .filter(Objects::nonNull)
                            .forEach(entity -> FabricDimensions.teleport(entity, dim, new TimeMachineEntityPlacer(tm, origin, pos, side, false)));
                } else {
                    Arrays.stream(serverPlayer.server.getPlayerManager().getOpList().getNames())
                            .map(op -> serverPlayer.server.getPlayerManager().getPlayer(op))
                            .forEach(op -> {
                                if (op != null)
                                    op.sendChatMessage(TimeMachineUtils.Check.UNREACHABLE_DIM.getCheaterReport(serverPlayer), MessageType.CHAT);
                            });
                }
            } else {
                if (!entitiesFlag.get()) {
                    serverPlayer.sendChatMessage(TimeMachineUtils.Check.ENTITIES_ESCAPED.getClientError(), MessageType.GAME_INFO);
                }
                Mod.LOGGER.error("Time Travel canceled due to incorrect conditions");
            }
        });
    }

    public static void applyCorruption(TimeMachine tm, DimensionType origDim, DimensionType destDim, MinecraftServer server) {
        TimeLine origTimeLine = origDim == DimensionType.OVERWORLD ? ModTimeLines.PRESENT : ((AbstractTimeLineDimension) server.getWorld(origDim).dimension).getTimeLine();
        TimeLine destTimeLine = destDim == DimensionType.OVERWORLD ? ModTimeLines.PRESENT : ((AbstractTimeLineDimension) server.getWorld(destDim).dimension).getTimeLine();
        int origTier = origTimeLine.getMinTier();
        int destTier = destTimeLine.getMinTier();
        
        int amount = Math.abs(destTier - origTier) * tm.getCorruptionMultiplier();
        origTimeLine.getCorruption().increaseCorruptionLevel(amount);
        destTimeLine.getCorruption().increaseCorruptionLevel(amount);
    }
}
