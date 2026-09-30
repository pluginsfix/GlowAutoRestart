package pluginsfix.glowautorestart.platform;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public final class PaperPlatformScheduler implements PlatformScheduler {

    private final Plugin plugin;
    private final List<BukkitTask> tasks = new CopyOnWriteArrayList<>();

    public PaperPlatformScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runGlobal(Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    @Override
    public void runAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Override
    public void runGlobalTimer(Runnable task, long delayTicks, long periodTicks) {
        BukkitTask scheduled = Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        tasks.add(scheduled);
    }

    @Override
    public void cancelAll() {
        for (BukkitTask task : tasks) {
            try {
                task.cancel();
            } catch (Exception ignored) {
            }
        }
        tasks.clear();
    }
}
