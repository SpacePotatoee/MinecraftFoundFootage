package com.sp.util;

import net.minecraft.client.gl.GlUniform;

public interface uniformTest {
    GlUniform orthoMatrix = null;
    GlUniform viewMatrix = null;
    GlUniform lightAngle = null;

    public GlUniform getOrthoMatrix();
    public GlUniform getViewMatrix();
    public GlUniform getLightAngle();

}
