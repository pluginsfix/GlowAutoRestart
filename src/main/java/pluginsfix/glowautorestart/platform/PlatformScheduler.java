package pluginsfix.glowautorestart.platform;

public interface PlatformScheduler {
    void runGlobal(Runnable task);
    void runAsync(Runnable task);
    void runGlobalTimer(Runnable task, long delayTicks, long periodTicks);
    void cancelAll();
}
