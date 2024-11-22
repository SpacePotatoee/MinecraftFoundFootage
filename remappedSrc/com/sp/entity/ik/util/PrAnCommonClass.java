package com.sp.entity.ik.util;

import java.util.logging.Logger;

public class PrAnCommonClass {
    public static Logger LOGGER = Logger.getLogger("PrAn");
    public static boolean isDev = true;
    public static boolean shouldRenderDebugLegs = false;

    public static void throwInDevOnly(RuntimeException exception) {
        if (isDev) {
            throw exception;
        }
    }
}
