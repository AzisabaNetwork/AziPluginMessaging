package net.azisaba.azipluginmessaging.api.util;

import org.jetbrains.annotations.NotNull;

public class ArrayUtil {
    @SuppressWarnings("unchecked")
    public static <T> T @NotNull [] dropFirst(T @NotNull [] array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        if (array.length == 1) {
            return (T[]) new Object[0];
        }
        T[] newArray = (T[]) new Object[array.length - 1];
        System.arraycopy(array, 1, newArray, 0, array.length - 1);
        return newArray;
    }
}
