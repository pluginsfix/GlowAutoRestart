package pluginsfix.glowautorestart.domain;

import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeParser {

    private static final Pattern TIME_PART_PATTERN = Pattern.compile("(\\d+)\\s*([a-zA-Zа-яА-Я]+)?");

    private TimeParser() {
    }

    public static OptionalLong parseToSeconds(String input) {
        if (input == null) {
            return OptionalLong.empty();
        }

        String raw = input.trim().toLowerCase();
        if (raw.isEmpty()) {
            return OptionalLong.empty();
        }

        try {
            long plainNumber = Long.parseLong(raw);
            return plainNumber > 0 ? OptionalLong.of(plainNumber) : OptionalLong.empty();
        } catch (NumberFormatException ignored) {
        }

        Matcher matcher = TIME_PART_PATTERN.matcher(raw);
        long totalSeconds = 0;
        int matchedLength = 0;

        while (matcher.find()) {
            String valueStr = matcher.group(1);
            String unitStr = matcher.group(2);

            long value;
            try {
                value = Long.parseLong(valueStr);
            } catch (NumberFormatException e) {
                return OptionalLong.empty();
            }

            long multiplier = resolveMultiplier(unitStr);
            if (multiplier == 0) {
                return OptionalLong.empty();
            }

            totalSeconds += value * multiplier;
            matchedLength += matcher.group(0).length();
        }

        if (matchedLength == 0 || totalSeconds <= 0) {
            return OptionalLong.empty();
        }

        return OptionalLong.of(totalSeconds);
    }

    private static long resolveMultiplier(String unit) {
        if (unit == null || unit.isEmpty() || unit.equals("s") || unit.equals("sec") || unit.equals("сек") || unit.equals("с")) {
            return 1;
        }
        if (unit.equals("m") || unit.equals("min") || unit.equals("мин") || unit.equals("м")) {
            return 60;
        }
        if (unit.equals("h") || unit.equals("hour") || unit.equals("ч") || unit.equals("час") || unit.equals("часов")) {
            return 3600;
        }
        if (unit.equals("d") || unit.equals("day") || unit.equals("д") || unit.equals("дн") || unit.equals("дней")) {
            return 86400;
        }
        return 0;
    }
}
