package com.sp.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevampedClient;
import com.sp.render.camera.CutsceneManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TitleText implements HudRenderCallback {
    public TitleText (){
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        CutsceneManager cutsceneManager = SPBRevampedClient.getCutsceneManager();

        if(cutsceneManager.backroomsBySP && !cutsceneManager.blackScreen.isBlackScreen){
            Text text = Text.literal("The Backrooms");
            Text text2 = Text.literal("A Mod By ").append(Text.literal("SpacePotato").formatted(Formatting.GOLD));
            MinecraftClient client = MinecraftClient.getInstance();
            if(client != null){
                int width = client.getWindow().getScaledWidth();
                int height = (client.getWindow().getScaledHeight());

                drawContext.getMatrices().push();
                drawContext.getMatrices().translate((float)(width / 2), (float)(height / 2), 0.0F);
                RenderSystem.enableBlend();
                drawContext.getMatrices().push();
                drawContext.getMatrices().scale(2.5F, 2.5F, 2.5F);
                int w = client.textRenderer.getWidth(text);
                int h = client.textRenderer.fontHeight;
                drawContext.drawText(client.textRenderer, text, -w/2,  -h/2, 0xFFFFFF, true);
                drawContext.getMatrices().pop();

                drawContext.getMatrices().push();
                drawContext.getMatrices().scale(1.0F, 1.0F, 1.0F);
                int w2 = client.textRenderer.getWidth(text2);
                int h2 = client.textRenderer.fontHeight;
                drawContext.drawText(client.textRenderer, text2, -w2/2,  10, 0xFFFFFF, true);
                drawContext.getMatrices().pop();

                RenderSystem.disableBlend();
                drawContext.getMatrices().pop();

            }
        }
    }
}
