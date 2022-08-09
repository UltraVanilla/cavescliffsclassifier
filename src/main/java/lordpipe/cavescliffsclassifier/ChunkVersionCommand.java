package lordpipe.cavescliffsclassifier;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChunkVersionCommand implements CommandExecutor {
    private CavesCliffsClassifier plugin;

    public ChunkVersionCommand(CavesCliffsClassifier pl) {
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            Location location = player.getLocation();

            String locationWorld = location.getWorld().getName().intern();

            if (locationWorld.equals(plugin.getConfig().getString("worlds.nether"))) {
                player.sendMessage("Multiplying coords by 8 to get overworld coords...");
                location.setX(location.getX() * 8.0d);
                location.setZ(location.getZ() * 8.0d);
            } else if (!locationWorld.equals(plugin.getConfig().getString("worlds.main"))) {
                player.sendMessage("You must be in the overworld or nether for this to work.");
                return true;
            }
            plugin.executor.execute(() -> {

                int locationX = (int) (((long) location.getX()) >> 4);
                int locationZ = (int) (((long) location.getZ()) >> 4);

                byte[] locationData = plugin.smartGetData(locationX, locationZ);

                int locationRegionIndex = (locationX & 0xff) | ((locationZ & 0xff) << 8);

                int locationVersion = BitManipulation.getSubBits(locationData, locationRegionIndex);

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    player.sendMessage(plugin.mm.deserialize("You are in <bold>" + plugin.getConfig().getStringList("version-names").get(locationVersion) + "</bold> chunks."));
                }, 0);
            });

            return true;
        } else {
            return false;
        }
    }
}
