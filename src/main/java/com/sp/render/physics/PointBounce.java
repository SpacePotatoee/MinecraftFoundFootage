package com.sp.render.physics;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class PointBounce implements HudRenderCallback {
    private boolean initialized = false;
    private PhysicsPoint pointA;
    private PhysicsPoint pointB;
    private PhysicsStick stick;

    public PointBounce(){
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        if(client.world != null) {
            if(!initialized){
//                this.pointA = new PhysicsPoint(new Vec3d((double) width/2, (double) height/2, 0), new Vec3d((double) width/2 + 1, (double) height/2 + 1, 0), width, height, false);
//                this.pointB = new PhysicsPoint(new Vec3d((double) width/2, (double) height/2 + 10, 0), new Vec3d((double) width/2 - 10, (double) height/2, 0), width, height, true);
                this.stick = new PhysicsStick(this.pointA, this.pointB, 30);
                this.initialized = true;
            }

            this.pointA.updatePoint();
            this.pointB.updatePoint();
            this.stick.updateSticks();
            drawContext.drawItem(new ItemStack(Items.HEART_OF_THE_SEA), (int) this.pointA.getX() - 8, (int) this.pointA.getY() - 8);
            drawContext.drawItem(new ItemStack(Items.HEART_OF_THE_SEA), (int) this.pointB.getX() - 8, (int) this.pointB.getY() - 8);

            drawContext.fill((int) this.pointA.getX() - 8, (int) this.pointA.getY() - 8, (int) this.pointB.getX() - 8, (int) this.pointB.getY() - 8, 16777215);
            drawContext.draw();

//            RenderSystem.assertOnRenderThread();
//            GlStateManager._depthMask(false);
//            GlStateManager._disableCull();
//            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
//            Tessellator tessellator = RenderSystem.renderThreadTesselator();
//            BufferBuilder bufferBuilder = tessellator.getBuffer();
//            RenderSystem.lineWidth(4.0F);
//            bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
//
//            bufferBuilder.vertex(this.pointA.x, this.pointA.y, 0.0).color(255, 0, 0, 255).normal(1.0F, 0.0F, 0.0F).next();
//            bufferBuilder.vertex(this.pointA.x, this.pointA.y, 0.0).color(255, 0, 0, 255).normal(1.0F, 0.0F, 0.0F).next();
//
//            tessellator.draw();
//            RenderSystem.lineWidth(2.0F);
//            bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
//
//            bufferBuilder.vertex(this.pointB.x, this.pointB.y, 0.0).color(255, 0, 0, 255).normal(1.0F, 0.0F, 0.0F).next();
//            bufferBuilder.vertex(this.pointB.x, this.pointB.y, 0.0).color(255, 0, 0, 255).normal(1.0F, 0.0F, 0.0F).next();
//
//            tessellator.draw();
//            RenderSystem.lineWidth(1.0F);
//            GlStateManager._enableCull();
//            GlStateManager._depthMask(true);
        }
    }
}
