package com.rdvdev2.timetravelmod.impl.common.dimension.oldwest;

import com.rdvdev2.timetravelmod.impl.ModBiomes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class OldWestSetBaseBiomesLayer implements IdentitySamplingLayer {

    private final int[] DRY_BIOMES;
    private final int[] TEMPERATE_BIOMES;
    private final int[] COOL_BIOMES;
    private final int[] SNOWY_BIOMES;

    public OldWestSetBaseBiomesLayer(Registry<Biome> biomeRegistry) {
        int oldWestId = biomeRegistry.getRawId(biomeRegistry.get(ModBiomes.OLD_WEST));
        DRY_BIOMES       = new int[]{oldWestId, 2};
        TEMPERATE_BIOMES = new int[]{oldWestId, 2};
        COOL_BIOMES      = new int[]{oldWestId, 2};
        SNOWY_BIOMES     = new int[]{oldWestId, 2};
    }

    public int sample(LayerRandomnessSource context, int value) { // FIXME: Recover old biome distribution logic
        int i = (value & 3840) >> 8;
        value &= -3841;
        if (!isOcean(value) && value != 14) {
            switch(value) {
                case 1:
                    if (i > 0) {
                        return context.nextInt(3) == 0 ? 39 : 38; // Badlands
                    }

                    return DRY_BIOMES[context.nextInt(DRY_BIOMES.length)];
                case 2:
                    return TEMPERATE_BIOMES[context.nextInt(TEMPERATE_BIOMES.length)];
                case 3:
                    return COOL_BIOMES[context.nextInt(COOL_BIOMES.length)];
                case 4:
                    return SNOWY_BIOMES[context.nextInt(SNOWY_BIOMES.length)];
                default:
                    return 14; // Mushrooms
            }
        } else {
            return value;
        }
    }

    private static boolean isOcean(int id) {
        return id == 44 || id == 45 || id == 0 || id == 46 || id == 10 || id == 47 || id == 48 || id == 24 || id == 49 || id == 50;
    }

}
