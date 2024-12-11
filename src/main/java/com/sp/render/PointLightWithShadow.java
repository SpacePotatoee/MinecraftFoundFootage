package com.sp.render;

import foundry.veil.api.client.render.deferred.light.PointLight;

import java.nio.ByteBuffer;

public class PointLightWithShadow extends PointLight {
    private boolean shouldRenderShadows;

//    @Override
//    public void store(ByteBuffer buffer) {
//        super.store(buffer);
//
//        if(this.shouldRenderShadows) {
//            buffer.putFloat(1);
//        } else {
//            buffer.putFloat(0);
//        }
//    }

    public PointLightWithShadow setShouldRenderShadows(boolean shouldRenderShadows) {
        this.shouldRenderShadows = shouldRenderShadows;
        return this;
    }

    public boolean shouldRenderShadows(){
        return this.shouldRenderShadows;
    }
}
