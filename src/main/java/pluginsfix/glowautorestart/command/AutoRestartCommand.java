package pluginsfix.glowautorestart.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pluginsfix.glowautorestart.config.PluginConfig;
import pluginsfix.glowautorestart.domain.TimeFormatter;
import pluginsfix.glowautorestart.domain.TimeParser;
import pluginsfix.glowautorestart.service.RestartService;
import pluginsfix.glowautorestart.text.Messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;

public final class AutoRestartCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final RestartService restartService;
    private final Messages messages;

    public AutoRestartCommand(Plugin plugin, RestartService restartService, Messages messages) {
        this.plugin = plugin;
        this.restartService = restartService;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    ) {
        if (!sender.hasPermission("glowautorestart.use") && !sender.hasPermission("glowautorestart.admin")) {
            messages.send(sender, "no-permission");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            messages.send(sender, "help");
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "time" -> {
                long remaining = restartService.getRemainingSeconds();
                String formatted = TimeFormatter.format(remaining, messages.getTimeUnits());
                messages.send(sender, "time-remaining", "time", formatted);
                return true;
            }
            case "start" -> {
                if (!sender.hasPermission("glowautorestart.admin")) {
                    messages.send(sender, "no-permission");
                    return true;
                }
                if (args.length < 2) {
                    messages.send(sender, "invalid-arguments");
                    return true;
                }
                OptionalLong parsed = TimeParser.parseToSeconds(args[1]);
                if (parsed.isEmpty()) {
                    messages.send(sender, "invalid-time-format");
                    return true;
                }
                long seconds = parsed.getAsLong();
                restartService.startManualCountdown(seconds);
                String formatted = TimeFormatter.format(seconds, messages.getTimeUnits());
                messages.send(sender, "start-success", "time", formatted);
                return true;
            }
            case "cancel" -> {
                if (!sender.hasPermission("glowautorestart.admin")) {
                    messages.send(sender, "no-permission");
                    return true;
                }
                restartService.cancelCountdown();
                messages.send(sender, "cancel-success");
                return true;
            }
            case "now" -> {
                if (!sender.hasPermission("glowautorestart.admin")) {
                    messages.send(sender, "no-permission");
                    return true;
                }
                messages.send(sender, "now-success");
                restartService.executeRestart();
                return true;
            }
            case "reload" -> {
                if (!sender.hasPermission("glowautorestart.admin")) {
                    messages.send(sender, "no-permission");
                    return true;
                }
                messages.reload();
                PluginConfig newConfig = PluginConfig.load(plugin);
                restartService.reload(newConfig);
                messages.send(sender, "reload-success");
                return true;
            }
            default -> {
                messages.send(sender, "invalid-arguments");
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    ) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            if (sender.hasPermission("glowautorestart.use") || sender.hasPermission("glowautorestart.admin")) {
                suggestions.add("time");
                suggestions.add("help");
            }
            if (sender.hasPermission("glowautorestart.admin")) {
                suggestions.add("start");
                suggestions.add("cancel");
                suggestions.add("now");
                suggestions.add("reload");
            }

            String current = args[0].toLowerCase();
            return suggestions.stream()
                .filter(s -> s.startsWith(current))
                .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("start") && sender.hasPermission("glowautorestart.admin")) {
            List<String> times = List.of("30s", "60s", "2m", "5m", "10m", "15m", "30m", "1h");
            String current = args[1].toLowerCase();
            return times.stream()
                .filter(t -> t.startsWith(current))
                .toList();
        }

        return Collections.emptyList();
    }
}
