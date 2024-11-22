package com.sp.entity.ik.util;

import java.util.List;

public class ArrayUtil {
    public static <T> T getFirst(List<T> list) {
        return list.get(0);
    }

    public static <T> T getLast(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static <T> T getOrNull(List<T> list, int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }
}
