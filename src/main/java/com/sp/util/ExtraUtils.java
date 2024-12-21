package com.sp.util;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.Identifier;

public class ExtraUtils {

    public static void stopAllOtherSounds(Identifier id, SoundSystem soundSystem){
        for (SoundInstance soundInstance : soundSystem.sounds.values()) {
            if (!soundInstance.getId().equals(id)) {
                soundSystem.stop(soundInstance);
            }
        }
    }

}
