package com.sp.mixin;

import com.sp.SPBRevampedClient;
import com.sp.render.PoolroomsDayCycle;
import foundry.veil.api.client.render.shader.program.MutableUniformAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Window;
import net.minecraft.screen.PlayerScreenHandler;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MutableUniformAccess.class, remap = false)
public interface MutableUniformAccessMixin {
    @Shadow void setVector(CharSequence name, float x, float y);

    @Shadow void setFloat(CharSequence name, float value);

    @Shadow void setVector(CharSequence name, Vector3fc value);

    @Inject(method = "applyRenderSystem", at = @At("TAIL"))
    default void applyRenderSystem(CallbackInfo ci) {

        if(SPBRevampedClient.cameraBobOffset != null) {
            this.setVector("cameraBobOffset", SPBRevampedClient.cameraBobOffset);
        }

        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();
        this.setVector("ScreenSize", window.getWidth(), window.getHeight());

        SpriteAtlasTexture texture = MinecraftClient.getInstance().getBakedModelManager().getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        if(texture != null) {
            this.setFloat("atlasAspectRatio", (float) texture.getHeight() / texture.getWidth());
        }

        if(client.world != null && SPBRevampedClient.camera != null) {
            this.setFloat("sunsetTimer", PoolroomsDayCycle.getDayTime(client.world));
            SPBRevampedClient.setShadowUniforms((MutableUniformAccess) this, client.world);

            this.setFloat("warpAngle", SPBRevampedClient.getWarpTimer(client.world));
        }

    }

}
