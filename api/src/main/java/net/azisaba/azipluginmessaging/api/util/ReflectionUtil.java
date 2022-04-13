package net.azisaba.azipluginmessaging.api.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class ReflectionUtil {
    // 1. look for method on superclass
    // 2. look for method on interfaces, then interfaces in interface...
    @Nullable
    public static Method findMethod(@NotNull Class<?> clazz, @NotNull Method m) {
        try {
            return clazz.getDeclaredMethod(m.getName(), m.getParameterTypes());
        } catch (ReflectiveOperationException ignore) {}
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            Method method = findMethod(superClass, m);
            if (method != null) return method;
        }
        for (Class<?> c : clazz.getInterfaces()) {
            Method method = findMethod(c, m);
            if (method != null) return method;
        }
        return null;
    }
}
