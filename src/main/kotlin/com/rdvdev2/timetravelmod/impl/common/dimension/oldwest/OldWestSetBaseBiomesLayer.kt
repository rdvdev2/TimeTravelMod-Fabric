package com.rdvdev2.timetravelmod.impl.common.dimension.oldwest

import com.rdvdev2.timetravelmod.impl.ModBiomes
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer
import net.minecraft.world.biome.layer.util.LayerRandomnessSource

class OldWestSetBaseBiomesLayer(biomeRegistry: Registry<Biome>) : IdentitySamplingLayer {

    private val DRY_BIOMES: IntArray
    private val TEMPERATE_BIOMES: IntArray
    private val COOL_BIOMES: IntArray
    private val SNOWY_BIOMES: IntArray

    override fun sample(context: LayerRandomnessSource, value: Int): Int { // FIXME: Recover old biome distribution logic
        var value = value
        val i = value and 3840 shr 8
        value = value and -3841
        return if (!isOcean(value) && value != 14) {
            when (value) {
                1 -> {
                    if (i > 0) {
                        return if (context.nextInt(3) == 0) 39 else 38 // Badlands
                    }
                    DRY_BIOMES[context.nextInt(DRY_BIOMES.size)]
                }
                2 -> TEMPERATE_BIOMES[context.nextInt(TEMPERATE_BIOMES.size)]
                3 -> COOL_BIOMES[context.nextInt(COOL_BIOMES.size)]
                4 -> SNOWY_BIOMES[context.nextInt(SNOWY_BIOMES.size)]
                else -> 14 // Mushrooms
            }
        } else {
            value
        }
    }

    companion object {
        private fun isOcean(id: Int) = id == 44 || id == 45 || id == 0 || id == 46 || id == 10 || id == 47 || id == 48 || id == 24 || id == 49 || id == 50
    }

    init {
        val oldWestId = biomeRegistry.getRawId(biomeRegistry[ModBiomes.OLD_WEST])
        DRY_BIOMES = intArrayOf(oldWestId, 2)
        TEMPERATE_BIOMES = intArrayOf(oldWestId, 2)
        COOL_BIOMES = intArrayOf(oldWestId, 2)
        SNOWY_BIOMES = intArrayOf(oldWestId, 2)
    }
}