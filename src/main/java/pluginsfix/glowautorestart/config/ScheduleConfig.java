package pluginsfix.glowautorestart.config;

import pluginsfix.glowautorestart.domain.ScheduleMode;

import java.time.ZoneId;
import java.util.List;

public record ScheduleConfig(
    ScheduleMode mode,
    List<String> fixedTimes,
    ZoneId timezone,
    long intervalSeconds
) {
}
