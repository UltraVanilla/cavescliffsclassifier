package lordpipe.cavescliffsclassifier;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MovementListener implements Listener {
    private CavesCliffsClassifier plugin;

    public MovementListener(CavesCliffsClassifier pl) {
        plugin = pl;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        onPlayerMove(event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        String fromWorld = from.getWorld().getName().intern();
        String toWorld = to.getWorld().getName().intern();
        plugin.executor.execute(() -> {
            if (toWorld != "world") return;

            int fromX = (int) (((long) from.getX()) >> 4);
            int fromZ = (int) (((long) from.getZ()) >> 4);
            int toX = (int) (((long) to.getX()) >> 4);
            int toZ = (int) (((long) to.getZ()) >> 4);

            if (fromWorld != toWorld || fromX != toX || fromZ != toZ) {
                byte[] fromData = plugin.smartGetData(fromX, fromZ);
                byte[] toData = plugin.smartGetData(toX, toZ);

                int fromRegionIndex = (fromX & 0xff) | ((fromZ & 0xff) << 8);
                int toRegionIndex = (toX & 0xff) | ((toZ & 0xff) << 8);

                int fromVersion = BitManipulation.getSubBits(fromData, fromRegionIndex);
                int toVersion = BitManipulation.getSubBits(toData, toRegionIndex);

                if (fromWorld != toWorld) {
                    fromVersion = -1;
                }

                if (fromVersion != toVersion) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        event.getPlayer().sendActionBar(plugin.mm.deserialize("You are entering <bold>" + plugin.getConfig().getStringList("version-names").get(toVersion) + "</bold> chunks"));
                    }, 0);
                }
            }
        });
    }
}
