package pluginsfix.glowautorestart.text;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import pluginsfix.glowautorestart.domain.TimeUnits;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class Messages {

    private final Plugin plugin;
    private final File file;
    private YamlConfiguration config;
    private TimeUnits timeUnits;

    public Messages(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "messages.yml");
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);

        String days = config.getString("time-units.days");
        String hours = config.getString("time-units.hours");
        String minutes = config.getString("time-units.minutes");
        String seconds = config.getString("time-units.seconds");

        if (days == null || hours == null || minutes == null || seconds == null) {
            this.timeUnits = TimeUnits.defaultRussian();
        } else {
            this.timeUnits = new TimeUnits(days, hours, minutes, seconds);
        }
    }

    public TimeUnits getTimeUnits() {
        return timeUnits;
    }

    public String getString(String key) {
        if (config == null || !config.contains(key)) {
            plugin.getLogger().log(Level.WARNING, "Missing message key in messages.yml: " + key);
            return "";
        }
        return config.getString(key, "");
    }

    public List<String> getRawList(String key) {
        if (config == null || !config.contains(key)) {
            plugin.getLogger().log(Level.WARNING, "Missing message key in messages.yml: " + key);
            return Collections.emptyList();
        }

        if (config.isList(key)) {
            return config.getStringList(key);
        }

        String single = config.getString(key);
        return single != null ? List.of(single) : Collections.emptyList();
    }

    public void send(Audience audience, String key) {
        send(audience, key, Collections.emptyMap());
    }

    public void send(Audience audience, String key, String placeholderKey, String placeholderValue) {
        Map<String, String> placeholders = new HashMap<>(1);
        placeholders.put(placeholderKey, placeholderValue);
        send(audience, key, placeholders);
    }

    public void send(Audience audience, String key, Map<String, String> placeholders) {
        List<String> lines = getRawList(key);
        if (lines.isEmpty()) {
            return;
        }

        for (String line : lines) {
            executeAction(audience, applyPlaceholders(line, placeholders));
        }
    }

    public Component getComponent(String key, Map<String, String> placeholders) {
        String raw = getString(key);
        String formatted = applyPlaceholders(raw, placeholders);
        return ColorProcessor.process(formatted);
    }

    private void executeAction(Audience audience, String rawLine) {
        if (rawLine == null) {
            return;
        }

        if (rawLine.startsWith("[actionbar] ")) {
            String content = rawLine.substring("[actionbar] ".length());
            audience.sendActionBar(ColorProcessor.process(content));
            return;
        }

        if (rawLine.startsWith("[sound] ")) {
            String soundName = rawLine.substring("[sound] ".length()).trim().toLowerCase();
            try {
                Sound sound = Sound.sound(Key.key(soundName), Sound.Source.MASTER, 1.0f, 1.0f);
                audience.playSound(sound);
            } catch (Exception ignored) {
            }
            return;
        }

        String messageContent = rawLine;
        if (rawLine.startsWith("[message] ")) {
            messageContent = rawLine.substring("[message] ".length());
        }

        audience.sendMessage(ColorProcessor.process(messageContent));
    }

    public static String applyPlaceholders(String text, Map<String, String> placeholders) {
        if (text == null || placeholders == null || placeholders.isEmpty()) {
            return text != null ? text : "";
        }

        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
            result = result.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return result;
    }
}
