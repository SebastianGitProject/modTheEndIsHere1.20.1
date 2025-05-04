package net.yastral.theendisheremod.worldgen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.yastral.theendisheremod.TheEndIsHereMod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

@Mod.EventBusSubscriber(modid = TheEndIsHereMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoidWorldTeleporter {
    // Resource key for the void dimension
    public static final ResourceKey<Level> VOID_DIMENSION = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            new ResourceLocation(TheEndIsHereMod.MOD_ID, "void")
    );

    // Specific coordinates to teleport to in the void world
    private static final int VOID_X = 129;
    private static final int VOID_Y = 4;
    private static final int VOID_Z = 129;

    // Store player's previous position before teleporting to void world
    private static final Map<UUID, TeleportData> playerPreviousLocations = new HashMap<>();

    // Teleport request flags
    private static boolean pendingVoidTeleport = false;
    private static boolean pendingReturnTeleport = false;
    private static int teleportDelay = 0;

    // Path to the void world in mod resources
    private static final String VOID_WORLD_RESOURCE_PATH = "worlds/void";

    // Singleton instance
    private static VoidWorldTeleporter instance;

    /**
     * Store teleport data for a player
     */
    private static class TeleportData {
        ResourceKey<Level> dimension;
        double x, y, z;
        float yRot, xRot;

        public TeleportData(ResourceKey<Level> dimension, double x, double y, double z, float yRot, float xRot) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yRot = yRot;
            this.xRot = xRot;
        }
    }

    /**
     * Get the singleton instance
     */
    public static VoidWorldTeleporter getInstance() {
        if (instance == null) {
            instance = new VoidWorldTeleporter();
        }
        return instance;
    }

    /**
     * Constructor - register events
     */
    private VoidWorldTeleporter() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Initialize the void world teleporter
     */
    public static void init() {
        getInstance();
        System.out.println("[TheEndIsHere] VoidWorldTeleporter initialized");

        // Ensure void world resources are prepared during initialization
        prepareVoidWorld();
    }

    /**
     * Prepare the void world by extracting it from mod resources if needed
     */
    private static void prepareVoidWorld() {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                // Server not available yet, will retry later
                System.out.println("[TheEndIsHere] Server not available yet, will prepare void world later");
                return;
            }

            // Check if void world directory exists in save folder
            Path savesDir = server.getWorldPath(LevelResource.ROOT);
            Path voidWorldDir = savesDir.resolve("void");

            if (!Files.exists(voidWorldDir)) {
                System.out.println("[TheEndIsHere] Void world not found in saves, extracting from mod resources");

                // Create directory structure
                Files.createDirectories(voidWorldDir);

                // Extract void world from mod resources
                extractVoidWorldFromModResources(voidWorldDir);

                // Log success
                System.out.println("[TheEndIsHere] Void world prepared at: " + voidWorldDir);
            } else {
                System.out.println("[TheEndIsHere] Void world already exists at: " + voidWorldDir);
            }
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Error preparing void world: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extract void world files from mod JAR to the target directory
     */
    private static void extractVoidWorldFromModResources(Path targetDir) throws IOException {
        // Get the location of our mod's JAR file
        String modJarPath = TheEndIsHereMod.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (modJarPath.startsWith("/")) {
            modJarPath = modJarPath.substring(1); // Fix path on Windows
        }

        System.out.println("[TheEndIsHere] Extracting void world from mod JAR at: " + modJarPath);

        // Open the JAR file
        try (JarFile jarFile = new JarFile(new File(modJarPath))) {
            // Look for all entries that are part of the void world
            Enumeration<JarEntry> entries = jarFile.entries();
            int filesExtracted = 0;

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                // Check if this entry is part of the void world
                if (entryName.startsWith(VOID_WORLD_RESOURCE_PATH + "/") && !entry.isDirectory()) {
                    // Get the relative path within the void world
                    String relativePath = entryName.substring(VOID_WORLD_RESOURCE_PATH.length() + 1);
                    Path targetFile = targetDir.resolve(relativePath);

                    // Create parent directories if they don't exist
                    Files.createDirectories(targetFile.getParent());

                    // Extract the file
                    try (InputStream is = jarFile.getInputStream(entry);
                         FileOutputStream fos = new FileOutputStream(targetFile.toFile())) {

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }

                        filesExtracted++;
                        System.out.println("[TheEndIsHere] Extracted: " + relativePath);
                    }
                }
            }

            System.out.println("[TheEndIsHere] Void world extraction complete. Files extracted: " + filesExtracted);
        }
    }

    /**
     * Handle client chat event to check for maze commands
     */
    @SubscribeEvent
    public static void onClientChat(ClientChatEvent event) {
        String message = event.getMessage().trim();

        if (message.equalsIgnoreCase("maze")) {
            // Cancel the message from being sent normally
            event.setCanceled(true);

            // Set pending teleport flag
            pendingVoidTeleport = true;
            pendingReturnTeleport = false;
            teleportDelay = 10; // Wait 10 ticks before attempting teleport

            // Send a confirmation message to the player
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendSystemMessage(Component.literal("[TheEndIsHere] Preparing void world...").withStyle(ChatFormatting.GREEN));
            }
        }
        else if (message.equalsIgnoreCase("maze-off")) {
            // Cancel the message from being sent normally
            event.setCanceled(true);

            // Set pending return teleport flag
            pendingReturnTeleport = true;
            pendingVoidTeleport = false;
            teleportDelay = 10; // Wait 10 ticks before attempting teleport

            // Send a confirmation message to the player
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendSystemMessage(Component.literal("[TheEndIsHere] Returning to previous location...").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    /**
     * Handle client tick events for delayed teleport
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (pendingVoidTeleport && teleportDelay > 0) {
                teleportDelay--;
            } else if (pendingVoidTeleport) {
                pendingVoidTeleport = false;
                teleportToVoidWorld();
            }

            if (pendingReturnTeleport && teleportDelay > 0) {
                teleportDelay--;
            } else if (pendingReturnTeleport) {
                pendingReturnTeleport = false;
                returnFromVoidWorld();
            }
        }
    }

    /**
     * Teleport player to the void world
     */
    private static void teleportToVoidWorld() {
        try {
            LocalPlayer clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer == null) return;

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                clientPlayer.sendSystemMessage(Component.literal("[TheEndIsHere] Server not available").withStyle(ChatFormatting.RED));
                return;
            }

            // Check if the void world is prepared, and if not, prepare it now
            Path savesDir = server.getWorldPath(LevelResource.ROOT);
            Path voidWorldDir = savesDir.resolve("void");
            if (!Files.exists(voidWorldDir)) {
                clientPlayer.sendSystemMessage(Component.literal("[TheEndIsHere] Setting up void world...").withStyle(ChatFormatting.YELLOW));
                prepareVoidWorld();
            }

            // Save current player location
            ResourceKey<Level> currentDimension = clientPlayer.level().dimension();
            double currentX = clientPlayer.getX();
            double currentY = clientPlayer.getY();
            double currentZ = clientPlayer.getZ();
            float currentYRot = clientPlayer.getYRot();
            float currentXRot = clientPlayer.getXRot();

            // Store previous location for later return
            playerPreviousLocations.put(clientPlayer.getUUID(),
                    new TeleportData(currentDimension, currentX, currentY, currentZ, currentYRot, currentXRot));

            // Get server player
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(clientPlayer.getUUID());
            if (serverPlayer == null) {
                clientPlayer.sendSystemMessage(Component.literal("[TheEndIsHere] Cannot find player on server").withStyle(ChatFormatting.RED));
                return;
            }

            // Get or load the void dimension
            ServerLevel voidLevel = server.getLevel(VOID_DIMENSION);
            if (voidLevel == null) {
                // If we can't find the void dimension through resource key, try by name
                for (ServerLevel level : server.getAllLevels()) {
                    if (level.dimension().location().getPath().equals("void")) {
                        voidLevel = level;
                        break;
                    }
                }

                if (voidLevel == null) {
                    clientPlayer.sendSystemMessage(Component.literal("[TheEndIsHere] Void world not found").withStyle(ChatFormatting.RED));
                    return;
                }
            }

            // Teleport player to the void world at specific coordinates
            serverPlayer.teleportTo(voidLevel, VOID_X, VOID_Y, VOID_Z, serverPlayer.getYRot(), serverPlayer.getXRot());

            // Confirm teleport
            clientPlayer.sendSystemMessage(Component.literal("[TheEndIsHere] Teleported to void world").withStyle(ChatFormatting.GREEN));
            System.out.println("[TheEndIsHere] Player " + clientPlayer.getName().getString() + " teleported to void world at " +
                    VOID_X + ", " + VOID_Y + ", " + VOID_Z);

        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Error teleporting to void world: " + e.getMessage());
            e.printStackTrace();

            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendSystemMessage(Component.literal("[TheEndIsHere] Error teleporting: " + e.getMessage()).withStyle(ChatFormatting.RED));
            }
        }
    }

    /**
     * Return player from the void world to their previous location
     */
    private static void returnFromVoidWorld() {
        try {
            LocalPlayer clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer == null) return;

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                clientPlayer.sendSystemMessage(Component.literal("[TheEndIsHere] Server not available").withStyle(ChatFormatting.RED));
                return;
            }

            // Get server player
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(clientPlayer.getUUID());
            if (serverPlayer == null) {
                clientPlayer.sendSystemMessage(Component.literal("[TheEndIsHere] Cannot find player on server").withStyle(ChatFormatting.RED));
                return;
            }

            // Get the stored previous location
            TeleportData prevLocation = playerPreviousLocations.get(clientPlayer.getUUID());
            if (prevLocation == null) {
                clientPlayer.sendSystemMessage(Component.literal("[TheEndIsHere] No previous location found").withStyle(ChatFormatting.RED));
                return;
            }

            // Get the dimension to return to
            ServerLevel returnLevel = server.getLevel(prevLocation.dimension);
            if (returnLevel == null) {
                clientPlayer.sendSystemMessage(Component.literal("[TheEndIsHere] Previous dimension not found").withStyle(ChatFormatting.RED));
                return;
            }

            // Teleport back to previous location
            serverPlayer.teleportTo(returnLevel, prevLocation.x, prevLocation.y, prevLocation.z, prevLocation.yRot, prevLocation.xRot);

            // Remove stored location
            playerPreviousLocations.remove(clientPlayer.getUUID());

            // Confirm teleport
            clientPlayer.sendSystemMessage(Component.literal("[TheEndIsHere] Returned from void world").withStyle(ChatFormatting.GREEN));
            System.out.println("[TheEndIsHere] Player " + clientPlayer.getName().getString() +
                    " returned from void world to " + prevLocation.dimension.location() +
                    " at " + prevLocation.x + ", " + prevLocation.y + ", " + prevLocation.z);

        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Error returning from void world: " + e.getMessage());
            e.printStackTrace();

            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendSystemMessage(Component.literal("[TheEndIsHere] Error returning: " + e.getMessage()).withStyle(ChatFormatting.RED));
            }
        }
    }
}