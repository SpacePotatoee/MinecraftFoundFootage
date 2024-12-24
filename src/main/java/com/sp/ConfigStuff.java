package com.sp;

import eu.midnightdust.lib.config.MidnightConfig;


public class ConfigStuff extends MidnightConfig {
    public static final String CAMERA = "cameraStuff";
    public static final String ADMIN = "adminStuff";
    public static final String WORLD = "world";

    @Entry(category = CAMERA)
    public static boolean enableVhsEffect = true;

    @Entry(category = CAMERA)
    public static boolean enableRealCamera = true;

    @Entry(category = CAMERA, isSlider = true, min = 0, max = 10)
    public static float lookRollMultiplier = 7.0f;

    @Entry(category = CAMERA, isSlider = true, min = 0, max = 10)
    public static float strafeRollMultiplier = 5.0f;

    @Entry(category = CAMERA)
    public static boolean disableHud = false;


    @Entry(category = WORLD, min = 10)
    public static int lightRenderDistance = 100;


    @Entry(category = ADMIN)
    public static boolean enableVanillaLighting = false;

    @Entry(category = ADMIN)
    public static boolean forceBackrooms = false;

    @Entry(category = ADMIN)
    public static boolean enable3rdPerson = false;

}
