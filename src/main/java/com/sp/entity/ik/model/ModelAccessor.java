package com.sp.entity.ik.model;

import java.util.Optional;

public interface ModelAccessor {
    Optional<BoneAccessor> getBone(String boneName);
}
