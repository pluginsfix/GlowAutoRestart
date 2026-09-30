package pluginsfix.glowautorestart;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import pluginsfix.glowautorestart.command.AutoRestartCommand;
import pluginsfix.glowautorestart.config.PluginConfig;
import pluginsfix.glowautorestart.notification.NotificationBroadcaster;
import pluginsfix.glowautorestart.platform.PlatformProvider;
import pluginsfix.glowautorestart.platform.PlatformScheduler;
import pluginsfix.glowautorestart.service.BossBarManager;
import pluginsfix.glowautorestart.service.RestartService;
import pluginsfix.glowautorestart.text.Messages;

public final class GlowAutoRestart extends JavaPlugin {

    private PlatformScheduler scheduler;
    private RestartService restartService;

    @Override
    public void onEnable() {
        PluginConfig config = PluginConfig.load(this);
        Messages messages = new Messages(this);

        this.scheduler = PlatformProvider.createScheduler(this);

        BossBarManager bossBarManager = new BossBarManager(messages, config.bossBar());
        NotificationBroadcaster broadcaster = new NotificationBroadcaster(messages);

        this.restartService = new RestartService(
            scheduler,
            messages,
            config,
            broadcaster,
            bossBarManager
        );
        this.restartService.start();

        PluginCommand command = getCommand("glowautorestart");
        if (command != null) {
            AutoRestartCommand executor = new AutoRestartCommand(this, restartService, messages);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }

    @Override
    public void onDisable() {
        if (restartService != null) {
            restartService.stop();
        }
        if (scheduler != null) {
            scheduler.cancelAll();
        }
    }
}
