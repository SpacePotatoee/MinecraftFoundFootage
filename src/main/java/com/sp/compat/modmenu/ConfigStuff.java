package com.sp.compat.modmenu;

import com.sp.render.grass.GrassQuality;
import eu.midnightdust.lib.config.MidnightConfig;


public class ConfigStuff extends MidnightConfig {
    public static final String VIDEO = "video";
    public static final String SHADERS = "shaders";
    public static final String SERVER = "server";

    @Entry(category = VIDEO)
    public static boolean enableRealCamera = true;

    @Entry(category = VIDEO, isSlider = true, min = 0, max = 10)
    public static float lookRollMultiplier = 7.0f;

    @Entry(category = VIDEO, isSlider = true, min = 0, max = 10)
    public static float strafeRollMultiplier = 7.0f;

    @Entry(category = VIDEO)
    public static boolean enableSmoothCamera = true;

    @Entry(category = VIDEO, isSlider = true, min = 0, max = 1)
    public static float cameraSmoothing = 0.7f;


    @Comment(category = VIDEO)
    public static Comment spacer1;


    @Entry(category = VIDEO)
    public static boolean showHands = false;

    @Entry(category = VIDEO)
    public static boolean useDefaultGUI = false;

    @Entry(category = VIDEO)
    public static boolean enableHint = true;

    @Entry(category = VIDEO)
    public static boolean showStaminaHUD = true;

    @Entry(category = VIDEO, isSlider = true, min = 0.1, max = 2.0, precision = 10)
    public static float staminaHUDScale = 0.2f;

    @Entry(category = VIDEO, isSlider = true, min = 0.0, max = 1.0, precision = 10)
    public static float staminaHUDOpacity = 0.25f;

    @Entry(category = VIDEO, isSlider = true, min = -200, max = 200)
    public static int staminaHUDOffsetX = 0;

    @Entry(category = VIDEO, isSlider = true, min = -200, max = 200)
    public static int staminaHUDOffsetY = 0;


    @Comment(category = VIDEO)
    public static Comment spacer2;


    @Comment(category = VIDEO)
    public static Comment VhsAspectRatioComment;

    @Entry(category = VIDEO)
    public static boolean enableVHSAspectRatio = false;



    @Entry(category = SHADERS)
    public static boolean enableVhsEffect = true;

    @Entry(category = SHADERS)
    public static boolean enableVhsEffectInTheBackrooms = true;

    @Entry(category = SHADERS, isSlider = true, min = 0, max = 1)
    public static float VHSDistortionMultiplier = 1.0f;

    @Entry(category = SHADERS, min = 10)
    public static int lightRenderDistance = 100;

    public static int getLightRenderDistance() {
        return lightRenderDistance;
    }


    @Comment(category = SHADERS)
    public static Comment spacer3;


    @Entry(category = SHADERS)
    public static boolean enableShadows = true;

    @Entry(category = SHADERS)
    public static boolean enableVolumetricLight = true;

    @Entry(category = SHADERS)
    public static boolean enablePuddles = true;

    @Entry(category = SHADERS)
    public static boolean enableLevel1Fog = true;

    @Entry(category = SHADERS)
    public static boolean renderWaterReflections = true;

    @Entry(category = SHADERS)
    public static boolean renderBlockReflections = true;


    @Comment(category = SHADERS)
    public static Comment spacer4;


    @Entry(category = SHADERS)
    public static boolean motionBlur = true;

    @Entry(category = SHADERS, isSlider = true, min = 0.1, max = 1, precision = 10)
    public static float motionBlurStrength = 0.5f;


    @Comment(category = SHADERS)
    public static Comment spacer5;

    @Entry(category = SHADERS)
    public static GrassQuality grassQuality = GrassQuality.MEDIUM;



    @Entry(category = SERVER, min = 200, max = 1000)
    public static int exitSpawnRadius = 300;

}
