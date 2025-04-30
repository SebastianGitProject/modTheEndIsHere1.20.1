package net.yastral.theendisheremod.entity.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
//import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class ModFakePlayer{
    private static final UUID SHADOW_PLAYER_UUID = UUID.fromString("9e0d28bd-0a9d-473a-aa45-e3c1b3ac6c16");

    /**
     * Crea un FakePlayer con skin nera
     * @param level Il ServerLevel in cui creare il player
     * @return Il FakePlayer creato
     */
    public static FakePlayer create(ServerLevel level) {
        // Creazione di un profilo del giocatore con un UUID specifico
        GameProfile profile = new GameProfile(SHADOW_PLAYER_UUID, "ShadowPlayer");

        // Aggiungiamo la proprietà della skin nera al profilo
        ShadowSkinManager.applyShadowSkin(profile);

        // Creazione del FakePlayer
        //ClientInformation clientInfo = ClientInformation.createDefault();  // Solo per Minecraft 1.20.1+
        FakePlayer fakePlayer = new FakePlayer(level, profile) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        };

        // Impostiamo la gamemode
        fakePlayer.setGameMode(GameType.SURVIVAL);

        // Assicuriamoci che il player sia visibile agli altri giocatori
        broadcastFakePlayerInfoToClients(fakePlayer);

        return fakePlayer;
    }

    /**
     * Invia le informazioni del FakePlayer ai client connessi
     * @param fakePlayer Il FakePlayer da inviare
     */
    private static void broadcastFakePlayerInfoToClients(FakePlayer fakePlayer) {
        ServerPlayer serverPlayer = (ServerPlayer) fakePlayer;
        var players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();

        // Aggiorniamo l'info del player in tutti i client
        ClientboundPlayerInfoUpdatePacket addPlayerPacket = new ClientboundPlayerInfoUpdatePacket(
                ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, serverPlayer);

        for (ServerPlayer player : players) {
            if (player != fakePlayer) {
                player.connection.send(addPlayerPacket);
            }
        }
    }
}
