package pluginsfix.glowautorestart.platform;

import org.bukkit.plugin.Plugin;

public final class PlatformProvider {

    private PlatformProvider() {
    }

    public static PlatformScheduler createScheduler(Plugin plugin) {
        if (isFolia()) {
            return new FoliaPlatformScheduler(plugin);
        }
        return new PaperPlatformScheduler(plugin);
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
