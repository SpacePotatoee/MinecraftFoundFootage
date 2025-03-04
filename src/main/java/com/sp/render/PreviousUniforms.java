package com.sp.render;

import foundry.veil.api.client.render.CameraMatrices;
import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PreviousUniforms {

    public static Matrix4f prevModelViewMat;
    public static Matrix4f prevProjMat;
    public static Vector3f prevCameraPos;

    public static void update(){
        CameraMatrices cameraMatrices = VeilRenderSystem.renderer().getCameraMatrices();
        prevProjMat = new Matrix4f(cameraMatrices.getProjectionMatrix());
        prevCameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().toVector3f();
        prevModelViewMat = new Matrix4f(cameraMatrices.getViewMatrix());
    }

}
