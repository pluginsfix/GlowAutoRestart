package pluginsfix.glowautorestart.notification;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pluginsfix.glowautorestart.domain.TimeFormatter;
import pluginsfix.glowautorestart.text.ColorProcessor;
import pluginsfix.glowautorestart.text.Messages;

import java.time.Duration;
import java.util.Map;

public final class NotificationBroadcaster {

    private final Messages messages;

    public NotificationBroadcaster(Messages messages) {
        this.messages = messages;
    }

    public void broadcastCountdown(long remainingSeconds) {
        String formattedTime = TimeFormatter.format(remainingSeconds, messages.getTimeUnits());
        Map<String, String> placeholders = Map.of("time", formattedTime);

        boolean isFinalCountdown = remainingSeconds <= 5;

        for (Player player : Bukkit.getOnlinePlayers()) {
            messages.send(player, "countdown-broadcast", placeholders);

            Component titleComponent;
            Component subtitleComponent;

            if (isFinalCountdown) {
                String rawTitle = messages.getString("countdown-final-title");
                String rawSubtitle = messages.getString("countdown-final-subtitle");
                titleComponent = ColorProcessor.process(Messages.applyPlaceholders(rawTitle, placeholders));
                subtitleComponent = ColorProcessor.process(Messages.applyPlaceholders(rawSubtitle, placeholders));
            } else {
                String rawTitle = messages.getString("countdown-title");
                String rawSubtitle = messages.getString("countdown-subtitle");
                titleComponent = ColorProcessor.process(Messages.applyPlaceholders(rawTitle, placeholders));
                subtitleComponent = ColorProcessor.process(Messages.applyPlaceholders(rawSubtitle, placeholders));
            }

            Title.Times times = Title.Times.times(Duration.ofMillis(200), Duration.ofMillis(1200), Duration.ofMillis(400));
            player.showTitle(Title.title(titleComponent, subtitleComponent, times));

            if (isFinalCountdown) {
                float pitch = 1.0f + (5 - remainingSeconds) * 0.2f;
                try {
                    Sound sound = Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1.0f, pitch);
                    player.playSound(sound);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void broadcastCancel() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            messages.send(player, "cancel-broadcast");
            player.clearTitle();
        }
    }
}
