package com.sp.mixin;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.VeilDeferredRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.BitSet;


/**
 * Veil overrides Fabric's fix for Vanilla lighting which makes it appear blocky <p>
 * This is a combination of both fabric's {@link net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator}
 * and Minecraft's {@link net.minecraft.client.render.block.BlockModelRenderer.AmbientOcclusionCalculator}
 * to make the lighting smooth again
 */
@SuppressWarnings("UnstableApiUsage")
@Mixin(targets = "net.minecraft.client.render.block.BlockModelRenderer$AmbientOcclusionCalculator")
public abstract class AOFixMixin {
    @Shadow @Final
    float[] brightness;

    @Inject(method = "apply", at = @At("HEAD"))
    private void fix(BlockRenderView world, BlockState state, BlockPos pos, Direction direction, float[] box, BitSet flags, boolean shaded, CallbackInfo ci){
        //Reinserting veil's "Disable Ambient Occlusion"
        VeilDeferredRenderer deferredRenderer = VeilRenderSystem.renderer().getDeferredRenderer();
        if (!deferredRenderer.getLightRenderer().isAmbientOcclusionEnabled()) {
            Arrays.fill(this.brightness, 1.0F);
        }
    }
}
