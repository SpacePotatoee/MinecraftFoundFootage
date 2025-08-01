package com.sp.render.gui.stamina;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import static net.minecraft.util.math.MathHelper.floor;

public class StaminaBar implements HudRenderCallback {
    private static final Identifier STAMINA_ICONS = new Identifier(SPBRevamped.MOD_ID, "textures/gui/stamina.png");
    private Long fadeStart;
    private float fadeTimer;


    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        // Check if stamina HUD is enabled in config
        if (!StaminaAPI.isStaminaHUDEnabled()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if(player != null) {
            PlayerComponent component = InitializeComponents.PLAYER.get(player);
            int width = 44;
            int height = 64;

            // Get configuration values
            float scale = StaminaAPI.getStaminaHUDScale();
            float opacity = StaminaAPI.getStaminaHUDOpacity();
            int offsetX = StaminaAPI.getStaminaHUDOffsetX();
            int offsetY = StaminaAPI.getStaminaHUDOffsetY();

            RenderSystem.enableBlend();
            if(component.getStamina() < StaminaAPI.MAX_STAMINA){
                this.fadeStart = null;
                this.fadeTimer = 0.0f;

                drawContext.getMatrices().push();
                drawContext.getMatrices().translate(
                    (float)(drawContext.getScaledWindowWidth() / 2) + offsetX,
                    (float)(drawContext.getScaledWindowHeight() / 2) + offsetY,
                    0.0F
                );
                drawContext.getMatrices().scale(scale, scale, scale);
                drawContext.setShaderColor(1.0f, 1.0f, 1.0f, opacity);

                float normalizedStamina = 1.0f - (float) component.getStamina() / StaminaAPI.MAX_STAMINA;
                int offset = floor(normalizedStamina * 64);

                drawContext.drawTexture(STAMINA_ICONS, width/2, -height/2, width, 0, 64, height);
                drawContext.drawTexture(STAMINA_ICONS, width/2 + 4, -height/2 + offset, 0, offset, width, height);

                drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                drawContext.getMatrices().pop();
            } else if(this.fadeTimer < 1.0f) {
                if(this.fadeStart == null){
                    this.fadeStart = Util.getMeasuringTimeMs();
                }
                this.fadeTimer = Math.min((float) (Util.getMeasuringTimeMs() - this.fadeStart) / 1000L, 1.0f);

                drawContext.getMatrices().push();
                drawContext.getMatrices().translate(
                    (float)(drawContext.getScaledWindowWidth() / 2) + offsetX,
                    (float)(drawContext.getScaledWindowHeight() / 2) + offsetY,
                    0.0F
                );
                drawContext.getMatrices().scale(scale, scale, scale);

                drawContext.setShaderColor(1.0f, 1.0f, 1.0f, opacity * (1.0f - this.fadeTimer));
                drawContext.drawTexture(STAMINA_ICONS, width/2, -height/2, width, 0, 64, height);
                drawContext.drawTexture(STAMINA_ICONS, width/2 + 4, -height/2, 0, 0, width, height);

                drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                drawContext.getMatrices().pop();

            }
            RenderSystem.disableBlend();

        }
    }
}
