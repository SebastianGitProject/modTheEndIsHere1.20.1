package net.yastral.theendisheremod.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.yastral.theendisheremod.entity.fakeplayer.ModFakePlayer;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.TooltipFlag;
import net.yastral.theendisheremod.item.ModItems;
import net.yastral.theendisheremod.particle.ModParticles;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Item personalizzato che genera un player ombra con skin nera
 * quando viene usato con il tasto destro
 */
public class ShadowSummonerItem extends Item {
    public ShadowSummonerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide() && level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel) level;

            // Calcola la posizione di spawn davanti al giocatore
            double spawnDistance = 2.0;  // Distanza davanti al giocatore
            float playerYaw = player.getYRot();
            float playerPitch = player.getXRot();

            // Calcola il vettore direzione dalla rotazione del giocatore
            double xDir = -Math.sin(Math.toRadians(playerYaw));
            double zDir = Math.cos(Math.toRadians(playerYaw));

            // Calcola la posizione di spawn
            double spawnX = player.getX() + xDir * spawnDistance;
            double spawnY = player.getY();
            double spawnZ = player.getZ() + zDir * spawnDistance;

            // Crea un FakePlayer con skin nera utilizzando la classe ModFakePlayer
            FakePlayer shadowPlayer = ModFakePlayer.create(serverLevel);
            player.sendSystemMessage(Component.literal("§eNull joined the game"));
            shadowPlayer.setGameMode(GameType.SURVIVAL);
            shadowPlayer.setInvulnerable(false);
            if (shadowPlayer != null) {
                // Imposta la posizione del player ombra
                shadowPlayer.setPos(spawnX, spawnY, spawnZ);
                // Imposta gli attributi del player
                shadowPlayer.setCustomName(Component.literal("ShadowPlayer"));
                shadowPlayer.setCustomNameVisible(false);

                // Fai guardare il player ombra verso il giocatore
                shadowPlayer.setYRot((playerYaw + 180) % 360);
                shadowPlayer.setXRot(playerPitch);

                // Aggiungi il player ombra al mondo
                serverLevel.addFreshEntity(shadowPlayer);

                // Crea un effetto di particelle attorno al shadow player
                serverLevel.sendParticles(
                        ModParticles.SHADOW_PARTICLES.get(),
                        shadowPlayer.getX(), shadowPlayer.getY() + 1.0, shadowPlayer.getZ(),
                        30, 0.5, 1.0, 0.5, 0.05
                );

                // Invia un feedback al giocatore
                player.sendSystemMessage(Component.literal("Shadow player summoned!"));

                // Riproduce un suono quando viene evocato il player ombra
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.5f, 1.5f);
            }
        }

        // Danneggia l'oggetto dopo l'uso
        itemstack.hurtAndBreak(1, player,
                p -> p.broadcastBreakEvent(p.getUsedItemHand()));

        // Aggiungi un cooldown per evitare spam
        player.getCooldowns().addCooldown(this, 100); // 5 secondi di cooldown (100 tick)

        return InteractionResultHolder.success(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.theendisheremod.shadow_summoner.tooltip"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }


}



/*public ShadowSummonerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.sendSystemMessage(Component.literal("diocane 1"));
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide() && level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel) level;

            ModFakePlayer fakePlayer = new ModFakePlayer(EntityType.VILLAGER, serverLevel);
            fakePlayer.setPos(player.getX(), player.getY(), player.getZ()); // Posiziona l'entità
            fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE));
            System.out.println("FakePlayer ItemStack: " + fakePlayer.getMainHandItem());

            serverLevel.addFreshEntity(fakePlayer);
            //Chicken chicken = new Chicken(EntityType.CHICKEN, serverLevel);
            //Player shadowPlayer = new Player(EntityType.PLAYER, serverLevel);
            //chicken.setPos(player.getX(), player.getY(), player.getZ());
            //serverLevel.addFreshEntity(chicken);
        }

        return InteractionResultHolder.success(itemstack);
    }*/
