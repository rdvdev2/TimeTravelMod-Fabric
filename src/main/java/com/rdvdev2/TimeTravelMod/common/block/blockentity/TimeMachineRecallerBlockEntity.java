package com.rdvdev2.TimeTravelMod.common.block.blockentity;

import com.rdvdev2.TimeTravelMod.ModBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class TimeMachineRecallerBlockEntity extends BlockEntity {

    private BlockPos controllerPos;
    private Direction side;
    private DimensionType dest;

    public TimeMachineRecallerBlockEntity() {
        super(ModBlocks.TileEntities.TM_RECALLER);
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        markDirty();
    }

    public Direction getSide() {
        return side;
    }

    public void setSide(Direction side) {
        this.side = side;
        markDirty();
    }

    public DimensionType getDest() {
        return dest;
    }

    public void setDest(DimensionType dest) {
        this.dest = dest;
        markDirty();
    }
    
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        if (this.controllerPos != null) {
            tag.putLong("controllerpos", this.controllerPos.asLong());
            tag.putInt("side", this.side.getId());
            tag.putString("dest", Registry.DIMENSION_TYPE.getId(this.dest).toString());
        }
        return tag;
    }
    
    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (tag.contains("controllerpos")) {
            this.controllerPos = BlockPos.fromLong(tag.getLong("controllerpos"));
            this.side = Direction.byId(tag.getInt("side"));
            this.dest = DimensionType.byId(Identifier.tryParse(tag.getString("dest")));
        }
    }
}
