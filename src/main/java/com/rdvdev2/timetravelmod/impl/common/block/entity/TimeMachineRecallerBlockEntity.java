package com.rdvdev2.timetravelmod.impl.common.block.entity;

import com.rdvdev2.timetravelmod.impl.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class TimeMachineRecallerBlockEntity extends BlockEntity {

    private RegistryKey<World> tmWorld;
    private BlockPos rootPos;
    private BlockPos trackerPos;

    public TimeMachineRecallerBlockEntity() {
        super(ModBlocks.Entities.TIME_MACHINE_RECALLER);
    }

    public void configure(RegistryKey<World> world, BlockPos rootPos, BlockPos trackerPos) {
        this.tmWorld = world;
        this.rootPos = rootPos;
        this.trackerPos = trackerPos;
        markDirty();
    }

    public void clear() {
        this.tmWorld = null;
        this.rootPos = null;
        this.trackerPos = null;
        markDirty();
    }

    public boolean isConfigured() {
        return tmWorld != null;
    }

    public RegistryKey<World> getTmWorld() {
        return tmWorld;
    }

    public BlockPos getRootPos() {
        return rootPos;
    }

    public BlockPos getTrackerPos() {
        return trackerPos;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        if (tag.contains("time_machine_world")) {
            tmWorld = RegistryKey.of(Registry.DIMENSION, Identifier.tryParse(tag.getString("time_machine_world")));

            CompoundTag rootPosTag = tag.getCompound("root_pos");
            rootPos = new BlockPos(rootPosTag.getInt("x"), rootPosTag.getInt("y"), rootPosTag.getInt("z"));

            CompoundTag trackerPosTag = tag.getCompound("tracker_pos");
            trackerPos = new BlockPos(trackerPosTag.getInt("x"), trackerPosTag.getInt("y"), trackerPosTag.getInt("z"));
        }
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (tmWorld != null) {
            tag.putString("time_machine_world", tmWorld.getValue().toString());

            CompoundTag rootPosTag = new CompoundTag();
            rootPosTag.putInt("x", rootPos.getX());
            rootPosTag.putInt("y", rootPos.getY());
            rootPosTag.putInt("z", rootPos.getZ());
            tag.put("root_pos", rootPosTag);

            CompoundTag trackerPosTag = new CompoundTag();
            trackerPosTag.putInt("x", trackerPos.getX());
            trackerPosTag.putInt("y", trackerPos.getY());
            trackerPosTag.putInt("z", trackerPos.getZ());
            tag.put("tracker_pos", trackerPosTag);
        }
        return super.toTag(tag);
    }
}
