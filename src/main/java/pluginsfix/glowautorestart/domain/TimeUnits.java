package pluginsfix.glowautorestart.domain;

public record TimeUnits(
    String days,
    String hours,
    String minutes,
    String seconds
) {
    public static TimeUnits defaultRussian() {
        return new TimeUnits("д.", "ч.", "мин.", "сек.");
    }
}
