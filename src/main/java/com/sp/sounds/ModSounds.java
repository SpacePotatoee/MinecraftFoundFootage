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


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(SPBRevamped.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        SPBRevamped.LOGGER.info("Registering Sounds for" + SPBRevamped.MOD_ID);
    }


}
