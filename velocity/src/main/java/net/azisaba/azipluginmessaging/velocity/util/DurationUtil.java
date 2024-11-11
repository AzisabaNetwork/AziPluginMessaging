package net.azisaba.azipluginmessaging.velocity.util;

import static net.azisaba.spicyAzisaBan.util.DateTimeConstantsKt.*;

public class DurationUtil {
    public static String unProcessTime(long l) {
        if (l < 0L) return "0s";
        long time = l;
        int d = 0, h = 0, m = 0, s = 0;
        if (time > day) {
            long t = (long) Math.floor(time / (double) day);
            d = (int) t;
            time -= t * day;
        }
        if (time > hour) {
            long t = (long) Math.floor(time / (double) hour);
            h = (int) t;
            time -= t * hour;
        }
        if (time > minute) {
            long t = (long) Math.floor(time / (double) minute);
            m = (int) t;
            time -= t * minute;
        }
        if (time > second) {
            long t = (long) Math.floor(time / (double) second);
            s = (int) t;
            //time -= t * second;
        }
        return String.format("%dd%dh%dm%ds", d, h, m, s);
    }
}
