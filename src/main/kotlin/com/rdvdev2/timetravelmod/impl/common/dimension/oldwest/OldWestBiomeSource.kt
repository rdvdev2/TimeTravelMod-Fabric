package com.rdvdev2.timetravelmod.impl.common.dimension.oldwest

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.rdvdev2.timetravelmod.impl.ModBiomes
import com.rdvdev2.timetravelmod.mixin.common.IVanillaLayeredBiomeSource
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.RegistryLookupCodec
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.source.BiomeSource
import java.util.function.Supplier

class OldWestBiomeSource(val seed: Long, val biomeRegistry: Registry<Biome>) : BiomeSource(BIOMES.stream().map { Supplier { biomeRegistry.getOrThrow(it) } }) {

    private val biomeSampler = OldWestLayers.build(seed, 4, 4, biomeRegistry)

    override fun getCodec() = CODEC

    @Environment(EnvType.CLIENT)
    override fun withSeed(seed: Long) = OldWestBiomeSource(seed, biomeRegistry)

    override fun getBiomeForNoiseGen(biomeX: Int, biomeY: Int, biomeZ: Int) = biomeSampler.sample(biomeRegistry, biomeX, biomeZ)!!

    companion object {
        val CODEC: Codec<OldWestBiomeSource> = RecordCodecBuilder.create {
            it.group(
                Codec.LONG.fieldOf("seed").stable().forGetter(OldWestBiomeSource::seed),
                RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(OldWestBiomeSource::biomeRegistry)
            ).apply(it, ::OldWestBiomeSource)
        }

        private val BIOMES: List<RegistryKey<Biome>> = IVanillaLayeredBiomeSource.getBiomes() + ModBiomes.OLD_WEST
    }

}