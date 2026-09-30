package pluginsfix.glowautorestart.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class TimeFormatterTest {

    private final TimeUnits units = TimeUnits.defaultRussian();

    @Test
    void formatsZeroSeconds() {
        String result = TimeFormatter.format(0, units);
        assertThat(result).isEqualTo("0 сек.");
    }

    @Test
    void formatsSecondsOnly() {
        String result = TimeFormatter.format(45, units);
        assertThat(result).isEqualTo("45 сек.");
    }

    @Test
    void formatsMinutesAndSeconds() {
        String result = TimeFormatter.format(125, units);
        assertThat(result).isEqualTo("2 мин. 5 сек.");
    }

    @Test
    void formatsHoursMinutesAndSeconds() {
        String result = TimeFormatter.format(3665, units);
        assertThat(result).isEqualTo("1 ч. 1 мин. 5 сек.");
    }

    @Test
    void formatsDaysHoursMinutesAndSeconds() {
        String result = TimeFormatter.format(90061, units);
        assertThat(result).isEqualTo("1 д. 1 ч. 1 мин. 1 сек.");
    }
}
