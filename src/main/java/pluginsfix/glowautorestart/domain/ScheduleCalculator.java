package pluginsfix.glowautorestart.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ScheduleCalculator {

    private ScheduleCalculator() {
    }

    public static Instant calculateNextRestart(
        ScheduleMode mode,
        List<String> fixedTimes,
        ZoneId zoneId,
        long intervalSeconds,
        Instant now
    ) {
        if (mode == ScheduleMode.INTERVAL) {
            long safeInterval = Math.max(10, intervalSeconds);
            return now.plusSeconds(safeInterval);
        }

        List<LocalTime> parsedTimes = parseTimes(fixedTimes);
        if (parsedTimes.isEmpty()) {
            return now.plusSeconds(Math.max(10, intervalSeconds));
        }

        ZonedDateTime currentZoned = now.atZone(zoneId);
        LocalDate currentDate = currentZoned.toLocalDate();
        LocalTime currentTime = currentZoned.toLocalTime();

        for (LocalTime targetTime : parsedTimes) {
            if (targetTime.isAfter(currentTime)) {
                return ZonedDateTime.of(currentDate, targetTime, zoneId).toInstant();
            }
        }

        LocalTime firstTimeNextDay = parsedTimes.getFirst();
        return ZonedDateTime.of(currentDate.plusDays(1), firstTimeNextDay, zoneId).toInstant();
    }

    public static List<LocalTime> parseTimes(List<String> rawTimes) {
        if (rawTimes == null || rawTimes.isEmpty()) {
            return Collections.emptyList();
        }

        List<LocalTime> list = new ArrayList<>();
        for (String raw : rawTimes) {
            if (raw == null) {
                continue;
            }
            String trimmed = raw.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            try {
                if (trimmed.length() == 4 && trimmed.indexOf(':') == 1) {
                    trimmed = "0" + trimmed;
                }
                list.add(LocalTime.parse(trimmed));
            } catch (DateTimeParseException ignored) {
            }
        }

        Collections.sort(list);
        return Collections.unmodifiableList(list);
    }
}
