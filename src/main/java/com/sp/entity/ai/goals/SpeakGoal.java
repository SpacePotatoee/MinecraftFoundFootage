package com.sp.entity.ai.goals;

import net.minecraft.entity.ai.goal.Goal;

public class SpeakGoal extends Goal {
    @Override
    public boolean canStart() {
        return false;
    }
}
