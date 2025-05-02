package net.yastral.theendisheremod.event;


import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yastral.theendisheremod.TheEndIsHereMod;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = TheEndIsHereMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FakeServerSimulator {
    private static FakeServerSimulator instance;
    private boolean isServerActive = false;
    private final List<String> fakePlayers = new ArrayList<>();
    private final Map<UUID, PlayerInfo> injectedPlayers = new HashMap<>();

    // Singleton pattern
    public static FakeServerSimulator getInstance() {
        if (instance == null) {
            instance = new FakeServerSimulator();
        }
        return instance;
    }

    private FakeServerSimulator() {
        // Initialize fake player list
        fakePlayers.add("Notch");
        fakePlayers.add("jeb_");
        fakePlayers.add("Dinnerbone");
        fakePlayers.add("Grumm");
        fakePlayers.add("MojangTeam");
        fakePlayers.add("xXDragonSlayerXx");
        fakePlayers.add("DiamondMiner2009");
        fakePlayers.add("CreeperKiller");
        fakePlayers.add("SkywalkerSteve");
        fakePlayers.add("EnderQueen");

        // Register events on the Forge EventBus
        MinecraftForge.EVENT_BUS.register(this);

        // Log initialization
        System.out.println("[TheEndIsHere] FakeServerSimulator initialized");
    }

    /**
     * Starts the fake server simulation
     */
    public void startServer() {
        if (!isServerActive) {
            isServerActive = true;

            // Clear any existing fake players
            injectedPlayers.clear();

            try {
                // Get the ClientPacketListener from Minecraft
                ClientPacketListener packetListener = Minecraft.getInstance().getConnection();
                if (packetListener == null) {
                    sendSystemMessage(Component.literal("Cannot start server simulation: not connected to any server")
                            .withStyle(ChatFormatting.RED));
                    return;
                }

                // Add fake players to the tablist
                for (String playerName : fakePlayers) {
                    addFakePlayerToTabList(playerName);
                }

                // Handle the player's own name (fix Dev to Yastral)
                LocalPlayer currentPlayer = Minecraft.getInstance().player;
                if (currentPlayer != null) {
                    String playerName = currentPlayer.getName().getString();
                    if (playerName.equals("Dev")) {
                        // If we find "Dev" in the players map, we'll replace it later in the tick event
                        System.out.println("[TheEndIsHere] Found player with name 'Dev', will replace with 'Yastral'");
                    }
                }

                // Send activation message
                sendSystemMessage(Component.literal("Server simulation started! Players online: " +
                        (fakePlayers.size() + 1)).withStyle(ChatFormatting.GREEN));
            } catch (Exception e) {
                System.err.println("[TheEndIsHere] Failed to start fake server: " + e.getMessage());
                e.printStackTrace();
                sendSystemMessage(Component.literal("Failed to start server simulation: " + e.getMessage())
                        .withStyle(ChatFormatting.RED));
            }
        }
    }

    /**
     * Stops the fake server simulation
     */
    public void stopServer() {
        if (isServerActive) {
            isServerActive = false;

            try {
                // Clear injected players
                injectedPlayers.clear();

                // Try to restore the original players list
                resetTabList();

                // Send deactivation message
                sendSystemMessage(Component.literal("Server simulation stopped!").withStyle(ChatFormatting.RED));
            } catch (Exception e) {
                System.err.println("[TheEndIsHere] Failed to stop fake server: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a fake player to the tab list
     */
    private void addFakePlayerToTabList(String username) {
        try {
            UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
            GameProfile profile = new GameProfile(uuid, username);

            // Create player info packet
            ClientboundPlayerInfoUpdatePacket.Entry entry = createPlayerInfoEntry(profile);
            if (entry != null) {
                // Add to our tracking map
                PlayerInfo playerInfo = createPlayerInfoFromEntry(entry);
                if (playerInfo != null) {
                    injectedPlayers.put(uuid, playerInfo);
                    System.out.println("[TheEndIsHere] Added fake player: " + username);
                }
            }
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Error adding fake player " + username + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a ClientboundPlayerInfoUpdatePacket.Entry for a player
     */
    private ClientboundPlayerInfoUpdatePacket.Entry createPlayerInfoEntry(GameProfile profile) {
        try {
            // Firma corretta del costruttore per 1.20.1
            return new ClientboundPlayerInfoUpdatePacket.Entry(
                    profile.getId(),      // UUID
                    profile,              // GameProfile
                    false,                // requiresHosting
                    0,                    // Latency
                    GameType.SURVIVAL,    // Game mode
                    Component.literal(profile.getName()),  // Display name
                    null                  // RemoteChatSession.Data
            );
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Failed to create player info entry: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a PlayerInfo object from an Entry
     */
    private PlayerInfo createPlayerInfoFromEntry(ClientboundPlayerInfoUpdatePacket.Entry entry) {
        try {
            // Try to create a new PlayerInfo
            // Since the constructor might be protected or have changed, we'll try different approaches

            // Method 1: Reflection to create PlayerInfo
            Class<?> playerInfoClass = PlayerInfo.class;
            try {
                return (PlayerInfo) playerInfoClass.getConstructors()[0].newInstance(entry);
            } catch (Exception e1) {
                // Method 2: Find the right constructor
                for (var constructor : playerInfoClass.getDeclaredConstructors()) {
                    constructor.setAccessible(true);
                    if (constructor.getParameterCount() == 1) {
                        try {
                            return (PlayerInfo) constructor.newInstance(entry);
                        } catch (Exception ignored) { }
                    }
                }

                // Method 3: Last resort, create a custom implementation
                return new CustomPlayerInfo(entry.profileId(), entry.profile().getName());
            }
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Failed to create PlayerInfo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Injects our fake players into the tab list
     */
    private void injectFakePlayersIntoTabList() {
        try {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection == null) return;

            // Get the players map using reflection
            Map<UUID, PlayerInfo> playerInfoMap = getPlayerInfoMap(connection);
            if (playerInfoMap == null) return;

            // Handle Dev -> Yastral replacement
            handleDevYastralReplacement(playerInfoMap);

            // Add our fake players
            for (Map.Entry<UUID, PlayerInfo> entry : injectedPlayers.entrySet()) {
                playerInfoMap.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Error injecting fake players: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the player info map using reflection
     */
    @SuppressWarnings("unchecked")
    private Map<UUID, PlayerInfo> getPlayerInfoMap(ClientPacketListener connection) {
        try {
            // Try different field names that might contain the player info map
            String[] possibleFieldNames = {"playerInfoMap", "f_105208_", "players", "field_147310_i"};

            for (String fieldName : possibleFieldNames) {
                try {
                    Field field = ClientPacketListener.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object map = field.get(connection);
                    if (map instanceof Map) {
                        return (Map<UUID, PlayerInfo>) map;
                    }
                } catch (NoSuchFieldException ignored) { }
            }

            // If named fields fail, try to find a field of type Map
            for (Field field : ClientPacketListener.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(connection);
                if (value instanceof Map) {
                    // Check if it looks like the player map
                    Map<?, ?> map = (Map<?, ?>) value;
                    if (!map.isEmpty()) {
                        Object key = map.keySet().iterator().next();
                        Object val = map.values().iterator().next();
                        if (key instanceof UUID && val instanceof PlayerInfo) {
                            return (Map<UUID, PlayerInfo>) map;
                        }
                    }
                }
            }

            System.err.println("[TheEndIsHere] Could not find player info map");
            return null;
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Error getting player info map: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Handles Dev -> Yastral replacement in the player info map
     */
    private void handleDevYastralReplacement(Map<UUID, PlayerInfo> playerInfoMap) {
        try {
            LocalPlayer currentPlayer = Minecraft.getInstance().player;
            if (currentPlayer == null) return;

            UUID playerUUID = currentPlayer.getUUID();
            PlayerInfo playerInfo = playerInfoMap.get(playerUUID);

            if (playerInfo != null && playerInfo.getProfile().getName().equals("Dev")) {
                // Remove the original entry
                playerInfoMap.remove(playerUUID);

                // Create a new entry with the corrected name
                GameProfile correctedProfile = new GameProfile(playerUUID, "Yastral");
                ClientboundPlayerInfoUpdatePacket.Entry entry = createPlayerInfoEntry(correctedProfile);
                if (entry != null) {
                    PlayerInfo correctedInfo = createPlayerInfoFromEntry(entry);
                    if (correctedInfo != null) {
                        playerInfoMap.put(playerUUID, correctedInfo);
                        System.out.println("[TheEndIsHere] Replaced 'Dev' with 'Yastral' in tab list");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Error replacing Dev with Yastral: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Resets the tab list to remove fake players
     */
    private void resetTabList() {
        try {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection == null) return;

            Map<UUID, PlayerInfo> playerInfoMap = getPlayerInfoMap(connection);
            if (playerInfoMap == null) return;

            // Remove our fake players
            for (UUID uuid : injectedPlayers.keySet()) {
                playerInfoMap.remove(uuid);
            }

            System.out.println("[TheEndIsHere] Removed fake players from tab list");
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Error resetting tab list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Intercepts chat messages to check for server commands
     * This event is fired when a player sends a chat message
     */
    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        String message = event.getMessage();
        if (message.contains("server-on")) {
            startServer();
            // Prevent the message from being sent to the server
            event.setCanceled(true);
        } else if (message.contains("server-off")) {
            stopServer();
            // Prevent the message from being sent to the server
            event.setCanceled(true);
        }
    }

    /**
     * Modifies the tablist on client ticks when the server is active
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && isServerActive) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.getConnection() != null) {
                injectFakePlayersIntoTabList();
            }
        }
    }

    /**
     * Sends a system message to the player
     */
    private void sendSystemMessage(Component message) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendSystemMessage(message);
        }
    }

    /**
     * Checks if the server simulation is active
     */
    public boolean isServerActive() {
        return isServerActive;
    }

    /**
     * Custom implementation of PlayerInfo for our fake players
     * Only used as a fallback if we can't create a real PlayerInfo
     */
    private static class CustomPlayerInfo extends PlayerInfo {
        private final UUID uuid;
        private final String name;

        public CustomPlayerInfo(UUID uuid, String name) {
            super(new GameProfile(uuid, name), false);
            this.uuid = uuid;
            this.name = name;
        }

        @Override
        public GameProfile getProfile() {
            return new GameProfile(uuid, name);
        }
    }
}