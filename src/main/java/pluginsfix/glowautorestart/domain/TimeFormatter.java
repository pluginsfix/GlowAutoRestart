package pluginsfix.glowautorestart.domain;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class TimeFormatter {

    private TimeFormatter() {
    }

    public static String format(long totalSeconds, TimeUnits units) {
        if (totalSeconds <= 0) {
            return "0 " + units.seconds();
        }

        Duration duration = Duration.ofSeconds(totalSeconds);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        List<String> parts = new ArrayList<>(4);

        if (days > 0) {
            parts.add(days + " " + units.days());
        }
        if (hours > 0) {
            parts.add(hours + " " + units.hours());
        }
        if (minutes > 0) {
            parts.add(minutes + " " + units.minutes());
        }
        if (seconds > 0 || parts.isEmpty()) {
            parts.add(seconds + " " + units.seconds());
        }

        return String.join(" ", parts);
    }
}
