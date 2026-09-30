package pluginsfix.glowautorestart.service;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pluginsfix.glowautorestart.config.PluginConfig;
import pluginsfix.glowautorestart.domain.ScheduleCalculator;
import pluginsfix.glowautorestart.notification.NotificationBroadcaster;
import pluginsfix.glowautorestart.platform.PlatformScheduler;
import pluginsfix.glowautorestart.text.ColorProcessor;
import pluginsfix.glowautorestart.text.Messages;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RestartService {

    private final PlatformScheduler scheduler;
    private final Messages messages;
    private final NotificationBroadcaster broadcaster;
    private final BossBarManager bossBarManager;

    private PluginConfig config;
    private Instant targetRestartInstant;
    private boolean manualCountdown;
    private final AtomicBoolean executingRestart = new AtomicBoolean(false);

    public RestartService(
        PlatformScheduler scheduler,
        Messages messages,
        PluginConfig config,
        NotificationBroadcaster broadcaster,
        BossBarManager bossBarManager
    ) {
        this.scheduler = scheduler;
        this.messages = messages;
        this.config = config;
        this.broadcaster = broadcaster;
        this.bossBarManager = bossBarManager;

        scheduleNextRegularRestart();
    }

    public void start() {
        scheduler.runGlobalTimer(this::tick, 20L, 20L);
    }

    public void stop() {
        bossBarManager.hide();
    }

    public void reload(PluginConfig newConfig) {
        this.config = newConfig;
        this.bossBarManager.updateConfig(newConfig.bossBar());
        if (!manualCountdown) {
            scheduleNextRegularRestart();
        }
    }

    public void scheduleNextRegularRestart() {
        this.manualCountdown = false;
        this.targetRestartInstant = ScheduleCalculator.calculateNextRestart(
            config.schedule().mode(),
            config.schedule().fixedTimes(),
            config.schedule().timezone(),
            config.schedule().intervalSeconds(),
            Instant.now()
        );
        long initialRemaining = getRemainingSeconds();
        this.bossBarManager.setTotalCountdownSeconds(initialRemaining);
    }

    public void startManualCountdown(long seconds) {
        this.manualCountdown = true;
        this.targetRestartInstant = Instant.now().plusSeconds(seconds);
        this.bossBarManager.setTotalCountdownSeconds(seconds);
        broadcaster.broadcastCountdown(seconds);
    }

    public void cancelCountdown() {
        scheduleNextRegularRestart();
        bossBarManager.hide();
        broadcaster.broadcastCancel();
    }

    public long getRemainingSeconds() {
        if (targetRestartInstant == null) {
            return 0;
        }
        long diff = Duration.between(Instant.now(), targetRestartInstant).getSeconds();
        return Math.max(0, diff);
    }

    public boolean isManualCountdown() {
        return manualCountdown;
    }

    private void tick() {
        if (executingRestart.get()) {
            return;
        }

        long remaining = getRemainingSeconds();
        bossBarManager.update(remaining);

        if (config.countdownMilestones().contains(remaining)) {
            broadcaster.broadcastCountdown(remaining);
        }

        if (remaining <= 0) {
            executeRestart();
        }
    }

    public void executeRestart() {
        if (!executingRestart.compareAndSet(false, true)) {
            return;
        }

        bossBarManager.hide();

        scheduler.runGlobal(() -> {
            for (String preCmd : config.execution().preRestartCommands()) {
                if (preCmd != null && !preCmd.isBlank()) {
                    try {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), preCmd);
                    } catch (Exception ignored) {
                    }
                }
            }

            if (config.execution().kickPlayers()) {
                List<String> rawKickLines = messages.getRawList("kick-message");
                Component kickReason;
                if (!rawKickLines.isEmpty()) {
                    String joined = String.join("\n", rawKickLines);
                    kickReason = ColorProcessor.process(joined);
                } else {
                    kickReason = Component.text("Server is restarting");
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    try {
                        player.kick(kickReason);
                    } catch (Exception ignored) {
                    }
                }
            }

            String restartCmd = config.execution().restartCommand();
            if (restartCmd != null && !restartCmd.isBlank()) {
                try {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), restartCmd);
                } catch (Exception e) {
                    Bukkit.shutdown();
                }
            } else {
                Bukkit.shutdown();
            }
        });
    }
}
