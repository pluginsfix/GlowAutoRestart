package pluginsfix.glowautorestart.config;

import net.kyori.adventure.bossbar.BossBar;

public record BossBarConfig(
    boolean enabled,
    BossBar.Color color,
    BossBar.Overlay overlay,
    long visibleSeconds
) {
    public static BossBarConfig create(boolean enabled, String colorName, String overlayName, long visibleSeconds) {
        BossBar.Color color = parseColor(colorName);
        BossBar.Overlay overlay = parseOverlay(overlayName);
        return new BossBarConfig(enabled, color, overlay, Math.max(1, visibleSeconds));
    }

    private static BossBar.Color parseColor(String name) {
        if (name == null) {
            return BossBar.Color.YELLOW;
        }
        try {
            return BossBar.Color.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BossBar.Color.YELLOW;
        }
    }

    private static BossBar.Overlay parseOverlay(String name) {
        if (name == null) {
            return BossBar.Overlay.PROGRESS;
        }
        try {
            String upper = name.toUpperCase();
            if (upper.equals("SOLID")) {
                return BossBar.Overlay.PROGRESS;
            }
            return BossBar.Overlay.valueOf(upper);
        } catch (IllegalArgumentException e) {
            return BossBar.Overlay.PROGRESS;
        }
    }
}
