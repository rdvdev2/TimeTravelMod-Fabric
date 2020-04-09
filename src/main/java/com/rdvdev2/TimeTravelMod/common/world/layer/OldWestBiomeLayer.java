package com.rdvdev2.TimeTravelMod.common.world.layer;

import com.rdvdev2.TimeTravelMod.ModBiomes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import java.util.LinkedHashMap;
import java.util.Map;

public class OldWestBiomeLayer implements IdentitySamplingLayer {

    @SuppressWarnings("unchecked")
    private Map<Biome, Integer> biomes = new LinkedHashMap<>();

    public OldWestBiomeLayer() {
        biomes.put(ModBiomes.OLDWEST, 80);
        biomes.put(Biomes.BADLANDS_PLATEAU, 5);
        biomes.put(Biomes.WOODED_BADLANDS_PLATEAU, 15);
    }

    @Override
    public int sample(LayerRandomnessSource context, int value) {
        int i = (value & 3840) >> 8;
        value = value & -3841;
        if (!OldWestLayers.isOcean(value)) {
            return Registry.BIOME.getRawId(getWeightedBiomeEntry(context)); }
        else {
            return value;
        }
    }

    protected Biome getWeightedBiomeEntry(LayerRandomnessSource context) {
        int totalWeight = 0;
        for (int weight: biomes.values()) totalWeight += weight;
        int result = context.nextInt(totalWeight);
        for (Map.Entry<Biome, Integer> entry: biomes.entrySet()) {
            if (result < entry.getValue()) return entry.getKey();
            else result -= entry.getValue();
        }
        return ModBiomes.OLDWEST;
    }
}