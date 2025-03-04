package com.sp.compat.modmenu;

import eu.midnightdust.lib.config.MidnightConfig;


public class ConfigStuff extends MidnightConfig {
    public static final String VIDEO = "video";
//    public static final String ADMIN = "adminStuff";
    public static final String SHADERS = "shaders";

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


    @Comment(category = VIDEO)
    public static Comment spacer2;


    @Comment(category = VIDEO)
    public static Comment VhsAspectRatioComment;

    @Entry(category = VIDEO)
    public static boolean enableVHSAspectRatio = false;



    @Entry(category = SHADERS)
    public static boolean enableVhsEffect = true;

    @Entry(category = SHADERS, min = 10)
    public static int lightRenderDistance = 100;

    @Entry(category = SHADERS)
    public static boolean renderBlockReflections = true;

    @Comment(category = VIDEO)
    public static Comment spacer3;

    @Entry(category = SHADERS)
    public static boolean motionBlur = true;

    @Entry(category = SHADERS, isSlider = true, min = 0.1, max = 1, precision = 10)
    public static float motionBlurStrength = 0.5f;


//    @Entry(category = ADMIN)
//    public static boolean enableVanillaLighting = false;
//
//    @Entry(category = ADMIN)
//    public static boolean forceBackrooms = false;
//
//    @Entry(category = ADMIN)
//    public static boolean enable3rdPerson = false;

}
