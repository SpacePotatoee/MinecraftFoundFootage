package com.sp.entity.ai.goals;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;

public class FinalFormWanderGoal extends WanderAroundFarGoal {
    private final SkinWalkerComponent component;

    public FinalFormWanderGoal(SkinWalkerEntity entity, double d) {
        super(entity, d);
        this.component = InitializeComponents.SKIN_WALKER.get(entity);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && component.isInTrueForm() && component.isIdle();
    }
}
