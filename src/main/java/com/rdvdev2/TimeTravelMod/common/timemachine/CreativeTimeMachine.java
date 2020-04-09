package com.rdvdev2.TimeTravelMod.common.timemachine;

import com.rdvdev2.TimeTravelMod.ModItems;
import com.rdvdev2.TimeTravelMod.ModRegistries;
import com.rdvdev2.TimeTravelMod.ModTriggers;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachineTemplate;
import com.rdvdev2.TimeTravelMod.common.networking.OpenTmGuiPKT;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CreativeTimeMachine extends TimeMachine {

    public CreativeTimeMachine() {
        super(new TimeMachineTemplate() {
            @Override
            public int getCooldownTime() {
                return 0;
            }

            private int tier = 0;

            @Override
            public int getTier() {
                if (tier == 0) {
                    ModRegistries.TIME_LINES.forEach(timeLine -> tier = Math.max(timeLine.getMinTier(), tier));
                }
                return tier;
            }

            @Override
            public List<BlockPos> coreBlocksPos() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<BlockPos> basicBlocksPos() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<BlockPos> airBlocksPos() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public BlockState[] getControllerBlocks() {
                return new BlockState[0];
            }

            @Override
            public BlockState[] getCoreBlocks() {
                return new BlockState[0];
            }

            @Override
            public BlockState[] getBasicBlocks() {
                return new BlockState[0];
            }

            @Override
            public int getCorruptionMultiplier() {
                return 0;
            }
        });
    }

    @Override
    public void run(World world, PlayerEntity playerIn, BlockPos controllerPos, Direction side) {
        if (isPlayerInside(world, controllerPos, side, playerIn) &&
                !isOverloaded(world, controllerPos, side)) {
            if (playerIn instanceof ServerPlayerEntity) {
                ModTriggers.ACCESS_TIME_MACHINE.trigger((ServerPlayerEntity) playerIn);
            }
            OpenTmGuiPKT pkt = new OpenTmGuiPKT(this, controllerPos, side);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerIn, OpenTmGuiPKT.ID, pkt.encode());
        }
    }

    @Override
    public boolean isBuilt(World world, BlockPos controllerPos, Direction side) {
        return true;
    }

    @Override
    public Box getAirSpace(BlockPos controllerPos, Direction side) {
        return new Box(
                controllerPos.getX() -1,
                controllerPos.getY() -1,
                controllerPos.getZ() -1,
                controllerPos.getX() +1,
                controllerPos.getY() +1,
                controllerPos.getZ() +1
        );
    }

    @Override
    public boolean isPlayerInside(World world, BlockPos controllerPos, Direction side, PlayerEntity player) {
        return ItemStack.areItemsEqual(player.inventory.getMainHandStack(), new ItemStack(ModItems.CREATIVE_TIME_MACHINE, 1));
    }

    @Override
    public void teleporterTasks(@Nullable Entity entity, World worldIn, World worldOut, BlockPos controllerPos, Direction side, boolean shouldBuild) {
        Chunk chunk = worldIn.getChunk(controllerPos);
        chunk = worldIn.getChunkManager().getChunk(chunk.getPos().x, chunk.getPos().z, ChunkStatus.FULL, true);
        if (entity != null) {
            int height = worldIn.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (int) entity.getX(), (int) entity.getZ());
            entity.setPos(entity.getX(), height + 1, entity.getZ());
        }
    }

    @Override
    public boolean isOverloaded(World world, BlockPos controllerPos, Direction side) {
        return false;
    }
}
