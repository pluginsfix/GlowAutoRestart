package pluginsfix.glowautorestart.domain;

import org.junit.jupiter.api.Test;

import java.util.OptionalLong;

import static org.assertj.core.api.Assertions.assertThat;

final class TimeParserTest {

    @Test
    void parsesPlainNumbersAsSeconds() {
        OptionalLong result = TimeParser.parseToSeconds("60");
        assertThat(result).hasValue(60);
    }

    @Test
    void parsesSecondsWithSuffix() {
        assertThat(TimeParser.parseToSeconds("45s")).hasValue(45);
        assertThat(TimeParser.parseToSeconds("30сек")).hasValue(30);
    }

    @Test
    void parsesMinutes() {
        assertThat(TimeParser.parseToSeconds("5m")).hasValue(300);
        assertThat(TimeParser.parseToSeconds("10мин")).hasValue(600);
    }

    @Test
    void parsesHours() {
        assertThat(TimeParser.parseToSeconds("2h")).hasValue(7200);
        assertThat(TimeParser.parseToSeconds("1ч")).hasValue(3600);
    }

    @Test
    void parsesDays() {
        assertThat(TimeParser.parseToSeconds("1d")).hasValue(86400);
        assertThat(TimeParser.parseToSeconds("2д")).hasValue(172800);
    }

    @Test
    void parsesCombinedParts() {
        assertThat(TimeParser.parseToSeconds("1h30m")).hasValue(5400);
        assertThat(TimeParser.parseToSeconds("5m10s")).hasValue(310);
    }

    @Test
    void returnsEmptyForInvalidInputs() {
        assertThat(TimeParser.parseToSeconds("")).isEmpty();
        assertThat(TimeParser.parseToSeconds("abc")).isEmpty();
        assertThat(TimeParser.parseToSeconds("-10")).isEmpty();
        assertThat(TimeParser.parseToSeconds("0")).isEmpty();
    }
}
