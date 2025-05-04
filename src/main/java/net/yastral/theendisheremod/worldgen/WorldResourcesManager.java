package net.yastral.theendisheremod.worldgen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Manager che estrae le risorse dalla cartella "void.zip" all'interno del JAR
 * alla directory delle risorse della mod
 */
@OnlyIn(Dist.CLIENT)
public class WorldResourcesManager {
    // Percorso del file zip all'interno delle risorse
    private static final String ZIP_SOURCE_PATH = "worlds/void.zip";
    // Percorso dove estrarre i file
    private static final String TARGET_PATH = "resources/assets/theendisheremod/worlds";
    private static WorldResourcesManager instance;

    // Identificatore del mondo corrente
    private String currentWorldId = "";

    /**
     * Costruttore privato per il pattern singleton
     */
    private WorldResourcesManager() {
        ensureDirectoryExists();
    }

    /**
     * Ottiene l'istanza singleton
     */
    public static WorldResourcesManager getInstance() {
        if (instance == null) {
            instance = new WorldResourcesManager();
        }
        return instance;
    }

    /**
     * Assicura che la directory di destinazione esista
     */
    private void ensureDirectoryExists() {
        try {
            Path directoryPath = Paths.get(TARGET_PATH);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                System.out.println("[TheEndIsHere] Directory per le risorse dei mondi creata: " + directoryPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("[TheEndIsHere] Impossibile creare le directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ottiene l'ID del mondo corrente
     */
    public String getCurrentWorldId() {
        if (currentWorldId.isEmpty()) {
            updateCurrentWorldId();
        }

        // Se è ancora "unknown", prova a usare un nome predefinito per scopi di test
        if (currentWorldId.equals("unknown")) {
            return "test_world";
        }

        return currentWorldId;
    }

    /**
     * Aggiorna l'identificatore del mondo corrente basato sul mondo del giocatore
     */
    public void updateCurrentWorldId() {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        String worldName = "unknown";

        if (server != null) {
            worldName = server.getWorldData().getLevelName();
            // Aggiungi un po' di unicità con il seed se disponibile
            try {
                long seed = server.getWorldData().worldGenOptions().seed();
                worldName += "_" + seed;
            } catch (Exception e) {
                // Fallback se non riusciamo a ottenere il seed
                System.err.println("[TheEndIsHere] Impossibile ottenere il seed del mondo: " + e.getMessage());
            }
        } else if (Minecraft.getInstance().level != null) {
            // Prova a ottenere un identificatore per i mondi multiplayer
            worldName = Minecraft.getInstance().level.dimension().location().toString();
            if (Minecraft.getInstance().getCurrentServer() != null) {
                worldName += "_" + Minecraft.getInstance().getCurrentServer().ip;
            }
        }

        this.currentWorldId = worldName;
        System.out.println("[TheEndIsHere] ID del mondo corrente: " + currentWorldId);
    }

    /**
     * Estrae il contenuto di void.zip nella directory "void" nella destinazione
     */
    public void extractVoidWorldResources() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            // Crea la directory "void" nella destinazione se non esiste
            Path voidTargetPath = Paths.get(TARGET_PATH, "void");
            if (!Files.exists(voidTargetPath)) {
                Files.createDirectories(voidTargetPath);
                System.out.println("[TheEndIsHere] Creata directory: " + voidTargetPath.toAbsolutePath());
            }

            // Ottieni il file zip dalle risorse
            InputStream zipFileStream = classLoader.getResourceAsStream(ZIP_SOURCE_PATH);
            if (zipFileStream == null) {
                System.err.println("[TheEndIsHere] File zip non trovato: " + ZIP_SOURCE_PATH);
                // Tenta con un approccio alternativo
                zipFileStream = new FileInputStream(new File("build/resources/main/" + ZIP_SOURCE_PATH));
            }

            // Estrai lo zip
            extractZipFile(zipFileStream, voidTargetPath.toString());

            System.out.println("[TheEndIsHere] Estrazione delle risorse completata");
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Errore durante l'estrazione dello zip: " + e.getMessage());
            e.printStackTrace();

            // Fallback con approccio alternativo
            try {
                File zipFile = new File("build/resources/main/" + ZIP_SOURCE_PATH);
                if (zipFile.exists()) {
                    Path voidTargetPath = Paths.get(TARGET_PATH, "void");
                    extractZipFile(new FileInputStream(zipFile), voidTargetPath.toString());
                    System.out.println("[TheEndIsHere] Estrazione dal fallback completata");
                } else {
                    System.err.println("[TheEndIsHere] File zip non trovato nel fallback: " + zipFile.getAbsolutePath());
                }
            } catch (Exception ex) {
                System.err.println("[TheEndIsHere] Anche il fallback è fallito: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Metodo per estrarre un file zip
     */
    private void extractZipFile(InputStream zipStream, String destinationPath) throws IOException {
        byte[] buffer = new byte[1024];

        try (ZipInputStream zis = new ZipInputStream(zipStream)) {
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                File newFile = new File(destinationPath, zipEntry.getName());

                // Verifica se l'entry è una directory
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory: " + newFile);
                    }
                } else {
                    // Crea le directory parent se necessario
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory: " + parent);
                    }

                    // Estrai il file
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    System.out.println("[TheEndIsHere] Estratto file: " + zipEntry.getName());
                }
                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
        }
    }

    /**
     * Metodo principale per inizializzare ed estrarre i file
     */
    public void initialize() {
        ensureDirectoryExists();
        extractVoidWorldResources();
        System.out.println("[TheEndIsHere] WorldResourcesManager inizializzato");
    }

    /**
     * Controlla se esiste già un mondo con l'ID corrente
     */
    public boolean worldResourcesExist() {
        Path worldPath = Paths.get(TARGET_PATH, getCurrentWorldId());
        return Files.exists(worldPath);
    }

    /**
     * Crea un nuovo mondo con l'ID corrente utilizzando le risorse estratte
     */
    public void createWorldResources() {
        String worldId = getCurrentWorldId();
        Path worldPath = Paths.get(TARGET_PATH, worldId);

        try {
            if (!Files.exists(worldPath)) {
                Files.createDirectories(worldPath);
                System.out.println("[TheEndIsHere] Creata directory per il mondo: " + worldId);
            }

            // Copia tutte le risorse dalla directory void alla directory del mondo
            Path voidPath = Paths.get(TARGET_PATH, "void");
            if (Files.exists(voidPath)) {
                System.out.println("[TheEndIsHere] Copio i file da: " + voidPath + " a: " + worldPath);

                // Copia ogni sottodirectory presente in void
                copyDirectory(voidPath, worldPath);
                System.out.println("[TheEndIsHere] Risorse create per il mondo: " + worldId);
            } else {
                System.err.println("[TheEndIsHere] Directory void non trovata in " + TARGET_PATH);
                System.err.println("[TheEndIsHere] Assicurati di aver chiamato initialize() prima di createWorldResources()");
            }
        } catch (IOException e) {
            System.err.println("[TheEndIsHere] Errore durante la creazione delle risorse del mondo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Copia ricorsivamente una directory
     */
    private void copyDirectory(Path source, Path target) throws IOException {
        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(source)) {
            for (Path entry : stream) {
                Path targetEntry = target.resolve(source.relativize(entry).toString());

                if (Files.isDirectory(entry)) {
                    copyDirectory(entry, targetEntry);
                } else {
                    Files.copy(entry, targetEntry, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}