package com.rdvdev2.TimeTravelMod.common.world.dimension.oldwest.biome;

import com.rdvdev2.TimeTravelMod.ModBlocks;
import com.rdvdev2.TimeTravelMod.ModFeatures;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import static net.minecraft.world.biome.Biome.Precipitation.NONE;

public final class OldWestBiome extends Biome {
    public OldWestBiome() {
        // Vanilla desert start
        super(new Biome.Settings().surfaceBuilder(new ConfiguredSurfaceBuilder<>(SurfaceBuilder.DEFAULT, SurfaceBuilder.SAND_CONFIG)).precipitation(NONE).category(Category.DESERT).depth(0.125F).scale(0.05F).temperature(2.0F).downfall(0.0F).waterColor(4159204).waterFogColor(329011).parent(null));
        this.addStructureFeature(Feature.VILLAGE.configure(new VillageFeatureConfig("minecraft:village/oldwest/town_centers", 6))); // Custom village
        this.addStructureFeature(Feature.PILLAGER_OUTPOST.configure(DefaultFeatureConfig.DEFAULT));
        //this.addStructureFeature();(Feature.DESERT_PYRAMID, IFeatureConfig.NO_FEATURE_CONFIG);
        this.addStructureFeature(Feature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004D, MineshaftFeature.Type.NORMAL))); // Custom mineshaft (planned)
        //this.addStructureFeature();(Feature.STRONGHOLD, IFeatureConfig.NO_FEATURE_CONFIG);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultStructures(this);
        DefaultBiomeFeatures.addDesertLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addDefaultGrass(this);
        DefaultBiomeFeatures.addDesertDeadBushes(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDesertVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addDesertFeatures(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
        this.addSpawn(EntityCategory.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, 19, 4, 4));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 1, 1, 1));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.HUSK, 80, 4, 4));

        // Time Travel Mod start
        this.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, ModBlocks.TIME_CRYSTAL_ORE.getDefaultState(), 4)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(1, 0, 0, 16))));
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ModFeatures.GUNPOWDER.configure(DefaultFeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHANCE_TOP_SOLID_HEIGHTMAP.configure(new ChanceDecoratorConfig(100))));
        DefaultBiomeFeatures.addExtraGoldOre(this);
        this.addStructureFeature(Feature.MINESHAFT.configure(new MineshaftFeatureConfig(0.002D, MineshaftFeature.Type.MESA)));
    }
}
