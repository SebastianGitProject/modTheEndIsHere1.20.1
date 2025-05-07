package net.yastral.theendisheremod.worldgen.dimension;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraftforge.registries.RegistryObject;
import net.yastral.theendisheremod.TheEndIsHereMod;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public class ModDimensions{

    public static final ResourceKey<LevelStem> VOID_KEY =
            ResourceKey.create(Registries.LEVEL_STEM, new ResourceLocation(TheEndIsHereMod.MOD_ID, "void"));

    public static final ResourceKey<Level> VOID_LEVEL_KEY =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(TheEndIsHereMod.MOD_ID, "void"));

    public static final ResourceKey<DimensionType> VOID_DIM_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(TheEndIsHereMod.MOD_ID, "void_type"));

    public static void bootstrapType(BootstapContext<DimensionType> context) {
        context.register(VOID_DIM_TYPE, new DimensionType(
                OptionalLong.of(12000), // fixedTime
                false, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                false, // natural
                1.0, // coordinateScale
                false, // bedWorks
                false, // respawnAnchorWorks
                0, // minY
                256, // height
                256, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                BuiltinDimensionTypes.OVERWORLD_EFFECTS, // effectsLocation
                1.0f, // ambientLight
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)));
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);


        List<Holder<PlacedFeature>> placedFeatures = List.of();
        Holder<Biome> voidBiome = biomeRegistry.getOrThrow(Biomes.THE_VOID);
        Optional<HolderSet<StructureSet>> structureSets = Optional.empty();

        // Crea un generatore di mondo flat con solo aria
        FlatLevelGeneratorSettings flatSettings = new FlatLevelGeneratorSettings(
                structureSets,
                voidBiome,
                placedFeatures);

        // Aggiungi solo uno strato di aria al layer 0
        flatSettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.AIR));

        // Imposta il biome di default
        //flatSettings.adjustGenerationSettings(biomeRegistry.getOrThrow(Biomes.THE_VOID));

        // Crea un generatore flat
        FlatLevelSource flatGenerator = new FlatLevelSource(flatSettings);

        // Crea un nuovo LevelStem con il generatore flat
        LevelStem stem = new LevelStem(
                dimTypes.getOrThrow(ModDimensions.VOID_DIM_TYPE),
                flatGenerator);


        context.register(VOID_KEY, stem);
    }
}