package lordpipe.cavescliffsclassifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Plugin to notify the user when they enter chunks generated in a specific version
 *
 * @author Copyright (c) lordpipe. Licensed GPLv3
 */
public class CavesCliffsClassifier extends JavaPlugin {
    public HashMap<Long, byte[]> data = new HashMap<Long, byte[]>();

    public Executor executor;

    Path dataFolder = Paths.get(getDataFolder().getAbsolutePath() + "/data/");

    public MiniMessage mm = MiniMessage.miniMessage();

    byte[] emptyRegion = new byte[8192];

    @Override
    public void onEnable() {
        saveDefaultConfig();

        executor = Executors.newSingleThreadExecutor();

        getServer().getPluginManager().registerEvents(new MovementListener(this), this);
        getCommand("chunkversion").setExecutor(new ChunkVersionCommand(this));
    }

    public byte[] smartGetData(int x, int z) {
        int regX = x >> 8;
        int regZ = z >> 8;

        byte[] targetRegion = emptyRegion;

        for (int xOff = -1; xOff <= 1; xOff++) {
            for (int zOff = -1; zOff <= 1; zOff++) {
                int curX = regX + xOff;
                int curZ = regZ + zOff;
                long id = BitManipulation.compact(curX, curZ);

                byte[] region = data.computeIfAbsent(id, k -> {
                    Path path = dataFolder.resolve("ccc." + curX + "." + curZ);
                    try {
                        return Files.readAllBytes(path);
                    } catch (Exception err) {
                        return emptyRegion;
                    }

                });

                if (xOff == 0 && zOff == 0) targetRegion = region;
            }
        }
        return targetRegion;
    }
}
