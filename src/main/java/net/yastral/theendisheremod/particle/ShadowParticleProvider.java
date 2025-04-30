package net.yastral.theendisheremod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Provider per le particelle dell'ombra
 * Da registrare nel ParticleFactoryRegisterEvent
 */
@OnlyIn(Dist.CLIENT)
public class ShadowParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprites;

    public ShadowParticleProvider(SpriteSet spriteSet) {
        this.sprites = spriteSet;
    }

    @Override
    public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                   double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed) {
        return new ShadowParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
    }

    /**
     * Classe per la particella dell'ombra
     */
    @OnlyIn(Dist.CLIENT)
    private static class ShadowParticle extends TextureSheetParticle {
        private final SpriteSet spriteSet;

        protected ShadowParticle(ClientLevel level, double x, double y, double z,
                                 double xSpeed, double ySpeed, double zSpeed,
                                 SpriteSet spriteSet) {
            super(level, x, y, z, xSpeed, ySpeed, zSpeed);

            this.spriteSet = spriteSet;
            this.lifetime = 20 + this.random.nextInt(10);
            this.gravity = 0.1f;
            this.friction = 0.9f;
            this.hasPhysics = true;

            // Colore nero per le particelle dell'ombra
            this.setColor(0.0f, 0.0f, 0.0f);
            this.setAlpha(0.8f);

            // Dimensione della particella
            this.quadSize = 0.2f + this.random.nextFloat() * 0.1f;

            this.setSpriteFromAge(spriteSet);
        }

        @Override
        public void tick() {
            super.tick();
            this.alpha = 0.8f * (1.0f - ((float)this.age / (float)this.lifetime));
            this.setSpriteFromAge(this.spriteSet);
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }
    }
}
