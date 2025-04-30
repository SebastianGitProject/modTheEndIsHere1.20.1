package net.yastral.theendisheremod.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yastral.theendisheremod.TheEndIsHereMod;

/**
 * Classe per la registrazione delle particelle personalizzate
 * Utilizzato per l'effetto dello spawn del Shadow Player
 */
public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TheEndIsHereMod.MOD_ID);

    // Registra una particella nera per l'effetto dello spawn del shadow player
    public static final RegistryObject<SimpleParticleType> SHADOW_PARTICLES =
            PARTICLE_TYPES.register("shadow_particles",
                    () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
