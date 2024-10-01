package com.sp.mixin.Veil;

import com.sp.SPBRevamped;
import foundry.veil.Veil;
import foundry.veil.api.client.render.deferred.VeilDeferredRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(VeilDeferredRenderer.class)
public class VeilDeferredRendererMixin {

    //@Shadow
    //public static final Identifier OPAQUE_MIX = new Identifier(SPBRevamped.MOD_ID, "core/mix_opaque");

}
