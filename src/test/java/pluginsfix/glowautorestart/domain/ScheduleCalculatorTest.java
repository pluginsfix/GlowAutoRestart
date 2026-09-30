package pluginsfix.glowautorestart.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class ScheduleCalculatorTest {

    private final ZoneId utc = ZoneId.of("UTC");

    @Test
    void calculatesIntervalMode() {
        Instant now = Instant.parse("2026-01-01T12:00:00Z");
        Instant next = ScheduleCalculator.calculateNextRestart(ScheduleMode.INTERVAL, List.of(), utc, 3600, now);
        assertThat(next).isEqualTo(Instant.parse("2026-01-01T13:00:00Z"));
    }

    @Test
    void calculatesFixedTimesSameDay() {
        Instant now = Instant.parse("2026-01-01T10:00:00Z");
        List<String> times = List.of("06:00", "12:00", "18:00");
        Instant next = ScheduleCalculator.calculateNextRestart(ScheduleMode.FIXED_TIMES, times, utc, 3600, now);
        assertThat(next).isEqualTo(Instant.parse("2026-01-01T12:00:00Z"));
    }

    @Test
    void calculatesFixedTimesRollsOverToNextDay() {
        Instant now = Instant.parse("2026-01-01T20:00:00Z");
        List<String> times = List.of("06:00", "12:00", "18:00");
        Instant next = ScheduleCalculator.calculateNextRestart(ScheduleMode.FIXED_TIMES, times, utc, 3600, now);
        assertThat(next).isEqualTo(Instant.parse("2026-01-02T06:00:00Z"));
    }
}
