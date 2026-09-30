package pluginsfix.glowautorestart.platform;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.List;

public final class FoliaPlatformScheduler implements PlatformScheduler {

    private final Plugin plugin;
    private final List<ScheduledTask> tasks = new CopyOnWriteArrayList<>();

    public FoliaPlatformScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runGlobal(Runnable task) {
        Bukkit.getGlobalRegionScheduler().run(plugin, t -> task.run());
    }

    @Override
    public void runAsync(Runnable task) {
        Bukkit.getAsyncScheduler().runNow(plugin, t -> task.run());
    }

    @Override
    public void runGlobalTimer(Runnable task, long delayTicks, long periodTicks) {
        ScheduledTask scheduled = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
            plugin,
            t -> task.run(),
            Math.max(1, delayTicks),
            Math.max(1, periodTicks)
        );
        tasks.add(scheduled);
    }

    @Override
    public void cancelAll() {
        for (ScheduledTask task : tasks) {
            try {
                task.cancel();
            } catch (Exception ignored) {
            }
        }
        tasks.clear();
    }
}
