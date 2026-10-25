package net.azisaba.azipluginmessaging.spigot.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SchedulerUtil {
    private static final boolean FOLIA;
    private static Method getGlobalRegionScheduler;
    private static Method globalRunDelayed;
    private static Method getAsyncScheduler;
    private static Method asyncRunDelayed;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        if (folia) {
            try {
                Class<?> globalClass = Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
                Class<?> asyncClass = Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
                getGlobalRegionScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler");
                globalRunDelayed = globalClass.getMethod("runDelayed", Plugin.class, Consumer.class, long.class);
                getAsyncScheduler = Bukkit.class.getMethod("getAsyncScheduler");
                asyncRunDelayed = asyncClass.getMethod("runDelayed", Plugin.class, Consumer.class, long.class, TimeUnit.class);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to initialize Folia scheduler", e);
            }
        }
        FOLIA = folia;
    }

    private SchedulerUtil() {
        throw new AssertionError();
    }

    public static boolean isFolia() {
        return FOLIA;
    }

    public static void runLater(@NotNull Plugin plugin, @NotNull Runnable task, long delayTicks) {
        if (FOLIA) {
            try {
                Object scheduler = getGlobalRegionScheduler.invoke(null);
                Consumer<Object> consumer = t -> task.run();
                // Folia requires delay > 0
                globalRunDelayed.invoke(scheduler, plugin, consumer, Math.max(1L, delayTicks));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public static void runLaterAsync(@NotNull Plugin plugin, @NotNull Runnable task, long delayTicks) {
        if (FOLIA) {
            try {
                Object scheduler = getAsyncScheduler.invoke(null);
                Consumer<Object> consumer = t -> task.run();
                asyncRunDelayed.invoke(scheduler, plugin, consumer, Math.max(1L, delayTicks) * 50L, TimeUnit.MILLISECONDS);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
        }
    }
}
