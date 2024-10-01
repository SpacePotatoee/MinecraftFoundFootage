package com.sp.screen;

import com.sp.SPBRevamped;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.awt.*;

public class TextOverlay implements HudRenderCallback {
    private static final Identifier TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/hud/camera_hud.png");
    int color;
    Color Col = new Color(0x0FFFFFF, true);
    int on;

    public TextOverlay (int on){
        this.on = on;
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int x = 0;
        int y = 0;

        if(client != null){
            int width = client.getWindow().getScaledWidth();
            int height = (client.getWindow().getScaledHeight());

            x = width/2;
            y = height;

            if(on > 0) {
                color = 0xFFFFFF;
            }
            else {
                color = 0x0FFFFFF;
            }

            //drawContext.drawCenteredTextWithShadow(client.textRenderer, , x, y - 45, 0x0FFFFFF);
        }
    }
}
