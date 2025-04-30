package net.yastral.theendisheremod.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yastral.theendisheremod.TheEndIsHereMod;
import net.yastral.theendisheremod.particle.ShadowParticleProvider;
import net.yastral.theendisheremod.particle.ModParticles;

/**
 * Classe per la gestione degli eventi lato client
 */
@Mod.EventBusSubscriber(modid = TheEndIsHereMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventHandlers {

    /**
     * Registra i provider delle particelle
     */
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        // Registra il provider per le particelle dell'ombra
        event.registerSpriteSet(ModParticles.SHADOW_PARTICLES.get(), ShadowParticleProvider::new);
    }
}
