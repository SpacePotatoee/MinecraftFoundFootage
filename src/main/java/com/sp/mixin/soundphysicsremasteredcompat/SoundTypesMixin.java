package com.sp.mixin.soundphysicsremasteredcompat;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.block.SprintBlockSoundGroup;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(targets = "com.sonicether.soundphysics.config.SoundTypes")
@Pseudo
public class SoundTypesMixin {

    @Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Collections;unmodifiableMap(Ljava/util/Map;)Ljava/util/Map;", shift = At.Shift.BEFORE), remap = false)
    private static void addMaps(CallbackInfo ci, @Local Map<BlockSoundGroup, String> names){
        names.put(SprintBlockSoundGroup.CARPET, "CARPET");
        names.put(SprintBlockSoundGroup.CONCRETE, "CONCRETE");
        names.put(SprintBlockSoundGroup.GRASS2, "GRASS2");
    }

}
