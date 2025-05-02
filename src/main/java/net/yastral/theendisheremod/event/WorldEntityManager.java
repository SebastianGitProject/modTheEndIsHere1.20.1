package net.yastral.theendisheremod.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class WorldEntityManager {
    private static final String JSON_FILE_PATH = "resources/assets/theendisheremod/worldentities/entities.json";
    private static WorldEntityManager instance;
    private final Map<String, WorldData> worldDataMap = new HashMap<>();
    private final Gson gson;

    // Current world identifier
    private String currentWorldId = "";

    /**
     * Data structure to hold information about a world
     */
    public static class WorldData {
        private final String worldId;
        private final List<String> fakePlayers = new ArrayList<>();
        private boolean serverActive = false;

        public WorldData(String worldId) {
            this.worldId = worldId;
        }

        public String getWorldId() {
            return worldId;
        }

        public List<String> getFakePlayers() {
            return fakePlayers;
        }

        public boolean isServerActive() {
            return serverActive;
        }

        public void setServerActive(boolean active) {
            this.serverActive = active;
        }

        public void addFakePlayer(String playerName) {
            if (!fakePlayers.contains(playerName)) {
                fakePlayers.add(playerName);
            }
        }
    }

    /**
     * Private constructor for singleton pattern
     */
    private WorldEntityManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        ensureDirectoryExists();
        loadData();
    }

    /**
     * Gets the singleton instance
     */
    public static WorldEntityManager getInstance() {
        if (instance == null) {
            instance = new WorldEntityManager();
        }
        return instance;
    }

    /**
     * Make sure the directory for the JSON file exists
     */
    private void ensureDirectoryExists() {
        try {
            Path directoryPath = Paths.get("resources/assets/theendisheremod/worldentities");
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                System.out.println("[TheEndIsHere] Created directories for world entities data");
            }
        } catch (IOException e) {
            System.err.println("[TheEndIsHere] Failed to create directories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads world data from the JSON file
     */
    private void loadData() {
        File jsonFile = new File(JSON_FILE_PATH);
        if (!jsonFile.exists()) {
            System.out.println("[TheEndIsHere] No entities.json file found, will create when needed");
            return;
        }

        try (Reader reader = new FileReader(jsonFile)) {
            Type typeOfMap = new TypeToken<Map<String, WorldData>>() {}.getType();
            Map<String, WorldData> loadedData = gson.fromJson(reader, typeOfMap);
            if (loadedData != null) {
                worldDataMap.clear();
                worldDataMap.putAll(loadedData);
                System.out.println("[TheEndIsHere] Loaded world data for " + worldDataMap.size() + " worlds");
            }
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Failed to load world entities data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Saves world data to the JSON file
     */
    public void saveData() {
        try (Writer writer = new FileWriter(JSON_FILE_PATH)) {
            gson.toJson(worldDataMap, writer);
            System.out.println("[TheEndIsHere] Saved world entities data");
        } catch (IOException e) {
            System.err.println("[TheEndIsHere] Failed to save world entities data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the current world identifier
     */
    public String getCurrentWorldId() {
        if (currentWorldId.isEmpty()) {
            updateCurrentWorldId();
        }
        return currentWorldId;
    }

    /**
     * Updates the current world identifier based on the player's world
     */
    public void updateCurrentWorldId() {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        String worldName = "unknown";

        if (server != null) {
            worldName = server.getWorldData().getLevelName();
            // Add some uniqueness with the seed if available
            try {
                long seed = server.getWorldData().worldGenOptions().seed();
                worldName += "_" + seed;
            } catch (Exception e) {
                // Fallback if we can't get the seed
                System.err.println("[TheEndIsHere] Couldn't get world seed: " + e.getMessage());
            }
        } else if (Minecraft.getInstance().level != null) {
            // Try to get some identifier for multiplayer worlds
            worldName = Minecraft.getInstance().level.dimension().location().toString();
            if (Minecraft.getInstance().getCurrentServer() != null) {
                worldName += "_" + Minecraft.getInstance().getCurrentServer().ip;
            }
        }

        this.currentWorldId = worldName;
        System.out.println("[TheEndIsHere] Current world ID: " + currentWorldId);
    }

    /**
     * Gets or creates world data for the current world
     */
    public WorldData getOrCreateCurrentWorldData() {
        return getOrCreateWorldData(getCurrentWorldId());
    }

    /**
     * Gets or creates world data for a specific world ID
     */
    public WorldData getOrCreateWorldData(String worldId) {
        if (!worldDataMap.containsKey(worldId)) {
            WorldData worldData = new WorldData(worldId);
            worldDataMap.put(worldId, worldData);
            saveData(); // Save immediately when creating a new world entry
        }
        return worldDataMap.get(worldId);
    }

    /**
     * Marks a world as having an active server and adds a fake player
     */
    public void activateServerForCurrentWorld(String fakePlayerName) {
        WorldData worldData = getOrCreateCurrentWorldData();
        worldData.setServerActive(true);
        worldData.addFakePlayer(fakePlayerName);
        saveData();
    }

    /**
     * Checks if the server should be active for the current world
     */
    public boolean shouldServerBeActiveForCurrentWorld() {
        WorldData worldData = getOrCreateCurrentWorldData();
        return worldData.isServerActive();
    }

    /**
     * Gets the fake players for the current world
     */
    public List<String> getFakePlayersForCurrentWorld() {
        WorldData worldData = getOrCreateCurrentWorldData();
        return new ArrayList<>(worldData.getFakePlayers());
    }
}
