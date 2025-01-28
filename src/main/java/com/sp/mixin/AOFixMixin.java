package com.sp.mixin;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.VeilDeferredRenderer;
import net.fabricmc.fabric.impl.client.indigo.Indigo;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Shadow @Final
    int[] light;

    @Shadow protected abstract int getBrightness(int i, int j, int k, int l, float f, float g, float h, float m);


    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void fix(BlockRenderView world, BlockState state, BlockPos pos, Direction direction, float[] box, BitSet flags, boolean shaded, CallbackInfo ci){
        ci.cancel();

        BlockPos lightPos = flags.get(0) ? pos.offset(direction) : pos;
        BlockModelRenderer.NeighborData neighborData = BlockModelRenderer.NeighborData.getData(direction);
        BlockPos.Mutable searchPos = new BlockPos.Mutable();
        BlockState searchState;
        BlockModelRenderer.BrightnessCache brightnessCache = BlockModelRenderer.BRIGHTNESS_CACHE.get();


        searchPos.set(lightPos, neighborData.faces[0]);
        searchState = world.getBlockState(searchPos);
        int light1 = brightnessCache.getInt(searchState, world, searchPos);
        float ao1 = brightnessCache.getFloat(searchState, world, searchPos);

        boolean bl = !searchState.shouldBlockVision(world, searchPos) || searchState.getOpacity(world, searchPos) == 0;


        searchPos.set(lightPos, neighborData.faces[1]);
        searchState = world.getBlockState(searchPos);
        int light2 = brightnessCache.getInt(searchState, world, searchPos);
        float ao2 = brightnessCache.getFloat(searchState, world, searchPos);

        boolean bl2 = !searchState.shouldBlockVision(world, searchPos) || searchState.getOpacity(world, searchPos) == 0;


        searchPos.set(lightPos, neighborData.faces[2]);
        searchState = world.getBlockState(searchPos);
        int light3 = brightnessCache.getInt(searchState, world, searchPos);
        float ao3 = brightnessCache.getFloat(searchState, world, searchPos);

        boolean bl3 = !searchState.shouldBlockVision(world, searchPos) || searchState.getOpacity(world, searchPos) == 0;


        searchPos.set(lightPos, neighborData.faces[3]);
        searchState = world.getBlockState(searchPos);
        int light4 = brightnessCache.getInt(searchState, world, searchPos);
        float ao4 = brightnessCache.getFloat(searchState, world, searchPos);

        boolean bl4 = !searchState.shouldBlockVision(world, searchPos) || searchState.getOpacity(world, searchPos) == 0;



        float n;
        int o;
        if (!bl3 && !bl) {
            n = ao1;
            o = light1;
        } else {
            searchPos.set(lightPos).move(neighborData.faces[0]).move(neighborData.faces[2]);
            searchState = world.getBlockState(searchPos);
            n = brightnessCache.getFloat(searchState, world, searchPos);
            o = brightnessCache.getInt(searchState, world, searchPos);
        }

        float p;
        int q;
        if (!bl4 && !bl) {
            p = ao1;
            q = light1;
        } else {
            searchPos.set(lightPos).move(neighborData.faces[0]).move(neighborData.faces[3]);
            searchState = world.getBlockState(searchPos);
            p = brightnessCache.getFloat(searchState, world, searchPos);
            q = brightnessCache.getInt(searchState, world, searchPos);
        }

        float r;
        int s;
        if (!bl3 && !bl2) {
            r = ao1;
            s = light1;
        } else {
            searchPos.set(lightPos).move(neighborData.faces[1]).move(neighborData.faces[2]);
            searchState = world.getBlockState(searchPos);
            r = brightnessCache.getFloat(searchState, world, searchPos);
            s = brightnessCache.getInt(searchState, world, searchPos);
        }

        float t;
        int u;
        if (!bl4 && !bl2) {
            t = ao1;
            u = light1;
        } else {
            searchPos.set(lightPos).move(neighborData.faces[1]).move(neighborData.faces[3]);
            searchState = world.getBlockState(searchPos);
            t = brightnessCache.getFloat(searchState, world, searchPos);
            u = brightnessCache.getInt(searchState, world, searchPos);
        }

        int v = brightnessCache.getInt(state, world, pos);
        searchPos.set(pos, direction);
        searchState = world.getBlockState(searchPos);


        if (flags.get(0) || !searchState.isOpaqueFullCube(world, searchPos)) {
            v = brightnessCache.getInt(searchState, world, searchPos);
        }

        float w = flags.get(0)
                ? brightnessCache.getFloat(world.getBlockState(lightPos), world, lightPos)
                : brightnessCache.getFloat(world.getBlockState(pos), world, pos);

        BlockModelRenderer.Translation translation = BlockModelRenderer.Translation.getTranslations(direction);

        float x = (ao4 + ao1 + p + w) * 0.25F;
        float y = (ao3 + ao1 + n + w) * 0.25F;
        float z = (ao3 + ao2 + r + w) * 0.25F;
        float aa = (ao4 + ao2 + t + w) * 0.25F;
        if (flags.get(1) && neighborData.nonCubicWeight) {
            float ab = box[neighborData.field_4192[0].shape] * box[neighborData.field_4192[1].shape];
            float ac = box[neighborData.field_4192[2].shape] * box[neighborData.field_4192[3].shape];
            float ad = box[neighborData.field_4192[4].shape] * box[neighborData.field_4192[5].shape];
            float ae = box[neighborData.field_4192[6].shape] * box[neighborData.field_4192[7].shape];
            float af = box[neighborData.field_4185[0].shape] * box[neighborData.field_4185[1].shape];
            float ag = box[neighborData.field_4185[2].shape] * box[neighborData.field_4185[3].shape];
            float ah = box[neighborData.field_4185[4].shape] * box[neighborData.field_4185[5].shape];
            float ai = box[neighborData.field_4185[6].shape] * box[neighborData.field_4185[7].shape];
            float aj = box[neighborData.field_4180[0].shape] * box[neighborData.field_4180[1].shape];
            float ak = box[neighborData.field_4180[2].shape] * box[neighborData.field_4180[3].shape];
            float al = box[neighborData.field_4180[4].shape] * box[neighborData.field_4180[5].shape];
            float am = box[neighborData.field_4180[6].shape] * box[neighborData.field_4180[7].shape];
            float an = box[neighborData.field_4188[0].shape] * box[neighborData.field_4188[1].shape];
            float ao = box[neighborData.field_4188[2].shape] * box[neighborData.field_4188[3].shape];
            float ap = box[neighborData.field_4188[4].shape] * box[neighborData.field_4188[5].shape];
            float aq = box[neighborData.field_4188[6].shape] * box[neighborData.field_4188[7].shape];
            this.brightness[translation.firstCorner] = x * ab + y * ac + z * ad + aa * ae;
            this.brightness[translation.secondCorner] = x * af + y * ag + z * ah + aa * ai;
            this.brightness[translation.thirdCorner] = x * aj + y * ak + z * al + aa * am;
            this.brightness[translation.fourthCorner] = x * an + y * ao + z * ap + aa * aq;
            int ar = this.meanBrightness(light4, light1, q, v);
            int as = this.meanBrightness(light3, light1, o, v);
            int at = this.meanBrightness(light3, light2, s, v);
            int au = this.meanBrightness(light4, light2, u, v);
            this.light[translation.firstCorner] = this.getBrightness(ar, as, at, au, ab, ac, ad, ae);
            this.light[translation.firstCorner] = this.getBrightness(ar, as, at, au, ab, ac, ad, ae);
            this.light[translation.secondCorner] = this.getBrightness(ar, as, at, au, af, ag, ah, ai);
            this.light[translation.thirdCorner] = this.getBrightness(ar, as, at, au, aj, ak, al, am);
            this.light[translation.fourthCorner] = this.getBrightness(ar, as, at, au, an, ao, ap, aq);
        } else {
            this.light[translation.firstCorner] = this.meanBrightness(light4, light1, q, v);
            this.light[translation.secondCorner] = this.meanBrightness(light3, light1, o, v);
            this.light[translation.thirdCorner] = this.meanBrightness(light3, light2, s, v);
            this.light[translation.fourthCorner] = this.meanBrightness(light4, light2, u, v);
            this.brightness[translation.firstCorner] = x;
            this.brightness[translation.secondCorner] = y;
            this.brightness[translation.thirdCorner] = z;
            this.brightness[translation.fourthCorner] = aa;
        }

        //Reinserting veil's "Disable Ambient Occlusion"
        VeilDeferredRenderer deferredRenderer = VeilRenderSystem.renderer().getDeferredRenderer();
        if (!deferredRenderer.getLightRenderer().isAmbientOcclusionEnabled()) {
            Arrays.fill(this.brightness, 1.0F);
        }
    }


    /**These 4 methods are also from
     * {@link net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator}
     */
    @Unique
    private int meanBrightness(int lightA, int lightB, int lightC, int lightD) {
        if (Indigo.FIX_MEAN_LIGHT_CALCULATION) {
            if (lightA == 0 || lightB == 0 || lightC == 0 || lightD == 0) {
                // Normalize values to non-zero minimum
                final int min = nonZeroMin(nonZeroMin(lightA, lightB), nonZeroMin(lightC, lightD));

                lightA = Math.max(lightA, min);
                lightB = Math.max(lightB, min);
                lightC = Math.max(lightC, min);
                lightD = Math.max(lightD, min);
            }

            return meanInnerBrightness(lightA, lightB, lightC, lightD);
        } else {
            return vanillaMeanBrightness(lightA, lightB, lightC, lightD);
        }
    }

    @Unique
    private int meanInnerBrightness(int a, int b, int c, int d) {
        // bitwise divide by 4, clamp to expected (positive) range
        return a + b + c + d >> 2 & 0xFF00FF;
    }

    @Unique
    private int vanillaMeanBrightness(int a, int b, int c, int d) {
        if (a == 0) a = d;
        if (b == 0) b = d;
        if (c == 0) c = d;
        // bitwise divide by 4, clamp to expected (positive) range
        return a + b + c + d >> 2 & 0xFF00FF;
    }

    @Unique
    private int nonZeroMin(int a, int b) {
        if (a == 0) return b;
        if (b == 0) return a;
        return Math.min(a, b);
    }

}
