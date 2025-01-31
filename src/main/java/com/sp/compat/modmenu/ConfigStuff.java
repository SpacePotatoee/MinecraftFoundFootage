package com.sp.compat.modmenu;

import eu.midnightdust.lib.config.MidnightConfig;


public class ConfigStuff extends MidnightConfig {
    public static final String CAMERA = "cameraStuff";
//    public static final String ADMIN = "adminStuff";
    public static final String SHADERS = "shaders";

    @Entry(category = CAMERA)
    public static boolean enableRealCamera = true;

    @Entry(category = CAMERA, isSlider = true, min = 0, max = 10)
    public static float lookRollMultiplier = 7.0f;

    @Entry(category = CAMERA, isSlider = true, min = 0, max = 10)
    public static float strafeRollMultiplier = 7.0f;

    @Entry(category = CAMERA)
    public static boolean disableHud = false;

    @Entry(category = CAMERA)
    public static boolean enableSmoothCamera = true;

    @Entry(category = CAMERA, isSlider = true, min = 0, max = 1)
    public static float cameraSmoothing = 0.5f;


    @Entry(category = SHADERS)
    public static boolean enableVhsEffect = true;

    @Entry(category = SHADERS, min = 10)
    public static int lightRenderDistance = 100;

    @Entry(category = SHADERS)
    public static boolean renderBlockReflections = true;


//    @Entry(category = ADMIN)
//    public static boolean enableVanillaLighting = false;
//
//    @Entry(category = ADMIN)
//    public static boolean forceBackrooms = false;
//
//    @Entry(category = ADMIN)
//    public static boolean enable3rdPerson = false;

}
