package lordpipe.cavescliffsclassifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin to notify the user when they enter chunks generated in a specific version
 *
 * @author Copyright (c) lordpipe. Licensed GPLv3
 */
public class CavesCliffsClassifier extends JavaPlugin {
    public HashMap<Long, byte[]> data = new HashMap<Long, byte[]>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        try {
            readData();
        } catch(Exception err) {
            throw new RuntimeException(err);
        }

        getServer().getPluginManager().registerEvents(new MovementListener(this), this);
        getCommand("chunkversion").setExecutor(new ChunkVersionCommand(this));
    }

    public void readData() throws IOException {
        Stream<Path> paths = Files.walk(Paths.get(getDataFolder().getAbsolutePath() + "/data"));

        paths.filter(Files::isRegularFile).forEach(path -> {
            String filename = path.getFileName().toString();
            String[] components = filename.split("\\.");

            if (components.length != 0 || components[0].equals("ccc")) {
                int x = Integer.parseInt(components[1]);
                int z = Integer.parseInt(components[2]);

                try {
                    data.put(BitManipulation.compact(x, z), Files.readAllBytes(path));
                } catch (Exception err) {
                    throw new RuntimeException(err);
                }
            }
        });

        paths.close();
    }
}
