package com.sp.mixin.soundphysicsremasteredcompat;

import com.llamalad7.mixinextras.sugar.Local;
import com.sonicether.soundphysics.config.AllowedSoundConfig;
import com.sp.init.ModSounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AllowedSoundConfig.class)
@Pseudo
public class AllowedSoundsConfigMixin {

    @Inject(method = "createDefaultMap", at = @At("RETURN"), remap = false)
    private void ignoreSoundPhysics(CallbackInfoReturnable<Map<String, Boolean>> cir, @Local Map<String, Boolean> map) {
        map.put(ModSounds.INTERCOM_BASIC1.getId().toString(), false);
        map.put(ModSounds.INTERCOM_BASIC2.getId().toString(), false);
        map.put(ModSounds.INTERCOM_FRIEND.getId().toString(), false);
        map.put(ModSounds.INTERCOM_REVERSED.getId().toString(), false);

        map.put(ModSounds.LIGHTS_OUT.getId().toString(), false);
        map.put(ModSounds.LIGHTS_ON.getId().toString(), false);
        map.put(ModSounds.LIGHT_BLINK.getId().toString(), false);

        map.put(ModSounds.CREEPY_MUSIC1.getId().toString(), false);
        map.put(ModSounds.CREEPY_MUSIC2.getId().toString(), false);
        map.put(ModSounds.FAR_CROWD.getId().toString(), false);

        map.put(ModSounds.LEVEL1_AMBIENCE1.getId().toString(), false);
        map.put(ModSounds.LEVEL1_AMBIENCE2.getId().toString(), false);
        map.put(ModSounds.LEVEL1_AMBIENCE3.getId().toString(), false);
        map.put(ModSounds.LEVEL1_AMBIENCE4.getId().toString(), false);

        map.put(ModSounds.SMILER_AMBIENCE.getId().toString(), false);
        map.put(ModSounds.SMILER_GLITCH.getId().toString(), false);

        map.put(ModSounds.CREAKING1.getId().toString(), false);
        map.put(ModSounds.CREAKING2.getId().toString(), false);
        map.put(ModSounds.LEVEL2_AMBIENCE.getId().toString(), false);
        map.put(ModSounds.AMBIENCE.getId().toString(), false);

        map.put(ModSounds.SKINWALKER_AMBIENCE.getId().toString(), false);
        map.put(ModSounds.SKINWALKER_BONE_CRACK.getId().toString(), false);
        map.put(ModSounds.SKINWALKER_CHASE.getId().toString(), false);
        map.put(ModSounds.SKINWALKER_FOOTSTEP.getId().toString(), false);
        map.put(ModSounds.SKINWALKER_NOTICE.getId().toString(), false);
        map.put(ModSounds.SKINWALKER_BONE_CRACK_LONG.getId().toString(), false);
        map.put(ModSounds.SKINWALKER_RELEASE.getId().toString(), false);
        map.put(ModSounds.SKINWALKER_REVEAL.getId().toString(), false);
        map.put(ModSounds.SKINWALKER_SNIFF.getId().toString(), false);

        map.put(ModSounds.POOLROOMS_SPLASH1.getId().toString(), false);
        map.put(ModSounds.POOLROOMS_SPLASH2.getId().toString(), false);
        map.put(ModSounds.POOLROOMS_DRIP1.getId().toString(), false);
        map.put(ModSounds.POOLROOMS_DRIP2.getId().toString(), false);

        map.put(ModSounds.MIDNIGHT_TRANSITION.getId().toString(), false);
        map.put(ModSounds.SUNSET_TRANSITION.getId().toString(), false);
        map.put(ModSounds.SUNSET_TRANSITION_END.getId().toString(), false);

        map.put(ModSounds.POOLROOMS_AMBIENCE_NOON.getId().toString(), false);
        map.put(ModSounds.POOLROOMS_AMBIENCE_SUNSET.getId().toString(), false);

        map.put(ModSounds.INFINITE_GRASS_AMBIENCE.getId().toString(), false);
        map.put(ModSounds.INFINITE_GRASS_SOUNDEVENT.getId().toString(), false);
        map.put(ModSounds.INFINITE_GRASS_SOUNDEVENT_FAR.getId().toString(), false);

        map.put(ModSounds.WINDTUNNEL_GRASS_AMBIENCE.getId().toString(), false);

        map.put(ModSounds.EMERGENCY_LIGHT_ALARM.getId().toString(), false);
    }
}
