package com.sp.mixin.soundphysicsremasteredcompat;

import com.sonicether.soundphysics.config.blocksound.BlockDefinition;
import com.sonicether.soundphysics.config.blocksound.BlockSoundConfigBase;
import com.sp.block.SprintBlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.Map;

@Mixin(targets = "com.sonicether.soundphysics.config.ReflectivityConfig")
@Pseudo
public abstract class SFRReflectivityMixin extends BlockSoundConfigBase {
    public SFRReflectivityMixin(Path path) {
        super(path);
    }

    @Inject(method = "addDefaults", at = @At("TAIL"), remap = false)
    private void addReflectivity(Map<BlockDefinition, Float> map, CallbackInfo ci) {
        putSoundType(map, SprintBlockSoundGroup.CARPET, 0.1F);
        putSoundType(map, SprintBlockSoundGroup.CONCRETE, 1.5F);
        putSoundType(map, SprintBlockSoundGroup.GRASS2, 0.1F);
    }
}
