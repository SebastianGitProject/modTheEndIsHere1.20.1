package net.yastral.theendisheremod.worldgen;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.yastral.theendisheremod.TheEndIsHereMod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = TheEndIsHereMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoidWorldRegion {
    // Resource key per la dimensione void
    public static final ResourceKey<Level> VOID_DIMENSION = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            new ResourceLocation(TheEndIsHereMod.MOD_ID, "void")
    );

    // Percorso alla cartella delle risorse del mondo void
    private static final String SOURCE_REGION_PATH = "resources/assets/theendisheremod/worlds/void/region";

    // Set per tenere traccia delle dimensioni già copiate
    private static final Set<String> initializedDimensions = new HashSet<>();

    // Singleton instance
    private static VoidWorldRegion instance;

    /**
     * Ottieni l'istanza singleton
     */
    public static VoidWorldRegion getInstance() {
        if (instance == null) {
            instance = new VoidWorldRegion();
        }
        return instance;
    }

    /**
     * Costruttore - registra gli eventi
     */
    private VoidWorldRegion() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Inizializza il VoidWorldTeleporter
     */
    public static void init() {
        getInstance();
        System.out.println("[TheEndIsHere] VoidWorldTeleporter initialized");
    }

    /**
     * Evento che si attiva quando un giocatore cambia dimensione
     */
    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        // Verifica se il giocatore è entrato nella dimensione void
        if (event.getTo().equals(VOID_DIMENSION)) {
            // Prova a copiare i file della regione, ma solo la prima volta
            String dimensionId = event.getTo().location().toString();
            if (!initializedDimensions.contains(dimensionId)) {
                initializedDimensions.add(dimensionId);
                copyRegionFiles();
            }
        }
    }

    /**
     * Evento che si attiva quando un giocatore entra nel mondo
     * (backup nel caso in cui l'evento di cambio dimensione non si attivi)
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            if (player.level().dimension().equals(VOID_DIMENSION)) {
                String dimensionId = player.level().dimension().location().toString();
                if (!initializedDimensions.contains(dimensionId)) {
                    initializedDimensions.add(dimensionId);
                    copyRegionFiles();
                }
            }
        }
    }

    /**
     * Copia i file della regione dal mondo void alla nuova dimensione flat
     */
    private static void copyRegionFiles() {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                System.err.println("[TheEndIsHere] Server non disponibile, impossibile copiare i file della regione");
                return;
            }

            // Ottieni il path alla cartella del mondo
            Path worldPath = server.getWorldPath(LevelResource.ROOT);

            // Path alla cartella region di destinazione
            Path targetRegionPath = worldPath.resolve("dimensions/theendisheremod/void/region");

            // Path alla cartella region di origine
            Path sourceRegionPath = Paths.get(SOURCE_REGION_PATH);

            // Controlla se la cartella di origine esiste
            if (!Files.exists(sourceRegionPath)) {
                System.err.println("[TheEndIsHere] Cartella region di origine non trovata: " + sourceRegionPath);

                // Tenta di utilizzare il WorldResourcesManager per localizzare le risorse
                tryLocateResourcesWithManager(targetRegionPath);
                return;
            }

            // Crea la cartella di destinazione se non esiste
            if (!Files.exists(targetRegionPath)) {
                Files.createDirectories(targetRegionPath);
                System.out.println("[TheEndIsHere] Creata cartella di destinazione: " + targetRegionPath);
            }

            // Copia tutti i file dalla cartella region di origine a quella di destinazione
            System.out.println("[TheEndIsHere] Copio i file da: " + sourceRegionPath + " a: " + targetRegionPath);
            copyDirectory(sourceRegionPath, targetRegionPath);

            System.out.println("[TheEndIsHere] Copia dei file della regione completata con successo");

        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Errore durante la copia dei file della regione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tenta di localizzare le risorse utilizzando il WorldResourcesManager
     */
    private static void tryLocateResourcesWithManager(Path targetRegionPath) {
        try {
            System.out.println("[TheEndIsHere] Tentativo di localizzare le risorse con WorldResourcesManager");

            // Usa il WorldResourcesManager per estrarre le risorse
            WorldResourcesManager resourcesManager = WorldResourcesManager.getInstance();
            resourcesManager.initialize();

            // Prova a trovare la cartella region dopo l'estrazione
            Path extractedRegionPath = Paths.get("resources/assets/theendisheremod/worlds/void/region");

            if (Files.exists(extractedRegionPath)) {
                System.out.println("[TheEndIsHere] Trovata cartella region dopo l'estrazione: " + extractedRegionPath);

                // Crea la cartella di destinazione se non esiste
                if (!Files.exists(targetRegionPath)) {
                    Files.createDirectories(targetRegionPath);
                }

                // Copia i file
                copyDirectory(extractedRegionPath, targetRegionPath);
                System.out.println("[TheEndIsHere] Copia dei file della regione completata con successo");
            } else {
                System.err.println("[TheEndIsHere] Impossibile trovare la cartella region anche dopo l'estrazione");
            }

        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Errore durante il tentativo di localizzare le risorse: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Copia una directory ricorsivamente
     */
    private static void copyDirectory(Path source, Path target) throws IOException {
        // Crea la directory di destinazione se non esiste
        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }

        // Scorri tutti i file e le sottodirectory
        Files.walk(source).forEach(sourcePath -> {
            try {
                // Ottieni il path relativo
                Path relativePath = source.relativize(sourcePath);
                Path targetPath = target.resolve(relativePath);

                // Salta se è lo stesso path
                if (sourcePath.equals(targetPath)) {
                    return;
                }

                // Crea directory o copia file
                if (Files.isDirectory(sourcePath)) {
                    if (!Files.exists(targetPath)) {
                        Files.createDirectories(targetPath);
                    }
                } else {
                    // Assicurati che le directory parent esistano
                    Files.createDirectories(targetPath.getParent());

                    // Copia il file
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("[TheEndIsHere] Copiato: " + sourcePath + " -> " + targetPath);
                }
            } catch (IOException e) {
                System.err.println("[TheEndIsHere] Errore durante la copia di: " + sourcePath + ": " + e.getMessage());
            }
        });
    }
}