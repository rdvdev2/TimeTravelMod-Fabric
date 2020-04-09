package com.rdvdev2.TimeTravelMod;

import com.rdvdev2.TimeTravelMod.common.networking.DimensionTpPKT;
import com.rdvdev2.TimeTravelMod.common.networking.OpenTmGuiPKT;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

public class ModPacketHandler {
    
    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ClientSidePacketRegistry.INSTANCE.register(OpenTmGuiPKT.ID, com.rdvdev2.TimeTravelMod.client.networking.OpenTmGuiPKT::decode);
    }
    
    public static void registerServer() {
        ServerSidePacketRegistry.INSTANCE.register(DimensionTpPKT.ID, DimensionTpPKT::decode);
    }
}
