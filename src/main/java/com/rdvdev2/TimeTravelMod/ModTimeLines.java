package com.rdvdev2.TimeTravelMod;

import com.rdvdev2.TimeTravelMod.api.dimension.TimeLine;
import com.rdvdev2.TimeTravelMod.common.world.dimension.PresentTimeLine;
import com.rdvdev2.TimeTravelMod.common.world.dimension.oldwest.OldWestDimension;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.rdvdev2.TimeTravelMod.Mod.MODID;

public class ModTimeLines {

    public static final TimeLine PRESENT = new PresentTimeLine();
    public static final TimeLine OLDWEST = TimeLine.getNew(1, OldWestDimension::new, true, new Identifier(MODID, "oldwest"));
    
    public static void register() {
        Registry.register(ModRegistries.TIME_LINES, new Identifier(MODID, "present"), PRESENT);
        Registry.register(ModRegistries.TIME_LINES, new Identifier(MODID, "oldwest"), OLDWEST);
    }
}
