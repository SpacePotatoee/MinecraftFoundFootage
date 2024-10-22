package com.sp.util;

import net.minecraft.client.gl.GlUniform;

public interface uniformTest {
    GlUniform getOrthoMatrix();
    GlUniform getViewMatrix();
    GlUniform getLightAngle();
    GlUniform getWarpAngle();

}
