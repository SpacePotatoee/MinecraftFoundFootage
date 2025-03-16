package com.sp.compat.modmenu;

import java.util.Map;
import java.util.function.Supplier;

public class ConfigDefinitions {

    public static Map<String, Supplier<Boolean>> definitions = Map.of(
            "SHADOWS",           ConfigDefinitions::isEnableShadows,
            "VOLUMETRIC_LIGHT",  ConfigDefinitions::isEnableVolumetricLight,
            "PUDDLES",           ConfigDefinitions::isEnablePuddles,
            "LEVEL1_FOG",        ConfigDefinitions::isEnableLevel1Fog,
            "WATER_REFLECTIONS", ConfigDefinitions::isRenderWaterReflections,
            "BLOCK_REFLECTIONS", ConfigDefinitions::isRenderBlockReflections,
            "MOTION_BLUR",       ConfigDefinitions::isMotionBlur);

    public static boolean isEnableShadows() {
        return ConfigStuff.enableShadows;
    }

    public static boolean isEnableVolumetricLight() {
        return ConfigStuff.enableVolumetricLight;
    }

    public static boolean isEnablePuddles() {
        return ConfigStuff.enablePuddles;
    }

    public static boolean isEnableLevel1Fog() {
        return ConfigStuff.enableLevel1Fog;
    }

    public static boolean isRenderWaterReflections() {
        return ConfigStuff.renderWaterReflections;
    }

    public static boolean isRenderBlockReflections() {
        return ConfigStuff.renderBlockReflections;
    }

    public static boolean isMotionBlur() {
        return ConfigStuff.motionBlur;
    }

}
