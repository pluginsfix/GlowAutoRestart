package pluginsfix.glowautorestart.config;

import java.util.List;

public record ExecutionConfig(
    boolean kickPlayers,
    String restartCommand,
    List<String> preRestartCommands
) {
}
