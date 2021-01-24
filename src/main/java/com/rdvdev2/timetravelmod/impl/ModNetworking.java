package com.rdvdev2.timetravelmod.impl;

import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineExecutor;
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineManager;
import com.rdvdev2.timetravelmod.impl.common.timemachine.exception.TimeMachineExecutionException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

// TODO: Separate packets
public class ModNetworking {

    public static final Identifier OPEN_TIME_MACHINE_GUI = Mod.identifier("open_time_machine_gui");
    public static final Identifier OPEN_CREATIVE_TIME_MACHINE_GUI = Mod.identifier("open_creative_time_machine_gui");
    public static final Identifier OPEN_ENGINEER_BOOK_GUI = Mod.identifier("open_engineer_book_gui");

    public static final Identifier RUN_TIME_MACHINE = Mod.identifier("run_time_machine");
    public static final Identifier RUN_CREATIVE_TIME_MACHINE = Mod.identifier("run_creative_time_machine");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(RUN_TIME_MACHINE,
                (server, player, handler, buf, responseSender) -> {
                    Identifier worldIdentifier = buf.readIdentifier();
                    BlockPos rootPos = buf.readBlockPos();
                    Identifier timelineIdentifier = buf.readIdentifier();

                    server.execute(() -> {
                        World world = player.getServer().getWorld(RegistryKey.of(Registry.DIMENSION, worldIdentifier));
                        if (world == null) return;
                        ITimeline timeline = ModRegistries.TIMELINE.get(timelineIdentifier);
                        if (timeline == null) return;
                        Optional<TimeMachineExecutor> _tme = TimeMachineManager.getInstance().generateExecutor(world, rootPos);
                        _tme.ifPresent(tme -> {
                            try {
                                tme.checkAndRun(timeline, player, server);
                            } catch (TimeMachineExecutionException e) {
                                player.sendMessage(e.getError().getClientError(), true);
                                if (ModConfig.getInstance().getCommon().getEnableCheaterReports()) {
                                    Arrays.stream(server.getPlayerManager().getOpNames())
                                            .map(server.getPlayerManager()::getPlayer)
                                            .filter(p -> !Objects.equals(p, player))
                                            .forEach(p -> p.sendMessage(e.getError().getCheaterReport(player), false));
                                }
                            }
                        });
                    });
                });

        ServerPlayNetworking.registerGlobalReceiver(RUN_CREATIVE_TIME_MACHINE,
                (server, player, handler, buf, responseSender) -> {
                    Identifier timelineIdentifier = buf.readIdentifier();

                    server.execute(() -> {
                        ITimeline timeline = ModRegistries.TIMELINE.get(timelineIdentifier);
                        if (timeline == null) return;
                        ServerWorld world = server.getWorld(timeline.getWorld());
                        if (world == null || world.equals(player.world)) return;
                        player.getItemsHand().forEach( itemStack -> {
                            if (itemStack.getItem() == ModItems.CREATIVE_TIME_MACHINE) {
                                BlockPos pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, player.getBlockPos());
                                if (pos.getY() == 0) pos = pos.up(world.getSeaLevel() + 2);
                                while (world.getBlockState(pos).isSolidBlock(world, pos) || world.getBlockState(pos.up()).isSolidBlock(world, pos.up())) pos = pos.up(); // Air space
                                while (!world.getBlockState(pos.down()).isSolidBlock(world, pos)) pos = pos.down(); // Touching ground
                                // TODO: Special logic for flying players
                                player.teleport(world, player.getX(), pos.getY(), player.getZ(), player.yaw, player.pitch);
                            }
                        });
                    });
                });
    }
}
