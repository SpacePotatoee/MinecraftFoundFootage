package com.sp.init;

import com.sp.SPBRevamped;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent SILENCE = registerSoundEvent("silence");

    public static final SoundEvent FLASHLIGHT_CLICK = registerSoundEvent("flashlight_click");


    public static final SoundEvent AMBIENCE = registerSoundEvent("ambience");


    public static final SoundEvent FALLING = registerSoundEvent("falling");
    public static final SoundEvent GLITCH = registerSoundEvent("glitch");

    public static final SoundEvent NO_ESCAPE = registerSoundEvent("noescape");

    public static final SoundEvent FLUORESCENT_LIGHT_HUM = registerSoundEvent("fluorescent_light");
    public static final SoundEvent FLUORESCENT_LIGHT_HUM2 = registerSoundEvent("fluorescent_light2");


    public static final SoundEvent LIGHTS_OUT = registerSoundEvent("lights_out");
    public static final SoundEvent LIGHTS_ON = registerSoundEvent("lights_on");
    public static final SoundEvent LIGHT_BLINK = registerSoundEvent("light_blink");


    public static final SoundEvent INTERCOM_BASIC1 = registerSoundEvent("intercom_basic1");
    public static final SoundEvent INTERCOM_BASIC2 = registerSoundEvent("intercom_basic2");
    public static final SoundEvent INTERCOM_FRIEND = registerSoundEvent("intercom_friend");
    public static final SoundEvent INTERCOM_REVERSED = registerSoundEvent("intercom_reversed");
    public static final SoundEvent CREEPY_MUSIC1 = registerSoundEvent("creepy_music1");
    public static final SoundEvent CREEPY_MUSIC2 = registerSoundEvent("creepy_music2");
    public static final SoundEvent FAR_CROWD = registerSoundEvent("far_crowd");


    public static final SoundEvent LEVEL1_AMBIENCE1 = registerSoundEvent("level1_ambience1");
    public static final SoundEvent LEVEL1_AMBIENCE2 = registerSoundEvent("level1_ambience2");
    public static final SoundEvent LEVEL1_AMBIENCE3 = registerSoundEvent("level1_ambience3");
    public static final SoundEvent LEVEL1_AMBIENCE4 = registerSoundEvent("level1_ambience4");


    public static final SoundEvent WATER_PIPE = registerSoundEvent("water_pipe");
    public static final SoundEvent GAS_PIPE = registerSoundEvent("gas_pipe");
    public static final SoundEvent CREAKING1 = registerSoundEvent("creaking1");
    public static final SoundEvent CREAKING2 = registerSoundEvent("creaking2");
    public static final SoundEvent LEVEL2_AMBIENCE = registerSoundEvent("level2_ambience");
    public static final SoundEvent LEVEL2_WARP_CREAKING_LOOP = registerSoundEvent("level2_warp_creaking_loop");


    public static final SoundEvent SWIM = registerSoundEvent("swim");
    public static final SoundEvent POOLROOMS_AMBIENCE_NOON = registerSoundEvent("poolrooms_ambience_noon");
    public static final SoundEvent POOLROOMS_AMBIENCE_SUNSET = registerSoundEvent("poolrooms_ambience_sunset");
    public static final SoundEvent POOLROOMS_SPLASH1 = registerSoundEvent("poolrooms_splash1");
    public static final SoundEvent POOLROOMS_SPLASH2 = registerSoundEvent("poolrooms_splash2");
    public static final SoundEvent POOLROOMS_DRIP1 = registerSoundEvent("poolrooms_drip1");
    public static final SoundEvent POOLROOMS_DRIP2 = registerSoundEvent("poolrooms_drip2");
    public static final SoundEvent SUNSET_TRANSITION = registerSoundEvent("sunset_transition");
    public static final SoundEvent MIDNIGHT_TRANSITION = registerSoundEvent("midnight_transition");
    public static final SoundEvent SUNSET_TRANSITION_END = registerSoundEvent("sunset_transition_end");


    public static final SoundEvent INFINITE_GRASS_AMBIENCE = registerSoundEvent("infinite_grass_ambience");
    public static final SoundEvent INFINITE_GRASS_SOUNDEVENT = registerSoundEvent("infinite_grass_soundevent");
    public static final SoundEvent INFINITE_GRASS_SOUNDEVENT_FAR = registerSoundEvent("infinite_grass_soundevent_far");


    public static final SoundEvent SKINWALKER_AMBIENCE = registerSoundEvent("skinwalker_ambience");
    public static final SoundEvent SKINWALKER_CHASE = registerSoundEvent("skinwalker_chase");
    public static final SoundEvent SKINWALKER_NOTICE = registerSoundEvent("skinwalker_notice");
    public static final SoundEvent SKINWALKER_SNIFF = registerSoundEvent("skinwalker_sniff");
    public static final SoundEvent SKINWALKER_FOOTSTEP = registerSoundEvent("skinwalker_footstep");

    public static final SoundEvent SKINWALKER_BONE_CRACK = registerSoundEvent("skinwalker_bone_crack");
    public static final SoundEvent SKINWALKER_BONE_CRACK_LONG = registerSoundEvent("skinwalker_bone_crack_long");
    public static final SoundEvent SKINWALKER_REVEAL = registerSoundEvent("skinwalker_reveal");
    public static final SoundEvent JUMPSCARE = registerSoundEvent("jumpscare");
    public static final SoundEvent SKINWALKER_RELEASE = registerSoundEvent("skinwalker_release");

    public static final SoundEvent SMILER_AMBIENCE = registerSoundEvent("smiler_laugh");
    public static final SoundEvent SMILER_GLITCH = registerSoundEvent("smiler_glitch");


    public static final SoundEvent CARPET_WALK = registerSoundEvent("carpet_walk");
    public static final SoundEvent CARPET_RUN = registerSoundEvent("carpet_run");

    public static final SoundEvent CONCRETE_WALK = registerSoundEvent("concrete_walk");
    public static final SoundEvent CONCRETE_RUN = registerSoundEvent("concrete_run");

    public static final SoundEvent GRASS_WALK = registerSoundEvent("grass_walk");
    public static final SoundEvent GRASS_RUN = registerSoundEvent("grass_run");


    public static final SoundEvent EMERGENCY_LIGHT_ALARM = registerSoundEvent("emergency_light_alarm");
//    public static final SoundEvent ENTERING_THE_BACKROOMS_ALARM = registerSoundEvent("entering_the_backrooms_alarm");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(SPBRevamped.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        SPBRevamped.LOGGER.info("Registering Sounds for" + SPBRevamped.MOD_ID);
    }
}
