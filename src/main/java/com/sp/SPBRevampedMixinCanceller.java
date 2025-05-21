package com.sp;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class SPBRevampedMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (mixinClassName.equals("foundry.veil.mixin.client.deferred.AmbientOcclusionFaceMixin") || mixinClassName.equals("foundry/veil/mixin/client/deferred/AmbientOcclusionFaceMixin"))
            return true;

        return false;
    }
}
