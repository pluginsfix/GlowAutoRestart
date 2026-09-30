package pluginsfix.glowautorestart.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import pluginsfix.glowautorestart.domain.ScheduleMode;

import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record PluginConfig(
    ScheduleConfig schedule,
    Set<Long> countdownMilestones,
    BossBarConfig bossBar,
    ExecutionConfig execution
) {
    public static PluginConfig load(Plugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration raw = plugin.getConfig();

        String rawMode = raw.getString("schedule.mode", "FIXED_TIMES");
        ScheduleMode mode;
        try {
            mode = ScheduleMode.valueOf(rawMode.toUpperCase());
        } catch (IllegalArgumentException e) {
            mode = ScheduleMode.FIXED_TIMES;
        }

        List<String> fixedTimes = raw.getStringList("schedule.fixed-times");
        String timezoneStr = raw.getString("schedule.timezone", "UTC");
        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(timezoneStr);
        } catch (Exception e) {
            zoneId = ZoneId.systemDefault();
        }

        long intervalSeconds = raw.getLong("schedule.interval-seconds", 21600);
        ScheduleConfig scheduleConfig = new ScheduleConfig(mode, fixedTimes, zoneId, intervalSeconds);

        List<Long> milestoneList = raw.getLongList("notification.countdown-milestones");
        Set<Long> milestoneSet = new HashSet<>(milestoneList);
        if (milestoneSet.isEmpty()) {
            milestoneSet = Set.of(3600L, 1800L, 900L, 600L, 300L, 120L, 60L, 30L, 10L, 5L, 4L, 3L, 2L, 1L);
        }

        boolean bossBarEnabled = raw.getBoolean("bossbar.enabled", true);
        String bossBarColor = raw.getString("bossbar.color", "YELLOW");
        String bossBarStyle = raw.getString("bossbar.style", "SOLID");
        long bossBarVisibleSeconds = raw.getLong("bossbar.visible-seconds", 300);
        BossBarConfig bossBarConfig = BossBarConfig.create(bossBarEnabled, bossBarColor, bossBarStyle, bossBarVisibleSeconds);

        boolean kickPlayers = raw.getBoolean("execution.kick-players", true);
        String restartCommand = raw.getString("execution.restart-command", "restart");
        List<String> preCommands = raw.getStringList("execution.pre-restart-commands");
        ExecutionConfig executionConfig = new ExecutionConfig(kickPlayers, restartCommand, preCommands);

        return new PluginConfig(
            scheduleConfig,
            Collections.unmodifiableSet(milestoneSet),
            bossBarConfig,
            executionConfig
        );
    }
}
