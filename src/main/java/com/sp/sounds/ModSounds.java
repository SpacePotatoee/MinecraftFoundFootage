package com.sp.sounds;

import com.sp.SPBRevamped;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent ZOOM_IN_SOUND = registerSoundEvent("zoomin");
    public static final SoundEvent ZOOM_OUT_SOUND = registerSoundEvent("zoomout");
    public static final SoundEvent FLASHLIGHT_CLICK = registerSoundEvent("flashlight_click");


    public static final SoundEvent AMBIENCE = registerSoundEvent("ambience");


    public static final SoundEvent FLUORESCENT_LIGHT_HUM = registerSoundEvent("fluorescent_light");
    public static final SoundEvent FLUORESCENT_LIGHT_HUM2 = registerSoundEvent("fluorescent_light2");


    public static final SoundEvent LIGHTS_OUT = registerSoundEvent("lights_out");
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
    public static final SoundEvent LEVEL2_WARP_CREAKING = registerSoundEvent("level2_warp_creaking");
    public static final SoundEvent LEVEL2_WARP_CREAKING_LOOP = registerSoundEvent("level2_warp_creaking_loop");


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(SPBRevamped.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        SPBRevamped.LOGGER.info("Registering Sounds for" + SPBRevamped.MOD_ID);
    }


}
