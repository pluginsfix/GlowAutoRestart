package pluginsfix.glowautorestart.service;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pluginsfix.glowautorestart.config.BossBarConfig;
import pluginsfix.glowautorestart.domain.TimeFormatter;
import pluginsfix.glowautorestart.text.ColorProcessor;
import pluginsfix.glowautorestart.text.Messages;

import java.util.Map;

public final class BossBarManager {

    private final Messages messages;
    private BossBarConfig config;
    private BossBar bossBar;
    private long totalCountdownSeconds = 300;

    public BossBarManager(Messages messages, BossBarConfig config) {
        this.messages = messages;
        this.config = config;
        initBossBar();
    }

    public void updateConfig(BossBarConfig newConfig) {
        this.config = newConfig;
        hide();
        initBossBar();
    }

    private void initBossBar() {
        if (!config.enabled()) {
            this.bossBar = null;
            return;
        }

        this.bossBar = BossBar.bossBar(
            Component.empty(),
            1.0f,
            config.color(),
            config.overlay()
        );
    }

    public void setTotalCountdownSeconds(long seconds) {
        this.totalCountdownSeconds = Math.max(1, seconds);
    }

    public void update(long remainingSeconds) {
        if (!config.enabled() || bossBar == null) {
            return;
        }

        if (remainingSeconds > config.visibleSeconds() || remainingSeconds <= 0) {
            hide();
            return;
        }

        float progress = (float) remainingSeconds / (float) Math.min(totalCountdownSeconds, config.visibleSeconds());
        progress = Math.max(0.0f, Math.min(1.0f, progress));
        bossBar.progress(progress);

        String formattedTime = TimeFormatter.format(remainingSeconds, messages.getTimeUnits());
        String rawTitle = messages.getString("bossbar-title");
        String formattedTitle = Messages.applyPlaceholders(rawTitle, Map.of("time", formattedTime));
        bossBar.name(ColorProcessor.process(formattedTitle));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showBossBar(bossBar);
        }
    }

    public void hide() {
        if (bossBar != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.hideBossBar(bossBar);
            }
        }
    }
}
