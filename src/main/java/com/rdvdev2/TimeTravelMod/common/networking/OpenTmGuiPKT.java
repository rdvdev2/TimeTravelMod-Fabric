package com.rdvdev2.TimeTravelMod.common.networking;

import com.rdvdev2.TimeTravelMod.Mod;
import com.rdvdev2.TimeTravelMod.ModRegistries;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import io.netty.buffer.Unpooled;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class OpenTmGuiPKT {
    
    public static final Identifier ID = new Identifier(Mod.MODID, "opentmgui");
    
    public OpenTmGuiPKT(){
        this.additionalEntities = new HashSet<>();
    }

    public TimeMachine tm;
    public BlockPos pos;
    public Direction side;
    public Set<UUID> additionalEntities;

    public OpenTmGuiPKT(TimeMachine tm, BlockPos pos, Direction side, UUID... aditionalEntities) {
        this();
        this.tm = tm.removeHooks();
        this.pos = pos;
        this.side = side;
        if (aditionalEntities != null && aditionalEntities.length != 0) this.additionalEntities = Arrays.stream(aditionalEntities).collect(Collectors.toSet());
    }

    public PacketByteBuf encode() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeIdentifier(ModRegistries.TIME_MACHINES.getId(tm));
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(side);
        buf.writeInt(additionalEntities.size());
        additionalEntities.forEach(buf::writeUuid);
        return buf;
    }
}
